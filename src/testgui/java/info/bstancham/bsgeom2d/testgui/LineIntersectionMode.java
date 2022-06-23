package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.Gfx;
import info.bstancham.bsgeom2d.Geom2D;
import info.bstancham.bsgeom2d.Geom2DInt;
import info.bstancham.bsgeom2d.LineInt;
import info.bstancham.bsgeom2d.PointInt;
import java.awt.Color;
import java.awt.Graphics;

public class LineIntersectionMode extends NumLinesMode {

    private boolean startEnd = true;
    private boolean includeTouching = false;
    private IntParam userIndex;
    private int oldUserIndex = 0;
    private Color intersectColor = Color.CYAN;

    public LineIntersectionMode() {

        addKeyBinding('s', () -> "user-line switch ends (" + (startEnd ? "START" : "END") + ")",
                      () -> userLineSwitchEnds());

        userIndex = new IntParam("user-line index", '[', ']', 0,
                                 () -> 0,
                                 () -> numLines.get() - 1,
                                 1, true);
        addKeyBinding(userIndex.getKeyBinding());

        addKeyBinding('i', () -> "include touching " + boolStr(includeTouching),
                      () -> toggleIncludeTouching());
    }

    private void userLineSwitchEnds() {
        startEnd = !startEnd;
        updateCursorPos();
        getCanvas().repaint();
    }

    private void toggleIncludeTouching() {
        includeTouching = !includeTouching;
        getCanvas().repaint();
    }

    private void updateCursorPos() {
        setCursorPos(getCurrentUserPoint());
    }

    private PointInt getCurrentUserPoint() {
        if (startEnd) return userLine().start();
        return userLine().end();
    }

    public LineInt userLine() { return lines.get(userIndex.get()); }
    public void setUserLine(LineInt l) { lines.set(userIndex.get(), l); }

    @Override
    public void update() {
        super.update();
        // check that userIndex is still in range
        if (userIndex.get() >= numLines.get()) userIndex.set(numLines.get() - 1);
        //
        if (oldUserIndex != userIndex.get()) {
            // user-line has changed
            setCursorPos(getCurrentUserPoint());
            oldUserIndex = userIndex.get();
        } else {
            // set current user-line-end to cursor point
            if (startEnd)
                setUserLine(new LineInt(cursorPoint(), userLine().end()));
            else
                setUserLine(new LineInt(userLine().start(), cursorPoint()));
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        LineInt user = userLine();
        int intersections = 0;
        int parallel = 0;
        int degenerate = 0;

        // paint lines
        for (LineInt ln : lines) {
            if (ln == user) {
                g.setColor(Color.YELLOW);
                if (user.isDegenerate()) degenerate++;
            } else if (ln.isDegenerate()) {
                degenerate++;
            } else if (user.intersectsLineSegment(ln, includeTouching)) {
                g.setColor(Color.GREEN);
                intersections++;
            } else if (user.isParallel(ln)) {
                g.setColor(Color.RED);
                parallel++;
            } else {
                g.setColor(Color.MAGENTA);
            }
            arrow(g, ln);
        }

        // paint intersections
        int size = 10;
        for (LineInt ln : lines) {
            if (ln != user) {
                try {
                    PointInt p = user.intersectionInt(ln);
                    Gfx.crosshairs(g, intersectColor, toScreenX(p.x()), toScreenY(p.y()), size);
                } catch (Geom2D.LinesParallelException e) {
                    // System.out.println("... LinesParallelException...");
                }
            }
        }

        // paint info
        Gfx.textBlock(g, null, Color.CYAN,
                      new String[] {
                          "   intersections: " + intersections,
                          "  parallel lines: " + parallel,
                          "degenerate lines: " + degenerate
                      }, 10, sizeY() - 80);
        Gfx.textBlock(g, null, (degenerate <= 0 ? Color.GREEN : Color.RED),
                      "degenerate lines: " + degenerate,
                      10, sizeY() - 40);
     }

    @Override
    protected void randomizeLines() {
        super.randomizeLines();
        setCursorPos(getCurrentUserPoint());
    }

}
