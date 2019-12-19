package info.bschambers.geom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Immutable data type representing a group of two-dimensional shapes
 * with integer co-ordinates.
 *
 * A shapes may lie inside a holes in other shapes, but no
 * intersection or touching is allowed.
 *
 * A WELL-FORMED shape-group:
 * <li>...
 */
public final class ShapeGroupInt implements Iterable<ShapeInt> {

    // public enum ShapeType {
    //     SIMPLE,
    //     CONVEX,
    //     NON_CONVEX,
    //     PERFORATED,
    //     FRAGMENTED,
    //     NESTED,
    //     INVALID
    // }

    private ShapeInt[] shapes;

    // values will be cached after they are calculated
    private BoxInt bounds = null;
    private PointInt[] vertices = null;
    private LineInt[] edges = null;

    public ShapeGroupInt(PolyInt outline) {
        this(new ShapeInt(outline));
    }

    public ShapeGroupInt(ShapeInt shape) {
        this(new ShapeInt[] { shape });
    }

    public ShapeGroupInt(ShapeInt[] shapes) {
        if (shapes == null)
            throw new NullPointerException("null argument given to constructor");
        // defensive copy of input array
        this.shapes = new ShapeInt[shapes.length];
        for (int i = 0; i < shapes.length; i++)
            this.shapes[i] = shapes[i];
    }



    /*--------------------------- ACCESSORS ----------------------------*/

    public int numSubShapes() { return shapes.length; }

    public ShapeInt subShape(int index) { return shapes[index]; }

    public int numVertices() { return fetchVertices().length; }

    public PointInt vertex(int index) { return fetchVertices()[index]; }

    public int numEdges() { return fetchEdges().length; }

    public LineInt edge(int index) { return fetchEdges()[index]; }

    /**
     * @return An array containing all of the vertices in this
     * shape-group. To maintain immutability, a new array is created
     * each time the method is called.
     */
    public PointInt[] getVertices() {
        PointInt[] out =  new PointInt[numVertices()];
        System.arraycopy(fetchVertices(), 0, out, 0, numVertices());
        return out;
    }

    /**
     * @return An array containing all of the edges in this
     * shape-group. To maintain immutability, a new array is created
     * each time the method is called.
     */
    public LineInt[] getEdges() {
        LineInt[] out =  new LineInt[numEdges()];
        System.arraycopy(fetchEdges(), 0, out, 0, numEdges());
        return out;
    }

    /**
     * Must remain private, or immutability of class would be broken!
     */
    private PointInt[] fetchVertices() {
        if (vertices == null) {
            List<PointInt> temp = new ArrayList<>();
            for (ShapeInt s : shapes)
                for (PointInt v : s.getVertices())
                    temp.add(v);
            vertices = temp.toArray(new PointInt[temp.size()]);
        }
        return vertices;
    }

    /**
     * Must remain private, or immutability of class would be broken!
     */
    private LineInt[] fetchEdges() {
        if (edges == null) {
            List<LineInt> temp = new ArrayList<>();
            for (ShapeInt s : shapes)
                for (LineInt e : s.getEdges())
                    temp.add(e);
            edges = temp.toArray(new LineInt[temp.size()]);
        }
        return edges;
    }



    /*----------------------------- TESTS ------------------------------*/

    public boolean isEmpty() { return shapes.length < 1; }



    /*--------------------------- ITERATORS ----------------------------*/

    public Iterator<ShapeInt> iterator() { return new SubShapeIterator(); }

    // public Iterator<ShapeInt> subShapeIterator() {
    //     return new SubShapeIterator();
    // }

    private class SubShapeIterator implements Iterator<ShapeInt> {
        private int i = 0;
        public boolean hasNext() { return i < shapes.length; }
        public ShapeInt next() { return shapes[i++]; }
        public void remove() {
            throw new UnsupportedOperationException(
                "operation not supported in this iterator");
        }
    }




    /*---------------------------- GEOMETRY ----------------------------*/

    /**
     * @return The centre of the bounding box.
     */
    public PointInt centre() { return boundingBox().centre(); }

    /**
     * @return Bounding box for this shape-group.
     *
     * Assumes that the shape-group is WELL-FORMED. If the shape-group
     * has holes which intersect outlines, some of these parts may lie
     * outside of the bounding box.
     */
    public BoxInt boundingBox() {
        if (bounds == null) {
            int left   = shapes[0].boundingBox().left();
            int bottom = shapes[0].boundingBox().bottom();
            int right  = shapes[0].boundingBox().right();
            int top    = shapes[0].boundingBox().top();
            for (ShapeInt s : shapes) {
                if (s.boundingBox().left() < left)
                    left = s.boundingBox().left();
                if (s.boundingBox().bottom() < bottom)
                    bottom = s.boundingBox().bottom();
                if (s.boundingBox().right() > right)
                    right = s.boundingBox().left();
                if (s.boundingBox().top() > top)
                    top = s.boundingBox().left();
            }
            bounds = new BoxInt(left, bottom, right, top);
        }
        return bounds;
    }

    public boolean contains(PointInt p) { return contains(p, true); }

    public boolean contains(PointInt p, boolean includeEdges) {
        for (ShapeInt s : shapes)
            if (s.contains(p, includeEdges))
                return true;
        return false;
    }



    /*------------------------ TRANSFORMATIONS -------------------------*/

    public ShapeGroupInt translate(int x, int y) {
        ShapeInt[] newShapes = new ShapeInt[shapes.length];
        for (int i = 0; i < shapes.length; i++)
            newShapes[i] = shapes[i].translate(x, y);
        return new ShapeGroupInt(newShapes);
    }

    public ShapeGroupInt reflectX(int mid) {
        ShapeInt[] newShapes = new ShapeInt[shapes.length];
        for (int i = 0; i < shapes.length; i++)
            newShapes[i] = shapes[i].reflectX(mid);
        return new ShapeGroupInt(newShapes);
    }

    public ShapeGroupInt reflectY(int mid) {
        ShapeInt[] newShapes = new ShapeInt[shapes.length];
        for (int i = 0; i < shapes.length; i++)
            newShapes[i] = shapes[i].reflectY(mid);
        return new ShapeGroupInt(newShapes);
    }

    public ShapeGroupInt rotate90(PointInt centre) {
        ShapeInt[] newShapes = new ShapeInt[shapes.length];
        for (int i = 0; i < shapes.length; i++)
            newShapes[i] = shapes[i].rotate90(centre);
        return new ShapeGroupInt(newShapes);
    }

    public ShapeGroupInt setVertex(int index, int x, int y) {
        ShapeInt[] newShapes = new ShapeInt[shapes.length];
        for (int i = 0; i < shapes.length; i++) {
            if (index >= 0 && index < shapes[i].numVertices()) {
                newShapes[i] = shapes[i].setVertex(index, x, y);
            } else {
                newShapes[i] = shapes[i];
            }
            index -= shapes[i].numVertices();
        }
        return new ShapeGroupInt(newShapes);
    }

    public ShapeGroupInt clipVertex(int index) {
        ShapeInt[] newShapes = new ShapeInt[shapes.length];
        for (int i = 0; i < shapes.length; i++) {
            if (index >= 0 && index < shapes[i].numVertices()) {
                newShapes[i] = shapes[i].clipVertex(index);
            } else {
                newShapes[i] = shapes[i];
            }
            index -= shapes[i].numVertices();
        }
        return new ShapeGroupInt(newShapes);
    }

    public ShapeGroupInt addVertexAfter(int index, PointInt p) {
        ShapeInt[] newShapes = new ShapeInt[shapes.length];
        for (int i = 0; i < shapes.length; i++) {
            if (index >= 0 && index < shapes[i].numVertices()) {
                newShapes[i] = shapes[i].addVertexAfter(index, p);
            } else {
                newShapes[i] = shapes[i];
            }
            index -= shapes[i].numVertices();
        }
        return new ShapeGroupInt(newShapes);
    }



    /*------------------------- TRIANGULATION --------------------------*/

    public TriInt[] triangulate() {
        List<TriInt> triangles = new ArrayList<>();
        for (ShapeInt s : shapes)
            for (TriInt t : s.triangulate())
                triangles.add(t);
        return new TriInt[triangles.size()];
    }



    /*-------------------- BOOLEAN TRANSFORMATIONS ---------------------*/

    public static class BooleanTransformation {

        private ShapeGroupInt subtraction1 = null;
        private ShapeGroupInt subtraction2 = null;
        private ShapeGroupInt intersection = null;
        private ShapeGroupInt union = null;
        private PolyInt.BooleanTransformation polyBool;

        public BooleanTransformation(ShapeGroupInt group1, ShapeGroupInt group2) {

            polyBool = new PolyInt.BooleanTransformation(group1.shapes[0].outline(),
                                                         group2.shapes[0].outline());

            subtraction1 = toSG(polyBool.subtraction1());
            subtraction2 = toSG(polyBool.subtraction2());
            intersection = toSG(polyBool.intersection());
            union        = toSG(polyBool.union());
        }

        private ShapeGroupInt toSG(PolyInt[] polys) {
            if (polys.length < 1) return new ShapeGroupInt(new ShapeInt[] {});
            // else
            return new ShapeGroupInt(polys[0]);
        }

        public ShapeGroupInt subtraction1() { return subtraction1; }
        public ShapeGroupInt subtraction2() { return subtraction2; }
        public ShapeGroupInt intersection() { return intersection; }
        public ShapeGroupInt union()        { return union; }

    }

}
