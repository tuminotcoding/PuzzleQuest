import java.util.EnumMap;
import java.util.Map;

class PlayerScore {
    private Map<GemType, Integer> gemCount;
    int health;
    int maxHealth;
    boolean nextTurn;

    PlayerScore() {
        health = maxHealth = 50;
        gemCount = new EnumMap<>(GemType.class);
        for (GemType gem : GemType.values()) {
            gemCount.put(gem, 0);
        }
    }

    public void restart(){
        gemCount.clear();
        health = maxHealth = 50;
    }

    public void clearGem(GemType gem) {
        gemCount.put(gem, 0);
    }

    public void addGem(GemType gem, int count) {
        gemCount.put(gem, gemCount.get(gem) + count);
    }

    public int getGemCount(GemType gem) {
        return gemCount.get(gem);
    }
}