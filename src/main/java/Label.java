import org.joml.Vector2i;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.io.File;

public class Label extends Component {
    public Font font;
    public String text;
    public Color color;

    public Color shadowColor;
    public int shadowOffset;

    public int outlineWidth;
    public Color outlineColor;
    public Color backgroundColor;
    public int backgroundWidth;

    Label(Vector2i position, String text, int size) {
        super(position);

        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            this.font = Font.createFont(Font.PLAIN, new File("resources\\MorrisRoman-Black.ttf")).deriveFont((float) size);
            ge.registerFont(font);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.text = text;
        this.position = position;
        this.shadowOffset = this.backgroundWidth = this.outlineWidth = 0;
        this.color  = this.shadowColor = this.outlineColor=  this.backgroundColor  = new Color(0x000000);
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void render(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g.create();

            FontMetrics metrics = g.getFontMetrics(this.font);
            int width = metrics.stringWidth(text);
            int height = metrics.getHeight();
            int ascent = metrics.getAscent();

            if(this.backgroundWidth > 0) {
                g.setColor(this.backgroundColor);
                g.fillRect(this.position.x - backgroundWidth / 2, this.position.y - backgroundWidth / 2 - ascent,
                        width + backgroundWidth, height + backgroundWidth);
            }

            // Draw the drop shadow
            if (this.shadowOffset > 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 178 / 255));
                int alpha = Math.min(color.getAlpha(), 178);
                this.shadowColor = new Color(outlineColor.getRed(), outlineColor.getRed(), outlineColor.getBlue(), alpha);
                g2d.setColor(this.shadowColor);


                AffineTransform orig = g2d.getTransform();
                g2d.translate(this.shadowOffset, this.shadowOffset);

                // Create a GlyphVector for the shadow
                FontRenderContext frcShadow = g2d.getFontRenderContext();
                GlyphVector gvShadow = font.createGlyphVector(frcShadow, this.text);
                Shape shadowShape = gvShadow.getOutline(this.position.x, this.position.y);
                g2d.fill(shadowShape);

                // Restores the original transformation and color settings.
                g2d.setTransform(orig);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            }

            // Draw the outline
            if (this.outlineWidth > 0) {
                g2d.setStroke(new BasicStroke(outlineWidth));
                g2d.setColor(outlineColor);
                int alpha = Math.min(color.getAlpha(), outlineColor.getAlpha());
                this.outlineColor = new Color(outlineColor.getRed(), outlineColor.getRed(), outlineColor.getBlue(), alpha);

                // Creates a GlyphVector for the outline.
                FontRenderContext frcOutline = g2d.getFontRenderContext();
                GlyphVector gvOutline = font.createGlyphVector(frcOutline, text);
                Shape outlineShape = gvOutline.getOutline(position.x, position.y);

                g2d.draw(outlineShape);
            }

            g2d.setColor(this.color);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setFont(this.font);
            g2d.drawString(this.text, this.position.x, this.position.y);
            g2d.dispose();
        }
    }
}
