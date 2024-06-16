import org.joml.Vector2i;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.List;

public class Gem {
    public static List<String> gemsType = List.of(
            "gem_green",
            "gem_red",
            "gem_yellow",
            "gem_blue",
            "gem_skull",
            "gem_exp",
            "gem_gold");

    public class GemAnimation {
        public double time;
        public double total;
        GemAnimation(){
            this(0, 0);
        }

        GemAnimation(double time, double total) {
            this.time = time;
            this.total = total;
        }
    }

    public String gemType;
    public BufferedImage sprite;
    public Vector2i coord;
    public Vector2i pos;
    public float alpha;
    public boolean isFadingOut, isSwapping, isMoving;
    public GemAnimation fadeoutAnim;
    public GemAnimation swapAnim;
    public GemAnimation movingDownAnim;
    public Vector2i start;
    public Vector2i end;
    public int fromY;
    public int toY;
    private Gem parent;

    Gem() {
        this.alpha = 1f;
        this.coord = new Vector2i();
        this.pos = new Vector2i();
        this.fadeoutAnim = new GemAnimation(0,0);
        this.swapAnim = new GemAnimation(0,0);
        this.movingDownAnim = new GemAnimation(0,0);
    }

    public boolean isEqual(Gem parent) {
        if (parent.gemType == null || this.gemType == null) {
            return false;
        }

        return this.gemType.equals(parent.gemType);
    }

    public void setCoord(int x, int y) {
        this.coord = new Vector2i(x, y);
    }

    public void setPosition(Vector2i position) {
        this.pos = position;
    }

    public void setType(String gemType) {
        this.gemType = gemType;
    }

    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    public void moveDown(int toY) {
        if(  this.isMoving) return;
        this.fromY = pos.y;
        this.toY = toY * (Grid.blockSize + Grid.gap);
        this.movingDownAnim = new GemAnimation(0, 0.5f);
        this.isMoving = true;
    }

    public void move(Gem parent) {
        this.start = new Vector2i(pos);
        this.end = new Vector2i(parent.pos);
        this.parent = parent;
        this.swapAnim = new GemAnimation(0,0.5f);
        this.isSwapping = true;
    }

    public void fadeOut() {
        this.fadeoutAnim = new GemAnimation(0,0.5f);
        this.isFadingOut = true;
    }

    public boolean isFadeOutComplete() {
        return this.fadeoutAnim.time >= this.fadeoutAnim.total && !this.isFadingOut;
    }

    public boolean hasSwapped() {
        return this.swapAnim.time >= this.swapAnim.total && !isSwapping;
    }

    public boolean hasMovedDown() {
        return this.movingDownAnim.time >= this.movingDownAnim.total && !isMoving;
    }

    public double updateFade(long dt, double animTime, double animDuration) {
        if (animTime < 0f) {
            return animTime;
        }

        animTime += dt / 1000.0f;

        if (isMoving) {
            if (animTime >= animDuration) {
                animTime = animDuration;
                isMoving = false;
            }
            else
            {
                if( pos.y < toY-1) {
                    pos.y = (int) MathUtils.lerp(fromY, toY, animTime / animDuration);
                }
            }

        } else if (isSwapping) {
            if (animTime >= animDuration) {
                animTime = animDuration;
                isSwapping = false;
            }
            int dx = Math.abs(coord.x - parent.coord.x);
            int dy = Math.abs(coord.y - parent.coord.y);
            if (dx == 1) {
                pos.x = (int) MathUtils.lerp(this.start.x, this.end.x, animTime / animDuration);
            }
            if (dy == 1) {
                pos.y = (int) MathUtils.lerp(this.start.y, this.end.y, animTime / animDuration);
            }
        } else if (isFadingOut) {
            if (animTime >= animDuration) {
                animTime = animDuration;
                isFadingOut = false;
            }
            alpha = (float) Math.clamp(1f - animTime / animDuration, 0f, 1f);
        }

        return animTime;
    }

    public void update(long dt) {
        if (this.isMoving) {
            this.movingDownAnim.time = this.updateFade(dt, this.movingDownAnim.time, this.movingDownAnim.total);
        } else if (this.isFadingOut) {
            this.fadeoutAnim.time = this.updateFade(dt, this.fadeoutAnim.time, this.fadeoutAnim.total);
        } else if (isSwapping) {
            this.swapAnim.time = this.updateFade(dt, this.swapAnim.time, this.swapAnim.total);
        }
    }

    public void draw(Graphics g) {
        if (this.sprite != null) {
            int w = this.sprite.getWidth();
            int h = this.sprite.getHeight();
            Graphics2D g2d = (Graphics2D) g;

            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));
            g2d.drawImage(this.sprite, Grid.offset.x + this.pos.x, Grid.offset.y + this.pos.y, w, h, null);
            g2d.setComposite(originalComposite);
        }
    }
}
