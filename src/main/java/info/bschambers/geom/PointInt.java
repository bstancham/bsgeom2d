package info.bschambers.geom;

import java.util.Comparator;

/**
 * An immutable data type representing a two-dimensional point with
 * integer coordinates.
 */
public final class PointInt implements Comparable<PointInt> {

    private final int x;
    private final int y;

    public PointInt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        // cast guaranteed to work here
        PointInt that = (PointInt) y;
        // test equality in all significant fields
        if (that.x != this.x) return false;
        if (that.y != this.y) return false;
        return true;
    }

    /**
     * Compares the two points by y co-ordinate, breaking ties by x
     * co-ordinate.
     *
     * @param p The input point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    @Override
    public int compareTo(PointInt p) {
        if (this.y < p.y) return -1;
        if (this.y > p.y) return 1;
        if (this.x < p.x) return -1;
        if (this.x > p.x) return 1;
        return 0; // points are the same
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public DistanceComparator distanceComparator() {
        return new DistanceComparator();
    }

    private class DistanceComparator implements Comparator<PointInt> {
        public int compare(PointInt p1, PointInt p2) {
            double dist1 = Geom2D.lineLength(PointInt.this, p1);
            double dist2 = Geom2D.lineLength(PointInt.this, p2);
            if (dist1 < dist2) return -1;
            if (dist1 > dist2) return 1;
            else               return 0; // equal
        }
    }



    /*------------------------ TRANSFORMATIONS -------------------------*/

    public PointDbl toDouble() { return new PointDbl(x, y); }

    public PointInt sum(PointInt p) {
        return sum(p.x, p.y);
    }

    public PointInt sum(int x, int y) {
        return new PointInt(this.x + x, this.y + y);
    }

    public int sumX(PointInt p) { return this.x + p.x; }
    public int sumY(PointInt p) { return this.y + p.y; }

    public PointInt reflectX(int xMid) { return new PointInt(xMid - x, y); }
    public PointInt reflectY(int yMid) { return new PointInt(x, yMid - y); }

    public PointInt invert() { return new PointInt(-x, -y); }

    public PointInt rotate90(PointInt centre) {
        return new PointInt(centre.x() +  (this.y - centre.y()),
                            centre.y() + -(this.x - centre.x()));
    }

    public PointInt multiply(int x, int y) {
        return new PointInt(this.x * x, this.y * y);
    }



    /*---------------------------- GEOMETRY ----------------------------*/

    /**
     * Returns the slope between this point and the input point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0).
     *
     * @param p The input point.
     * @return The slope between this point and the input point.<br>
     * SPECIAL CASES:<br>
     * - horizontal line returns +0.0.<br>
     * - vertical line returns Double.POSITIVE_INFINITY.<br>
     * - degenerate line returns Double.NEGATIVE_INFINITY.
     */
    public double slopeTo(PointInt p) {
        if (p.equals(this)) return Double.NEGATIVE_INFINITY; // degenerate
        if (p.y == this.y) return +0.0;                      // horizontal
        if (p.x == this.x) return Double.POSITIVE_INFINITY;  // vertical
        return (p.y - (double) this.y) / (p.x - (double) this.x);
    }

}
