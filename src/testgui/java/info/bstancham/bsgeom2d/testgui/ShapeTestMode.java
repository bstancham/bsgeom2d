package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.*;
import info.bstancham.bsgeom2d.testgui.gui.GfxTransform;
// import info.bstancham.bsutil.IntFunc;
import info.bstancham.bsgeom2d.ShapeInt;
import info.bstancham.bsgeom2d.ShapeGroupInt;
import info.bstancham.bsgeom2d.PolyInt;
import info.bstancham.bsgeom2d.LineInt;
import info.bstancham.bsgeom2d.PointInt;
import info.bstancham.bsgeom2d.Geom2DInt;
import java.awt.Graphics;
import java.awt.Color;

/**
 * maintains a list of shapes
 *
 * controls for switching between shapes on list
 *
 * controls for editing shapes on list
 */
public abstract class ShapeTestMode extends PixelZoomMode {

    private DisplayShape[] displayShapes;
    private int displayShapeIndex = 0;
    protected boolean editMode;

    public ShapeTestMode(int numberOfShapes) {
        buildDisplayShapes(numberOfShapes);

        addKeyBinding('u', 'i',
                      () -> "prev/next shape-set (" + (1 + displayShape().shapeSetIndex()) +
                      " of " + displayShape().numShapeSets() + " --> " + shapeSet().name() + ")",
                      () -> previousShapeSet(),
                      () -> nextShapeSet());

        addKeyBinding('o', 'p',
                      () -> "prev/next shape (" + (1 + shapeSet().index()) +
                      " of " + shapeSet().size() + " --> " + shapeWrapper().name() + ")",
                      () -> previousShape(),
                      () -> nextShape());

        if (numberOfShapes > 1)
            addKeyBinding('s',
                          () -> "switch display-shape (" + (displayShapeIndex + 1) +
                          " of " + displayShapes.length + ")",
                          () -> switchDisplayShape());

        addKeyBinding('e', () -> "shape editing mode " + boolStr(editMode),
                      () -> toggleEditMode());

        addKeyBinding('[', ']',
                      () -> "prev/next vertex (" + (1 + shapeWrapper().vertexIndex()) +
                      " of " + shape().numVertices() + ")",
                      () -> previousVertex(),
                      () -> nextVertex());

        addKeyBinding('d', () -> "delete current vertex",
                      () -> clipCurrentVertex());

        addKeyBinding('a', () -> "add vertex after current", () -> addVertex());

        addKeyBinding('r', () -> "reset shape",
                      () -> { shapeWrapper().reset();
                              softUpdateAndRepaint(); });

        addKeyBinding('x', () -> "reflect (x-axis)",
                      () -> { shapeWrapper().reflectX();
                              softUpdateAndRepaint(); });

        addKeyBinding('y', () -> "reflect (y-axis)",
                      () -> { shapeWrapper().reflectY();
                              softUpdateAndRepaint(); });

        addKeyBinding('z', () -> "rotate 90 degrees",
                      () -> { shapeWrapper().rotate();
                              softUpdateAndRepaint(); });

        addKeyBinding('S', () -> "save shape to disk",
                      () -> { System.out.println("... serializing shape object..."); });

        addKeyBinding('L', () -> "load shapes from disk",
                      () -> { System.out.println("... attempt to deserialize shape objects..."); });
    }

    protected void softUpdateAndRepaint() {
        if (editMode) setCursorPos(shapeWrapper().getCanvasVertex());
        else setCursorPos(shape().centre());
        getCanvas().repaint();
    }

    private void buildDisplayShapes(int numShapes) {
        // init
        displayShapes = new DisplayShape[numShapes];
        for (int i = 0; i < numShapes; i++)
            displayShapes[i] = new DisplayShape(getShapeSets());
        // transpose each shape-set
        int yStep = 12;
        int y = (numShapes > 1 ?
                 (yStep * numShapes) / 2 :
                 0);
        for (int i = 0; i < numShapes; i++) {
            int yOffset =  y - (i * yStep);
            displayShapes[i].translate(0, yOffset);
        }
    }

    protected ShapeGroupInt shape()          { return displayShape().shape(); }
    protected ShapeGroupInt shape(int index) { return displayShape(index).shape(); }

    protected ShapeWrapper shapeWrapper()          { return displayShape().wrapper(); }
    protected ShapeWrapper shapeWrapper(int index) { return displayShape(index).wrapper(); }

    protected ShapeSet shapeSet()          { return displayShape().shapeSet(); }
    protected ShapeSet shapeSet(int index) { return displayShape(index).shapeSet(); }

    private DisplayShape displayShape()          { return displayShape(displayShapeIndex); }
    private DisplayShape displayShape(int index) { return displayShapes[index]; }


    private void switchDisplayShape() {
        displayShapeIndex++;
        if (displayShapeIndex >= displayShapes.length)
            displayShapeIndex = 0;
        softUpdateAndRepaint();
    }

    private void previousShape() {
        displayShape().shapeSet().previousShape();
        // getCanvas().updateAndRepaint();
        // if (!editMode) shapeWrapper().setPosition(cursorX(), cursorY());
        softUpdateAndRepaint();
    }

    private void nextShape() {
        displayShape().shapeSet().nextShape();
        // getCanvas().updateAndRepaint();
        // if (!editMode) shapeWrapper().setPosition(cursorX(), cursorY());
        softUpdateAndRepaint();
    }

    private void incrVertexIndex(int amount) {
        shapeWrapper().incrVertexIndex(amount);
        softUpdateAndRepaint();
    }

    private void previousVertex() { incrVertexIndex(-1); }
    private void nextVertex()     { incrVertexIndex(1); }

    private void previousShapeSet() {
        displayShape().previousShapeSet();
        // if (!editMode) shapeWrapper().setPosition(cursorX(), cursorY());
        softUpdateAndRepaint();
    }

    private void nextShapeSet() {
        displayShape().nextShapeSet();
        // if (!editMode) shapeWrapper().setPosition(cursorX(), cursorY());
        softUpdateAndRepaint();
    }

    private void toggleEditMode() {
        editMode = !editMode;
        softUpdateAndRepaint();
        // if (editMode) {
        //     // set cursor to current vertex
        //     setCursorPos(shapeWrapper().getCanvasVertex());
        // } else {
        //     // set cursor to centre of current shape
        //     setCursorPos(shapeWrapper().centrePoint());
        // }
        // getCanvas().repaint();
    }

    protected void setCurrentVertex(int x, int y) {
        shapeWrapper().setVertex(cursorX(), cursorY());
    }

    protected void clipCurrentVertex() {
        System.out.println("delete vertex at index: " + shapeWrapper().vertexIndex());
        shapeWrapper().clipVertex();
        getCanvas().updateAndRepaint();
    }

    protected void addVertex() {
        System.out.println("add vertex after index: " + shapeWrapper().vertexIndex());
        shapeWrapper().addVertex();
        getCanvas().updateAndRepaint();
    }

    @Override
    public void update() {
        if (editMode) {
            setCurrentVertex(cursorX(), cursorY());
        }
    }



    /*---------------------- SHAPE-WRAPPER CLASS -----------------------*/

    /**
     * A mutable wrapper for ShapeInt, with some extra features...
     */
    class ShapeWrapper {
        private String name;
        private ShapeGroupInt originalShape;
        private ShapeGroupInt modShape;
        private int xPos = 0;
        private int yPos = 0;
        private int vertexIndex = 0;

        public ShapeWrapper(String name, ShapeGroupInt s) {
            this.name = name;
            originalShape = s;
            modShape = s;
        }

        public String name() { return name; }

        public int vertexIndex() { return vertexIndex; }

        public void incrVertexIndex(int amount) {
            vertexIndex += amount;
            if (vertexIndex < 0)
                vertexIndex = shape().numVertices() - 1;
            else if (vertexIndex >= shape().numVertices())
                vertexIndex = 0;
        }

        /**
         * @return Position-adjusted shape.
         */
        public ShapeGroupInt shape() { return modShape.translate(xPos, yPos); }

        public PointInt centrePoint() {
            return new PointInt(xPos + modShape.centre().x(),
                                yPos + modShape.centre().y());
        }

        public void setPosition(int x, int y) {
            xPos = x - modShape.centre().x();
            yPos = y - modShape.centre().y();
        }

        public void reset() { modShape = originalShape; }

        public void reflectX() {
            modShape = modShape.reflectX(modShape.centre().x());
        }

        public void reflectY() {
            modShape = modShape.reflectY(modShape.centre().y());
        }

        public void rotate() {
            modShape = modShape.rotate90(modShape.centre());
        }

        /**
         * Get vertex, adjusted for canvas co-ordinates.
         */
        public PointInt getCanvasVertex() {
            return modShape.vertex(vertexIndex).sum(xPos, yPos);
        }

        /**
         * Sets the current vertex.
         */
        public void setVertex(int x, int y) {
            modShape = modShape.setVertex(vertexIndex, x - xPos, y - yPos);
        }

        public void clipVertex() {
            modShape = modShape.clipVertex(vertexIndex);
        }

        public void addVertex() {
            PointInt p = Geom2DInt.midPoint(modShape.edge(vertexIndex));
            modShape = modShape.addVertexAfter(vertexIndex, p);
        }

        public void paint(Graphics g) {
            g.setColor(Color.MAGENTA);
            for (LineInt ln : modShape.getEdges()) {
                g.drawLine(ln.startX() + xPos,
                           ln.startY() + yPos,
                           ln.endX() + xPos,
                           ln.endY() + yPos);
            }
        }
    }





    /*---------------------- DISPLAY-SHAPE CLASS -----------------------*/

    /**
     * Encapsulates a shape-display slot.
     *
     * Each instance maintains it's own list of shape-sets.
     */
    class DisplayShape {

        private ShapeSet[] shapeSets;
        private int shapeSetIndex = 0;

        public DisplayShape(ShapeSet[] shapeSets) {
            this.shapeSets = shapeSets;
        }

        public ShapeGroupInt shape() { return wrapper().shape(); }

        public ShapeWrapper wrapper() { return shapeSet().current(); }

        public ShapeSet shapeSet() { return shapeSets[shapeSetIndex]; }

        public int numShapeSets() { return shapeSets.length; }

        public int shapeSetIndex() { return shapeSetIndex; }

        public void previousShapeSet() {
            shapeSetIndex--;
            if (shapeSetIndex < 0) shapeSetIndex = shapeSets.length - 1;
        }

        public void nextShapeSet() {
            shapeSetIndex++;
            if (shapeSetIndex >= shapeSets.length) shapeSetIndex = 0;
        }

        public void translate(int x, int y) {
            for (ShapeSet ss : shapeSets)
                for (ShapeWrapper sw : ss.all())
                    sw.setPosition(x, y);
        }

    }





    /*------------------------ SHAPE-SET CLASS -------------------------*/

    class ShapeSet {
        private String name;
        private ShapeWrapper[] wrappers;
        private int index = 0;
        public ShapeSet(String name, ShapeWrapper[] wrappers) {
            this.name = name;
            this.wrappers = wrappers;
        }
        public String name() { return name; }
        public ShapeWrapper current() { return wrappers[index]; }
        public ShapeWrapper[] all() { return wrappers; }
        public int index() { return index; }
        public int size() { return wrappers.length; }
        public void previousShape() {
            index--;
            if (index < 0) index = wrappers.length - 1;
        }
        public void nextShape() {
            index++;
            if (index >= wrappers.length) index = 0;
        }
    }





    /*--------------------------- SHAPE SETS ---------------------------*/

    private ShapeSet[] getShapeSets() {
        return new ShapeSet[] {
            simplePolygonsRectilinear(),
            simplePolygons45(),
            simplePolygons(),
            fragmentedShapes(),
            perforatedShapes(),
            fragmentedAndPerforatedShapes(),
            nestedShapes(),
            unusualButValidShapes(),
            invalidShapes(),
        };
    }

    private ShapeWrapper newSW(String name, PointInt ... points) {
        return new ShapeWrapper(name, new ShapeGroupInt(new PolyInt(points)));
    }

    private ShapeWrapper newSW(String name, int[] xs, int[] ys) {
        return new ShapeWrapper(name, new ShapeGroupInt(new PolyInt(xs, ys)));
    }

    private ShapeWrapper newSWFrag(String name, PolyInt ... outlines) {
        ShapeInt[] shapes = new ShapeInt[outlines.length];
        for (int i = 0; i < outlines.length; i++)
            shapes[i] = new ShapeInt(outlines[i]);
        return new ShapeWrapper(name, new ShapeGroupInt(shapes));
    }

    private ShapeWrapper newSWPerf(String name, PointInt[][] points) {
        // PolyInt[] polys = new PolyInt[points.length];
        // for (int i = 0; i < points.length; i++)
        //     polys[i] = new PolyInt(points[i]);
        return new ShapeWrapper(name, new ShapeGroupInt(newShape(points)));
    }

    private ShapeWrapper newSWFragPerf(String name, PointInt[][][] points) {
        ShapeInt[] shapes = new ShapeInt[points.length];
        for (int i = 0; i < points.length; i++)
            shapes[i] = newShape(points[i]);
        return new ShapeWrapper(name, new ShapeGroupInt(shapes));
    }

    private ShapeInt newShape(PointInt[][] points) {
        PolyInt[] polys = new PolyInt[points.length];
        for (int i = 0; i < points.length; i++)
            polys[i] = new PolyInt(points[i]);
        return new ShapeInt(polys);
    }

    // private ShapeInt newShapeGroup(PointInt[][] points) {
    //     ShapeInt[] shapes = new ShapeInt[outlines.length];
    //     for (int i = 0; i < outlines.length; i++)
    //         shapes[i] = new ShapeInt(outlines[i]);
    //     return new ShapeGroupInt(shapes);
    // }

    private ShapeSet simplePolygonsRectilinear() {
        return new ShapeSet("Simple Polygons (rectilinear)",
            new ShapeWrapper[] {
                newSW("square",
                      new PointInt( 0,  0),
                      new PointInt(20,  0),
                      new PointInt(20, 20),
                      new PointInt( 0, 20)),
                newSW("thin rectangle",
                      new int[] { 0, 5,  5,  0 },
                      new int[] { 0, 0, 25, 25 }),
                newSW("L-shape",
                      new int[] { 0, 7, 7, 14, 14,  0 },
                      new int[] { 0, 0, 7,  7, 14, 14 }),
                newSW("comb",
                      new int[] { 0, 4,  4,  8, 8, 12, 12, 16, 16, 20, 20, 24, 24, 28, 28, 0  },
                      new int[] { 4, 4, 16, 16, 4,  4, 16, 16,  4,  4, 16, 16,  0,  0, 22, 22 }),
                newSW("spiral",
                      new PointInt( 0,  0),
                      new PointInt(27,  0),
                      new PointInt(27,  3),
                      new PointInt( 3,  3),
                      new PointInt( 3, 24),
                      new PointInt(24, 24),
                      new PointInt(24,  9),
                      new PointInt( 9,  9),
                      new PointInt( 9, 18),
                      new PointInt(18, 18),
                      new PointInt(18, 15),
                      new PointInt(12, 15),
                      new PointInt(12, 12),
                      new PointInt(21, 12),
                      new PointInt(21, 21),
                      new PointInt( 6, 21),
                      new PointInt( 6,  6),
                      new PointInt(27,  6),
                      new PointInt(27, 27),
                      new PointInt( 0, 27)),
                // new ShapeWrapper("corridor",
                //                  new ShapeInt(new PolyInt(new int[] { 0, 7, 7, 14, 14,  0 },
                //                                           new int[] { 0, 0, 7,  7, 14, 14 }))),
                // new ShapeWrapper("branching corridor",
                //                  new ShapeInt(new PolyInt(new int[] { 0, 7, 7, 14, 14,  0 },
                //                                           new int[] { 0, 0, 7,  7, 14, 14 })))
            });
    }

    private ShapeSet simplePolygons45() {
        return new ShapeSet("Simple Polygons (inc 45 degree)",
            new ShapeWrapper[] {
                newSW("diamond (45)",
                      new int[] { 0,  10, 20, 10 },
                      new int[] { 10,  0, 10, 20 }),
                newSW("octagon",
                      new int[] { 0, 5, 15, 20, 20, 15,  5,  0 },
                      new int[] { 5, 0,  0,  5, 15, 20, 20, 15 }),
                newSW("parallelogram (45)",
                      new int[] { 0, 20, 30, 10 },
                      new int[] { 0, 0, 10, 10 }),
                newSW("thin parallelogram (45)",
                      new int[] { 0, 5, 35, 30 },
                      new int[] { 0, 0, 30, 30 }),
                newSW("angle (45)",
                      new int[] { 0, 5,  5, 20, 15,  0 },
                      new int[] { 0, 0, 15, 30, 30, 15 }),
                newSW("slope & chimmney (45)",
                      new int[] { 30, 20, 5,  5,  0, 0, 30 },
                      new int[] { 20, 20, 5, 15, 15, 0,  0 }),
            });
    }

    private ShapeSet simplePolygons() {
        return new ShapeSet("Simple Polygons",
            new ShapeWrapper[] {
                newSW("parallelogram",
                      new int[] { 0, 20, 25, 5 },
                      new int[] { 0, 0, 10, 10 }),
                newSW("diamond",
                      new int[] { 10, 20, 10, 0 },
                      new int[] { 0, 20, 40, 20 }),
                newSW("irregular quadrilateral",
                      new PointInt( 1, -2),
                      new PointInt( 9, -3),
                      new PointInt(-4, 12),
                      new PointInt(-2, -8)),
                newSW("irregular septagon",
                      new int[] { 0, 0, 3, 36, 42, 32,  5 },
                      new int[] { 9, 3, 0,  2, 15, 21, 15 }),
                newSW("non-convex 1",
                      new int[] {  0, 6, 6, 24, 21, 18,  6 },
                      new int[] { 12, 0, 9,  9, 21, 18, 24 }),
                newSW("non-convex 2",
                      new PointInt( 0,  0),
                      new PointInt(10,  0),
                      new PointInt(16,  6),
                      new PointInt(16, 10),
                      new PointInt(20, 10),
                      new PointInt(25, 15),
                      new PointInt(35, 35),
                      new PointInt(25, 35),
                      new PointInt(17, 19),
                      new PointInt( 3, 19),
                      new PointInt( 3, 16),
                      new PointInt( 5, 16),
                      new PointInt( 5, 12),
                      new PointInt( 3, 12),
                      new PointInt( 3,  4),
                      new PointInt( 0,  4)),
            });
    }

    private ShapeSet fragmentedShapes() {
        return new ShapeSet("Fragmented",
            new ShapeWrapper[] {
                newSWFrag("two rectangles",
                          new PolyInt(new int[] { 0,  10, 10,   0 },
                                      new int[] { 0,   0, 20,  20 }),
                          new PolyInt(new int[] { 15, 25, 25,  15 },
                                      new int[] { 0,   0, 20,  20 })),
                newSWFrag("multiple rectangles (small)",
                          new PolyInt(new int[] { 0, 4, 4, 0 },
                                      new int[] { 0, 0, 6, 6 }),
                          new PolyInt(new int[] {  9,  9,  8,  8 },
                                      new int[] { 12, 13, 13, 12 }),
                          new PolyInt(new int[] { 11, 11, 14, 14 },
                                      new int[] { 10,  3,  3, 10 }),
                          new PolyInt(new int[] { 1, 9,  9,  1 },
                                      new int[] { 8, 8, 10, 10 }),
                          new PolyInt(new int[] { 10, 10, 8, 8 },
                                      new int[] {  3,  5, 5, 3 })),
            });
    }

    private ShapeSet perforatedShapes() {
        return new ShapeSet("Perforated",
            new ShapeWrapper[] {
                newSWPerf("perforated square",
                    new PointInt[][] { { new PointInt( 0,  0),
                                         new PointInt(20,  0),
                                         new PointInt(20, 20),
                                         new PointInt( 0, 20), },
                                       { new PointInt(10, 16),
                                         new PointInt(18, 10),
                                         new PointInt(10, 10), } }
                    ),
            });
    }

    private ShapeSet fragmentedAndPerforatedShapes() {
        return new ShapeSet("Fragmented and Perforated",
            new ShapeWrapper[] {
                newSWFragPerf("fragmented & perforated 1",
                    new PointInt[][][] { { { new PointInt( 0,  0),
                                             new PointInt(20,  0),
                                             new PointInt(20, 14),
                                             new PointInt(14, 20),
                                             new PointInt( 0, 20), },
                                           { new PointInt(10, 16),
                                             new PointInt(18, 10),
                                             new PointInt(10, 10), } },
                                         { { new PointInt(24,  0),
                                             new PointInt(30,  0),
                                             new PointInt(30, 20),
                                             new PointInt(18, 20),
                                             new PointInt(24, 14), } } }
                    ),
            });
    }

    private ShapeSet nestedShapes() {
        return new ShapeSet("Nested Shapes",
            new ShapeWrapper[] {
                newSWFragPerf("box & nested triangle",
                    new PointInt[][][] { { { new PointInt( 0,  0),
                                             new PointInt(20,  0),
                                             new PointInt(20, 20),
                                             new PointInt( 0, 20), },
                                           { new PointInt( 4,  6),
                                             new PointInt( 4, 17),
                                             new PointInt(15, 17),
                                             new PointInt(15,  6), } },
                                         { { new PointInt( 6,  8),
                                             new PointInt(12,  8),
                                             new PointInt(12, 14), } } }
                    ),
                newSWFragPerf("multiple nesting (4 levels) rectilinear",
                    new PointInt[][][] { { { new PointInt( 0,  0),
                                             new PointInt(20,  0),
                                             new PointInt(20, 20),
                                             new PointInt( 0, 20), },
                                           { new PointInt( 4,  6),
                                             new PointInt(15,  6),
                                             new PointInt(15, 17),
                                             new PointInt( 4, 17), } },
                                         { { new PointInt( 6,  8),
                                             new PointInt(12,  8),
                                             new PointInt(12, 14), } } }
                    ),
            });
    }

    private ShapeSet unusualButValidShapes() {
        return new ShapeSet("Unusual but valid",
            new ShapeWrapper[] {
                newSW("rectangle with multiple vertices on line",
                      new PointInt( 0,  0),
                      new PointInt(25,  0),
                      new PointInt(25, 20),
                      new PointInt(15, 20),
                      new PointInt(12, 20),
                      new PointInt( 5, 20),
                      new PointInt( 0, 20)),
                newSW("rectangle with multiple vertices on line (v2)",
                      new PointInt( 0,  0),
                      new PointInt(14,  0),
                      new PointInt(15,  0),
                      new PointInt(21,  0),
                      new PointInt(25,  0),
                      new PointInt(25, 20),
                      new PointInt(15, 20),
                      new PointInt( 0, 20)),
            });
    }

    private ShapeSet invalidShapes() {
        return new ShapeSet("Invalid Shapes",
            new ShapeWrapper[] {
                newSW("bad winding",
                      new int[] { 0,  0, 10, 15, 30 },
                      new int[] { 0, 10, 10, 15,  0 }),
                newSWPerf("bad winding in hole",
                    new PointInt[][] { { new PointInt( 0,  0),
                                         new PointInt(20,  0),
                                         new PointInt(20, 20),
                                         new PointInt( 0, 20), },
                                       { new PointInt(10, 16),
                                         new PointInt(10, 10),
                                         new PointInt(18, 10), } }
                    ),
                newSW("self-intersecting",
                      new int[] { 0, 41, 41, 30, 15,  0 },
                      new int[] { 0,  0,  6, -5, 10, 10 }),
                newSW("vertex on an edge",
                      new int[] { 0, 22, 22, 15, 15, 15,  0 },
                      new int[] { 0,  0,  8, 13, 20, 10, 10 }),
                newSW("duplicate vertex",
                      new int[] { 0, 30, 20, 30, 30, 35, 35, 10, 10 },
                      new int[] { 0,  0,  5, 10,  0, 10, 15, 15, 10 }),
                newSW("sub-shapes intersect",
                      new int[] { 0, 20, 20,  0 },
                      new int[] { 0,  0, 20, 20 }),
                newSW("hole intersects outline",
                      new int[] { 0, 20, 20,  0 },
                      new int[] { 0,  0, 20, 20 }),
                newSW("holes intersect",
                      new int[] { 0, 20, 20,  0 },
                      new int[] { 0,  0, 20, 20 }),
            });
    }

}
