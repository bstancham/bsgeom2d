package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.*;
import info.bstancham.bsgeom2d.Geom2D;
import info.bstancham.bsgeom2d.LineInt;
import info.bstancham.bsgeom2d.PointInt;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Displays a line and shows some information about the angle.
 */
public class LineAngleMode extends PixelZoomMode {

    private PointInt p1 = new PointInt(0, 250);
    private PointInt p2 = new PointInt(0, 0);
    private PointInt p3 = new PointInt(250, 250);
    private LineInt line1 = new LineInt(p1, p2);
    private LineInt line2 = new LineInt(p2, p3);
    private int controlPoint = 3;
    private Color angleBetweenColor = new Color(48, 175, 48);
    private Color lineLengthColor =   new Color(48, 175, 191);
    private InfoBlock info = new InfoBlock();

    public LineAngleMode() {
        addKeyBinding('s', () -> "switch ends ("
                      + (controlPoint == 1 ? "START" :
                            (controlPoint == 2 ? "MID" : "END")) + ")",
                      () -> switchEnds());
    }

    private void switchEnds() {
        // increment controlPoint
        controlPoint--;
        if (controlPoint < 1) controlPoint = 3;
        // set cursor to current control point, then repaint
        if (controlPoint == 1)
            setCursorPos(p1.x(), p1.y());
        else if (controlPoint == 2)
            setCursorPos(p2.x(), p2.y());
        else
            setCursorPos(p3.x(), p3.y());
        getCanvas().repaint();
    }

    @Override
    public void update() {

        if (controlPoint == 1)
            p1 = new PointInt(cursorX(), cursorY());
        else if (controlPoint == 2)
            p2 = new PointInt(cursorX(), cursorY());
        else
            p3 = new PointInt(cursorX(), cursorY());

        line1 = new LineInt(p1, p2);
        line2 = new LineInt(p2, p3);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // lines
        g.setColor(Color.CYAN);
        line(g, line1);
        arrow(g, line2);

        // paint angles
        double angleBetween = Geom2D.angle(p1, p2, p3);
        double angleTurned = Geom2D.angleTurned(p1, p2, p3);
        // absolute angle
        drawAbsoluteAngle(g, line1);
        drawAbsoluteAngle(g, line2);
        // angle-between
        g.setColor(angleBetweenColor);
        drawAngle(g, p2, 45,
                  (int) -Math.toDegrees(line1.angle()) - 90,
                  (int) -Math.toDegrees(angleBetween));
        // angle turned
        drawAngleTurned(g, p1, p2, p3);

        // info
        info.reset();
        info.add(Color.CYAN,
                 coordsString("(start)", p1),
                 coordsString("  (mid)", p2),
                 coordsString("  (end)", p3));
        info.add(lineLengthColor,
                 lineLengthString("LINE 1", line1),
                 lineLengthString("LINE 2", line2));
        info.add((angleTurned < 0 ? Color.RED : Color.GREEN),
                 "ANGLE TURNED (degrees): " + Math.toDegrees(angleTurned),
                 "ANGLE TURNED (radians): " + angleTurned);
        info.add(angleBetweenColor,
                 "ANGLE BETWEEN (degrees): " + Math.toDegrees(angleBetween),
                 "ANGLE BETWEEN (radians): " + angleBetween);
        info.add(Color.PINK,
                 lineAngleString("LINE 1", line1),
                 lineAngleString("LINE 2", line2));
        info.paint(g);
    }

    private void drawAbsoluteAngle(Graphics g, LineInt ln) {
        g.setColor(Color.PINK);
        drawAngle(g, ln.start(), 60, 90, (int) -Math.toDegrees(ln.angle()));
        PointInt endPt = toScreen(ln.start());
        g.drawLine(endPt.x(), endPt.y(),
                   endPt.x(), endPt.y() - 70);
    }

    private String lineAngleString(String title, LineInt ln) {
        return String.format("%s: slope=%f intercept=%f --- ANGLE = (degree) %f, (radians) %f",
                             title, ln.slope(), ln.intercept(), Math.toDegrees(ln.angle()), ln.angle());
    }

    private String coordsString(String title, PointInt p) {
        return String.format("%s co-ords: %4d, %4d", title, p.x(), p.y());
    }

    private String lineLengthString(String title, LineInt ln) {
        return String.format("%s: xDist=%4d yDist=%4d --- LENGTH = %.4f",
                             title, ln.xDist(), ln.yDist(), ln.length());
    }

}
