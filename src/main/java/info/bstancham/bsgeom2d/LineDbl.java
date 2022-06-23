package info.bstancham.bsgeom2d;

/**
 * An immutable data type representing a 2D line with
 * <code>double</code> co-ordinates.
 */
public final class LineDbl {

    private final PointDbl start;
    private final PointDbl end;

    public LineDbl(double x1, double y1, double x2, double y2) {
        this(new PointDbl(x1, y1), new PointDbl(x2, y2));
    }

    public LineDbl(PointDbl start, PointDbl end) {
        this.start = start;
        this.end = end;
    }

    public PointDbl start() { return start; }
    public PointDbl end() { return end; }

    public double startX() { return start.x(); }
    public double startY() { return start.y(); }
    public double endX() { return end.x(); }
    public double endY() { return end.y(); }

    /** May return a negative number. */
    public double xDist() { return end.x() - start.x(); }
    /** May return a negative number. */
    public double yDist() { return end.y() - start.y(); }

    public boolean isDegenerate() { return start.equals(end); }

    public boolean isVertical() {
        return !isDegenerate() && startX() == endX();
    }

    public boolean isHorizontal() {
        return !isDegenerate() && startY() == endY();
    }

    public boolean equalsIgnorePolarity(LineDbl l) {
        return (start.equals(l.start) && end.equals(l.end)) ||
            (start.equals(l.end) && end.equals(l.start()));
    }

}
