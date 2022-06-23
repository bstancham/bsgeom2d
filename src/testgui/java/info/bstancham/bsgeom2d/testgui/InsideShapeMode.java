package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.ShapeInt;
import info.bstancham.bsgeom2d.PointInt;
import java.awt.Graphics;
import java.awt.Color;

public class InsideShapeMode extends ShapeTestMode {

    private boolean includeEdges = true;
    private Color outsideColor = new Color(128, 0, 0);
    private Color insideColor = new Color(0, 192, 0);
    private Color outlineColor = Color.GRAY;

    public InsideShapeMode() {
        super(1);
        zoom.set(10);

        addKeyBinding('t', () -> "include edges " + boolStr(includeEdges),
                      () -> { includeEdges = !includeEdges;
                          getCanvas().repaint(); });
    }

    @Override
    public void paint(Graphics g) {
        // paint behind grid
        g.setColor(cursorIsInsideShape() ? insideColor : outsideColor);
        fillShape(g, shape());
        // paint grid
        super.paint(g);
        // paint shape outline
        g.setColor(outlineColor);
        drawShape(g, shape());
    }

    private boolean cursorIsInsideShape() {
        if (includeEdges)
            return shape().contains(cursorPoint());
        else
            return shape().contains(cursorPoint(), false);
    }

}
