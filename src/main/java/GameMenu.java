import org.joml.Vector2i;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameMenu {
    private static Grid grid;
    private static Sprite backgroundSprite;
    public static Map<String, Sprite> splashSprite;

    public static Label label;
    public static Button[] button = new Button[2];

    private static ScoreBoard scoreBoard;
    GameMenu() {
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

        button[0] = new Button(new Vector2i(700, 150), "Novo jogo");
        button[1] = new Button(new Vector2i(700, 210), "Continuar partida");

        grid = new Grid(8, 8);
        grid.generateGrid();

        scoreBoard = new ScoreBoard();
    }

    public static void update(long dt) {

        for (var btn : button){
            btn.update(dt);
        }
        grid.update(dt);
    }

    public static void render(Graphics g) {
        /*splashSprite.forEach((key, value) -> {
            value.render(g);
        });

        for (var btn : button){
            btn.render(g);
        }*/

        backgroundSprite.render(g, 0, 0, 1024, 768);
        grid.render(g);

        scoreBoard.render(g);
    }

    public static void mouseEvent(MouseHandler handler) {
        for (var btn : button){
            btn.mouseEvent(handler);
        }
        grid.mouseEvent(handler);
    }
}