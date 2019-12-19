package info.bschambers.geom;

/**
 * Static methods for 2D geometry.
 */
public final class Geom2D {

    private Geom2D() {}

    public static final double QUARTER_TURN = Math.PI * 0.5;
    public static final double HALF_TURN = Math.PI;
    public static final double THREE_QUARTER_TURN = Math.PI * 1.5;
    public static final double FULL_TURN = Math.PI * 2.0;

    public static class LinesParallelException extends Exception {
	public LinesParallelException(LineInt line1, LineInt line2) {}
	public LinesParallelException(LineDbl line1, LineDbl line2) {}
    }



    /*--------------------------------- LINE LENGTH ----------------------------------*/

    // public static double lineLength(LineDbl l) {
    //     return lineLength(l.start(), l.end());
    // }

    // public static double lineLength(LineInt l) {
    //     return lineLength(l.start(), l.end());
    // }

    public static double lineLength(PointDbl p1, PointDbl p2) {
        return lineLength(p1.x(), p1.y(), p2.x(), p2.y());
    }

    public static double lineLength(PointInt p1, PointInt p2) {
        return lineLength(p1.x(), p1.y(), p2.x(), p2.y());
    }

    public static double lineLength(double x1, double y1, double x2, double y2) {
        // find length using Pythagoras' theorem
	double lenAdjacent = Math.abs(x2 - x1);
	double lenOpposite = Math.abs(y2 - y1);
	double squareHypotenuse = lenOpposite * lenOpposite + lenAdjacent * lenAdjacent;
	double lenHypotenuse = Math.sqrt(squareHypotenuse);
	return lenHypotenuse;
    }



    /*---------------------------------- LINE ANGLE ----------------------------------*/

    /**
     * @return The angle ABC.
     */
    public static double angle(PointInt a, PointInt b, PointInt c) {
        return angle(a.toDouble(), b.toDouble(), c.toDouble());
    }

    public static double angle(PointDbl a, PointDbl b, PointDbl c) {
	double angleBA = lineAngle(b,a);
	double angleBC = lineAngle(b,c);
	if (angleBC < angleBA) angleBC += (Math.PI * 2);
	return angleBC - angleBA;
    }

    public static double angleTurned(PointInt a, PointInt b, PointInt c) {
        return angleTurned(a.toDouble(), b.toDouble(), c.toDouble());
    }

    /**
     * @return The angle of the turn from path of line AB to line BC.
     */
    public static double angleTurned(PointDbl a, PointDbl b, PointDbl c) {
        double angleAB = lineAngle(a,b);
        double angleBC = lineAngle(b,c);
        boolean onLeftSide = onRelativeLeftSide(new LineDbl(a,b), c);
        if (onLeftSide && angleAB < angleBC) angleAB += (Math.PI * 2);
        if (!onLeftSide && angleAB > angleBC) angleBC += (Math.PI * 2);
        return angleAB - angleBC;
    }

    // /**
    //  * For a given line segment, finds the linear equation for it's line in
    //  * slope-intercept form...
    //  * <br/>... the linear equation can be understood like this:
    //  * <br/>Y = (slope * X) + intercept
    //  *
    //  * @return (slope, intercept)
    //  * <br/>NOTE: For a vertical line, returns (INFINITY, INFINITY).
    //  * @author Ben Chambers
    //  * @bsc.date Sat Jun 28 14:43:45 2014
    //  */
    // public static double[] slopeIntercept(LineInt segment) {
    //     double slope = (segment.yDist() == 0 ? 0 : segment.yDist() / segment.xDist());
    //     double intercept = segment.start().y() - segment.start().x() * slope;
    //     return new double[] { slope, intercept };
    // }

    // public static double lineAngle(LineDbl ln) {
    //     return lineAngle(ln.start(), ln.end());
    // }

    // public static double lineAngle(LineInt ln) {
    //     return lineAngle(ln.toDouble());
    // }

    public static double lineAngle(PointInt start, PointInt end) {
        return lineAngle(start.toDouble(), end.toDouble());
    }

    /*
     * STEP 1: Test for zero-angle condition - if true, throw a RuntimeException!
     * STEP 2: Test for the four special orthgonal cases - if true, return angle.
     * STEP 3: Calculate angle using math.abs values.
     * STEP 4: Add number of quarter turns based direction of line (see four cases
     * illustrated in the diagram below.
     *
     *
     *	  Q = One quarter turn (PI / 2)
     *
     *                    ORTHOGONAL
     *                    x=0
     *                    y=-
     *			   |
     *			   |
     *	       x=-	   |     x=+
     *	       y=-	   |     y=-
     *	      (Add Q*2)    |    (Add Q)
     *		  	   |
     *   ORTHOGONAL	   |                  ORTHOGONAL
     *   x=-  -------------+----------------- x=+
     *   y=0		   |                  y=0
     *	       	       	   |
     *	       x=-	   |    x=+
     *	       y=+	   |    y=+
     *	      (Add Q*3)    |
     *			   |
     *			   |
     *                    ORTHOGONAL
     *                    x=0
     *                    y=+
     *
     */
    public static double lineAngle(PointDbl start, PointDbl end) {

	// get line length (using pythagoras)
	// ... square on the hypotenuse is equal to the squares on the other two sides...
        double xDist = xDist(start,end);
        double yDist = yDist(start,end);

	// TEST FOR DEGENERATE LINE CONDITION
	if (xDist == 0 && yDist == 0) throw new IllegalArgumentException("degenerate line");

	// TEST FOR ORTHOGONAL SPECIAL CASES
	if (xDist == 0)
	    return (yDist > 0 ? 0.0 : HALF_TURN);
	if (yDist == 0)
	    return (xDist > 0 ? QUARTER_TURN : THREE_QUARTER_TURN);

	// ALL OTHER CASES
	// ... get length of hypotenuse...
	// ... square of hypotenuse is equal to sum of other two squares...
	double lenAdjacent = Math.abs(xDist);
	double lenOpposite = Math.abs(yDist);
	double squareHypotenuse = lenOpposite * lenOpposite + lenAdjacent * lenAdjacent;
	double lenHypotenuse = Math.sqrt(squareHypotenuse);
	// get angle (using trigonometry)
	double angle = Math.asin(lenAdjacent/lenHypotenuse);

	// make quadrant adjustments
	if (xDist > 0 && yDist < 0) angle = 2 * QUARTER_TURN - angle;
	else if (xDist < 0 && yDist < 0) angle += QUARTER_TURN * 2;
	else if (xDist < 0 && yDist > 0) angle = 4 * QUARTER_TURN - angle;
	//
	return angle;
    }

    private static double xDist(PointDbl start, PointDbl end) { return end.x() - start.x(); }
    private static double yDist(PointDbl start, PointDbl end) { return end.y() - start.y(); }

    public static double[] lineEquation(LineInt l) {
        return lineEquation(l.toDouble());
    }

    /**
     * Returns the linear equation of this line in slope-intercept form...<br>
     * ... the linear equation can be understood like this:<br>
     * Y = (slope * X) + intercept
     * <p>
     * NOTE: ...
     *
     * @return An array of two elements [slope, intercept]
     */
    public static double[] lineEquation(LineDbl segment) {
	double slope = (segment.yDist() == 0 ? 0 : segment.yDist() / (segment.xDist() + 0.0));
	double intercept = segment.startY() - segment.startX() * slope;
	return new double[] { slope, intercept };
    }

    /**
     *  X and Y roles are reversed!
     */
    private static double[] lineEquationINVERSE(LineDbl segment) {
	double slope = (segment.xDist() == 0 ? 0 : segment.xDist() / segment.yDist());
	double intercept = segment.startX() - segment.startY() * slope;
	return new double[] { slope, intercept };
    }



    // public static class LinesParallelException extends Exception {
    //     public LinesParallelException(LineDbl line1, LineDbl line2) {}
    // }



    /*---------------------------- VERTEX TRANSFORMATIONS ----------------------------*/

    // /**
    //  * ... rotate a vertex around given centre point...
    //  *
    //  * @param amt Amount of rotation, in radians(?)
    //  *
    //  */
    // private static PointDbl rotateVertex(PointDbl vertex, double amt, PointDbl centre) {
    //     if (amt == 0) return vertex.copy();
    //     // PointDbl centre = new PointDbl();
    //     if (vertex.equals(centre)) return centre.copy();
    //     // get angle and length of line from centre point
    //     double angle = lineAngle(centre, vertex) + amt;
    //     double length = lineLength(centre, vertex);
    //     //
    //     return new PointDbl(centre.x + Math.sin(angle) * length,
    //     	      centre.y + Math.cos(angle) * length);
    // }

    /**
     * ... gets point on circumference of circle of given radius...
     *
     * @param angle Angle in radians(?)
     */
    public static PointDbl circlePoint(double angle, double radius) {
        return new PointDbl(Math.sin(angle) * radius,
        	      Math.cos(angle) * radius);
    }



    /*------------------------------ LINE INTERSECTION -------------------------------*/

    // public static PointDbl[] getIntersectionPoints(Shape s1, Shape s2) {
    //     ArrayList<PointDbl> out = new ArrayList<PointDbl>();
    //     for (LineDbl line1 : s1.getLines()) {
    //         for (LineDbl line2 : s2.getLines()) {
    //     	if (lineSegmentsIntersect(line1, line2)) {
    //     	    try {
    //     		out.add(lineIntersectionPoint(line1, line2));
    //     	    } catch (LinesParallelException e) {}
    //     	}
    //         }
    //     }
    //     PointDbl[] outArray = new PointDbl[out.size()];
    //     return out.toArray(outArray);
    // }

    // /**
    //  * SPECIAL CASE: one line is vertical
    //  * SPECIAL CASE: both lines are vertical --- lines are paralell (see below)
    //  * SPECIAL CASE: lines are parallel -------- THROW EXCEPTION
    //  *
    //  * @bsc.date Sat Jul 19 00:20:59 2014
    //  * @throws LinesParallelException
    //  *
    //  * @bsc.todo UNIT TESTING
    //  * @bsc.todo UNIT TESTING: for horiz/vert cases, intersection point must lie EXACTLY on the horizontal/vertical.
    //  */
    // public static PointDbl lineIntersectionPoint(LineDbl segment1, LineDbl segment2)
    //     throws LinesParallelException {
    //     // SPECIAL CASE: both lines are vertical OR both horizontal
    //     if ((segment1.isVertical() && segment2.isVertical()) ||
    //         (segment1.isHorizontal() && segment2.isHorizontal()))
    //         throw new LinesParallelException(segment1,segment2);
    //     // SPECIAL CASE: one line is vertical
    //     if (segment1.isVertical())
    //         return getPointForXValue(segment2, segment1.startX());
    //     if (segment2.isVertical())
    //         return getPointForXValue(segment1, segment2.startX());
    //     // SPECIAL CASE: one line is horizontal
    //     if (segment1.isHorizontal())
    //         return getPointForYValue(segment2, segment1.startY());
    //     if (segment2.isHorizontal())
    //         return getPointForYValue(segment1, segment2.startY());

    //     // SPECIAL CASE: lines are the same
    //     if (segment1.equalsIgnorePolarity(segment2))
    //         throw new LinesParallelException(segment1,segment2);

    //     // SPECIAL CASE: one shared vertex
    //     if (segment1.start().equals(segment2.start()) ||
    //         segment1.start().equals(segment2.end()))
    //         return segment1.start();
    //     // SPECIAL CASE: one shared vertex
    //     if (segment1.end().equals(segment2.start()) ||
    //         segment1.end().equals(segment2.end()))
    //         return segment1.end();

    //     // SPECIAL CASE: lines are parallel
    //     if (linesParallel(segment1, segment2))
    //         throw new LinesParallelException(segment1,segment2);
    //     // ... else...
    //     return lineIntersectionPoint(lineEquation(segment1), lineEquation(segment2));
    // }

    // public static PointDbl lineIntersectionPoint(double[] slopeIntercept1, double[] slopeIntercept2) {
    // 	return lineIntersectionPoint(slopeIntercept1[0], slopeIntercept1[1],
    // 				     slopeIntercept2[0], slopeIntercept2[1]);
    // }

    /**
     * Finds the intersection point of two lines, by their slope/intercept values.
     */
    public static PointDbl slopeInterceptIntersection(double slope1, double intercept1,
                                                      double slope2, double intercept2) {
        // FIND VALUE OF X
        // ((slope1 * x) + intercept1) == ((slope2 * x) + intercept2)
        // ...
        // reduce intercept to 0 on first side of equation
        double intercept = intercept2 - intercept1;
        // reduce slope to 0 on second side
        double slope = slope1 - slope2;
        // make slope equal 1.0
        double divisor = 1.0 / slope;
        double x = intercept * divisor;

        double y = (slope1 * x) + intercept1;
        return new PointDbl(x, y);
    }

    public static PointDbl getPointForXValue(LineDbl lineSegment, double x) {
        return new PointDbl(x, getYForXValue(lineSegment, x));
    }

    public static double getYForXValue(LineDbl lineSegment, double x) {
        double[] slopeIntercept = lineEquation(lineSegment);
        double slope = slopeIntercept[0];
        double intercept = slopeIntercept[1];
        return (slope * x) + intercept;
    }

    public static PointDbl getPointForYValue(LineDbl lineSegment, double y) {
        return new PointDbl(getXForYValue(lineSegment, y), y);
    }

    public static double getXForYValue(LineDbl lineSegment, double y) {
        double[] slopeIntercept = lineEquationINVERSE(lineSegment);
        double slope = slopeIntercept[0];
        double intercept = slopeIntercept[1];
        return (slope * y) + intercept;
    }



    /**
     * WARNING: This works for lines with integer-only co-ordinates but due to
     * the lack of accuracy in floating point arithmetic, this cannot be relied
     * on for lines with non-integer co-ordinates. It works for all horizontal
     * or vertical lines including with floating-point co-ords.
     *
     * @return true if the two line segments are parallel.
     *
     * @bsc.todo Perhaps use BigDecimal for proper precision...
     */
    public static boolean linesParallel(LineDbl line1, LineDbl line2) {
        // special vertical cases
        if (line1.isVertical() && line2.isVertical()) { return true; }
        if (line1.isVertical() || line2.isVertical()) { return false; }
        // get slope-intercept linear equations to calculate y
        double slope1 = lineEquation(line1)[0];
        double slope2 = lineEquation(line2)[0];
        return slope1 == slope2;
    }



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
    // public static boolean lineSegmentsIntersect(LineDbl segment1, LineDbl segment2) {
    //     return lineSegmentsIntersect(segment1, segment2, true);
    // }
    // /**
    //  * @bsc.todo UNIT TESTS
    //  */
    // public static boolean lineSegmentsIntersectExcludeTouching(LineDbl segment1, LineDbl segment2) {
    //     return lineSegmentsIntersect(segment1, segment2, false);
    // }
    // public static boolean lineSegmentsIntersect(LineDbl segment1, LineDbl segment2, boolean includeTouching) {
    //     PointDbl intersection = null;
    //     try {
    //         intersection = lineIntersectionPoint(segment1, segment2);
    //     } catch (LinesParallelException e) {
    //         return false;
    //     }
    //     /*
    //      * SPECIAL CASE: horiz or vertical AND exclude-touching
    //      * Can't use Box2D in this case because the box will have zero area!
    //      */
    //     // if (!includeTouching) {
    //     //     if (segment1.isPerpendicular() ||
    //     // 	segment2.isPerpendicular()) {

    //     //     }
    //     // }
    //     // //
    //     // Box2D box1 = getBoundingBox(segment1);
    //     // Box2D box2 = getBoundingBox(segment2);
    //     // boolean contains1 = box1.contains(intersection, includeTouching);
    //     // boolean contains2 = box2.contains(intersection, includeTouching);
    //     // return box1.contains(intersection, includeTouching) &&
    //     //     box2.contains(intersection, includeTouching);
    //     // boolean contains1 = lineBoundsContains(segment1, intersection, includeTouching);
    //     // boolean contains1 = lineBoundsContains(segment2, intersection, includeTouching);
    //     // return contains1 && contains2;
    //     return lineBoundsContain(segment1, intersection, includeTouching)
    //         && lineBoundsContain(segment2, intersection, includeTouching);
    // }
    // private static boolean lineBoundsContain(LineDbl line, PointDbl p, boolean includeTouching) {
    //     Box2D box = getBoundingBox(line);
    //     boolean contains = box.contains(p, includeTouching);
    //     // if (line.isPerpendicular()) {}
    //     if (!contains && !includeTouching) {
    //         if (line.isPerpendicular()) {
    //     	if (box.contains(p, true)) {
    //     	    if (line.isHorizontal()) {
    //     		if (p.x > box.left() && p.x < box.right()) contains = true;
    //     	    } else if (line.isVertical()) {
    //     		if (p.y > box.bottom() && p.y < box.top()) contains = true;
    //     	    }
    //     	}
    //         }
    //     }
    //     return contains;
    // }



    // private static boolean anyIntersections(LineDbl line, Shape shape) {
    //     for (LineDbl line2 : shape.getLines())
    //         if (!anySharedEndPoints(line, line2))
    //     	if (lineSegmentsIntersect(line, line2))
    //     	    return true;
    //     return false;
    // }



    /*--------------------- FIND RELATIVE LEFT-HAND SIDE OF LINE ---------------------*/

    /**
     * TODO: IMPLEMENT FULLY...
     */
    public static boolean pointIsOnLine(LineDbl line, PointDbl p) {
	// SPECIAL PERPENDICULAR CASES
	if (line.isVertical() &&
	    p.x() == line.startX()) return true;
	if (line.isHorizontal() &&
	    p.y() == line.startY()) return true;
	// else
	return false;
    }

    /**
     * TODO: IMPLEMENT FULLY...
     */
    public static boolean pointIsOnLineSegment(LineDbl line, PointDbl p) {
	return false;
    }

    public static boolean onRelativeLeftSide(LineDbl line, PointDbl p) {
	// SPECIAL CASE: point is on line...
	if (pointIsOnLine(line, p)) return false;
	// SPECIAL CASE: vertical line...
	if (line.isVertical()) {
	    if (line.startY() < line.endY()) { // pointing upwards
		return p.x() < line.startX();
	    } else { // pointing downwards
		return p.x() > line.startX();
	    }
	}
	// ... ALL OTHER LINES
	boolean out = p.y() > getPointForXValue(line, p.x()).y();
	if (line.endX() > line.startX()) return out;
	return !out;
    }

}
