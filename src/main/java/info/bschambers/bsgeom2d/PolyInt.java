package info.bschambers.bsgeom2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;

/**
 * Immutable data type representing a polygon with integer
 * co-ordinates.
 *
 * TODO: polygon validation
 *
 * A WELL-FORMED polygon must satisfy all of the following conditions:
 * <li>Counter-clockwise winding.
 * <li>No intersecting edges.
 * <li>No vertex contained on another edge.
 * <li>No duplicate vertices.
 * <li>...
 */
public class PolyInt {

    public enum WindingOrder {
        CLOCKWISE,
        COUNTER_CLOCKWISE,
        INVALID,
    }

    private PointInt[] vertices;

    // values to be cached after they are calculated
    private LineInt[] edges = null;
    private Double sumAngles = null;
    private Boolean convexP = null;
    private Boolean ccwP = null;
    private BoxInt bounds = null;
    private WindingOrder winding = null;

    /**
     * @param vertices Order of vertices must be consistent with
     * specified winding type.
     */
    public PolyInt(PointInt ... vertices) {
        if (vertices == null)
            throw new NullPointerException("null argument given to constructor");
        if (vertices.length < 3)
            throw new IllegalArgumentException("must have at least 3 vertices - only supplied " + vertices.length);
        // make defensive copy of input array, to preserve immutability...
        // ... build bounding box as same time
        int left   = vertices[0].x();
        int bottom = vertices[0].y();
        int right  = vertices[0].x();
        int top    = vertices[0].y();
        this.vertices = new PointInt[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            this.vertices[i] = vertices[i];
            if (vertices[i].x() < left)   left   = vertices[i].x();
            if (vertices[i].y() < bottom) bottom = vertices[i].y();
            if (vertices[i].x() > right)  right  = vertices[i].x();
            if (vertices[i].y() > top)    top    = vertices[i].y();
        }
        bounds = new BoxInt(left, bottom, right, top);
    }

    /**
     * Co-ordinates must be given in counter-clockwise winding order.
     *
     * @param xs The x co-ordinates.
     * @param ys The y co-ordinates.
     * @throws IllegalArgumentException if the number of x and y
     * co-ordinates is not the same.
     */
    public PolyInt(int[] xs, int[] ys) {
        // this(WindingType.COUNTER_CLOCKWISE, buildPoints(xs, ys));
        this(buildPoints(xs, ys));
    }

    private static PointInt[] buildPoints(int[] xs, int[] ys) {
        if (xs.length != ys.length) {
            String msg = "x and y co-ordinate arrays must be the same length " +
                "---> xs.length=" + xs.length + " ys.length=" + ys.length;
            throw new IllegalArgumentException(msg);
        }
        PointInt[] newPoints = new PointInt[xs.length];
        for (int i = 0; i < xs.length; i++) {
            newPoints[i] = new PointInt(xs[i], ys[i]);
        }
        return newPoints;
    }



    /*--------------------------- ACCESSORS ----------------------------*/

    public PointInt vertex(int index) { return vertices[index]; }

    public int numVertices() { return vertices.length; }

    /**
     * @return An array containing all of the vertices in this
     * polygon. To maintain immutability, a new array is created each
     * time the method is called.
     */
    public PointInt[] getVertices() {
        PointInt[] out = new PointInt[numVertices()];
        // for (int i = 0; i < numVertices(); i++)
        //     out[i] = vertex(i);
        System.arraycopy(vertices, 0, out, 0, numVertices());
        return out;
    }

    /**
     * @param The index of the vertex which forms this edge's start
     * point.
     * @return The edge which starts at the given vertex-index.
     */
    public LineInt edge(int index) { return fetchEdges()[index]; }
    //     return new LineInt(vertex(index),
    //                        vertex(wrapIndex(index + 1)));
    // }

    /**
     * @return An array containing all of the edges in this
     * polygon. To maintain immutability, a new array is created each
     * time the method is called.
     */
    public LineInt[] getEdges() {
        LineInt[] out = new LineInt[numVertices()];
        System.arraycopy(fetchEdges(), 0, out, 0, numVertices());
        return out;
    }

    private LineInt[] fetchEdges() {
        if (edges == null) {
            edges = new LineInt[numVertices()];
            for (int i = 0; i < numVertices(); i++)
                edges[i] = new LineInt(vertex(i),
                                       vertex(wrapIndex(i + 1)));
        }
        return edges;
    }

    /**
     * @return The centre point of the bounding box.
     */
    public PointInt centre() { return boundingBox().centre(); }

    /**
     * @return The bounding box for this polygon.
     */
    public BoxInt boundingBox() { return bounds; }

    /**
     * TODO: unit tests
     */
    public int wrapIndex(int i) {
        if (i < 0)              return numVertices() + (i % numVertices());
        if (i >= numVertices()) return i % numVertices();
        else                    return i;
    }



    /*--------------------------- COMPARISON ---------------------------*/

    /**
     * @return True, if the input object is a PolyInt instance with
     * exactly the same shape as this one.<br>
     * The winding order of the two polygons must be the same,
     * although the vertex-list doesn't have to start at the same
     * point.
     */
    @Override
    public boolean equals(Object x) {
        if (x == this) return true;
        if (x == null) return false;
        if (x.getClass() != this.getClass()) return false;
        // cast guaranteed to succeed
        PolyInt that = (PolyInt) x;
        // compare vertices...
        // ... number of vertices...
        if (this.numVertices() != that.numVertices()) return false;
        // ... winding order, if known...
        if (this.winding != null && that.winding != null
            && this.winding != that.winding) return false;
        // ... find first matching vertex...
        int n = -1;
        for (int i = 0; i < this.numVertices(); i++) {
            if (this.vertex(0).equals(that.vertex(i))) {
                n = i;
                break;
            }
        }
        if (n < 0) return false; // no matching vertex
        // ... compare all vertices in order...
        for (int i = 0; i < this.numVertices(); i++)
            if (!this.vertex(i).equals(that.vertex(that.wrapIndex(n + i))))
                return false;
        // else
        return true;
    }

    /**
     * @return True, if line intersects any of the lines in this
     * polygon. When an end point of the line lies on a vertex, this
     * does not count as an intersection.
     */
    public boolean intersectsExcludeVertices(LineInt ln) {
        for (LineInt l : getEdges())
            if (Geom2DInt.lineSegmentsIntersectExcludeVertices(l, ln))
                return true;
        return false;
    }

    public boolean contains(PointInt p) { return contains(p, true); }

    public boolean contains(PointInt p, boolean includeEdges) {
        // check whether point is on any line
	for (LineInt ln : getEdges())
	    if (ln.contains(p))
		return includeEdges;
        // use JPolygon method
	return toJPolygon().contains(p.x(), p.y());
    }

    private java.awt.Polygon jpoly = null;

    private java.awt.Polygon toJPolygon() {
        if (jpoly == null) {
            jpoly = new java.awt.Polygon();
            for (PointInt v : vertices)
                jpoly.addPoint(v.x(), v.y());
	}
        return jpoly;
    }

    /**
     * WARNING!
     *
     * ONLY USE IF LINE AND POLYGON HAVE BEEN SPLIT
     * TOGETHER!... otherwise, vertices will likely not line up how
     * they need to!
     *
     *
     * True, if line is completely inside polygon.
     *
     * False, if any part of the line passes outside the polygon.
     *
     * FAILS! CANNOT SUCCEED...
     * ... need to split polygon lines at same time, otherwise integer
     * rounding means that end points of split line segments don't
     * always fall on edges of the polygon!
     *
     * @TODO: fix it!
     *
     * @TODO: optimization? --- private method to use internally for already-split lines
     */
    public boolean contains(LineInt l) {

        // POSSIBLE CONDITIONS:
        // - outside bounding box
        // - outside polygon
        // - one outside, one inside
        // - one outside, one on vertex
        // - one outside, one on edge
        // -
        // - line is duplicate of polygon edge
        // - line is section of polygon edge
        // -
        // - line inside, both ends on vertex/edge
        // - line outside, both ends on vertex/edge

        // OPTIMIZATION: either point outside of bounding box?
        if (!boundingBox().contains(l.start()) ||
            !boundingBox().contains(l.end()))
            return false;

        // either point outside of polygon?
        boolean containsStart = contains(l.start());
        boolean containsEnd = contains(l.end());
        if (!containsStart || !containsEnd)
            return false;

        // split line on intersections
        LineInt[] parts = splitLineAtIntersections(l);

        //
        if (parts.length < 2) {
            // line did not intersect at all...
            // ... if both ends are fully inside...
            if (contains(parts[0].start(), false))
                return true;
        }

        // fail if any segment not contained
        for (LineInt segment : parts)
            if (!containsSegment(segment))
                return false;

        // must be inside
        return true;
    }

    /**
     * Used by contains(LineInt)...
     *
     * WARNING: DON'T USE UNLESS YOU KNOW WHAT YOU'RE DOING
     */
    private boolean containsSegment(LineInt l) {

        // segment exactly matches, or is section of a polygon line
        for (LineInt e : fetchEdges())
            if (l.isSectionOf(e))
                return true;

        // either end inside (not on perimeter)
        if (contains(l.start(), false) || contains(l.end(), false))
            return true;

        // neither end inside...
        // ... both ends must lie on edges...
        // ... (SEE DISCLAIMERS - probably fail, unless line and poly were split together)
        for (LineInt edge : fetchEdges()) {

            // TODO: test for vertices first...

            if (edge.contains(l.start())) {
                if (edge.isRelativeLeft(l.end()))
                    return true;
            }
        }



        // TODO: line between two vertices which is outside of poly




        // // test each line segment
        // for (LineInt segment : parts) {
        //     boolean startMatch = hasVertex(segment.start());
        //     boolean endMatch = hasVertex(segment.end());
        //     if (startMatch && endMatch) return true;
        // }




        // if (parts.length < 2) return true;



        return false;
    }

    // private boolean hasVertex(PointInt p) {
    //     for (PointInt v : vertices)
    //         if (v.equals(p))
    //             return true;
    //     return false;
    // }



    /*------------------------ TRANSFORMATIONS -------------------------*/

    public PolyInt translate(int x, int y) {
        PointInt[] nv = new PointInt[numVertices()];
        for (int i = 0; i < numVertices(); i++)
            nv[i] = vertex(i).sum(x, y);
        return new PolyInt(nv);
    }

    public PolyInt reverseVertexOrder() {
        PointInt[] newPoints = new PointInt[numVertices()];
        for (int i = 0; i < numVertices(); i++)
            newPoints[numVertices() - 1 - i] = vertex(i);
        return new PolyInt(newPoints);
    }

    /**
     * Clip the vertex at specified index and return a new polygon.
     */
    public PolyInt clipVertex(int index) {
        PointInt[] newPoints = new PointInt[numVertices() - 1];
        for (int i = 0; i < newPoints.length; i++) {
            int ii = (i < index ? i : i + 1);
            newPoints[i] = vertex(ii);
        }
        return new PolyInt(newPoints);
    }

    public PolyInt addVertexAfter(int index, PointInt p) {
        PointInt[] newPoints = new PointInt[numVertices() + 1];
        for (int i = 0; i < newPoints.length; i++) {
            if (i == index + 1) {
                // insert new vertex
                newPoints[i] = p;
            } else {
                // copy existing vertex
                int ii = (i > index + 1 ? i - 1 : i);
                newPoints[i] = vertex(ii);
            }
        }
        return new PolyInt(newPoints);
    }

    public PolyInt setVertex(int index, int x, int y) {
        PointInt[] newPoints = new PointInt[numVertices()];
        for (int i = 0; i < numVertices(); i++) {
            if (i == index) {
                newPoints[i] = new PointInt(x, y);
            } else {
                newPoints[i] = vertex(i);
            }
        }
        return new PolyInt(newPoints);
    }

    public PolyInt reflectX(int xMid) {
        return reflect(PointInt::reflectX, xMid);
    }

    public PolyInt reflectY(int yMid) {
        return reflect(PointInt::reflectY, yMid);
    }

    private PolyInt reflect(BiFunction<PointInt, Integer, PointInt> func, int mid) {
        PointInt[] vs = new PointInt[numVertices()];
        for (int n = 0; n < numVertices(); n++)
            vs[n] = func.apply(vertex(n), mid);
        return new PolyInt(vs);
    }

    public PolyInt rotate90(PointInt centre) {
        PointInt[] vs = new PointInt[numVertices()];
        for (int n = 0; n < numVertices(); n++)
            vs[n] = vertex(n).rotate90(centre);
        return new PolyInt(vs);
    }





    /*---------------------------- GEOMETRY ----------------------------*/

    public WindingOrder windingOrder() {
        if (winding == null) {
            if (nearlyEquals(sumOfAngles(), Geom2D.FULL_TURN))
                winding = WindingOrder.COUNTER_CLOCKWISE;
            else if (nearlyEquals(sumOfAngles(), -Geom2D.FULL_TURN))
                winding = WindingOrder.CLOCKWISE;
            else
                winding = WindingOrder.INVALID;
        }
        return winding;
    }

    private boolean nearlyEquals(double a, double b) {
        double epsilon = 0.0000000001;
        return a >= b - epsilon &&
               a <= b + epsilon;
    }

    public boolean windingIsCCW() {
        return windingOrder() == WindingOrder.COUNTER_CLOCKWISE;
    }

    /**
     * @return True, if all angles are convex.
     */
    public boolean isConvex() {
        if (convexP == null) {
            for (int i = 0; i < numVertices(); i++) {
                int iPrev = (i < 1 ? numVertices() -1 : i - 1);
                int iNext = (i < numVertices() - 1 ? i + 1 : 0);
                double angle = Geom2D.angleTurned(vertex(iPrev),
                                                   vertex(i),
                                                   vertex(iNext));
                if (angle <= 0) {
                    convexP = false;
                    return convexP;
                }
            }
            convexP = true;
        }
        return convexP;
    }

    /**
     * @return The sum of angles, in radians.
     */
    public double sumOfAngles() {
        if (sumAngles == null) {
            double sum = 0;
            for (int i = 0; i < numVertices(); i++) {
                int iPrev = (i < 1 ? numVertices() -1 : i - 1);
                int iNext = (i < numVertices() - 1 ? i + 1 : 0);
                sum += Geom2D.angleTurned(vertex(iPrev),
                                          vertex(i),
                                          vertex(iNext));
            }
            sumAngles = sum;
        }
        return sumAngles;
    }



    /*------------------------- TRIANGULATION --------------------------*/

    public TriInt[] getTriangulation() {
        if (isConvex()) return getTriangulationSimpleConvex();
        return getTriangulationEarClipping();
    }

    public TriInt[] getTriangulationSimpleConvex() {
        // System.out.println("... using getTriangulationSimpleConvex()");
        TriInt[] out = new TriInt[numVertices() - 2];
        for (int i = 1; i <= out.length; i++) {
            out[i - 1] = new TriInt(vertex(0),
                                    vertex(i),
                                    vertex(i + 1));
        }
        return out;
    }


    public TriInt[] getTriangulationEarClipping() {
        // System.out.println("... using getTriangulationEarClipping()");
        // System.out.println("... using ear-clipping...");
        ArrayList<TriInt> triangles = new ArrayList<>();
        // triangles = nextEarClipping(this, triangles);
        nextEarClipping(this, triangles);
        return triangles.toArray(new TriInt[triangles.size()]);
    }

    // private ArrayList<TriInt> nextEarClipping(PolyInt poly, ArrayList<TriInt> triangles) {
    private void nextEarClipping(PolyInt poly, ArrayList<TriInt> triangles) {
        // find a safe ear to clip...
        int i = 0;
        for (i = 0; i < poly.numVertices() + 1; i++) {
            if (!poly.intersectsExcludeVertices(new LineInt(poly.vertex(poly.wrapIndex(i + 1)),
                                                            poly.vertex(poly.wrapIndex(i - 1))))) {
                if (Geom2DInt.isCCW(poly.vertex(poly.wrapIndex(i - 1)),
                                    poly.vertex(poly.wrapIndex(i)),
                                    poly.vertex(poly.wrapIndex(i + 1)))) {

                    // potential line does not intersect any others...
                    // ... stick with this index
                    break;
                }
            }
        }
        triangles.add(getEar(poly, poly.wrapIndex(i)));
        //
        if (poly.numVertices() > 3) {
            nextEarClipping(poly.clipVertex(poly.wrapIndex(i)), triangles);
        }
        // return triangles;
    }

    private TriInt getEar(PolyInt poly, int index) {
        // System.out.println("... getEar() at " + poly.vertex(poly.wrapIndex(index)));

        return new TriInt(poly.vertex(poly.wrapIndex(index - 1)),
                          poly.vertex(poly.wrapIndex(index)),
                          poly.vertex(poly.wrapIndex(index + 1)));
    }



    /*-------------------- BOOLEAN TRANSFORMATIONS ---------------------*/

    /**
     * Subtracts the input polygon from this polygon.
     *
     */
    public PolyInt[] subtraction(PolyInt in) {

        // ... split lines at all intersections...
        List<PointInt> split1 = splitAtAllIntersections(this, in);
        List<PointInt> split2 = splitAtAllIntersections(in, this);

        // ... make new polygons for next step - integer conversion of
        // split-points may mean that they don't lie quite on the
        // original lines...
        PolyInt splitPoly1 = new PolyInt(split1.toArray(new PointInt[split1.size()]));
        PolyInt splitPoly2 = new PolyInt(split2.toArray(new PointInt[split2.size()]));

        // LineInt[] splitLines1 = toLines(split1);
        // LineInt[] splitLines2 = toLines(split2);

        // ... traverse outline, switching at each intersection...
        LineInt[][] sort1 = sortLinesOutsideOrIn(splitPoly1.getEdges(), splitPoly2);
        LineInt[][] sort2 = sortLinesOutsideOrIn(splitPoly2.getEdges(), splitPoly1);

        // ... assemble the results...
        PolyInt[] out1 = assembleLines(sort1[0], sort2[1]);
        PolyInt[] out2 = assembleLines(sort2[0], sort1[1]);
        return out1;



        // ArrayList<LineInt> subLines = new ArrayList<>();
        // for (LineInt thisLine : getLines()) {
        //     boolean ok = true;
        //     for (LineInt thatLine : in.getLines()) {
        //         if (thisLine.intersectsLineSegment(thatLine, true))
        //             ok = false;
        //     }
        //     if (ok) subLines.add(thisLine);
        // }

        // PointInt[] subPoints = new PointInt[subLines.size()];
        // for (int i = 0; i < subLines.size(); i++)
        //     subPoints[i] = subLines.get(i).start();
        // return new PolyInt[] { new PolyInt(subPoints) };
    }

    public PolyInt[] intersection(PolyInt in) {
        return new PolyInt[] {};
    }

    public PolyInt[] union(PolyInt in) {
        return new PolyInt[] {};
    }




    private static LineInt[] toLines(List<PointInt> points) {
        LineInt[] lines = new LineInt[points.size()];
        for (int i = 0; i < points.size(); i++) {
            int next = (i < points.size() - 1 ? i + 1 : 0);
            lines[i] = new LineInt(points.get(i), points.get(next));
        }
        return lines;
    }

    private static List<PointInt> splitAtAllIntersections(PolyInt poly1, PolyInt poly2) {
        ArrayList<PointInt> splitPoints = new ArrayList<>();
        for (LineInt ln : poly1.getEdges())
            for (PointInt p : splitAtAllIntersections(ln, poly2))
                splitPoints.add(p);
        return splitPoints;
    }

    /**
     * Returns a set of points sorted in order of the shortest
     * distance from the start point of the line.
     *
     * Start point of the line is included. End point is not included
     * because it will be included in the next line.
     *
     * If there are no intersections, just the start point will be returned.
     */
    private static Set<PointInt> splitAtAllIntersections(LineInt ln, PolyInt poly) {
        Set<PointInt> splitPoints = new TreeSet<>(ln.start().distanceComparator());
        splitPoints.add(ln.start());
        for (LineInt pLine : poly.getEdges()) {
            if (ln.intersectsLineSegment(pLine)) {
                try {
                    PointInt ip = ln.intersectionInt(pLine);
                    // don't add end point, it will be added as start point of next line
                    if (!ip.equals(ln.end())) {
                        splitPoints.add(ip);
                    }
                } catch (Geom2D.LinesParallelException e) {
                    // handle special case of parallel touching lines...

                }
            }
        }
        return splitPoints;
    }

    // private static LineInt[] splitLineAtIntersections(LineInt ln, PolyInt poly) {
    //     Set<PointInt> startPoints = splitAtAllIntersections(ln, poly);
    //     if (startPoints.size() == 1) return new LineInt[] { ln };
    //     // build output array
    //     LineInt[] lines = new LineInt[startPoints.size()];
    //     int i = 0;
    //     PointInt pStart = null;
    //     for (PointInt p : startPoints) {
    //         if (pStart == null) {
    //             pStart = p;
    //         } else {
    //             lines[i] = new LineInt(pStart, p);
    //             pStart = p;
    //             i++;
    //         }
    //     }
    //     lines[i] = new LineInt(pStart, ln.end());
    //     return lines;
    // }

    public LineInt[] splitLineAtIntersections(LineInt ln) {
        Set<PointInt> startPoints = splitAtAllIntersections(ln, this);
        if (startPoints.size() == 1) return new LineInt[] { ln };
        // build output array
        LineInt[] lines = new LineInt[startPoints.size()];
        int i = 0;
        PointInt pStart = null;
        for (PointInt p : startPoints) {
            if (pStart == null) {
                pStart = p;
            } else {
                lines[i] = new LineInt(pStart, p);
                pStart = p;
                i++;
            }
        }
        lines[i] = new LineInt(pStart, ln.end());
        return lines;
    }

    private LineInt[][] sortLinesOutsideOrIn(LineInt[] lines, PolyInt poly) {
        List<LineInt> outside = new ArrayList<>();
        List<LineInt> inside = new ArrayList<>();
        for (LineInt l : lines)
            if (poly.contains(l))
                inside.add(l.reverse());
            else
                outside.add(l);
        return new LineInt[][] { outside.toArray(new LineInt[outside.size()]),
                                 inside.toArray(new LineInt[inside.size()]) };
    }

    private PolyInt[] assembleLines(LineInt[] primary, LineInt[] reversed) {
        List<LineInt> lines = new ArrayList<>();
        for (LineInt l : primary) lines.add(l);
        for (LineInt l : reversed) lines.add(l);
        List<LineInt> progress = new ArrayList<>();
        List<PolyInt> polygons = new ArrayList<>();

        // repeat till all lines are used
        while (lines.size() > 0) {
            // remove first line and put in progress
            progress.add(lines.remove(0));

        }
        return polygons.toArray(new PolyInt[polygons.size()]);
    }



    /*----------------- BOOLEAN-TRANSFORMATION OBJECT ------------------*/

    /**
     * Performs the four possible boolean operations on a pair of polygons and then stores the results.
     *
     * Constructor performs the following four operations on the polygons A and B:
     * - union of A and B
     * - intersection of A and B
     * - subtraction of B from A
     * - subtraction of A from B
     *
     * WARNING! inputs must be WELL FORMED polygons...
     */
    public static class BooleanTransformation {

        private PolyInt[] subtraction1 = null;
        private PolyInt[] subtraction2 = null;
        private PolyInt[] intersection = null;
        private PolyInt[] union = null;

        public BooleanTransformation(PolyInt poly1, PolyInt poly2) {
            ArrayList<LineInt> sub1Lines = new ArrayList<>();
            ArrayList<LineInt> sub2Lines = new ArrayList<>();
            ArrayList<LineInt> interLines = new ArrayList<>();
            ArrayList<LineInt> unionLines = new ArrayList<>();

            // split lines at intersections
            List<PointInt> split1 = splitAtAllIntersections(poly1, poly2);
            List<PointInt> split2 = splitAtAllIntersections(poly2, poly1);

            // ... make new polygons for next step - integer conversion of
            // split-points may mean that they don't lie quite on the
            // original lines...
            PolyInt splitPoly1 = new PolyInt(split1.toArray(new PointInt[split1.size()]));
            PolyInt splitPoly2 = new PolyInt(split2.toArray(new PointInt[split2.size()]));




            Traverser primary   = new Traverser(splitPoly1);
            Traverser secondary = new Traverser(splitPoly2);

            // ... find an outside point to start on...
            // TODO: what if no points are outside of secondary shape!
            for (int i = 0; i < primary.poly.numVertices(); i++) {
                if (!splitPoly2.contains(primary.vertex()))
                    break;
                else
                    primary.next();
            }

            // store start point and add first line
            PointInt startPoint = primary.vertex();
            sub1Lines.add(primary.line());
            // primary.next();

            // traverse combined outlines, always turning left...
            boolean primaryLoop = true;
            traverseLoop:
            while (true) {

                // make match

                // is there any vertex in secondary polygon which matches the end of this line?
                boolean match = (primaryLoop ? secondary.iterateToMatch(primary.line().end())
                                             : primary.iterateToMatch(secondary.prevLine().start()));
                if (match) {
                    // switch to other traverser
                    primaryLoop = !primaryLoop;
                }



                if (primaryLoop) {

                    // add next line
                    primary.next();
                    sub1Lines.add(primary.line());
                    // have we finished
                    if (startPoint.equals(primary.line().end()))
                        break traverseLoop;

                } else {

                    // add previous line
                    secondary.prev();
                    sub1Lines.add(secondary.line().reverse());
                    // have we finished
                    if (startPoint.equals(secondary.line().start()))
                        break traverseLoop;

                }

            }


            PointInt[] sub1Vertices = new PointInt[sub1Lines.size()];
            for (int i = 0; i < sub1Vertices.length; i++)
                sub1Vertices[i] = sub1Lines.get(i).start();
            subtraction1 = new PolyInt[] { new PolyInt(sub1Vertices) };

            // subtraction1 = new PolyInt[] { splitPoly1 };
            subtraction2 = new PolyInt[] { splitPoly2 };
            intersection = new PolyInt[] {};
            union = new PolyInt[] {};
        }

        public PolyInt[] subtraction1() { return subtraction1; }
        public PolyInt[] subtraction2() { return subtraction2; }
        public PolyInt[] intersection() { return intersection; }
        public PolyInt[] union()        { return union; }

        private class Traverser {
            PolyInt poly;
            int index = 0;
            public Traverser(PolyInt poly) {
                this.poly = poly;
            }
            public PointInt vertex() { return poly.vertex(index); }
            public LineInt line() { return poly.edge(index); }
            public LineInt prevLine() { return poly.edge(poly.wrapIndex(index - 1)); }
            public LineInt nextLine() { return poly.edge(poly.wrapIndex(index + 1)); }
            public LineInt prev() { incr(-1); return line(); }
            public LineInt next() { incr(1); return line(); }
            protected void incr(int amt) {
                index += amt;
                validateIndex();
            }
            private void validateIndex() {
                if (index >= poly.numVertices()) index = 0;
                if (index < 0) index = poly.numVertices() - 1;
            }

            /**
             * Iterates through vertices and stops if a match is found.
             * @return True, if a match was found. False otherwise.
             */
            public boolean iterateToMatch(PointInt p) {
                for (int i = 0; i < poly.numVertices(); i++) {
                    next();
                    if (vertex().equals(p))
                        return true;
                }
                return false;
            }
        }

    }

}
