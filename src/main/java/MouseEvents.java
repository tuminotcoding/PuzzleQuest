import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseEvents extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
        GameMenu.mouseEvent(new MouseHandler(e, MouseHandler.Type.MOUSE_PRESSED));
    }

    public void mouseReleased(MouseEvent e) {
        GameMenu.mouseEvent(new MouseHandler(e, MouseHandler.Type.MOUSE_RELEASED));
    }

    public void mouseMoved(MouseEvent e) {
        GameMenu.mouseEvent(new MouseHandler(e, MouseHandler.Type.MOUSE_MOVED));
    }

    public void mouseDragged(MouseEvent e) {
        GameMenu.mouseEvent(new MouseHandler(e, MouseHandler.Type.MOUSE_DRAGGED));
    }
}
