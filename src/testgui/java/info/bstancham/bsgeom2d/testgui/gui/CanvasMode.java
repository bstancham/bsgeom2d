package info.bstancham.bsgeom2d.testgui.gui;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.function.IntSupplier;
import java.awt.Graphics;

/**
 * Implements switchable modes in CanvasFrame.
 *
 * Cursor keys do pixel-at-a-time adjustment of mouse position.
 *
 * The simplest possible mode would just implement the paint method.
 * <code>
    public static class DoNothingMode extends CanvasMode {
        public void paint(Graphics g) {}
    }
 * </code>
 *
 * See RedSquareMode for some examples of how to use CanvasMode features.
 */
public abstract class CanvasMode {

    private CanvasPanel canvas = null;
    private ArrayList<KeyBinding> keyBindings = new ArrayList<>();
    private int mouseX = 0;
    private int mouseY = 0;

    /**
     * This method is called automatically by CanvasPanel when you use
     * addModes or setModes.
     */
    public void setCanvasRef(CanvasPanel canvas) {
        // System.out.println("setCanvasReference: before=" + this.canvas);
        this.canvas = canvas;
        // System.out.println("... setCanvasReference: after=" + this.canvas);
    }

    public CanvasPanel getCanvas() { return canvas; }

    /**
     *  Get rid of loitering object references so that the garbage collector can take care of it.
     */
    public void dispose() { canvas = null; }

    /**
     * Do painting specific to this mode.
     *
     * This is called by CanvasPanel when it repaints.
     */
    public abstract void paint(Graphics g);

    /**
     * Does nothing by default. Override this method if you like to
     * update things before repaint.
     */
    public void update() {}

    /**
     * Override this to add andy extra lines of text beneath the help text.
     */
    public String[] getUserInstructions() { return null; }



    /*------------------------- KEYBOARD INPUT -------------------------*/

    public ArrayList<KeyBinding> getKeyBindings() { return keyBindings; }

    public void addKeyBinding(KeyBinding kb) { keyBindings.add(kb); }

    // public void addKeyBinding(char c, String description, SimpleAction a) {
    // public void addKeyBinding(char c, Supplier<String> description, SimpleAction a) {
    public void addKeyBinding(char c, Supplier<String> description, Runnable a) {
        addKeyBinding(new KeyBinding(c, description, a));
    }

    // public void addKeyBinding(char key1, char key2, Supplier<String> description, SimpleAction a1, SimpleAction a2) {
    public void addKeyBinding(char key1, char key2, Supplier<String> description,
                              Runnable a1, Runnable a2) {
        // addKeyBinding(new KeyBinding(c, description, a));
        addKeyBinding(new KeyBinding.Incr(key1, key2, description, a1, a2));
    }

    public void keyInput(char c) {
        for (KeyBinding kb : keyBindings) kb.keyInput(c);
    }

    /** Override this to do something with the left-arrow key. */
    public void pressedLeft() {
        // System.out.println("CanvasMode.pressedLeft()");
        mouseX -= 1;
        getCanvas().updateAndRepaint();
    }

    /** Override this to do something with the right-arrow key. */
    public void pressedRight() {
        // System.out.println("CanvasMode.pressedRight()");
        mouseX += 1;
        getCanvas().updateAndRepaint();
    }

    /** Override this to do something with the up-arrow key. */
    public void pressedUp() {
        // System.out.println("CanvasMode.pressedUp()");
        mouseY -= 1;
        getCanvas().updateAndRepaint();
    }

    /** Override this to do something with the down-arrow key. */
    public void pressedDown() {
        // System.out.println("CanvasMode.pressedDown()");
        mouseY += 1;
        getCanvas().updateAndRepaint();
    }

    // private int keyX() { return keyX; }
    // private int keyY() { return keyY; }



    /*-------------------------- MOUSE INPUT ---------------------------*/

    public int mouseX() { return mouseX; }
    public int mouseY() { return mouseY; }

    public void setMousePos(int x, int y) {
        mouseX = x;
        mouseY = y;
    }

    public void mouseDragged(int x, int y) {
        // System.out.println("mouse dragged at: " + x + " " + y);
    }

    public void mousePressed(int x, int y) {
        // System.out.println("mouse pressed at: " + x + " " + y);
    }

    public void mouseMoved(int x, int y) {
        // System.out.println("mouse moved at: " + x + " " + y);
        mouseX = x;
        mouseY = y;
        // System.out.println("... mouse co-ords = " + mouseX + ", " + mouseY);
    }




    /*--------------------- MINIMUM IMPLEMENTATION ---------------------*/

    /**
     * A CanvasMode which does absolutely nothing... It's main purpose
     * is to provide a default mode for CanvasPanel, so that there
     * won't be any runtime errors if CanvasPanel is used without
     * adding any other modes.
     */
    public static class NullMode extends CanvasMode {
        public void paint(Graphics g) {}
    }



    /*------------------------- INNER CLASSES --------------------------*/

    public class BoolParam {
        private String description;
        private char key;
        private boolean val;
        public BoolParam(String description, char key, boolean initialVal) {
            this.description = description;
            this.key = key;
            val = initialVal;
        }

        public boolean get() { return val; }
        public void set(boolean b) { val = b; }

        public KeyBinding getKeyBinding() {
            return new KeyBinding(key, () -> description + " (" + val + ")",
                                  () -> { val = !val;
                                      getCanvas().updateAndRepaint(); } );
        }
    }



    public class IntParam {

        private String name;
        private char decrChar;
        private char incrChar;
        private int val;
        private IntSupplier min;
        private IntSupplier max;
        private int step;
        private boolean rollOver;

        public IntParam(String name, char decrKey, char incrKey,
                        int initialValue, int min, int max) {
            this(name, decrKey, incrKey, initialValue, min, max, 1, false);
        }

        public IntParam(String name, char decrKey, char incrKey,
                        int initialValue, int min, int max, int incrementStep,
                        boolean rollOver) {
            this(name, decrKey, incrKey, initialValue,
                 () -> min, () -> max,
                 incrementStep, rollOver);
        }

        public IntParam(String name, char decrKey, char incrKey,
                        int initialValue, IntSupplier min, IntSupplier max,
                        int incrementStep, boolean rollOver) {
            this.name = name;
            this.decrChar = decrKey;
            this.incrChar = incrKey;
            this.val = initialValue;
            this.min = min;
            this.max = max;
            this.step = incrementStep;
            this.rollOver = rollOver;
        }

        public int get() { return val; }

        public void set(int i) {
            val = i;
            if (val < min())      val = min();
            else if (val > max()) val = max();
        }

        private int min() { return min.getAsInt(); }
        private int max() { return max.getAsInt(); }

        private void incr(int amt) {
            val += amt;
            if (rollOver) {
                if (val < min()) val = max() - (min() - val - 1);
                if (val > max()) val = min() + (val - max() - 1);
            } else {
                if (val < min()) val = min();
                if (val > max()) val = max();
            }
        }

        public KeyBinding getKeyBinding() {
            return new KeyBinding.Incr(decrChar, incrChar, () -> name + " (" + val + ")",
                                       () -> { incr(-step);
                                           getCanvas().updateAndRepaint(); },
                                       () -> { incr(step);
                                           getCanvas().updateAndRepaint(); });
        }
    }
}
