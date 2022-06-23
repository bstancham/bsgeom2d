package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.LineInt;
import info.bstancham.bsgeom2d.PointInt;
import info.bstancham.bsgeom2d.TiledShape;
import info.bstancham.bsgeom2d.TriInt;
import java.awt.Color;
import java.awt.Graphics;

public class TiledShapeMode extends PixelZoomMode {

    private InfoBlock info = new InfoBlock();
    private DisplayShapeSlot[] slots;
    private int slotIndex = 0;

    private Color originalColor = Color.GRAY;
    private Color sub1Color = Color.CYAN;
    private Color sub2Color = Color.GREEN;
    private Color interColor = Color.YELLOW;
    private Color union1Color = Color.MAGENTA;
    private Color union2Color = Color.MAGENTA;
    private boolean showSub1 = true;
    private boolean showSub2 = true;
    private boolean showInter = true;
    private boolean showUnion1 = true;
    private boolean showUnion2 = true;
    private boolean showOriginal = true;
    private boolean doTriangulation = true;

    public TiledShapeMode() {
        zoom.set(20);
        slots = buildSlots(2);

        addKeyBinding('u', 'i',
                      () -> "prev/next shape-set (" + (1 + displaySlot().index) +
                      " of " + displaySlot().size() + " --> " + shapeSet().name + ")",
                      () -> incrShapeSet(-1),
                      () -> incrShapeSet(1));

        addKeyBinding('o', 'p',
                      () -> "prev/next shape (" + (1 + shapeSet().index) +
                      " of " + shapeSet().size() + " --> " + shapeSet().name + ")",
                      () -> incrShapes(-1),
                      () -> incrShapes(1));

        if (slots.length > 1)
            addKeyBinding('s',
                          () -> "switch display-shape (" + (slotIndex + 1) +
                          " of " + slots.length + ")",
                          () -> switchDisplaySlot());

        addKeyBinding('3', () -> "show subtraction 1 " + boolStr(showSub1),
                      () -> { showSub1 = !showSub1;
                              getCanvas().repaint(); });
        addKeyBinding('4', () -> "show subtraction 2 " + boolStr(showSub2),
                      () -> { showSub2 = !showSub2;
                              getCanvas().repaint(); });
        addKeyBinding('5', () -> "show intersection " + boolStr(showInter),
                      () -> { showInter = !showInter;
                              getCanvas().repaint(); });
        addKeyBinding('6', () -> "show union 1 " + boolStr(showUnion1),
                      () -> { showUnion1 = !showUnion1;
                              getCanvas().repaint(); });
        addKeyBinding('7', () -> "show union 2 " + boolStr(showUnion2),
                      () -> { showUnion2 = !showUnion2;
                              getCanvas().repaint(); });

        addKeyBinding('8', () -> "show originals " + boolStr(showOriginal),
                      () -> { showOriginal = !showOriginal;
                              getCanvas().repaint(); });

        addKeyBinding('9', () -> "do triangulation " + boolStr(doTriangulation),
                      () -> { doTriangulation = !doTriangulation;
                              getCanvas().repaint(); });
    }

    private DisplayShapeSlot[] buildSlots(int numSlots) {
        DisplayShapeSlot[] newSlots = new DisplayShapeSlot[numSlots];
        for (int i = 0; i < numSlots; i++) {
            newSlots[i] = new DisplayShapeSlot(getShapeSets());
            for (TSWList shapeSet : newSlots[i].shapeSets) {
                for (TSWrapper tsw : shapeSet.wrappers) {
                    tsw.setPosition(0, i * -(tsw.shape().dimension() + 2));
                }
            }
        }
        return newSlots;
    }

    private void incrShapes(int amt) {
        shapeSet().incrIndex(amt);
        softUpdateAndRepaint();
    }

    private void incrShapeSet(int amt) {
        displaySlot().incrIndex(amt);
        softUpdateAndRepaint();
    }

    private void softUpdateAndRepaint() {
        setCursorPos(shapeWrapper().getPosition());
        getCanvas().repaint();
    }

    private TSWrapper shape1() { return slots[0].shapeSet().wrapper(); }
    private TSWrapper shape2() { return slots[1].shapeSet().wrapper(); }

    private TSWrapper shapeWrapper() { return shapeSet().wrapper(); }

    private TSWList shapeSet() { return displaySlot().shapeSet(); }

    private DisplayShapeSlot displaySlot() { return slots[slotIndex]; }

    private void switchDisplaySlot() {
        slotIndex++;
        if (slotIndex >= slots.length) slotIndex = 0;
        softUpdateAndRepaint();
    }

    @Override
    public void update() {
        shapeWrapper().setPosition(cursorX(), cursorY());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        info.reset();

        // original shapes
        if (showOriginal) {
            g.setColor(originalColor);
            drawTiledShape(g, shape1(), doTriangulation);
            drawTiledShape(g, shape2(), doTriangulation);
        }

        // boolean transformations
        paintIf(g, showSub1, "SUBTRACTION 1", shape1().shape().subtract(shape2().shape()),
                sub1Color, doTriangulation);
        paintIf(g, showSub2, "SUBTRACTION 2", shape2().shape().subtract(shape1().shape()),
                sub2Color, doTriangulation);
        paintIf(g, showInter, "INTERSECTION", shape1().shape().intersect(shape2().shape()),
                interColor, doTriangulation);
        paintIf(g, showUnion1, "UNION 1", shape1().shape().union(shape2().shape()),
                union1Color, doTriangulation);
        paintIf(g, showUnion2, "UNION 2", shape2().shape().union(shape1().shape()),
                union1Color, doTriangulation);

        // info.add(Color.WHITE, "TEST");
        info.paint(g);
    }

    private void paintIf(Graphics g, boolean b, String title, TiledShape ts, Color c, boolean triangulation) {
        if (b) {
            g.setColor(c);
            drawTiledShape(g, ts, triangulation);
        }
    }

    //protected void drawTiledShape(Graphics g, TiledShape shape) {
    protected void drawTiledShape(Graphics g, TSWrapper tsw, boolean triangulation) {
        info.add(g.getColor(), "    SHAPE: " + tsw.name(),
                 "dimension: " + tsw.shape().dimension(),
                 " position: " + tsw.shape().position());
        drawTiledShape(g, tsw.shape(), doTriangulation);
    }

    protected void drawTiledShape(Graphics g, TiledShape ts, boolean triangulation) {

        String triStr = "N/A";
        if (triangulation) {
            int triCount = 0;
            for (TriInt t : ts.triangulation()) {
                drawTriangle(g, t);
                triCount++;
            }
            triStr = "" + triCount;
        } else {
            for (LineInt ln : ts.getLines()) {
                line(g, ln);
            }
        }

        info.add(g.getColor(), "triangles: " + triStr);
    }

    /*------------------- TILED-SHAPE WRAPPER CLASS --------------------*/

    public class TSWrapper {
        public String name;
        private TiledShape original;
        private TiledShape mod;
        private PointInt pos = new PointInt(0,0);

        public TSWrapper(String name, TiledShape shape) {
            this.name = name;
            original = shape;
            mod = shape;
        }

        public String name() { return name; }

        public TiledShape shape() { return mod.translate(pos.x(), pos.y()); }

        public void setPosition(int x, int y) { pos = new PointInt(x, y); }
        public PointInt getPosition() { return pos; }
    }

    private class TSWList {
        public String name;
        public TSWrapper[] wrappers;
        public int index = 0;

        public TSWList(String name, TSWrapper[] wrappers) {
            this.name = name;
            this.wrappers = wrappers;
        }

        public TSWrapper wrapper() { return wrappers[index]; }
        // public String name() { return name; }
        // public int index() { return index; }
        public int size() { return wrappers.length; }

        private void incrIndex(int amt) {
            index += amt;
            if (index < 0) index = wrappers.length - 1;
            if (index >= wrappers.length) index = 0;
        }
    }

    /**
     *
     *
     */
    private class DisplayShapeSlot {
        public TSWList[] shapeSets;
        public int index = 0;
        public DisplayShapeSlot(TSWList[] lists) {
            this.shapeSets = lists;
        }
        public TSWList shapeSet() { return shapeSets[index]; }
        public int size() { return shapeSets.length; }
        // public int index() { return index; }
        public void incrIndex(int amt) {
            index += amt;
            if (index < 0) index = shapeSets.length - 1;
            if (index >= shapeSets.length) index = 0;
        }
    }



    /*-------------------------- SHAPE LISTS ---------------------------*/

    private TSWList[] getShapeSets() {
        return new TSWList[] {
            tswListBuildingBlocks(),
            tswListSimpleShapes(),
        };
    }

    private TSWrapper newTSW(String name, int dimension, char ... tiles) {
        return new TSWrapper(name, new TiledShape(dimension, tiles));
    }

    private TSWList tswListBuildingBlocks() {
        return new TSWList("building blocks",
                        new TSWrapper[] {
                            newTSW("single (square)", 1, 'x'),
                            newTSW("single (top-left)", 1, 'l'),
                            newTSW("single (top-right)", 1, 'r'),
                            newTSW("single (bottom-left)", 1, 'e'),
                            newTSW("single (bottom-right)", 1, 'i'),
                        });
    }

    private TSWList tswListSimpleShapes() {
        return new TSWList("simple shapes",
                        new TSWrapper[] {
                            newTSW("4x4 lambda", 4,
                                   '.', '.', 'x', '.',
                                   '.', 'l', 'x', '.',
                                   'l', 'x', 'x', 'r',
                                   'x', 'i', 'e', 'x'),
                            newTSW("3x3 pokeball", 3,
                                   'l', 'x', 'r',
                                   'x', 'x', 'x',
                                   'e', 'x', 'i'),
                        });
    }
}
