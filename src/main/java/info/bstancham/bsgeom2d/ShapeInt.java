package info.bstancham.bsgeom2d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Immutable data type representing a two-dimensional shape with
 * integer co-ordinates.
 *
 * A shape has a single outline, and may have any number of holes
 * contained within.
 *
 * A WELL-FORMED shape must satisfy the following conditions:
 * <li>All holes must be contained inside the outline.
 * <li>No shared vertices, or vertices on edges i.e. no areas of zero-width.
 * <li>No self-intersection.
 * <li>Outline must have counter-clockwise winding.
 * <li>Holes must have clockwise winding.
 */
public final class ShapeInt implements Iterable<PolyInt> {

    private PolyInt[] polygons;

    // values will be cached once they have been calculated for the first time
    private PointInt[] vertices = null;
    private LineInt[] edges = null;


    /**
     * @param polygons The first polygon is the outline. All
     * subsequent polygons are holes, and must be contained inside the
     * outline, or the shape is invalid.
     */
    public ShapeInt(PolyInt ... polygons) {
        if (polygons == null)
            throw new NullPointerException("null argument given to constructor");
        if (polygons.length < 1)
            throw new IllegalArgumentException("polygon list is empty");
        // make defensive copy of array...
        this.polygons = new PolyInt[polygons.length];
        for (int i = 0; i < polygons.length; i++)
            this.polygons[i] = polygons[i];
    }



    /*--------------------------- ACCESSORS ----------------------------*/

    public PolyInt outline() { return polygons[0]; }

    public int numHoles() { return polygons.length - 1; }

    public PolyInt hole(int index) { return polygons[index + 1]; }

    public int numVertices() {
        return fetchVertices().length;
    }

    public PointInt vertex(int index) {
        return fetchVertices()[index];
    }

    public int numEdges() {
        return fetchEdges().length;
    }

    public LineInt edge(int index) {
        return fetchEdges()[index];
    }

    /**
     * @return An array containing all of the vertices in this
     * shape. To maintain immutability, a new array is created each
     * time the method is called.
     */
    public PointInt[] getVertices() {
        PointInt[] out = new PointInt[numVertices()];
        for (int i = 0; i < numVertices(); i++)
            out[i] = vertex(i);
        return out;
    }

    /**
     * @return An array containing all of the lines in this shape. To
     * maintain immutability, a new array is created each time the
     * method is called.
     */
    public LineInt[] getEdges() {
        LineInt[] out = new LineInt[numEdges()];
        for (int i = 0; i < numEdges(); i++)
            out[i] = edge(i);
        return out;
    }

    /**
     * Fetches the cached array of all of the vertices in this shape.
     * Builds it if it doesn't already exist.
     */
    private PointInt[] fetchVertices() {
        if (vertices == null) {
            List<PointInt> temp = new ArrayList<>();
            for (PolyInt p : polygons)
                for (PointInt v : p.getVertices())
                    temp.add(v);
            vertices = temp.toArray(new PointInt[temp.size()]);
        }
        return vertices;
    }

    /**
     * Fetches the cached array of all of the lines in this shape.
     * Builds it if it doesn't already exist.
     */
    private LineInt[] fetchEdges() {
        if (edges == null) {
            List<LineInt> temp = new ArrayList<>();
            for (PolyInt p : polygons)
                for (LineInt e : p.getEdges())
                    temp.add(e);
            edges = temp.toArray(new LineInt[temp.size()]);
        }
        return edges;
    }



    /*--------------------------- ITERATORS ----------------------------*/

    /**
     * @return An iterator which includes all polygons: the outline,
     * and all holes.
     */
    public Iterator<PolyInt> iterator() { return new PolygonIterator(0); }

    // public Iterator<PolyInt> polygonIterator() {
    //     return new PolygonIterator();
    // }


    /**
     * @return An iterator which includes only the holes.
     */
    public Iterator<PolyInt> holesIterator() { return new PolygonIterator(1); }

    private class PolygonIterator implements Iterator<PolyInt> {
        private int i;
        public PolygonIterator(int startIndex) {
            i = startIndex;
        }
        public boolean hasNext() { return i < polygons.length; }
        public PolyInt next() { return polygons[i++]; }
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
     * Assumes that the shape is WELL-FORMED. If the shape has holes
     * which intersect the outline, some of these parts may lie
     * outside of the bounding box.
     */
    public BoxInt boundingBox() { return outline().boundingBox(); }

    public boolean contains(PointInt p) { return contains(p, true); }

    public boolean contains(PointInt p, boolean includeEdges) {
        boolean outlineContains = outline().contains(p, includeEdges);
        if (outlineContains)
            // is point in a hole?
            for (int i = 1; i < polygons.length; i++)
                if (polygons[i].contains(p, !includeEdges))
                    return false;
        return outlineContains;
    }



    /*----------------------------- TESTS ------------------------------*/

    // public boolean isValid() {
    //     // return !isSelfIntersecting
    //     // && holes are all contained
    //     // && no duplicate vertices
    //     // && no vertices on edges
    //     // && outline is CCWW
    //     // && holes are CW
    //     // ...
    // }

    public boolean isPerforated() { return polygons.length > 1; }



    /*------------------------ TRANSFORMATIONS -------------------------*/

    public ShapeInt translate(int x, int y) {
        PolyInt[] newPolys = new PolyInt[polygons.length];
        for (int i = 0; i < polygons.length; i++)
            newPolys[i] = polygons[i].translate(x, y);
        return new ShapeInt(newPolys);
    }

    public ShapeInt reflectX(int mid) {
        PolyInt[] newPolys = new PolyInt[polygons.length];
        for (int i = 0; i < polygons.length; i++)
            newPolys[i] = polygons[i].reflectX(mid);
        return new ShapeInt(newPolys);
    }

    public ShapeInt reflectY(int mid) {
        PolyInt[] newPolys = new PolyInt[polygons.length];
        for (int i = 0; i < polygons.length; i++)
            newPolys[i] = polygons[i].reflectY(mid);
        return new ShapeInt(newPolys);
    }

    public ShapeInt rotate90(PointInt centre) {
        PolyInt[] newPolys = new PolyInt[polygons.length];
        for (int i = 0; i < polygons.length; i++)
            newPolys[i] = polygons[i].rotate90(centre);
        return new ShapeInt(newPolys);
    }

    public ShapeInt setVertex(int index, int x, int y) {
        PolyInt[] newPolys = new PolyInt[polygons.length];
        for (int i = 0; i < polygons.length; i++) {
            if (index >= 0 && index < polygons[i].numVertices()) {
                newPolys[i] = polygons[i].setVertex(index, x, y);
            } else {
                newPolys[i] = polygons[i];
            }
            index -= polygons[i].numVertices();
        }
        return new ShapeInt(newPolys);
    }

    public ShapeInt clipVertex(int index) {
        PolyInt[] newPolys = new PolyInt[polygons.length];
        for (int i = 0; i < polygons.length; i++) {
            if (index >= 0 && index < polygons[i].numVertices()) {
                newPolys[i] = polygons[i].clipVertex(index);
            } else {
                newPolys[i] = polygons[i];
            }
            index -= polygons[i].numVertices();
        }
        return new ShapeInt(newPolys);
    }

    public ShapeInt addVertexAfter(int index, PointInt p) {
        PolyInt[] newPolys = new PolyInt[polygons.length];
        for (int i = 0; i < polygons.length; i++) {
            if (index >= 0 && index < polygons[i].numVertices()) {
                newPolys[i] = polygons[i].addVertexAfter(index, p);
            } else {
                newPolys[i] = polygons[i];
            }
            index -= polygons[i].numVertices();
        }
        return new ShapeInt(newPolys);
    }



    /*------------------------- TRIANGULATION --------------------------*/

    public TriInt[] triangulate() {
        return outline().getTriangulation();
    }

}
