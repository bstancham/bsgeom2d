package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.Gfx;
import info.bstancham.bsgeom2d.testgui.gui.KeyBinding;
import info.bstancham.bsgeom2d.PointInt;
import info.bstancham.bsgeom2d.PolyInt;
import info.bstancham.bsgeom2d.ShapeGroupInt;
import info.bstancham.bsgeom2d.ShapeInt;
import info.bstancham.bsgeom2d.TriInt;
import java.awt.Color;
import java.awt.Graphics;

public class TriangulateShapesMode extends ShapeMoveMode {

    private InfoBlock info = new InfoBlock();
    private Color outlineColor = Color.PINK;
    private Color insideColor = Color.BLUE;
    private Color triangleLabelColor = new Color(128, 128, 255);
    private boolean showOutline = true;

    private interface TriFunc {
        public TriInt[] triangulate(PolyInt poly);
    }
    private class TParam {
        public TriFunc func;
        public String name;
        public TParam(String name, TriFunc func) {
            this.name = name;
            this.func = func;
        }
    }

    // TriFunc t1 = PolyInt::getTriangulation;

    private TParam[] tFuncs = new TParam[] {
        new TParam("default",       PolyInt::getTriangulation),
        new TParam("simple-convex", PolyInt::getTriangulationSimpleConvex),
        new TParam("ear-clipping",  PolyInt::getTriangulationEarClipping)
    };
    private int funcIndex = 0;

    public TriangulateShapesMode() {
        super(1);
        zoom.set(10);

        addKeyBinding('t', () -> "show outline " + boolStr(showOutline),
                      () -> {
                          showOutline = !showOutline;
                          getCanvas().repaint();
                      });
        addKeyBinding(new KeyBinding('f',
                                     () -> "triangulation function ("
                                     + (funcIndex + 1) + " of " + tFuncs.length
                                     + " -> " + tFuncs[funcIndex].name + ")",
                                     () -> nextFunc()
                                     ));
    }

    private void nextFunc() {
        funcIndex++;
        if (funcIndex >= tFuncs.length) funcIndex = 0;
        getCanvas().updateAndRepaint();
    }

    @Override
    public void paint(Graphics g) {
        info.reset();
        ShapeGroupInt s = shape();
        TriInt[] triangles = s.triangulate();
        // TriInt[] triangles = t1.triangulate(s.outline());
        // TriInt[] triangles = tFuncs[funcIndex].func.triangulate(s.outline());

        // hilight any triangles with non-ccw winding
        int num = 0;
        for (TriInt t : triangles) {
            if (!t.windingIsCCW()) {
                num++;
                g.setColor(Color.RED);
                fillPoly(g, t.toPoly());
            }
        }
        info.add(Color.CYAN, "TRIANGLES:   " + triangles.length);
        addErrorInfo("bad-winding: ", num);

        // count degenerate triangles
        num = 0;
        for (TriInt t : triangles)
            if (t.isDegenerate())
                num++;
        addErrorInfo("degenerate : ", num);

        // paint grid
        super.paint(g);

        // paint triangles
        num = 0;
        for (TriInt t : triangles) {
            g.setColor(insideColor);
            drawTriangle(g, t);
            g.setColor(triangleLabelColor);
            PointInt c = t.centroid().toInt();
            g.drawString("" + ++num, toScreenX(c.x()), toScreenY(c.y()));
        }

        // paint shape outline
        if (showOutline) {
            g.setColor(outlineColor);
            drawShape(g, s);
            // vertex numbers
            int vnum = 0;
            g.setColor(Color.WHITE);
            for (PointInt v : s.getVertices()) {
                g.drawString("" + ++vnum, toScreenX(v.x()), toScreenY(v.y()));
            }
        }

        info.paint(g);
    }

    private void addErrorInfo(String text, int num) {
        info.add((num <= 0 ? Color.GREEN : Color.RED), text + num);
    }

}
