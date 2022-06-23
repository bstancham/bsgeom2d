package info.bstancham.bsgeom2d.testgui.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.function.Supplier;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * ...
 *
 * CanvasPanel always grabs keyboard focus on switch to frame.
 */
public class CanvasPanel extends JPanel implements KeyListener {

    /**
     * Setup modes-list with CanvasMode.NullMode as default. Note that
     * CanvasMode.NullMode doesn't need to have a panel-reference
     * added because it does nothing.
     */
    protected CanvasMode[] modes = new CanvasMode[] { new CanvasMode.NullMode() };
    protected int modeIndex = 0;

    private ArrayList<KeyBinding> keyBindings = new ArrayList<>();
    private boolean showHelp = true;
    private Color bgCol = Color.BLACK;
    private Color fgCol = Color.WHITE;

    public CanvasPanel() {
        // setBorder(BorderFactory.createLineBorder(Color.black));
        // preferredDim = new Dimension(xSize, ySize);

        setBackground(bgCol);

        addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    getMode().mousePressed(e.getX(), e.getY());
                }
            });

        addMouseMotionListener(new MouseAdapter() {
                public void mouseMoved(MouseEvent e) {
                    getMode().mouseMoved(e.getX(), e.getY());
                }
                public void mouseDragged(MouseEvent e) {
                    getMode().mouseDragged(e.getX(), e.getY());
                }
            });

        // keyboard input
        addKeyListener(this);

        // key bindings
        addKeyBinding('h', () -> "show/hide help",
                      () -> { showHelp = !showHelp; repaint(); });
        addKeyBinding('q', () -> "quit", () -> {
                    System.out.println("... goodbye");
                    System.exit(0);
                });



        // special keys
        getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "pressed-left");
        getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "pressed-right");
        getInputMap().put(KeyStroke.getKeyStroke("UP"), "pressed-up");
        getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "pressed-down");
        getActionMap().put("pressed-left", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    // System.out.println("... pressed left");
                    getMode().pressedLeft();
                }
            });
        getActionMap().put("pressed-right", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    // System.out.println("... pressed right");
                    getMode().pressedRight();
                }
            });
        getActionMap().put("pressed-up", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    // System.out.println("... pressed up");
                    getMode().pressedUp();
                }
            });
        getActionMap().put("pressed-down", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    // System.out.println("... pressed down");
                    getMode().pressedDown();
                }
            });
    }


    public void addKeyBinding(KeyBinding kb) {
        keyBindings.add(kb);
    }

    // public void addKeyBinding(char keyChar, String description, SimpleAction action) {
    //     addKeyBinding(new KeyBinding(keyChar, description, action));
    // }

    // public void addKeyBinding(char keyChar, KeyBinding.Describer description, SimpleAction action) {
    // public void addKeyBinding(char keyChar, Supplier<String> description, SimpleAction action) {
    public void addKeyBinding(char keyChar, Supplier<String> description, Runnable action) {
        addKeyBinding(new KeyBinding(keyChar, description, action));
    }

    /**
     * Calls updateMode() then repaint().
     */
    public void updateAndRepaint() {
        updateMode();
        repaint();
    }

    public int centreX() { return getSize().width / 2; }
    public int centreY() { return getSize().height / 2; }



    /*----------------------------- MODES ------------------------------*/

    public void setModes(CanvasMode[] newModes) {
        // cleanup old modes
        for (CanvasMode m : modes) m.dispose();
        // setup new modes
        modes = newModes;
        for (CanvasMode m : modes) m.setCanvasRef(this);
    }

    public void addModes(CanvasMode ... newModes) {
        // add frame reference to new modes
        for (CanvasMode m : newModes) m.setCanvasRef(this);
        // expand array and add new modes
        CanvasMode[] canvModes = new CanvasMode[modes.length + newModes.length];
        System.arraycopy(modes, 0, canvModes, 0, modes.length);
        System.arraycopy(newModes, 0, canvModes, modes.length, newModes.length);
        modes = canvModes;
    }

    /**
     * @return The currently active mode.
     */
    private CanvasMode getMode() { return modes[modeIndex]; }

    /**
     * Calls current mode's update() method.
     */
    public void updateMode() { getMode().update(); }



    /*---------------------------- PAINTING ----------------------------*/

    public Color backgroundColor() { return bgCol; }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        getMode().paint(g);
        // paint help text on top of everything
        g.setColor(Color.GRAY);
        g.setFont(Gfx.FIXED_FONT);
        String modeStr = getMode().getClass().getSimpleName()
            + (showHelp ? "" : " (press 'h' for help)");
        g.setColor(Color.GRAY);
        g.drawString(modeStr, 10, 20);
        if (showHelp) paintHelp(g);
    }

    /**
     * If showHelp is true, paint mode name and description of all
     * keybindings, plus anything got from
     * CanvasMode.getUserInstructions().
     *
     * If showHelp is false, just paint mode name and help key.
     */
    private void paintHelp(Graphics g) {
        int x = 10;
        int y = 40;
        int spacing = 15;
        for (KeyBinding kb : keyBindings) {
            // g.drawString(kb.getKeyChar() + " --- " + kb.getDescription(), x, y);
            g.drawString(kb.getHelpString(), x, y);
            y += spacing;
        }
        for (KeyBinding kb : getMode().getKeyBindings()) {
            // g.drawString(kb.getKeyChar() + " --- " + kb.getDescription(), x, y);
            g.drawString(kb.getHelpString(), x, y);
            y += spacing;
        }
        String[] instructions = getMode().getUserInstructions();
        if (instructions != null) {
            g.drawString("------------------------------", x, y);
            y += spacing;
            for (String s : instructions) {
                g.drawString(s, x, y);
                y += spacing;
            }
        }
    }



    /*------------------------- KEYBOARD INPUT -------------------------*/

    protected void keyInput(char c) {
        for (KeyBinding kb : keyBindings) kb.keyInput(c);
        getMode().keyInput(c);
    }



    /*---------------------- KeyListener methods -----------------------*/

    public void keyPressed(KeyEvent e) {
        // System.out.println("pressed " + e.getKeyChar());
    }

    public void keyReleased(KeyEvent e) {
        // System.out.println("released " + e.getKeyChar());
    }

    public void keyTyped(KeyEvent e) {
        // System.out.println("typed: keyChar=" + e.getKeyChar() +
        //                    " keyText=" + KeyEvent.getKeyText(e.getKeyCode()));
        keyInput(e.getKeyChar());
    }

}
