package info.bschambers.bsgeom2d;

import info.bschambers.bsgeom2d.Geom2D.LinesParallelException;

/**
 * An immutable data structure representing a two-dimensional line
 * with integer coordinates.
 */
public final class LineInt {

    private final PointInt start;
    private final PointInt end;

    // values will be cached after they are calculated...
    // ... since LineInt is immutable, we needn't worry about these values
    // changing...
    private Double length = null;
    private Double slope = null;
    private Double intercept = null;
    private Double angle = null;
    private BoxInt bounds = null;

    public LineInt(int sx, int sy, int ex, int ey) {
        this(new PointInt(sx, sy), new PointInt(ex, ey));
    }
    public LineInt(PointInt start, PointInt end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "LineInt(" + start + ", " + end + ")";
    }



    /*--------------------------- ACCESSORS ----------------------------*/

    public PointInt start() { return start; }
    public PointInt end()   { return end; }

    public int startX()     { return start.x(); }
    public int startY()     { return start.y(); }

    public int endX()       { return end.x(); }
    public int endY()       { return end.y(); }

    /** May return a negative number. */
    public int xDist() { return end.x() - start.x(); }

    /** May return a negative number. */
    public int yDist() { return end.y() - start.y(); }



    /*--------------------------- COMPARISON ---------------------------*/

    @Override
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        LineInt that = (LineInt) y;
        if (!that.start.equals(this.start)) return false;
        if (!that.end.equals(this.end)) return false;
        return true;
    }

    public boolean equalsIgnorePolarity(LineInt that) {
        return (that.start.equals(this.start) && that.end.equals(this.end)) ||
            (that.start.equals(this.end) && that.end.equals(this.start));
    }

    public boolean contains(PointInt p) {
        if (p.equals(start) || p.equals(end)) return true;
        if (slope() == start.slopeTo(p) &&
            boundingBox().contains(p)) return true;
        return false;
    }

    public BoxInt boundingBox() {
        if (bounds == null) {
            // int left   = Math.min(startX(), endX());
            // int bottom = Math.min(startY(), endY());
            // int right  = Math.max(startX(), endX());
            // int top    = Math.max(startY(), endY());
            // bounds = new BoxInt(left, bottom, right, top);
            bounds = new BoxInt(Math.min(startX(), endX()),
                                Math.min(startY(), endY()),
                                Math.max(startX(), endX()),
                                Math.max(startY(), endY()));
        }
        return bounds;
    }

    /**
     * @return True, if this line is a section of, or is the same as
     * the input line.
     *
     * @TODO: unit tests
     */
    public boolean isSectionOf(LineInt ln) {
        // are both ends collinear?
        if (!ln.isCollinear(this.start()) ||
            !ln.isCollinear(this.end())) return false;
        // are both ends within bounds?
        if (!ln.lineBoundsContain(this.start().toDouble()) ||
            !ln.lineBoundsContain(this.end().toDouble())) return false;
        // must be a section
        return true;
    }



    /*------------------------ TRANSFORMATIONS -------------------------*/

    public LineDbl toDouble() {
        return new LineDbl(start.x(), start.y(), end.x(), end.y());
    }

    public LineInt reverse() {
        return new LineInt(end, start);
    }



    /*----------------------------- TESTS ------------------------------*/

    /**
     * @return True, if line is degenerate, i.e. if start and end point
     * are equal.
     */
    public boolean isDegenerate() { return start.equals(end); }

    /**
     * @return True, if line is vertical. False if line is not
     * vertical, or if line is degenerate.
     */
    public boolean isVertical() {
        return !isDegenerate() && startX() == endX();
    }

    /**
     * @return True, if line is horizontal. False if line is not
     * horizontal, or if line is degenerate.
     */
    public boolean isHorizontal() {
        return !isDegenerate() && startY() == endY();
    }

    public boolean isPerpendicular() {
        return isHorizontal() || isVertical();
    }

    /*------------------------ TRANSFORMATIONS -------------------------*/

    public LineInt translate(int x, int y) {
        return new LineInt(start.sum(x, y), end.sum(x, y));
    }



    /*---------------------------- GEOMETRY ----------------------------*/

    public double length() {
        if (length == null) length = Geom2D.lineLength(start, end);
        return length;
    }

    /**
     * @return Angle of the line in radians.
     */
    public double angle() {
        if (angle == null) angle = Geom2D.lineAngle(start, end);
        return angle;
    }

    /**
     * The slope part of the line equation:
     * Y = (slope * X) + intercept
     *
     * SPECIAL CASES...
     * ... SEE DOCUMENTATION for PointInt.slopeTo()...
     */
    public double slope() {
        if (slope == null) slope = start.slopeTo(end);
        return slope;
    }

    /**
     * The intercept part of the line equation:
     * Y = (slope * X) + intercept
     *
     * This is equivalent the Y co-ordinate of the point where the
     * line crosses the X axis (where X = 0).
     *
     *
     */
    public double intercept() {
        if (intercept == null) intercept = startY() - startX() * slope();
        return intercept;
    }

    // /**
    //  * TODO: optimize --- at present, Geom2D converts LineInt with toDouble()...
    //  */
    // private void cacheEquation() {
    //     double[] equation = Geom2D.lineEquation(this);
    //     slope = equation[0];
    //     intercept = equation[1];
    // }

    /**
     * Returns true, if point is collinear with start and end point of
     * this line. Equivalent to <code>Geom2DInt.collinear(start(),
     * end(), p)</code>.
     */
    public boolean isCollinear(PointInt p) {
        // return Geom2DInt.collinear(start, end, p);
        return Geom2DInt.ccw(start, end, p) == 0;
    }

    /**
     * Returns true, if point is on relative left-hand side of
     * line. Equivalent to <code>Geom2DInt.isCCW(start(), end()
     * p)</code>.
     */
    public boolean isRelativeLeft(PointInt p) {
        return Geom2DInt.ccw(start, end, p) > 0;
    }

    /**
     * Returns true, if point is on relative right-hand side of
     * line. Equivalent to <code>!Geom2DInt.isCCW(start(), end() p) &&
     * !Geom2DInt.collinear(start(), end(), p)</code>.
     */
    public boolean isRelativeRight(PointInt p) {
        return Geom2DInt.ccw(start, end, p) < 0;
    }

    public boolean isParallel(LineInt ln) {
        return this.slope() == ln.slope();
    }

    /**
     * Returns the intersection point of the two lines to the nearest
     * integer co-ordinates.
     *
     * TODO: test rounding
     */
    public PointInt intersectionInt(LineInt ln) throws LinesParallelException {
        return intersection(ln).toIntRounded();
    }

    public PointDbl intersection(LineInt ln) throws LinesParallelException {
        // SPECIAL CASE: lines are parallel
        if (this.isParallel(ln))
            throw new LinesParallelException(this, ln);

        // SPECIAL CASE: one line is vertical
        if (this.isVertical())
            return new PointDbl(this.startX(), ln.getYForXValue(this.startX()));
        if (ln.isVertical())
            return new PointDbl(ln.startX(), this.getYForXValue(ln.startX()));

        // // SPECIAL CASE: one line is horizontal
        // if (this.isHorizontal())
        //     return getPointForYValue(ln, this.startY());
        // if (ln.isHorizontal())
        //     return getPointForYValue(this, ln.startY());

        // FIND VALUE OF X
        // ((slope1 * x) + intercept1) == ((slope2 * x) + intercept2)
        // ...
        // reduce intercept to 0 on first side of equation
        double intercept = ln.intercept() - this.intercept();
        // reduce slope to 0 on second side
        double slope = this.slope() - ln.slope();
        // make slope equal 1.0
        double divisor = 1.0 / slope;
        double x = intercept * divisor;
        // double y = (this.slope() * x) + this.intercept();
        return new PointDbl(x, getYForXValue(x));
    }

    private double getYForXValue(double x) {
        return (slope() * x) + intercept();
    }

    // /**
    //  * Returns the intersection point of the two lines with
    //  * floating-point precision.
    //  *
    //  * @throws LinesParallelException if the input line is parallel
    //  * with this line.
    //  */
    // public PointDbl legacyIntersection(LineInt ln) throws LinesParallelException {
    //     // SPECIAL CASE: both lines are vertical OR both horizontal
    //     if ((this.isVertical()   && ln.isVertical()) ||
    //         (this.isHorizontal() && ln.isHorizontal()))
    //         throw new LinesParallelException(this, ln);

    //     // SPECIAL CASE: one line is vertical
    //     if (this.isVertical())
    //         return getPointForXValue(ln, this.startX());
    //     if (ln.isVertical())
    //         return getPointForXValue(this, ln.startX());
    //     // SPECIAL CASE: one line is horizontal
    //     if (this.isHorizontal())
    //         return getPointForYValue(ln, this.startY());
    //     if (ln.isHorizontal())
    //         return getPointForYValue(this, ln.startY());

    //     // SPECIAL CASE: lines are the same
    //     if (this.equalsIgnorePolarity(ln))
    //         throw new LinesParallelException(this. ln);

    //     // SPECIAL CASE: one shared vertex
    //     if (this.start().equals(ln.start()) ||
    //         this.start().equals(ln.end()))
    //         return this.start();
    //     // SPECIAL CASE: one shared vertex
    //     if (this.end().equals(ln.start()) ||
    //         this.end().equals(ln.end()))
    //         return this.end();

    //     // SPECIAL CASE: lines are parallel
    //     if (this.isParallel(ln))
    //         throw new LinesParallelException(this, ln);

    //     // ... else...
    //     return Geom2D.slopeInterceptIntersection(this.slope(), this.intercept(),
    //                                              ln.slope(), ln.intercept());
    // }

    /**
     * TODO: UNIT TESTS
     * - overlapping collinear lines without shared vertices
     */
    public boolean intersectsLineSegmentExcludeTouching(LineInt segment) {
        return intersectsLineSegment(segment, false);
    }

    public boolean intersectsLineSegment(LineInt segment) {
        return intersectsLineSegment(segment, true);
    }

    public boolean intersectsLineSegment(LineInt segment, boolean includeTouching) {
        PointDbl intersection = null;
        try {
            intersection = intersection(segment);
        } catch (Geom2D.LinesParallelException e) {
            return false;
        }
        return    this.lineBoundsContain(intersection, includeTouching)
            && segment.lineBoundsContain(intersection, includeTouching);
    }

    private boolean lineBoundsContain(PointDbl p) {
        return lineBoundsContain(p, true);
    }

    private boolean lineBoundsContain(PointDbl p, boolean includeTouching) {
        boolean contains = boundingBox().contains(p, includeTouching);
        // SPECIAL CASE:
        // bounding box for perpendicular lines will not contain
        // anything if includeTouching is false
        if (!contains && !includeTouching)
            if (isPerpendicular())
                contains = boundingBox().contains(p, true);
        return contains;
    }

}
