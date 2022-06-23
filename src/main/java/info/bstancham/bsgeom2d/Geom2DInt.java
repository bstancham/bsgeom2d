package info.bstancham.bsgeom2d;

import java.util.Set;
import java.util.TreeSet;

/**
 * Static methods for 2D geometry.
 * <p>
 * Unlike Geom2D, all methods here return integer values.
 */
public class Geom2DInt {

    /**
     * Return mid point, rounded down to integer co-ordinates.
     *
     * TODO: unit tests
     */
    public static PointInt midPoint(LineInt l) {
        return midPoint(l.start(), l.end());
    }

    /**
     * Return mid point, rounded down to integer co-ordinates.
     *
     * TODO: unit tests
     */
    public static PointInt midPoint(PointInt p1, PointInt p2) {
        return new PointInt(minX(p1, p2) + (absDistX(p1, p2) / 2),
                            minY(p1, p2) + (absDistY(p1, p2) / 2));
    }

    /** @return The lowest x co-ordinate. */
    public static int minX(PointInt p1, PointInt p2) {
        if (p2.x() < p1.x()) return p2.x();
        return p1.x();
    }

    /** @return The lowest y co-ordinate. */
    public static int minY(PointInt p1, PointInt p2) {
        if (p2.y() < p1.y()) return p2.y();
        return p1.y();
    }

    /**
     * @return The distance between start.x() and end.x(). Always a
     * positive number.
     */
    public static int absDistX(PointInt start, PointInt end) {
        return Math.abs(end.x() - start.x());
    }

    /**
     * @return The distance between start.y() and end.y(). Always a
     * positive number.
     */
    public static int absDistY(PointInt start, PointInt end) {
        return Math.abs(end.y() - start.y());
    }

    /**
     * @return The distance from start.x() to end.x(). Will be
     * negative if start.x() is greater than end.x().
     */
    public static int distX(PointInt start, PointInt end) {
        return end.x() - start.x();
    }

    /**
     * @return The distance from start.y() to end.y(). Will be
     * negative if start.y() is greater than end.y().
     */
    public static int distY(PointInt start, PointInt end) {
        return end.y() - start.y();
    }



    /*--------------------- FIND COLLINEAR POINTS ----------------------*/

    /**
     * Compare three or more points and return true if they are all
     * collinear.
     *
     * NOTE: A single unique point, or any two unique points will
     * return true.
     *
     * TODO: unit testing
     */
    public static boolean collinear(PointInt p1, PointInt p2, PointInt p3,
                                    PointInt ... rest) {
        // compare only unique points
        Set<PointInt> points = new TreeSet<>();
        points.add(p1);
        points.add(p2);
        points.add(p3);
        for (PointInt pr : rest) points.add(pr);

        // double slope = p1.slopeTo(p2);
        // if (slope != p1.slopeTo(p3)) return false;
        // for (PointInt pr : rest)
        //     if (slope != p1.slopeTo(pr))
        //         return false;

        PointInt first = null;
        PointInt second = null;
        Double slope = null;

        for (PointInt p : points) {
            if (first == null) {
                first = p;
            } else if (second == null) {
                second = p;
                slope = first.slopeTo(second);
            } else {
                if (slope != first.slopeTo(p))
                    return false;
            }
        }

        return true;
    }



    /*------------------- FIND DIRECTION OF WINDING --------------------*/

    public static boolean isCCW(PointInt a, PointInt b, PointInt c) {
        return ccw(a, b, c) > 0;
    }

    /**
     * Test whether the angle ABC is counter-clockwise or not, or if
     * the three points are collinear.
     *
     * @return 0 if points are collinear.<b>
     *         Positive integer if angle is counter-clockwise.<b>
     *         Negative integer if angle is clockwise.
     *
     * TODO: unit tests
     */
    public static int ccw(PointInt a, PointInt b, PointInt c) {
        double area2 = ((b.x() - a.x()) * (c.y()-a.y())) - ((b.y() - a.y()) * (c.x() - a.x()));
        if (area2 < 0) return -1; // clockwise
        if (area2 > 0) return +1; // counter-clockwise
        else           return  0; // collinear
    }

    // /**
    //  * @bsc.todo IMPLEMENT FULLY...
    //  */
    // public static boolean pointIsCollinear(LineInt line, Pt p) {
    //     // SPECIAL PERPENDICULAR CASES
    //     if (line.isVertical() &&
    //         p.x == line.start.x) return true;
    //     if (line.isHorizontal() &&
    //         p.y == line.start.y) return true;
    //     // else
    //     return false;
    // }

    // /**
    //  * @bsc.todo IMPLEMENT FULLY...
    //  */
    // public static boolean pointIsOnLineSegment(LineInt line, Pt p) {
    //     return false;
    // }

    /**
     *
     * @throws IllegalArgumentException if the input line is degenerate.
     */
    public static boolean onRelativeLeftSide(LineInt line, PointInt p) {
        if (line.isDegenerate())
            throw new IllegalArgumentException("line is degenerate");
	// SPECIAL CASE: point is on line...
	// if (pointIsCollinear(line, p)) return false;
	// SPECIAL CASE: vertical line...
	if (line.isVertical()) {
	    if (line.startY() < line.endY()) {
                // pointing up
		return p.x() < line.startX();
	    } else {
                // pointing down
		return p.x() > line.startX();
	    }
	}
	// ... ALL OTHER LINES
	boolean out = p.y() > Geom2D.getPointForXValue(line.toDouble(), p.x()).y();
	if (line.endX() > line.startX()) return out;
	return !out;
    }

    // /**
    //  * @bsc.todo TEST THIS PROPERLY!
    //  */
    // public static boolean onRelativeRightSide(LineInt line, Pt p) {
    //     // SPECIAL CASE: point is on line...
    //     if (pointIsOnLine(line, p)) return false;
    //     // SPECIAL CASE: vertical line...
    //     if (line.isVertical()) {
    //         if (line.start.y < line.end.y) { // pointing upwards
    //     	return p.x > line.start.x;
    //         } else { // pointing downwards
    //     	return p.x < line.start.x;
    //         }
    //     }
    //     // ... ALL OTHER LINES
    //     boolean out = p.y < getPointForXValue(line, p.x).y;
    //     if (line.end.x > line.start.x) return out;
    //     return !out;
    // }

        // /**
    //  * @param includeTouching If true, lines who share one or more vertex
    //  * @return True, if the two line segments intersect.
    //  *
    //  * @bsc.todo DEAL WITH PARALLEL LINES BETTER?
    //  *
    //  * @bsc.todo SPECIAL CASE: lines parallel but also precisely on top...?
    //  *
    //  * @bsc.todo UNIT TESTS
    //  */
    // public static boolean lineSegmentsIntersect(Ln segment1, Ln segment2) {
    //     return lineSegmentsIntersect(segment1, segment2, true);
    // }

    /**
     * @bsc.todo UNIT TESTS
     */
    public static boolean lineSegmentsIntersectExcludeVertices(LineInt segment1, LineInt segment2) {
        return lineSegmentsIntersect(segment1, segment2, false);
    }

    public static boolean lineSegmentsIntersect(LineInt segment1, LineInt segment2, boolean includeVertices) {
        PointDbl intersection = null;
        try {
            // intersection = Geom2D.lineIntersectionPoint(segment1.toDouble(), segment2.toDouble());
            intersection = segment1.intersection(segment2);
        } catch (Geom2D.LinesParallelException e) {
            return false;
        }
        /*
         * SPECIAL CASE: horiz or vertical AND exclude-touching
         * Can't use Box2D in this case because the box will have zero area!
         */
        // if (!includeVertices) {
        //     if (segment1.isPerpendicular() ||
        // 	segment2.isPerpendicular()) {

        //     }
        // }
        // //
        // Box2D box1 = getBoundingBox(segment1);
        // Box2D box2 = getBoundingBox(segment2);
        // boolean contains1 = box1.contains(intersection, includeVertices);
        // boolean contains2 = box2.contains(intersection, includeVertices);
        // return box1.contains(intersection, includeVertices) &&
        //     box2.contains(intersection, includeVertices);
        // boolean contains1 = lineBoundsContains(segment1, intersection, includeVertices);
        // boolean contains1 = lineBoundsContains(segment2, intersection, includeVertices);
        // return contains1 && contains2;
        return lineBoundsContain(segment1, intersection, includeVertices)
            && lineBoundsContain(segment2, intersection, includeVertices);
    }

    private static boolean lineBoundsContain(LineInt line, PointDbl p, boolean includeVertices) {
        BoxInt box = line.boundingBox();
        boolean contains = box.contains(p, includeVertices);
        // if (line.isPerpendicular()) {}
        if (!contains && !includeVertices) {
            if (line.isPerpendicular()) {
        	if (box.contains(p, true)) {
        	    if (line.isHorizontal()) {
        		if (p.x() > box.left() && p.x() < box.right()) contains = true;
        	    } else if (line.isVertical()) {
        		if (p.y() > box.bottom() && p.y() < box.top()) contains = true;
        	    }
        	}
            }
        }
        return contains;
    }

    // private static boolean anyIntersections(Ln line, Shape shape) {
    //     for (Ln line2 : shape.getLines())
    //         if (!anySharedEndPoints(line, line2))
    //     	if (lineSegmentsIntersect(line, line2))
    //     	    return true;
    //     return false;
    // }

}
