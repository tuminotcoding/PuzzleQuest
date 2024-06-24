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
                bitmapDataList.add(new BitmapData(imageGroup, new Sprite(image)));
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
                    System.out.println("name:"+imageName+" x:"+x+" y:"+y+" w:"+w+" h:"+h);
                    BufferedImage subImage = data.sprite.image.getSubimage(x, y, w, h);
                    bitmapList.add(new BitmapData(imageName, new Sprite(subImage, x, y, w, h)));
                }
            }
            imageCache.put(data.name, bitmapList);
        }
    }

    public static List<BitmapData> getBitmapDataList(String group) {
        return imageCache.get(group);
    }

    public static Sprite getImage(String imageName) {
        if (!imageName.isEmpty()) {
            for (var entry : imageCache.entrySet()) {
                for (var data : imageCache.get(entry.getKey())) {
                    if (data.name.equals(imageName)) {
                        return data.sprite;
                    }
                }
            }
        }
        return null;
    }

    public static List<Sprite> getSpritesInRange(String group, String firstImageName, String lastImageName) {
        List<Sprite> imageList = new ArrayList<>();
        boolean foundFirst = false;

        List<BitmapData> dataList = imageCache.get(group);
        if (dataList != null) {
            for (var data : dataList) {
                System.out.println("found" + data.name);
                if (foundFirst || data.name.equals(firstImageName)) {

                    imageList.add(data.sprite);
                    foundFirst = true;

                    if (data.name.equals(lastImageName)) {
                        break; // We find the last sprite, break the loop.
                    }
                }
            }
        }
        else {
            System.out.println("Error: Group not found in Assets: " + group);
        }

        return imageList;
    }

    public static Sprite getImage(String group, String imageName) {
        List<BitmapData> dataList = imageCache.get(group);
        if (dataList != null) {
            for (var data : imageCache.get(group)) {
                if (data.name.equals(imageName)) {
                    return data.sprite;
                }
            }
        }
        return null;
    }
}
