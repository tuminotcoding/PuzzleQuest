import org.joml.Vector2i;
import java.awt.*;
import java.util.List;

public class AnimatedSprite {
    private List<Sprite> frames;
    private int currentFrame;
    private double  timer;
    private final double speed;
    public Vector2i size;

    public AnimatedSprite(List<Sprite> frames, int speed) {
        this.speed = (double) speed / 1000;

        this.frames = frames;

        currentFrame = 0;
        timer = 0;
        size = frames.getFirst().size;
    }

    public void update(double dt) {
        timer += dt;
        if (timer > speed) {
            currentFrame++;
            timer = 0;

            if (currentFrame >= frames.size()) {
                currentFrame = 0;
            }
        }
    }

    public void render(Graphics g, Vector2i position) {
        frames.get(currentFrame).render(g, position);
    }

    public void render(Graphics g, Vector2i position, Vector2i size) {
        frames.get(currentFrame).render(g, position, size);
    }
}
