import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GemsManager {
    private static Grid grid;
    private static Sprite backgroundSprite = null;

    GemsManager() {
        try {
            backgroundSprite = new Sprite(ImageIO.read(new File("src\\main\\resources\\background.png")));
            ImageManager.parseJsonFromFile("src\\main\\resources\\Assets.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        grid = new Grid(8, 8);
        grid.generateGrid();
    }

    public static void update(long dt) {
        grid.update(dt);
    }

    public static void render(Graphics g) {
        backgroundSprite.render(g, 0, 0, 1024, 768);

        grid.render(g);
    }

    public static void mousePressed(MouseEvent e) {

        grid.mousePressed(e);
    }
    public static void mouseReleased(MouseEvent e) {
    }

    public static void mouseMoved(MouseEvent e) {

        grid.mouseMoved(e);
    }
}