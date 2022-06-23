package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.ShapeInt;
import info.bstancham.bsgeom2d.PolyInt;
import info.bstancham.bsgeom2d.testgui.gui.Gfx;
import java.awt.Graphics;
import java.awt.Color;

public class PolygonWindingMode extends ShapeMoveMode {

    private InfoBlock info = new InfoBlock();
    private Color groupInfoColor = Color.CYAN;
    private Color subInfoColor = new Color(0, 192, 192);
    private Color holeInfoColor = new Color(0, 128, 128);

    public PolygonWindingMode() {
        super(1);
        zoom.set(10);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawShapeWithWinding(g, shape());

        // general shape info
        info.reset();
        info.add(groupInfoColor,
                 "SHAPE-GROUP (" + shape().numSubShapes() +
                 " sub-shape" + (shape().numSubShapes() > 1 ? "s" : "") +
                 " --- " + shape().numVertices() + " vertices)");

        // sub-shape info
        int i = 0;
        for (ShapeInt sub : shape())
            addSubShapeInfo(sub, ++i);

        info.paint(g);
    }

    private void addSubShapeInfo(ShapeInt shape, int num) {
        info.addSeparator(subInfoColor);
        addPolygonInfo(shape.outline(), "SUB-SHAPE " + num + " (outline)",
                       subInfoColor, PolyInt.WindingOrder.COUNTER_CLOCKWISE);
        for (int i = 0; i < shape.numHoles(); i++)
            addPolygonInfo(shape.hole(i), "HOLE " + (i + 1),
                           holeInfoColor, PolyInt.WindingOrder.CLOCKWISE);
    }

    private void addPolygonInfo(PolyInt poly, String title, Color col,
                                PolyInt.WindingOrder validWinding) {
        info.add(col, title + ": " + poly.numVertices() + " vertices");
        info.add((poly.windingOrder() == validWinding ? col : Color.RED),
                 "winding: " + poly.windingOrder() +
                 " --- sum angles: " + Math.toDegrees(poly.sumOfAngles()));
    }

}
