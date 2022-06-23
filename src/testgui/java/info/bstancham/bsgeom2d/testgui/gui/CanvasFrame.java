package info.bstancham.bsgeom2d.testgui.gui;

import java.awt.Dimension;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Easy way to make a frame with the following properties:
 * - setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
 * - easy access to keyboard and mouse interaction
 * - easy access to drawing methods
 * - automatically centre of screen
 *
 * To show the frame, use setVisible(true).
 *
 * NOTES:
 * - CanvasFrame does a few basic things, then asks 'mode' for additional actions/keybindings etc..
 *
 * TODO:
 * - mode-switching
 * - help text ---> generate from list of key and mouse bindings
 *
 */
public class CanvasFrame extends JFrame {

    private CanvasPanel canvas;

    public CanvasFrame(int xSize, int ySize, String title) {
        this(xSize, ySize, title, new CanvasPanel());
    }

    public CanvasFrame(int xSize, int ySize, String title, CanvasPanel canvPanel) {
        // System.out.println("... CanvasFrame constructor...");
        setTitle(title);
        // setSize(xSize, ySize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //panel = new CanvasPanel(xSize, ySize);
        canvas = canvPanel;
        canvas.setPreferredSize(new Dimension(xSize, ySize));
        add(canvas);
        pack();

        // when frame gains keyboard focus, give it to canvas
        addWindowFocusListener(new WindowAdapter() {
                public void windowGainedFocus(WindowEvent e) {
                    canvas.requestFocusInWindow();
                }
            });

        // position in middle of screen
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screen.width / 2 - xSize / 2,
                    screen.height / 2 - ySize / 2);
    }

    public CanvasPanel getCanvas() { return canvas; }

}
