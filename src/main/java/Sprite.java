import org.joml.Math;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sprite {

    public BufferedImage image;
    public int angle;
    public int alpha;

    public Sprite(BufferedImage image){
        this.image = image;
        this.alpha = 255;
        this.angle = 0;
    }

    public void render(Graphics g, int x, int y, int w, int h) {

        if(this.angle == 0) {
            Graphics2D g2d = (Graphics2D) g;

            Composite composite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha / 255));
            g2d.drawImage(this.image, x, y, w, h, null);
            g2d.setComposite(composite);
            return;
        }

        BufferedImage rotated = new BufferedImage(w, h, image.getType());
        Graphics2D g2d = rotated.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha / 255));
        g2d.translate((double) w / 2, (double) h / 2);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawRenderedImage(this.image, AffineTransform.getTranslateInstance(-w / 2.0, -h / 2.0));
        g2d.dispose();
        g.drawImage(rotated, x, y, null);
    }
}
