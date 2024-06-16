import org.json.JSONObject;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.file.Paths;
import java.util.*;
import org.json.JSONArray;
import java.io.File;
import java.nio.file.Files;

public class ImageManager {

    public static class BitmapData {
        public String name;
        public BufferedImage image;

        BitmapData(String name, BufferedImage image) {
            this.name = name;
            this.image = image;
        }
    }

    private static final Map<String, List<BitmapData>> imageCache = new HashMap<>();

    public static void parseJsonFromFile(String file) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(file)));
        parseJson(content);
    }

    public static void parseJson(String content) throws IOException {
        JSONObject jsonObj = new JSONObject(content);
        JSONObject assets = jsonObj.getJSONObject("Assets");

        List<BitmapData> data = processBitmaps(assets);
        processBitmapImages(assets, data);
    }

    private static List<BitmapData> processBitmaps(JSONObject assets) throws IOException {
        List<BitmapData> bitmapDataList = new ArrayList<>();
        JSONArray bitmapArray = assets.getJSONArray("Bitmap");
        for (Object item : bitmapArray) {
            JSONObject bitmap = (JSONObject) item;
            String imagePath = bitmap.getString("__text");
            String imageGroup = bitmap.getString("_tag");
            try {
                BufferedImage image = ImageIO.read(new File(imagePath));
                bitmapDataList.add(new BitmapData(imageGroup, image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmapDataList;
    }

    private static void processBitmapImages(JSONObject assets, List<BitmapData> bitmapDataList) {
        for (BitmapData data : bitmapDataList) {
            List<BitmapData> bitmapList = new ArrayList<>();
            JSONArray bitmapImageArray = assets.getJSONArray("BitmapImage");
            for (Object item : bitmapImageArray) {
                JSONObject bitmapImage = (JSONObject) item;
                String thisGroup = bitmapImage.getString("_bitmap");

                if (data.name.equals(thisGroup)) {
                    String imageName = bitmapImage.getString("_tag");
                    int x = bitmapImage.getInt("_x");
                    int y = bitmapImage.getInt("_y");
                    int w = bitmapImage.getInt("_width");
                    int h = bitmapImage.getInt("_height");
                    BufferedImage subImage = data.image.getSubimage(x, y, w, h);
                    bitmapList.add(new BitmapData(imageName, subImage));
                }
            }

            System.out.println(data.name);
            imageCache.put(data.name, bitmapList);
        }
    }

    public static List<BitmapData> getBitmapDataList(String group) {
        return imageCache.get(group);
    }

    public static BufferedImage getImage(String imageName) {
        if (!imageName.isEmpty()) {
            for (var entry : imageCache.entrySet()) {
                for (var data : imageCache.get(entry.getKey())) {
                    if (data.name.equals(imageName)) {
                        return data.image;
                    }
                }
            }
        }
        return null;
    }

    public static BufferedImage getImage(String group, String imageName) {
        List<BitmapData> dataList = imageCache.get(group);
        if (dataList != null) {
            for (var data : imageCache.get(group)) {
                if (data.name.equals(imageName)) {
                    return data.image;
                }
            }
        }
        return null;
    }
}
