package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.PolyInt;
import info.bstancham.bsgeom2d.PointInt;
import info.bstancham.bsgeom2d.LineInt;
import java.awt.Graphics;
import java.awt.Color;

public class IntersectLineAndShapeMode extends ShapeTestMode {

    private InfoBlock info = new InfoBlock();
    private Color infoColor = new Color(0x333399);
    private Color originalColor = Color.GRAY;
    private Color shapeColor = Color.CYAN;
    private Color lineColor = Color.RED;
    private Color containsColor = Color.GREEN;
    private Color lineIntersectionColor = Color.YELLOW;
    private LineInt line = new LineInt(-30, 10, 30, 0);
    private boolean startEnd = false;
    private boolean shapeContainsLine = false;

    public IntersectLineAndShapeMode() {
        super(1);
        zoom.set(10);

        addKeyBinding('s', () -> "switch line-end (" + (startEnd ? "START" : "END") + ")",
                      () -> toggleLineEnd());
    }

    private void toggleLineEnd() {
        startEnd = !startEnd;
        updateCursorPos();
        getCanvas().repaint();
    }

    private void updateCursorPos() {
        setCursorPos(currentUserPoint());
    }

    private PointInt currentUserPoint() {
        if (startEnd) return line.start();
        else          return line.end();
    }

    @Override
    public void update() {
        if (startEnd) line = new LineInt(cursorPoint(), line.end());
        else          line = new LineInt(line.start(), cursorPoint());
    }

    private PolyInt getPoly() { return shape().subShape(0).outline(); }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // originals
        g.setColor(originalColor);
        drawShape(g, shape());
        line(g, line);

        // line
        LineInt[] segments = getPoly().splitLineAtIntersections(line);
        int segmentsContained = 0;
        for (int i = 0; i < segments.length; i++) {
            // colour
            g.setColor(lineColor);
            if (getPoly().contains(segments[i])) {
                g.setColor(containsColor);
                segmentsContained++;
            }
            // line
            if (i >= segments. length - 1) {
                arrow(g, segments[i]);
            } else {
                line(g, segments[i]);
            }
            // intersection point
            if (i > 0) {
                g.setColor(lineIntersectionColor);
                drawCrosshairs(g, segments[i].start(), 10);
            }
        }

        shapeContainsLine = getPoly().contains(line);

        //
        info.reset();
        info.add(infoColor,
                 "      line segments: " + segments.length,
                 "  segments in shape: " + segmentsContained);
        info.add((shapeContainsLine ? containsColor : infoColor),
                 "shape contains line: " + shapeContainsLine);
        info.paint(g);
    }

}
