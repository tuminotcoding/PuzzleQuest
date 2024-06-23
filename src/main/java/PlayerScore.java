import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

class PlayerScore {
    public Map<GemType, Integer> gemCount;
    int health;
    int maxHealth;
    boolean nextTurn;

    PlayerScore() {
        gemCount = new HashMap<>();
        this.restart();
    }

    public void restart(){
        for (GemType gem : GemType.values()) {
            gemCount.put(gem, 0);
        }
        health = maxHealth = 50;
    }

    public void clearGem(GemType gem) {
        gemCount.put(gem, 0);
    }

    public void addGem(GemType gem, int count) {
        gemCount.put(gem, gemCount.get(gem) + count);
    }

    public int getGemCount(GemType gem) {
        if(gemCount == null) {
            System.out.println("getGemCount error");
            return 0;
        }

        return gemCount.get(gem);
    }
}