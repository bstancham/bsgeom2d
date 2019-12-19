package info.bschambers.bsgeom2d;

/**
 * Immutable data type representing a two-dimensional triangle with
 * integer co-ordinates.
 */
public final class TriInt {

    private PointInt a;
    private PointInt b;
    private PointInt c;

    public TriInt(int ax, int ay, int bx, int by, int cx, int cy) {
        this(new PointInt(ax, ay),
             new PointInt(bx, by),
             new PointInt(cx, cy));
    }

    public TriInt(PointInt a, PointInt b, PointInt c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ")";
    }

    public PointInt a() { return a; }
    public PointInt b() { return b; }
    public PointInt c() { return c; }

    /**
     * Returns the centroid of the triangle.
     *
     * The centroid is point where the three medians of the triangle
     * intersect.  It is also the 'center of gravity', and one of a
     * triangle's points of concurrency.
     */
    public PointDbl centroid() {
        // The coordinates of the centroid are simply the average of
        // the coordinates of the vertices.
        return new PointDbl((a.x() + b.x() + c.x()) / 3.0,
                            (a.y() + b.y() + c.y()) / 3.0);
    }

    public PolyInt toPoly() {
        // return new PolyInt(new PointInt[] { a, b, c });
        return new PolyInt(a, b, c);
    }

    /**
     * TODO: optimize
     */
    public boolean windingIsCCW() {
        return this.toPoly().windingIsCCW();
    }

    /**
     * @return True, if there are any duplicate vertices, or if the
     * vertices are collinear i.e. the triangle is a zero-width
     * sliver.
     */
    public boolean isDegenerate() {
        // duplicate vertices?
        if (a.equals(b) || b.equals(c) || c.equals(a)) return true;
        // collinear?
        if (Geom2DInt.collinear(a, b, c)) return true;
        // else
        return false;
    }

    public TriInt translate(PointInt p) {
        return translate(p.x(), p.y());
    }

    public TriInt translate(int x, int y) {
        return new TriInt(a.sum(x, y),
                          b.sum(x, y),
                          c.sum(x, y));
    }

}
