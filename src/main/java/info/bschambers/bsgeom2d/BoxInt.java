package info.bschambers.bsgeom2d;

public final class BoxInt {

    private PointInt bottomLeft;
    private PointInt topRight;
    private PointInt centre;

    public BoxInt(int left, int bottom, int right, int top) {
	this(new PointInt(left, bottom), new PointInt(right, top));
    }
    public BoxInt(PointInt bottomLeft, PointInt topRight) {
	this.bottomLeft = bottomLeft;
	this.topRight = topRight;
        this.centre = new PointInt(left() + (width() / 2),
                                   bottom() + (height() / 2));
    }

    public int left()   { return bottomLeft.x(); }
    public int right()  { return topRight.x(); }
    public int bottom() { return bottomLeft.y(); }
    public int top()    { return topRight.y(); }

    public PointInt centre() { return centre; }

    public int height() { return topRight.y() - bottomLeft.y(); }
    public int width()  { return topRight.x() - bottomLeft.x(); }

    public boolean contains(PointInt p) {
	return contains(p, true);
    }

    public boolean contains(PointInt p, boolean edgeInclusive) {
        return contains(p.toDouble(), edgeInclusive);
    }

    public boolean contains(PointDbl p, boolean edgeInclusive) {
	if (edgeInclusive)
	    return p.x() >= left()
		&& p.x() <= right()
		&& p.y() >= bottom()
		&& p.y() <= top();
        else
            return p.x() > left()
                && p.x() < right()
                && p.y() > bottom()
                && p.y() < top();
    }

}
