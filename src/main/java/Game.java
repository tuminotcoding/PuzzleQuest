import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable, MouseListener, MouseMotionListener {
    private BufferStrategy bs;
    private Thread thread;
    private static final long TIME_FRAME = 1000 / 60;
    private static JFrame frame = null;
    private long lastFrameTime = System.currentTimeMillis();
    static boolean isRunning = true;

    public Game() {
        new GameMenu();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void run() {

        createBufferStrategy(3);
        bs = getBufferStrategy();

        requestFocus();

        lastFrameTime = System.currentTimeMillis();
        long currentTime = System.currentTimeMillis();
        while (isRunning) {
            do {
                do {
                    long elapsedTime = currentTime - lastFrameTime;

                    Graphics g = null;
                    try {
                        g = bs.getDrawGraphics();
                        render(g);
                    } finally {
                        g.dispose();
                    }

                    while (elapsedTime < TIME_FRAME) {
                        try {
                            Thread.sleep(TIME_FRAME - elapsedTime);
                        } catch (InterruptedException e) {
                        }

                        currentTime = System.currentTimeMillis();
                        elapsedTime = currentTime - lastFrameTime;

                        update(elapsedTime);
                        lastFrameTime = System.currentTimeMillis();
                    }
                } while (bs.contentsRestored());
                bs.show();
            } while (bs.contentsLost());
        }
        bs.dispose();
    }

    public void update(long dt) {
        GameMenu.update(dt);
    }

    public void render(Graphics g) {
        GameMenu.render(g);
    }

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

    public void mouseClicked(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public synchronized void start() {
        thread = new Thread(this, "Display");
        thread.start();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setBackground(Color.BLACK);
        frame = new JFrame("Puzzle Quest");
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(1038, 788);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.toFront();
        frame.setAlwaysOnTop(false);

        game.start();
    }
}