package info.bschambers.geom;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class LineIntTest {

    private final static double exact = 0.0;
    private final static double almostExact = 0.000000000001;

    private Random rand = new Random();
    private final int randx1 = rand.nextInt();
    private final int randy1 = rand.nextInt();
    private final int randx2 = rand.nextInt();
    private final int randy2 = rand.nextInt();

    // perpendicular and degenerate lines
    private LineInt ldegenerate = new LineInt(randx1, randy1, randx1, randy1);
    private LineInt lvertical = new LineInt(randx1, randy1, randx1, randy2);
    private LineInt lvertical2 = new LineInt(randx2, randy1, randx2, randy2);
    private LineInt lhorizontal = new LineInt(randx1, randy1, randx2, randy1);
    private LineInt lhorizontal2 = new LineInt(randx1, randy2, randx2, randy2);

    // NOT perpendicular OR degenerate
    private LineInt lrand1 = randLineSmaller();
    private LineInt lrand2 = randLineSmaller();

    // lines with predictable angles
    private LineInt l0 = new LineInt(0, 0, 0, 31);
    private LineInt l45 = new LineInt(0, 0, 31, 31);
    private LineInt l90 = new LineInt(0, 0, 31, 0);
    private LineInt l135 = new LineInt(0, 0, 27, -27);
    private LineInt l180 = new LineInt(0, 0, 0, -5);
    private LineInt l225 = new LineInt(2, 7, -5, 0); // -5 on each axis
    private LineInt l270 = new LineInt(-2, -7, -26, -7); // -24 on x axis
    private LineInt l315 = new LineInt(2076, -371, 2063, -358); // -13 on x axis, +13 on y axis

    // points collinear with preceding lines
    private PointInt collinearL0 = new PointInt(0, 10);
    private PointInt leftL45 = new PointInt(93, 94);
    private PointInt rightL45 = new PointInt(93, 92);

    public LineIntTest() {
        // make sure that random line is not degenerate OR perpendicular
        while (lrand1.isDegenerate() || lrand1.isPerpendicular())
            lrand1 = randLine();
        // this time, also check that this is not equal or parallel with first line
        while (lrand2.isDegenerate() || lrand2.isPerpendicular()
               || lrand2.equals(lrand1) || lrand2.isParallel(lrand1))
            lrand2 = randLine();
    }

    private LineInt randLine() {
        return new LineInt(rand.nextInt(),rand.nextInt(),rand.nextInt(),rand.nextInt());
    }

    private LineInt randLineSmaller() {
        return new LineInt(randInt(), randInt(), randInt(), randInt());
    }

    private int randInt() {
        return rand.nextInt(2000) - 1000;
    }

    int rmax = 5;

    @Test
    public void testEquals() {
        LineInt l1 = new LineInt(12, 34, -21, 3);
        LineInt l1b = new LineInt(12, 34, -21, 3);
        LineDbl l1double = new LineDbl(12, 34, -21, 3);
        LineInt l1Rev = new LineInt(-21, 3, 12, 34);
        LineInt l2 = new LineInt(12, 32, -21, 3); // start point different
        LineInt l3 = new LineInt(12, 34, -20, 3); // end point different
        assertTrue("compare to self", l1.equals(l1));
        assertTrue("different point with same co-ords", l1.equals(l1b));
        assertFalse("compare to null", l1.equals(null));
        assertFalse("compare to different class", l1.equals(l1double));
        assertFalse("different start point", l1.equals(l2));
        assertFalse("different end point", l1.equals(l3));
        assertFalse("two distinct horizontal lines", lhorizontal.equals(lhorizontal2));
        assertFalse("two distinct vertical lines", lvertical.equals(lvertical2));
        // reversed polarity
        assertFalse("reversed polarity", l1.equals(l1Rev));
        assertTrue("reversed polarity", l1.equalsIgnorePolarity(l1Rev));
    }

    @Test
    public void testContains() {
        assertTrue("line contains it's own start point", lrand1.contains(lrand1.start()));
        assertTrue("line contains it's own end point", lrand1.contains(lrand1.end()));
        //
        LineInt l1in3 = new LineInt(-6, -4, 15, 3);
        assertTrue("point on line", l1in3.contains(new PointInt(-3, -3)));
        assertTrue("point on line", l1in3.contains(new PointInt(12, 2)));
        PointInt pNot = new PointInt(18, 4);
        assertTrue(l1in3.isCollinear(pNot));
        assertFalse("collinear point not on line", l1in3.contains(pNot));
    }

    @Test
    public void testIsParalell() {
        assertFalse("non-parallel lines", lrand1.isParallel(lrand2));
        assertTrue("compare to self", lrand1.isParallel(lrand1));
        assertTrue("horizontal lines", lhorizontal.isParallel(lhorizontal2));
        assertTrue("horizontal lines (polarity reversed)", lhorizontal.isParallel(lhorizontal2.reverse()));
        assertTrue("vertical lines", lvertical.isParallel(lvertical2));
        assertTrue("vertical lines (polarity reversed)", lvertical.isParallel(lvertical2.reverse()));
        LineInt transposed = new LineInt(lrand1.start().sum(32 ,-7004), lrand1.end().sum(32, -7004));
        assertTrue("arbitrary line transposed", lrand1.isParallel(transposed));
        assertTrue("arbitrary line transposed and reversed", lrand1.isParallel(transposed.reverse()));
        // // tricky vertical case...
        // LineInt v1 = new LineInt(0, 1, 0, 35);
        // LineInt v2 = new LineInt(4, 9, 4, 2);
        // assertTrue("vertical lines of opposite polarity", v1.isParallel(v2));
    }

    @Test
    public void testIsDegenerateVerticalHorizontalPerpendicular() {
        // test isDegenerate()
        assertTrue("a degenerate line",      ldegenerate.isDegenerate());
        assertFalse("a non-degenerate line", lrand1.isDegenerate());
        // test isVertical()
        assertTrue("a vertical line",     lvertical.isVertical());
        assertFalse("a horizontal line",   lhorizontal.isVertical());
        assertFalse("a non-vertical line", lrand1.isVertical());
        assertFalse("a degenerate line",   ldegenerate.isVertical());
        // test isHorizontal()
        assertFalse("a vertical line",     lvertical.isHorizontal());
        assertTrue("a horizontal line",   lhorizontal.isHorizontal());
        assertFalse("a non-horizontal line", lrand1.isHorizontal());
        assertFalse("a degenerate line",   ldegenerate.isHorizontal());
        // test isPerpendicular()
        assertTrue("a vertical line",     lvertical.isPerpendicular());
        assertTrue("a horizontal line",   lhorizontal.isPerpendicular());
        assertFalse("a non-perpendicular line", lrand1.isPerpendicular());
        assertFalse("a degenerate line",   ldegenerate.isPerpendicular());
    }

    @Test
    public void testLength() {
        // LineInt.length() should give exactly the same as Geom2DInt.lineLength()
        int x1i = 2;
        int y1i = 3;
        int x2i = 2;
        int y2i = 7;
        PointInt p1Start = new PointInt(x1i, y1i);
        PointInt p1End = new PointInt(x2i, y2i);
        LineInt l1Int = new LineInt(p1Start, p1End);
        int expect1 = 4;
        double len1 = l1Int.length();
        assertEquals("vertical LineInt with negative end", 8, new LineInt(5,6,5,-2).length(), exact);
        assertEquals("compare LineInt & PointInt input", l1Int.length(),
                     Geom2D.lineLength(p1Start, p1End), exact);
        assertEquals("compare LineInt & int input", l1Int.length(),
                     Geom2D.lineLength(x1i, y1i, x2i, y2i), exact);
    }

    @Test
    public void testAngle() {
        // test each line angle against expected value...
        // ... and test that LineInt.angle gives exactly same value as Geom2D.lineAngle()
        assertEquals(0, l0.angle(), almostExact);
        assertEquals(l0.angle(), Geom2D.lineAngle(l0.start(), l0.end()), exact);
        assertEquals(Math.toRadians(45), l45.angle(), almostExact);
        assertEquals(l45.angle(), Geom2D.lineAngle(l45.start(), l45.end()), exact);
        assertEquals(Geom2D.QUARTER_TURN, l90.angle(), almostExact);
        assertEquals(l90.angle(), Geom2D.lineAngle(l90.start(), l90.end()), exact);
        assertEquals(Math.toRadians(135), l135.angle(), almostExact);
        assertEquals(l135.angle(), Geom2D.lineAngle(l135.start(), l135.end()), exact);
        assertEquals(Geom2D.HALF_TURN, l180.angle(), almostExact);
        assertEquals(l180.angle(), Geom2D.lineAngle(l180.start(), l180.end()), exact);
        assertEquals(Math.toRadians(225), l225.angle(), almostExact);
        assertEquals(l225.angle(), Geom2D.lineAngle(l225.start(), l225.end()), exact);
        assertEquals(Geom2D.THREE_QUARTER_TURN, l270.angle(), almostExact);
        assertEquals(l270.angle(), Geom2D.lineAngle(l270.start(), l270.end()), exact);
        assertEquals(Math.toRadians(315), l315.angle(), almostExact);
        assertEquals(l315.angle(), Geom2D.lineAngle(l315.start(), l315.end()), exact);
    }

    @Test
    public void testLeftRightOrCollinear() {
        // any point should return true to exactly one of the following:
        // - isCollinear(p)
        // - isRelativeLeft(p)
        // - isRelativeRight(p)

        // collinear
        assertTrue(l0.isCollinear(collinearL0));
        assertTrue(l180.isCollinear(collinearL0));
        assertFalse(l0.isRelativeLeft(collinearL0));
        assertFalse(l0.isRelativeRight(collinearL0));

        // left/right side
        assertTrue(l45.isRelativeLeft(leftL45));
        assertTrue(l45.isRelativeRight(rightL45));
        assertFalse(l45.isRelativeRight(leftL45));
        assertFalse(l45.isRelativeLeft(rightL45));
        assertFalse(l45.isCollinear(leftL45));
        assertFalse(l45.isCollinear(rightL45));
        // left/right reversed when line is reversed
        LineInt reversed = l45.reverse();
        assertTrue(reversed.isRelativeRight(leftL45));
        assertTrue(reversed.isRelativeLeft(rightL45));

        // make sure that duplicate point at either end of the line returns true...
        assertTrue(l135.isCollinear(l135.start()));
        assertTrue(l135.isCollinear(l135.end()));
    }

    @Test
    public void testIntersection() {

        // Test symmetry for each case...
        // ... a.intersection(b) must be same as b.intersection(a)

        try {
            PointDbl i1 = lrand1.intersection(lrand2);
            PointDbl i2 = lrand2.intersection(lrand1);
            assertEquals(i1.x(), i2.x(), almostExact);
            // TODO: occaisionally, this fails! ...
            // ...
            assertEquals(i1.y(), i2.y(), almostExact);
        } catch (Geom2D.LinesParallelException e) {}
        // assertEquals("symmetry of intersection operations: intersection1=" + i1 + " intersection2=" + i2,
        //              i1.equals(i2));


        // assertEquals("symmetry of intersection operations",
        //              lrand1.intersection(lrand2).equals(lrand2.intersection(lrand1)));

    }

    @Test
    public void testIntersectionInt() {

        // Test symmetry for each case...
        // ... a.intersection(b) must be same as b.intersection(a)
        try {
            PointInt i1 = lrand1.intersectionInt(lrand2);
            PointInt i2 = lrand2.intersectionInt(lrand1);
            assertTrue("symmetry of intersection operations: intersection1=" + i1 + " intersection2=" + i2,
                       i1.equals(i2));
        } catch (Geom2D.LinesParallelException e) {}

        // Test rounding of non-integer values

    }

}
