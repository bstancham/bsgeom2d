package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.CanvasMode;
import info.bstancham.bsgeom2d.Geom2DInt;
import info.bstancham.bsgeom2d.LineInt;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Mode for testing counter-clockwise-angle method...
 *
 * TODO:
 * - fix it...
 * - fix arrows
 * - collinear point
 */
public class RelativeLeftHandSideMode extends NumLinesMode {

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (LineInt ln : lines) {
            // test all three possible conditions...
            // ... if they all fail, something is wrong with LineInt...
            if (ln.isRelativeLeft(cursorPoint())) {
                paintLineLEFT(g, ln);
            } else if (ln.isRelativeRight(cursorPoint())) {
                paintLineRIGHT(g, ln);
            } else if (ln.isCollinear(cursorPoint())) {
                paintLineCOLLINEAR(g, ln);
            } else {
                System.out.println("RelativeLeftHandSideMode FAILED TO FIND WHICH SIDE!");
            }
        }
    }

    private void paintLineLEFT(Graphics g, LineInt ln) {
        g.setColor(Color.GREEN);
        leftSideIndicatorLine(g, ln);
        // g.drawLine(ln.startX(), ln.startY(), ln.endX(), ln.endY());
    }

    private void paintLineRIGHT(Graphics g, LineInt ln) {
        g.setColor(Color.RED);
        rightSideIndicatorLine(g, ln);
        // g.drawLine(ln.startX(), ln.startY(), ln.endX(), ln.endY());
    }

    private void paintLineCOLLINEAR(Graphics g, LineInt ln) {
        g.setColor(Color.ORANGE);
        arrow(g, ln);
        // arrow(g, ln);
        // g.drawLine(ln.startX(), ln.startY(), ln.endX(), ln.endY());
    }

}
