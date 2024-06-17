import org.joml.Vector2i;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Grid {
    public static final int blockSize = 71; // Tamanho do bloco do grid
    public static final int gap = 3; // Espaço entre os blocos do grid
    public static final Vector2i offset = new Vector2i(217, 133);
    private final Sprite[] selectorSprite = new Sprite[2];
    private Gem[] table;

    private final int numRows;
    private final int numColumns;
    private double animTime;
    private float alpha;

    private boolean killSelector;
    private boolean firstGemClicked;

    private Vector2i selectorSpriteSize;
    private Vector2i firstGemSelectedCoord;
    private Vector2i secondGemSelectedCoord;
    private Vector2i dragStartCoord;

    public Map<String, Sprite> gemsSprites;
    public List<Vector2i> matches;
    public GemSwap gemSwaps;

    Grid(int x, int y) {
        this.numRows = x;
        this.numColumns = y;
        this.table = new Gem[x * y];
        this.alpha = 1.0f;
        this.firstGemSelectedCoord = new Vector2i();
        this.secondGemSelectedCoord = new Vector2i();
        this.dragStartCoord = new Vector2i();
        this.gemsSprites = new HashMap<>();
        this.matches = new ArrayList<>();
        this.gemSwaps = null;

        try {
            var bitmapSelector = ImageManager.getBitmapDataList("bmp_skin_battlemisc");
            this.selectorSprite[0] = bitmapSelector.get(0).sprite;
            this.selectorSprite[1] = bitmapSelector.get(1).sprite;
            this.selectorSpriteSize = new Vector2i(selectorSprite[0].size);

            var bitmapGems = ImageManager.getBitmapDataList("bmp_skin_gemsgrid");
            bitmapGems.forEach(item -> {
                String imgName = item.name.replace("img_", "");
                if (Gem.gemsType.contains(imgName)) {
                    this.gemsSprites.put(imgName, item.sprite);
                } else {
                    System.out.println("invalid texture: " + item.name);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Gem getGem(Vector2i vec) {
        return table[vec.x * numColumns + vec.y];
    }

    private Gem getGem(int row, int column) {
        return table[row * numColumns + column];
    }

    private void setGem(int row, int column, Gem gem) {
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
                Sprite sprite = gemsSprites.get(randomType);

                Gem gem = new Gem();
                gem.setType(randomType);
                gem.setSprite(sprite.image);
                gem.setCoord(i, j);
                gem.pos.x = i * (Grid.blockSize + Grid.gap);
                gem.pos.y = j * (Grid.blockSize + Grid.gap);
                this.setGem(i, j, gem);
            }
        }
    }

    public boolean trySwap(Vector2i coordA, Vector2i coordB) {
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

    // Checks if two blocks are adjacent to each other on the grid.
    public boolean isAdjacent(Vector2i coordA, Vector2i coordB) {
        int dX = Math.abs(coordA.x - coordB.x);
        int dY = Math.abs(coordA.y - coordB.y);
        return (dX + dY == 1);
    }

    // Calculates a grid block position from pixel coordinates and gem size.
    public Vector2i getBlockPosition(Vector2i coords, Vector2i size) {
        int x = (Grid.offset.x - size.x / 2 + Grid.blockSize / 2) + coords.x * (Grid.blockSize + Grid.gap);
        int y = (Grid.offset.y - size.y / 2 + Grid.blockSize / 2) + coords.y * (Grid.blockSize + Grid.gap);
        return new Vector2i(x, y);
    }

    // Scans for and records sequences of three or more identical gems by rows and columns.
    public boolean findMatches() {
        matches.clear();

        //Check horizontal matches
        for (int i = 0; i < numRows; i++) {
            int found = 1;
            for (int j = 0; j < numColumns; j++) {
                if (j < numColumns - 1 && this.getGem(i, j).isEqual(this.getGem(i, j + 1))) {
                    found++;
                } else {
                    // Add to the list of match if 3 or more identical gems were found in a row.
                    if (found >= 3) {
                        for (int k = 0; k < found; k++) {
                            matches.add(new Vector2i(i, j - k));
                        }
                    }
                    found = 1;
                }
            }
        }

        // Check vertical matches
        for (int j = 0; j < numColumns; j++) {
            int found = 1;
            for (int i = 0; i < numRows; i++) {
                if (i < numRows - 1 && this.getGem(i, j).isEqual(this.getGem(i + 1, j))) {
                    found++;
                } else {
                    // Add to the list of match if 3 or more identical gems were found in a row.
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

    // Remove all matching gems.
    public void removeMatchingGems() {
        for (Vector2i match : matches) {
            this.getGem(match).setType(null);
            this.getGem(match).fadeOut();
        }
    }

    public boolean isGemsFadding (){
        for (var gems : table) {
            if (!gems.isFadeOutComplete()) {
                return true;
            }
        }

        return false;
    }

    // Gets a list of dropping gems.
    public List<GemDrop> getDroppingGems() {
        List<GemDrop> dropList = new ArrayList<>();
        for (int x = 0; x < numRows; x++) {
            int drop = 0;
            for (int y = numColumns - 1; y >= 0; y--) {

                if (!this.getGem(x, y).isFadeOutComplete())
                    continue;

                if (this.getGem(x, y).gemType == null) {
                    dropList.add(new GemDrop(x, y, 0));
                    drop++;
                } else if (drop > 0) {
                    dropList.add(new GemDrop(x, y, drop));
                }
            }
        }

        return dropList;
    }

    static boolean hasDropped = false;
    // Drop and insert new gems.
    public void dropGems(long dt) {
        var dropList = this.getDroppingGems();

        for (GemDrop drop : dropList) {
            Vector2i coord = new Vector2i(drop.x, drop.y + drop.shift);

            // Insert new gems
            String randomType = getRandomGemType();
            Sprite sprite = gemsSprites.get(randomType);
            this.getGem(coord).setType(randomType);
            this.getGem(coord).setSprite(sprite.image);
            this.getGem(coord).alpha = 1f;

            // Swap gems
            if (drop.shift > 0) {
                swap(new Vector2i(drop.x, drop.y), coord);
            }
        }
        hasDropped = true;
    }

    static boolean isSwappingBack = false;

    // Processes and executes gem swap actions if matches.
    public void processGemsSwaps() {
        if (gemSwaps != null) {
            Gem gemA = gemSwaps.gemA;
            Gem gemB = gemSwaps.gemB;
            var coordA = gemSwaps.gemA.coord;
            var coordB = gemSwaps.gemB.coord;

            // Wait for the gems to complete their animations before swapping them.
            if (gemSwaps.canSwap()) {
                if (isAdjacent(coordA, coordB)) {
                    if (trySwap(coordA, coordB)) {
                        // We need to swap the position of the gems.
                        Vector2i temp = new Vector2i(gemA.pos);
                        gemA.pos = new Vector2i(gemB.pos);
                        gemB.pos = temp;
                        gemSwaps = null;
                    } else {
                        // If the sequence doesn't match, then swap it back.
                        if (!isSwappingBack) {
                            gemA.move(gemB);
                            gemB.move(gemA);
                            isSwappingBack = true;
                        }
                    }
                }
            }
        }
        if (isSwappingBack && gemSwaps.canSwap()) {
            isSwappingBack = false;
            gemSwaps = null;
        }
    }

    public void update(long dt) {
        this.findMatches();
        this.removeMatchingGems();
        this.dropGems(dt);
        this.processGemsSwaps();

        for (Gem gem : table) {
            if (gem != null) {
                gem.update(dt);
            }
        }

        if (killSelector) {
            animTime += dt / 1000.0f;
            int scale = (int) (animTime * 4);
            selectorSpriteSize.add(scale, scale);
            alpha = (float) Math.clamp(1f - animTime / 1.2f, 0, 1);
            if (alpha <= 0.0f) {
                firstGemSelectedCoord.set(-1);
                secondGemSelectedCoord.set(-1);
                killSelector = false;
                animTime = 0.0f;
            }
        } else {
            alpha = 1.0f;
            selectorSpriteSize = new Vector2i(selectorSprite[0].size);
        }
    }

    public void render(Graphics g) {
        for (Gem gem : table) {
            if (gem != null) {
                gem.draw(g);
            }
        }

        if (firstGemSelectedCoord.x > -1 && firstGemSelectedCoord.y > -1) {
            Vector2i coords = this.getBlockPosition(firstGemSelectedCoord, selectorSprite[0].size);
            selectorSprite[0].angle = (selectorSprite[0].angle + 1) % 360;
            selectorSprite[0].alpha = alpha;
            selectorSprite[0].render(g, coords, selectorSpriteSize);
            if (secondGemSelectedCoord.x > -1 && secondGemSelectedCoord.y > -1) {
                coords = this.getBlockPosition(secondGemSelectedCoord, selectorSprite[0].size);
                selectorSprite[0].render(g, coords, selectorSpriteSize);
            }
        }
    }

    public Vector2i getCoordFromMousePos(MouseEvent e) {
        int x = (e.getX() - Grid.offset.x) / (Grid.blockSize + Grid.gap);
        int y = (e.getY() - Grid.offset.y) / (Grid.blockSize + Grid.gap);

        x = Math.clamp(0, x, numRows - 1);
        y = Math.clamp(0, y, numColumns - 1);
        return new Vector2i(x, y);
    }

    public boolean isMouseHover(MouseEvent e) {
        return (e.getX() >= Grid.offset.x && e.getX() < Grid.offset.x + numRows * (Grid.blockSize + Grid.gap)) &&
                (e.getY() >= Grid.offset.y && e.getY() < Grid.offset.y + numColumns * (Grid.blockSize + Grid.gap));
    }

    public void mouseEvent(MouseHandler handler) {
        MouseEvent e = handler.event;

        if (!this.isMouseHover(e))
            return;

        if (this.isGemsFadding())
            return;

        if (gemSwaps != null && !gemSwaps.gemA.hasSwapped() && !gemSwaps.gemB.hasSwapped())
            return;

        Vector2i gridCoords = getCoordFromMousePos(e);
        switch (handler.type) {
            case MOUSE_PRESSED:
                if (!firstGemClicked) {
                    firstGemSelectedCoord = gridCoords;
                    secondGemSelectedCoord.set(-1);
                    firstGemClicked = true;
                    System.out.println("first gem clicked");
                } else {
                    if (firstGemSelectedCoord.equals(gridCoords)) {
                        firstGemClicked = false;
                        firstGemSelectedCoord.set(-1);
                        System.out.println("gems equals");
                    } else {
                        if (isAdjacent(firstGemSelectedCoord, gridCoords)) {
                            this.getGem(firstGemSelectedCoord).move(this.getGem(gridCoords));
                            this.getGem(gridCoords).move(this.getGem(firstGemSelectedCoord));
                            gemSwaps = new GemSwap(this.getGem(firstGemSelectedCoord), this.getGem(gridCoords));
                            secondGemSelectedCoord = gridCoords;
                            killSelector = true;
                            firstGemClicked = false;
                            System.out.println("move gem");
                        } else {
                            firstGemSelectedCoord = gridCoords;
                            firstGemClicked = true;
                            System.out.println("out gem");
                        }
                    }
                }
                dragStartCoord = gridCoords;
                break;
            case MOUSE_DRAGGED:
                if (isAdjacent(dragStartCoord, gridCoords)) {
                    this.getGem(dragStartCoord).move(this.getGem(gridCoords));
                    this.getGem(gridCoords).move(this.getGem(dragStartCoord));
                    firstGemSelectedCoord = new Vector2i(dragStartCoord);
                    gemSwaps = new GemSwap(this.getGem(dragStartCoord), this.getGem(gridCoords));
                    dragStartCoord.set(-1);
                    firstGemClicked = false;
                    killSelector = true;
                }
                break;
            default:
        }
    }
}
