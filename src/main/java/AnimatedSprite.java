import org.joml.Vector2i;
import java.awt.*;
import java.util.List;

public class AnimatedSprite {
    private List<Sprite> frames;
    private int currentFrame;
    private long lastTime, timer;
    private long speed;

    public AnimatedSprite(List<Sprite> frames, int speed) {
        this.speed = speed;

        this.frames = frames;
        currentFrame = 0;
        timer = 0;
        lastTime = System.currentTimeMillis();
    }

    public void update() {
        timer += System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();

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
}
