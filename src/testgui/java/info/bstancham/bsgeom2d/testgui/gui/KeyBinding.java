package info.bstancham.bsgeom2d.testgui.gui;

import java.util.function.Supplier;

public class KeyBinding {

    protected Supplier<String> description;
    protected Binding[] binds;

    public KeyBinding(char c, String description, Runnable a) {
        this(new Binding[] { new Binding(c, a) }, () -> description);
    }

    public KeyBinding(char c, Supplier<String> description, Runnable a) {
        this(new Binding[] { new Binding(c, a) }, description);
    }

    protected KeyBinding (Binding[] binds, Supplier<String> description) {
        this.binds = binds;
        this.description = description;
    }

    public String getHelpString() { return binds[0].keyChar + "   --- " + description.get(); }

    public void keyInput(char inputChar) {
        for (Binding b : binds) b.keyInput(inputChar);
    }





    public static class Incr extends KeyBinding {

        public Incr(char decr, char incr, Supplier<String> description,
                    Runnable decrAction, Runnable incrAction) {
            super(new Binding[] { new Binding(decr, decrAction),
                                  new Binding(incr, incrAction) },
                description);
        }

        public Incr(char decr, char incr, String description,
                    Runnable decrAction, Runnable incrAction) {
            super(new Binding[] { new Binding(decr, decrAction),
                                  new Binding(incr, incrAction) },
                () -> description);
        }

        public String getHelpString() {
            return binds[0].keyChar + "/" + binds[1].keyChar + " --- " + description.get();
        }
    }



    private static class Binding {

        private char keyChar;
        private Runnable keyAction;

        public Binding(char c, Runnable a) {
            keyChar = c;
            keyAction = a;
        }

        public void keyInput(char inputKey) {
            if (inputKey == keyChar) {
                keyAction.run();
            }
        }
    }

    // // public static abstract class Describer {
    // //     public abstract String get();
    // // }
    // public interface Describer {
    //     public String get();
    // }

}
