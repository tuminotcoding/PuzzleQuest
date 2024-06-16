import org.joml.Math;
import org.joml.Vector2i;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Grid {
    public static final int blockSize = 71; // Tamanho do bloco do grid
    public static final int gap = 3; // Espaço entre os blocos do grid
    public static Vector2i offset = new Vector2i(217, 133);
    private Gem[] table;

    private final int numRows;
    private final int numColumns;

    boolean isMousePressed;
    boolean firstGemClicked;

    private Sprite[] selectedSprite = new Sprite[2];
    private Vector2i selectedSpriteCoord;
    private Vector2i selectedSpriteSize;
    private Vector2i currentSpriteSelected;
    public Map<String, BufferedImage> sprites = new HashMap<>();
    public List<Vector2i> matches = new ArrayList<>();

    public class DropGems {
        int x, y, shift;

        DropGems(int x, int y, int shift){
            this.x = x;
            this.y = y;
            this.shift = shift;
        }
    }

    public class GemSwap{
        public Vector2i coordA;
        public Vector2i coordB;
        GemSwap(Vector2i coordA, Vector2i coordB){
            this.coordA = new Vector2i(coordA);
            this.coordB = new Vector2i(coordB);
        }
    }

    static List<GemSwap> gemSwaps = new ArrayList<>();

    Grid(int x, int y) {
        numRows = x;
        numColumns = y;
        table = new Gem[x*y];

        selectedSpriteCoord = new Vector2i();
        currentSpriteSelected= new Vector2i();

        var bitmapSelector = ImageManager.getBitmapDataList("bmp_skin_battlemisc");
        selectedSprite[0] = new Sprite(bitmapSelector.get(0).image);
        selectedSprite[1] = new Sprite(bitmapSelector.get(1).image);

        selectedSpriteSize = new Vector2i(selectedSprite[0].image.getWidth(), selectedSprite[0].image.getHeight());

        var bitmapGems = ImageManager.getBitmapDataList("bmp_skin_gemsgrid");
        bitmapGems.forEach(item -> {
            System.out.println(item.name);
            String imgName = item.name.replace("img_", "");
            if (Gem.gemsType.contains(imgName)) {
                sprites.put(imgName, item.image);
            } else {
                System.out.println("invalid texture: " + item.name);
            }
        });
    }

    private Gem getGem (Vector2i vec) {
        return table[vec.x * numColumns + vec.y];
    }

    private Gem getGem (int row, int column) {
        return table[row * numColumns + column];
    }

    private void setGem(int row, int column, Gem gem){
        table[row * numColumns + column] = gem;
    }

    private String getRandomGemType() {
        Random random = new Random();
        return Gem.gemsType.get(random.nextInt(Gem.gemsType.size()));
    }

    public void generateGrid() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                String randomType = getRandomGemType();
                BufferedImage sprite = sprites.get(randomType);

                Gem gem = new Gem();
                gem.setType(randomType);
                gem.setSprite(sprite);
                gem.setCoord(i, j);
                gem.pos.x = i * (Grid.blockSize + Grid.gap);
                gem.pos.y = j * (Grid.blockSize + Grid.gap);
                this.setGem(i, j, gem);
            }
        }
    }
    
    public boolean trySwap (Vector2i coordA, Vector2i coordB) {
        this.swap(coordA, coordB);
        if (this.findMatches()) {
            return true;
        }
        this.swap(coordA, coordB);

        return false;
    }

    public void swap(Vector2i coordA, Vector2i coordB) {
        Gem gemA = this.getGem(coordA);
        Gem gemB = this.getGem(coordB);

        String type = gemA.gemType;
        BufferedImage sprite = gemA.sprite;
        gemA.setType(gemB.gemType);
        gemA.setSprite(gemB.sprite);
        gemB.setType(type);
        gemB.setSprite(sprite);
    }

    // Check if two tiles can be swapped
    public boolean isAdjacent(Vector2i coordA, Vector2i coordB) {
        return (Math.abs(coordA.x - coordB.x) == 1 && coordA.y == coordB.y) ||
                (Math.abs(coordA.y - coordB.y) == 1 && coordA.x == coordB.x);
    }

    public Vector2i getBlockPosition(Vector2i coords, Vector2i size) {
        int x = (Grid.offset.x - size.x / 2 + Grid.blockSize / 2) + coords.x * (Grid.blockSize + Grid.gap);
        int y = (Grid.offset.y - size.y / 2 + Grid.blockSize / 2) + coords.y * (Grid.blockSize + Grid.gap);
        return new Vector2i(x, y);
    }

    public boolean findMatches() {
        matches.clear();

        // Horizontal swaps
        for (int i = 0; i < numRows; i++) {
            int found = 1;
            for (int j = 0; j < numColumns; j++) {
                if (j < numColumns - 1 && this.getGem(i, j).isEqual(this.getGem(i,j + 1))) {
                    found++;
                } else {
                    if (found >= 3) {
                        for (int k = 0; k < found; k++) {
                            matches.add(new Vector2i(i, j - k));
                        }
                    }
                    found = 1;
                }
            }
        }

        // Vertical swaps
        for (int j = 0; j < numColumns; j++) {
            int found = 1;
            for (int i = 0; i < numRows; i++) {
                if (i < numRows - 1 && this.getGem(i, j).isEqual(this.getGem(i + 1, j))) {
                    found++;
                } else {
                    if (found >= 3) {
                        for (int k = 0; k < found; k++) {
                            matches.add(new Vector2i(i - k, j));
                        }
                    }
                    found = 1;
                }
            }
        }
        return !matches.isEmpty();
    }

    public void removeMatchingGems(){
        for (Vector2i match : matches) {
            this.getGem(match).setType(null);
            this.getGem(match).fadeOut();
        }
    }

    public List<DropGems> getDroppingGems() {
        List<DropGems> dropList = new ArrayList<>();
        for (int x = 0; x < numRows; x++) {
            int drop = 0;
            for (int y = numColumns - 1; y >= 0; y--) {
                if (!this.getGem(x, y).isFadeOutComplete())
                    continue;

                if (this.getGem(x, y).gemType == null) {
                    dropList.add(new DropGems(x, y, 0));
                    drop++;
                } else if (drop > 0) {
                    dropList.add(new DropGems(x, y, drop));
                }
            }
        }

        return dropList;
    }

    public void dropGems(long dt) {
        var dropList = this.getDroppingGems();

        for (DropGems drop : dropList) {
            Vector2i coord = new Vector2i(drop.x, drop.y + drop.shift);
            String randomType = getRandomGemType();
            BufferedImage sprite = sprites.get(randomType);
            this.getGem(coord).setType(randomType);
            this.getGem(coord).setSprite(sprite);
            this.getGem(coord).alpha = 1f;

            if (drop.shift > 0) {
                swap(new Vector2i(drop.x, drop.y), coord);
            }
        }
    }

    public void update (long dt) {
        this.findMatches();
        this.removeMatchingGems();
        this.dropGems(dt);

        boolean finished = false;

        for (GemSwap swaps : gemSwaps) {
            Gem gemA = this.getGem(swaps.coordA);
            Gem gemB = this.getGem(swaps.coordB);

            if (!gemA.hasSwapped() && !gemB.hasSwapped()) {
                continue;
            }

            if (isAdjacent(swaps.coordA, swaps.coordB)) {
                if (trySwap(swaps.coordA, swaps.coordB)) {
                    Vector2i temp = new Vector2i(gemA.pos);
                    gemA.pos = new Vector2i(gemB.pos);
                    gemB.pos = temp;
                } else {
                    gemA.move(gemB);
                    gemB.move(gemA);
                }
                finished = true;
            }
        }

        if (finished) {
            gemSwaps.clear();
            selectedSpriteCoord.set(-1);
            currentSpriteSelected.set(-1);
        }

        for (Gem gem : table) {
            if (gem != null) {
                gem.update(dt);
            }
        }
    }

    public void render (Graphics g) {
        for (Gem gem : table) {
            if (gem != null) {
                gem.draw(g);
            }
        }

        if (selectedSpriteCoord.x > -1 && selectedSpriteCoord.y > -1) {

            Vector2i coords = this.getBlockPosition(selectedSpriteCoord, selectedSpriteSize);
            selectedSprite[0].angle = (selectedSprite[0].angle + 1) % 360;
            selectedSprite[0].render(g, coords.x, coords.y, selectedSpriteSize.x, selectedSpriteSize.y);

            if (isMousePressed && currentSpriteSelected.x >-1 && currentSpriteSelected.y >-1) {
                coords = this.getBlockPosition(currentSpriteSelected, selectedSpriteSize);
                selectedSprite[0].render(g, coords.x, coords.y, selectedSpriteSize.x, selectedSpriteSize.y);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        if (!gemSwaps.isEmpty()) {
            GemSwap firstSwap = gemSwaps.getFirst();
            if (!this.getGem(firstSwap.coordA).hasSwapped()) {
                return;
            }
        }

        int x = (e.getX() - Grid.offset.x) / (Grid.blockSize + Grid.gap);
        int y = (e.getY() - Grid.offset.y) / (Grid.blockSize + Grid.gap);

        // Verificar se o sprite selecionado está nos limites do grid.
        x = Math.clamp(0, x, numRows - 1);
        y = Math.clamp(0, y, numColumns - 1);

        if (!firstGemClicked) {
            selectedSpriteCoord.set(x, y);
            currentSpriteSelected.set(-1);
            firstGemClicked = true;
        } else {
            if (selectedSpriteCoord.equals(x, y)) {
                firstGemClicked = false;
                currentSpriteSelected.set(-1);
                isMousePressed = false;
            } else {
                currentSpriteSelected.set(x, y);
                isMousePressed = true;
                firstGemClicked = false;
                if (isAdjacent(currentSpriteSelected, selectedSpriteCoord)) {
                    this.getGem(selectedSpriteCoord).move(this.getGem(currentSpriteSelected));
                    this.getGem(currentSpriteSelected).move(this.getGem(selectedSpriteCoord));
                    gemSwaps.add(new GemSwap(currentSpriteSelected, selectedSpriteCoord));
                } else {
                    currentSpriteSelected.set(-1);
                    selectedSpriteCoord.set(x, y);
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }
}
