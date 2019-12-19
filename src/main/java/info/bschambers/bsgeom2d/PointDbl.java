package info.bschambers.bsgeom2d;

/**
 * Immutable data type representing a 2D point with
 * <code>double</code> co-ordinates.
 */
public final class PointDbl {

    private final double x;
    private final double y;

    public PointDbl(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() { return x; }
    public double y() { return y; }

    @Override
    public boolean equals(Object y) {

        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        // cast guaranteed to work here
        PointDbl that = (PointDbl) y;
        // test equality in all significant fields
        if (that.x != this.x) return false;
        if (that.y != this.y) return false;
        return true;
    }

    /**
     * ... x and y co-ordinates rounded down with simple type-cast
     * from double to int.
     */
    public PointInt toInt() {
        return new PointInt((int) x, (int) y);
    }

    /**
     * x and y co-ordinates rounded with <code>Math.round()</code>.
     *
     * WARNING: Possible lossy conversion for very large
     * numbers... <code>double</code> is a 64 bit number, and
     * <code>Math.round()</code> converts it into <code>long</code>
     * rather than <code>int</code>.
     */
    public PointInt toIntRounded() {
        return new PointInt((int) Math.round(x),
                            (int) Math.round(y));
    }

    public String toString() {
        return "[" + x + ", " + y + "]";
    }

}
