import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GemsManager {
    private static Grid grid;
    private static Sprite backgroundSprite;
    public static Map<String, Sprite> splashSprite;

    GemsManager() {
        splashSprite = new HashMap<>();

        try {
            backgroundSprite = new Sprite(ImageIO.read(new File("src\\main\\resources\\background.png")));
            ImageManager.parseJsonFromFile("src\\main\\resources\\Assets.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        var bitmapGems = ImageManager.getBitmapDataList("bmp_skin_backdrop");
        bitmapGems.forEach(item -> {
            splashSprite.put(item.name, item.sprite);
        });

        grid = new Grid(8, 8);
        grid.generateGrid();
    }

    public static void update(long dt) {
        grid.update(dt);
    }

    public static void render(Graphics g) {
       // splashSprite.forEach((key, value) -> {
        //    value.render(g);
        //});

        backgroundSprite.render(g, 0, 0, 1024, 768);

        grid.render(g);
    }

    public static void mouseEvent(MouseHandler handler) {
        grid.mouseEvent(handler);
    }
}