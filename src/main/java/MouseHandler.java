import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    public enum Type {
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_DRAGGED,
        MOUSE_MOVED
    }

    public MouseEvent event;
    public Type type;

     MouseHandler(MouseEvent e, MouseHandler.Type type) {
        this.event = e;
        this.type = type;
    }
}
