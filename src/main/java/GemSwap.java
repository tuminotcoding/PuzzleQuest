import org.joml.Vector2i;

public class GemSwap {
    public Gem gemA;
    public Gem gemB;

    GemSwap(Gem gemA, Gem gemB) {
        this.gemA = gemA;
        this.gemB = gemB;
    }

    public boolean canSwap() {
        return this.gemA.hasSwapped() && this.gemB.hasSwapped();
    }
}
