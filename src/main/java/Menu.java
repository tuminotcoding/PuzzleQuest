import org.joml.Vector2i;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class Menu extends Component {

    public interface MenuCallback {
        void onItemSelected(String item, boolean delete);
    }

    public MenuCallback callback;

    private static final int TOP_MENU = 0;
    private static final int CENTER_MENU = 1;
    private static final int BOTTOM_MENU = 2;
    private static final int SELECTED_MENU = 3;
    private static final int REMOVE_ITEM = 4;
    private static final int REMOVE_ITEM_OVER = 5;
    public Sprite[] sprites = new Sprite[6];
    Map<String, String> items;
    Label label;
    String selectedItem;
    boolean isInsideBtn;
    Menu(Vector2i position) {
        super(position);

        try {
            this.sprites[TOP_MENU] = ImageManager.getImage("bmp_skin_buttmisc", "img_popup_0");
            this.sprites[CENTER_MENU] = ImageManager.getImage("bmp_skin_buttmisc", "img_popup_1");
            this.sprites[BOTTOM_MENU] = ImageManager.getImage("bmp_skin_buttmisc", "img_popup_2");
            this.sprites[SELECTED_MENU] = ImageManager.getImage("bmp_skin_buttmisc", "img_popup_3");
            this.sprites[REMOVE_ITEM] = ImageManager.getImage("bmp_skin_buttmisc", "img_button_x");
            this.sprites[REMOVE_ITEM_OVER] = ImageManager.getImage("bmp_skin_buttmisc", "img_button_x_ovr");
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectedItem = "";
        size = new Vector2i(this.sprites[CENTER_MENU].size);
        size.x -= 6;

        items = new LinkedHashMap<>();

        IntStream.range(0, 10).forEach(i -> items.put("save" + i, "No save"));

        label = new Label(position, "", 21);
    }

    public void addItem(String item) {
        for (Map.Entry<String, String> entry : items.entrySet()) {
            if (entry.getValue().equals("No save")) {
                items.replace(entry.getKey(), item);
                return;
            }
        }
    }

    public Object getItemValue(String item) {
        return items.get(item);
    }

    boolean contains (Point p, Vector2i position, Vector2i size) {
        return p.x >= position.x && p.x <= position.x + size.x &&
                p.y >= position.y && p.y <= position.y + size.y;
    }

    public void mouseEvent(MouseHandler handler) {
        if (this.disable)
            return;

        MouseEvent e = handler.event;
        switch (handler.type) {
            case MOUSE_PRESSED:
                if (!selectedItem.isEmpty()) {
                    onClick();
                }
                break;
            case MOUSE_RELEASED:
                if (isPressed && contains(e.getPoint())) {
                    onClick();
                } else {
                    isInside = false;
                }
                isPressed = false;
                break;
            case MOUSE_MOVED:
                int yPos = position.y + 15;
                for (var item : items.entrySet()) {

                    this.isInsideBtn = contains(e.getPoint(), new Vector2i(position.x + size.x - this.sprites[REMOVE_ITEM].size.x - 6, yPos-3), new Vector2i(28));

                    this.isInside = contains(e.getPoint(), new Vector2i(position.x, yPos), new Vector2i(size.x-6, size.y));
                    if (isInside && !item.getValue().equals("No save")) {
                        selectedItem = item.getKey();
                        break;
                    } else {
                        selectedItem = "";
                    }
                    yPos += 26;
                }
                break;
            default:
        }
    }

    public void render(Graphics g) {
        if (this.disable)
            return;

        this.sprites[TOP_MENU].render(g, position);
        label.position = new Vector2i(position);
        this.label.position.x += 20;
        this.sprites[BOTTOM_MENU].render(g, new Vector2i(position.x, position.y + 15 + items.size() * 26));

        List<String> keys = new ArrayList<>(items.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = items.get(key);

            int yPos = position.y + 15 + i * 26;

            if (this.selectedItem.equals(key) && !value.equals("No save")) {
                this.sprites[SELECTED_MENU].render(g, new Vector2i(position.x, yPos));
            } else {
                this.sprites[CENTER_MENU].render(g, new Vector2i(position.x, yPos));
            }

            if (this.isInsideBtn && this.selectedItem.equals(key) && !value.equals("No save")) {
                this.sprites[REMOVE_ITEM_OVER].render(g, new Vector2i(position.x + size.x - this.sprites[REMOVE_ITEM].size.x - 6, yPos + 2));
            } else {
                this.sprites[REMOVE_ITEM].render(g, new Vector2i(position.x + size.x - this.sprites[REMOVE_ITEM].size.x - 6, yPos - 3));
            }
            label.position.y = yPos + 18;
            label.text = value;
            label.render(g);
        }
    }

    public void onClick() {
        if (callback != null) {
            if (!selectedItem.isEmpty() && this.isInsideBtn) {
                for (var item : items.entrySet()) {
                    if(selectedItem.equals(item.getKey())){
                      items.remove(selectedItem);
                      break;
                    }
                }
                callback.onItemSelected(selectedItem, true);
            } else {
                callback.onItemSelected(selectedItem, false);
            }
        }
    }

    public void setCallback(MenuCallback callback) {
        this.callback = callback;
    }
}
