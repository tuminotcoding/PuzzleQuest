import org.joml.Vector2i;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.EnumMap;
import java.util.Vector;

public class ScoreBoard {
    private Label gemsText;
    public int currentPlayer;
    public PlayerScore[] playerScore = new PlayerScore[2];
    Sprite progressBar;
    Map<String, Integer> scoreGems;

    ScoreBoard() {
        gemsText = new Label(new Vector2i(106, 260), "", 16);

        playerScore[0] = new PlayerScore();
        playerScore[1] = new PlayerScore();
        playerScore[0].addGem(GemType.GEM_YELLOW, 3);
        playerScore[1].addGem(GemType.GEM_EXP, 3);

        this.progressBar = ImageManager.getImage("bmp_skin_battlemisc", "img_selglow");
    }

    void addGems(GemType gemType, int count) {
        int totalGems = playerScore[currentPlayer].getGemCount(gemType);
        int invertPlayer = currentPlayer > 0 ? 1 : 0;

        if (count >= 3 && gemType.equals(GemType.GEM_SKULL))
            playerScore[invertPlayer].health -= 3;

        if (totalGems == 10 && gemType.equals(GemType.GEM_EXP)) {
            playerScore[currentPlayer].clearGem(gemType);
            playerScore[invertPlayer].maxHealth -= 3;
        }

        if (totalGems == 10 && gemType.equals(GemType.GEM_GOLD)) {
            if (playerScore[currentPlayer].nextTurn) {
                playerScore[currentPlayer].clearGem(gemType);
                playerScore[currentPlayer].nextTurn = false;
                playerScore[invertPlayer].health -= 3;
            }

            playerScore[currentPlayer].nextTurn = true;
        }

        if (count >= 3 && gemType.equals(GemType.GEM_YELLOW))
            playerScore[invertPlayer].clearGem(gemType);

        if (count >= 3 && gemType.equals(GemType.GEM_RED))
            playerScore[currentPlayer].health += 3;

        playerScore[currentPlayer].addGem(gemType, count);
    }

    void update(long dt) {

        if(playerScore[0].health >= playerScore[0].maxHealth)
            playerScore[0].health = playerScore[0].maxHealth;

        if(playerScore[1].health >= playerScore[1].maxHealth)
            playerScore[1].health = playerScore[1].maxHealth;

        if(playerScore[0].health <= 0|| playerScore[1].health <= 0){
            String playerWin = playerScore[0].health > 0? "Player 1 wins" : "Player 2 wins";
            System.out.println(playerWin);
            playerScore[0].restart();
            playerScore[1].restart();
        }
    }

    void drawScore(Graphics g, Vector2i position, Color color, int score){
        gemsText.text  = String.valueOf(score);
        gemsText.color = color;
        gemsText.position = position;
        gemsText.render(g);
    }

    void drawPlayerScore(Graphics g, int playerID, int posX, int y) {
        int score = playerScore[playerID].getGemCount(GemType.GEM_GOLD);
        drawScore(g, new Vector2i(posX, y + 28), Color.YELLOW, score);
        score = playerScore[playerID].getGemCount(GemType.GEM_EXP);
        drawScore(g, new Vector2i(posX + 73, y + 28), Color.MAGENTA, score);

        for (int i = 0; i < 4; i++) {
            score = playerScore[playerID].getGemCount(GemType.values()[i]);
            drawScore(g, new Vector2i(posX + 64, y), Color.WHITE, score);
            posX += 21;
        }
    }

    void drawPlayerHealth(Graphics g, int playerID, int posX) {
        int health = playerScore[playerID].health;
        int maxHealth = playerScore[playerID].maxHealth;
        Vector2i position = new Vector2i(posX, 123);
        int progress = health *  this.progressBar.size.x / maxHealth;
        this.progressBar.render(g, new Vector2i(posX, 123), new Vector2i(progress, this.progressBar.size.y));

        this.gemsText.text  = health + " of " + maxHealth;
        this.gemsText.color = Color.WHITE;
        this.gemsText.shadowOffset = 0;
        FontMetrics metrics = g.getFontMetrics(this.gemsText.font);

        int width = metrics.stringWidth(this.gemsText.text);
        int height = metrics.getHeight();
        int ascent = metrics.getAscent();

        // Align the text on the progress bar center.
        this.gemsText.position.x = position.x + (this.progressBar.size.x - width) / 2;
        this.gemsText.position.y = position.y + (this.progressBar.size.y - height) / 2 + ascent;
        this.gemsText.render(g);
    }

    void render(Graphics g) {
        gemsText.shadowOffset = 2;

        drawPlayerScore(g,0, 44, 260);
        drawPlayerScore(g,1, 868, 260);

        drawPlayerHealth(g, 0, 15);
        drawPlayerHealth(g, 1, 838);
    }
}
