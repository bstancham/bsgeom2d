package info.bstancham.bsgeom2d.testgui.gui;

import java.awt.Graphics;
import java.awt.Color;

/**
 * NOTE: CanvasPanel always grabs keyboard focus on switch to frame.
 */
public class ModeSwitchingPanel extends CanvasPanel {

    private boolean modeSwitchingMode = false;
    private boolean wrongInputMode = false;

    public ModeSwitchingPanel() {
        addKeyBinding('m', () -> "switch mode", () -> enterModeSwitchingMode());
        // addKeyBinding(new KeyBinding.Incr('1', '2', "previous/next mode",
        //                                   () -> {
        //                                       modeIndex--;
        //                                       if (modeIndex < 0)
        //                                           modeIndex = modes.length -1;
        //                                       repaint();
        // },
        //                                   () -> {
        //                                       modeIndex++;
        //                                       if (modeIndex > modes.length - 1)
        //                                           modeIndex = 0;
        //                                       repaint();
        //                                   }));
        addKeyBinding(new KeyBinding.Incr('1', '2',
                                          () -> "previous/next mode (" + (modeIndex + 1) + " of " + modes.length + ")",
                                          () -> {
                                              modeIndex--;
                                              if (modeIndex < 0)
                                                  modeIndex = modes.length -1;
                                              repaint();
        },
                                          () -> {
                                              modeIndex++;
                                              if (modeIndex > modes.length - 1)
                                                  modeIndex = 0;
                                              repaint();
                                          }));


    }

    /**
     * When modeSwitchingMode is activated, mode-switching menu will
     * be displayed, and next keystroke will be intercepted and passed
     * to mode-switching menu.
     */
    private void enterModeSwitchingMode() {
        // System.out.println("... modeSwitchingMode...");
        modeSwitchingMode = true;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (modeSwitchingMode) paintModeSwitchingMenu(g);
    }

    private void paintModeSwitchingMenu(Graphics g) {
        // System.out.println("... painting mode-switch menu...");
        if (wrongInputMode) {
            Gfx.textBlock(g, Color.RED, Color.WHITE,
                          new String[] { "NO MODE SELECTED...",
                                         "---------------------------",
                                         "... press a key to continue" },
                          20, 20);

        } else {
            Gfx.textBlock(g, Color.BLUE, Color.WHITE,
                          concatStrArrays(new String[] { "SWITCH MODE:",
                                                         "---------------------------" },
                              getModeSwitchingList()),
                          20, 20);
        }
    }

    private String[] concatStrArrays(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private String[] getModeSwitchingList() {
        String[] out = new String[modes.length];
        for (int i = 0; i < modes.length; i++) {
            // char key = (char) i;
            // if (i > 9) key = (char) ('a' + (i - 10));
            String key = "" + (i + 1);
            if (i >= 9) key = "" + (char) ('a' + (i - 9));
            out[i] = key + ": " + modes[i].getClass().getSimpleName();
        }
        return out;
    }

    /**
     * When modeSwitchingMode is true, all key input is redirected to
     * the mode-switching menu, until one of the options is selected.
     */
    @Override
    protected void keyInput(char c) {
        if (modeSwitchingMode) {
            if (wrongInputMode) {
                modeSwitchingMode = false;
                wrongInputMode = false;
            } else {
                // intercept key char
                // System.out.println("... getting mode-switch key input...");
                // convert input char to modes array index
                int index = (c <= '9' ? c - '1': 9 + (c - 'a'));
                // check index
                if (index >= 0 && index < modes.length) {
                    modeIndex = index;
                    modeSwitchingMode = false;
                } else {
                    wrongInputMode = true;
                }
            }
            repaint();
        } else {
            super.keyInput(c);
        }
    }

}
