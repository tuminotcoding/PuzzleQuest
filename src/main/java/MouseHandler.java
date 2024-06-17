import java.awt.event.MouseEvent;

public class MouseHandler {
    public enum Type {
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_DRAGGED,
        MOUSE_MOVED
    }

    public MouseEvent event;
    public Type type;

    public MouseHandler(MouseEvent e, MouseHandler.Type type) {
        this.event = e;
        this.type = type;
    }
}
