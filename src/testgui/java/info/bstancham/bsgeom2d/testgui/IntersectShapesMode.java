package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.Gfx;
import info.bstancham.bsgeom2d.ShapeInt;
import info.bstancham.bsgeom2d.ShapeGroupInt;
import info.bstancham.bsgeom2d.TriInt;
import info.bstancham.bsgeom2d.PolyInt;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;

public class IntersectShapesMode extends ShapeMoveMode {

    private InfoBlock info = new InfoBlock();
    private Color originalColor = Color.GRAY;
    private Color sub1Color = Color.BLUE;
    private Color sub2Color = Color.GREEN;
    private Color interColor = Color.YELLOW;
    private Color unionColor = Color.MAGENTA;
    private boolean showSub1 = true;
    private boolean showSub2 = true;
    private boolean showInter = true;
    private boolean showUnion = true;

    public IntersectShapesMode() {
        super(2);
        zoom.set(10);
        addKeyBinding('3', () -> "show subtraction 1 " + boolStr(showSub1),
                      () -> { showSub1 = !showSub1;
                              getCanvas().repaint(); });
        addKeyBinding('4', () -> "show subtraction 2 " + boolStr(showSub2),
                      () -> { showSub2 = !showSub2;
                              getCanvas().repaint(); });
        addKeyBinding('5', () -> "show intersection " + boolStr(showInter),
                      () -> { showInter = !showInter;
                              getCanvas().repaint(); });
        addKeyBinding('6', () -> "show union " + boolStr(showUnion),
                      () -> { showUnion = !showUnion;
                              getCanvas().repaint(); });
    }

    private ShapeGroupInt shape1() { return shape(0); }
    private ShapeGroupInt shape2() { return shape(1); }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // original shapes
        g.setColor(originalColor);
        drawShape(g, shape1());
        drawShape(g, shape2());

        // boolean transformations
        ShapeGroupInt.BooleanTransformation bool =
            new ShapeGroupInt.BooleanTransformation(shape1(), shape2());

        // info
        info.reset();
        paintGroup(g, "SUBTRACTION 1", sub1Color,  bool.subtraction1(), showSub1);
        paintGroup(g, "SUBTRACTION 2", sub2Color,  bool.subtraction2(), showSub2);
        paintGroup(g, "INTERSECTION ", interColor, bool.intersection(), showInter);
        paintGroup(g, "UNION        ", unionColor, bool.union(),        showUnion);
        info.paint(g);
    }

    private void paintGroup(Graphics g, String label, Color c, ShapeGroupInt sg, boolean show) {
        if (show) {
            // shape group
            g.setColor(c);
            drawShape(g, sg);
            // vertices
            g.setColor(Color.WHITE);
            drawVertices(g, sg.getVertices(), 10);
        }
        // add info to block
        String pad = padSpaces(label.length());
        info.add((show ? c : Color.GRAY),
                 label + ": sub-shapes=" + (show ? sg.numSubShapes() : "N/A"),
                   pad + "       lines=" + (show ? sg.numEdges()     : "N/A"),
                   pad + "    vertices=" + (show ? sg.numVertices()  : "N/A"));
    }

    private String padSpaces(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++)
            sb.append(' ');
        return sb.toString();
    }

}
