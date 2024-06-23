import java.nio.ByteBuffer;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class Database {
    private Connection connect() {
        String url = "jdbc:sqlite:puzzle.db";
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public void create() {
        String sql = "CREATE TABLE IF NOT EXISTS game_save ("
                + " id integer PRIMARY KEY,"
                + " save_name TEXT NOT NULL,"
                + " gemGreen INTEGER NOT NULL,"
                + " gemRed INTEGER NOT NULL,"
                + " gemYellow INTEGER NOT NULL,"
                + " gemBlue INTEGER NOT NULL,"
                + " gemSkull INTEGER NOT NULL,"
                + " gemExp INTEGER NOT NULL,"
                + " gemGold INTEGER NOT NULL,"
                + " health INTEGER NOT NULL"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertGems(String save_name, SaveData saveData) {
        String sql = "INSERT INTO game_save(save_name, gemGreen, gemRed, gemYellow, gemBlue, gemSkull, gemExp, gemGold, health) VALUES(?,?,?,?,?,?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, save_name);

            for (int i = 2; i < saveData.encodedGems.size() + 2; i++) {
                stmt.setInt(i, saveData.encodedGems.get(GemType.values()[i - 2]));
            }

           /* int[] pattern = {
                    2, 1, 3, 0, 4, 5, 6, 4,
                    1, 5, 1, 3, 5, 0, 2, 2,
                    3, 6, 4, 1, 3, 0, 1, 3,
                    0, 4, 2, 6, 0, 1, 3, 1,
                    4, 5, 1, 0, 1, 3, 5, 3,
                    5, 6, 5, 4, 3, 0, 4, 0,
                    6, 2, 1, 3, 1, 6, 5, 4,
                    2, 1, 3, 0, 4, 5, 4, 6
            };

            ByteBuffer buffer = ByteBuffer.allocate(pattern.length * Integer.BYTES);
            IntStream.range(0, pattern.length).forEach(buffer::putInt);
            byte[] patternBytes = buffer.array();

            stmt.setBytes(11, patternBytes);*/

            stmt.setInt(9, saveData.encodedHealth);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateGems(String saveName, SaveData saveData) {
        String sql = "UPDATE game_save SET gemGreen=?, gemRed=?, gemYellow=?, gemBlue=?, gemSkull=?, gemExp=?, gemGold=?, health=? WHERE save_name=?";

        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= saveData.encodedGems.size(); i++) {
                stmt.setInt(i, saveData.encodedGems.get(GemType.values()[i - 1]));
            }

            stmt.setInt(8, saveData.encodedHealth);
            stmt.setString(9, saveName);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public SaveData getGems(String saveName) {
        String sql = "SELECT * FROM game_save WHERE save_name = ?";
        Map<GemType, Integer> gems = new HashMap<>();
        int health = 0;

        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, saveName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                for (GemType gemType : GemType.values()) {

                    // "GEM_GREEN" -> "gemGreen"...
                    String type = gemType.name().toLowerCase();
                    int i = type.indexOf("_");
                    String converted = type.substring(0, i) +
                            type.substring(i + 1).substring(0, 1).toUpperCase() +
                            type.substring(i + 1).substring(1);

                    gems.put(gemType, rs.getInt(converted));
                }
                health = rs.getInt("health");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return new SaveData(gems, health);
    }

    public void deleteGems(String saveName) {
        String sql = "DELETE FROM game_save WHERE save_name = ?";

        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, saveName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<String> getAllSavesName () {
        List<String> names = new ArrayList<>();
        String sql = "SELECT save_name FROM game_save";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                names.add(rs.getString("save_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    public int getNumSaves() {
        String sql = "SELECT COUNT(DISTINCT save_id) AS total FROM game_save";
        int count = 0;

        try (Connection conn = this.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return count;
    }

}
