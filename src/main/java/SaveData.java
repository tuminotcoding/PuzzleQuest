import java.util.Map;

public class SaveData {
    public Map<GemType, Integer> encodedGems;
    public int encodedHealth;

    SaveData(Map<GemType, Integer> encodedGems, int encodedHealth){
        this.encodedGems = encodedGems;
        this.encodedHealth = encodedHealth;
    }
}
    