package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.*;
import info.bstancham.bsgeom2d.LineInt;
import info.bstancham.bsgeom2d.PointInt;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * Mode maintains a collection of lines, which can be accessed by any
 * child class...  -
 */
public abstract class NumLinesMode extends PixelZoomMode {

    protected ArrayList<LineInt> lines = new ArrayList<>();
    protected IntParam numLines;
    private boolean perpendicularMode = false;

    public NumLinesMode() {
        numLines = new IntParam("number of lines", 'o', 'p', 5, 1, 1000);
        addKeyBinding(numLines.getKeyBinding());


        addKeyBinding('r', () -> "randomize lines",
                      () -> {
                          randomizeLines();
                          getCanvas().repaint();
                      });

        addKeyBinding('t', () -> "toggle perpendicular mode " + boolStr(perpendicularMode),
                      () -> {
                          togglePerpendicular();
                          getCanvas().repaint();
                      });
    }

    @Override
    public void setCanvasRef(CanvasPanel canvas) {
        super.setCanvasRef(canvas);
        // init random line positions within the canvas boundaries
        updateLines();
    }

    @Override
    public void update() { updateLines(); }

    /**
     * Add or remove lines as necessary.
     */
    protected void updateLines() {
        // add or remove lines as needed
        int diff = numLines.get() - lines.size();
        if (diff > 0)
            for (int i = 0; i < diff; i++)
                lines.add(randomLine());
        if (diff < 0)
            for (int i = 0; i < -diff; i++)
                lines.remove(lines.size() - 1);
    }

    private LineInt randomLine() {
        LineInt out = null;
        while (out == null || out.isDegenerate())
            out = randomLineUnchecked();
        return out;
    }

    /**
     * Returns a line between two random points in the frame.
     *
     * If perpendicularMode is true, the line will be perpendicular.
     */
    private LineInt randomLineUnchecked() {

        PointInt p1 = randomScreenPoint();
        PointInt p2 = randomScreenPoint();
        if (perpendicularMode) {
            if (Math.random() < 0.5) {
                // horizontal
                return new LineInt(p1.x(), p1.y(), p2.x(), p1.y());
            } else {
                // vertical
                return new LineInt(p1.x(), p1.y(), p1.x(), p2.y());
            }
        } else {
            return new LineInt(randomScreenPoint(), randomScreenPoint());
        }
    }

    /**
     * Random point within the zoomed screen area.
     */
    private PointInt randomScreenPoint() {
        return new PointInt(toCanvasX((int) (Math.random() * getCanvas().getSize().width)),
                            toCanvasY((int) (Math.random() * getCanvas().getSize().height)));
    }

    private PointInt randomPoint() {
        return new PointInt((int) (Math.random() * getCanvas().getSize().width) - centreX(),
                            (int) (Math.random() * getCanvas().getSize().height) - centreY());
    }

    private void togglePerpendicular() {
        perpendicularMode = !perpendicularMode;
        randomizeLines();
    }

    /**
     * Randomizes all line positions within the bounds or the currently
     * zoomed canvas, and then repaints.
     */
    protected void randomizeLines() {
        for (int i = 0; i < lines.size(); i++)
            lines.set(i, randomLine());
    }

}
