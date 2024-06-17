import org.joml.Vector2i;
import java.awt.*;

public class Button extends Component {
    public Sprite actionSprte;
    public Sprite pressedSprite;
    public AnimatedSprite animatedSprite;
    public String text;
    public Label label;

    Button(Vector2i position, String text) {
        super(position);

        try {
            this.actionSprte = ImageManager.getImage("bmp_skin_buttmain", "img_buttwide_act");
            this.pressedSprite = ImageManager.getImage("bmp_skin_buttmain", "img_buttwide_pres");
            this.size = this.actionSprte.size;

            var spritesList = ImageManager.getSpritesInRange("bmp_skin_buttmain",
                    "img_buttwide_hi0",
                    "img_buttwide_hi3");

            this.animatedSprite = new AnimatedSprite(spritesList, 30);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.text = text;
        this.label = new Label(new Vector2i(), text, 22);
    }

    public void update(long dt) {
        this.animatedSprite.update();
    }

    public void render(Graphics g) {
        this.label.position = new Vector2i(this.position);

        if (this.isPressed) {
            this.pressedSprite.render(g, this.position);
            this.label.position.y++;
        } else if (this.isInside) {
            this.animatedSprite.render(g, this.position);
        } else {
            this.actionSprte.render(g, this.position);
        }

        FontMetrics metrics = g.getFontMetrics(this.label.font);
        int width = metrics.stringWidth(text);
        int height = metrics.getHeight();
        int ascent = metrics.getAscent();

        // Align the text on the button center.
        this.label.position.x += (this.actionSprte.size.x - width) / 2;
        this.label.position.y += (this.actionSprte.size.y - height) / 2 + ascent;

        this.label.text = this.text;
        this.label.outlineWidth = 2;
        this.label.render(g);
    }
}
