package info.bstancham.bsgeom2d;

import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertTrue(l1.equals(l1), "compare to self");
        assertTrue(l1.equals(l1b), "different point with same co-ords");
        assertFalse(l1.equals(null), "compare to null");
        assertFalse(l1.equals(l1double), "compare to different class");
        assertFalse(l1.equals(l2), "different start point");
        assertFalse(l1.equals(l3), "different end point");
        assertFalse(lhorizontal.equals(lhorizontal2), "two distinct horizontal lines");
        assertFalse(lvertical.equals(lvertical2), "two distinct vertical lines");
        // reversed polarity
        assertFalse(l1.equals(l1Rev), "reversed polarity");
        assertTrue(l1.equalsIgnorePolarity(l1Rev), "reversed polarity");
    }

    @Test
    public void testContains() {
        assertTrue(lrand1.contains(lrand1.start()), "line contains it's own start point");
        assertTrue(lrand1.contains(lrand1.end()), "line contains it's own end point");
        //
        LineInt l1in3 = new LineInt(-6, -4, 15, 3);
        assertTrue(l1in3.contains(new PointInt(-3, -3)), "point on line");
        assertTrue(l1in3.contains(new PointInt(12, 2)), "point on line");
        PointInt pNot = new PointInt(18, 4);
        assertTrue(l1in3.isCollinear(pNot));
        assertFalse(l1in3.contains(pNot), "collinear point not on line");
    }

    @Test
    public void testIsParalell() {
        assertFalse(lrand1.isParallel(lrand2), "non-parallel lines");
        assertTrue(lrand1.isParallel(lrand1), "compare to self");
        assertTrue(lhorizontal.isParallel(lhorizontal2), "horizontal lines");
        assertTrue(lhorizontal.isParallel(lhorizontal2.reverse()), "horizontal lines (polarity reversed)");
        assertTrue(lvertical.isParallel(lvertical2), "vertical lines");
        assertTrue(lvertical.isParallel(lvertical2.reverse()), "vertical lines (polarity reversed)");
        LineInt transposed = new LineInt(lrand1.start().sum(32 ,-7004), lrand1.end().sum(32, -7004));
        assertTrue(lrand1.isParallel(transposed), "arbitrary line transposed");
        assertTrue(lrand1.isParallel(transposed.reverse()), "arbitrary line transposed and reversed");
        // // tricky vertical case...
        // LineInt v1 = new LineInt(0, 1, 0, 35);
        // LineInt v2 = new LineInt(4, 9, 4, 2);
        // assertTrue("vertical lines of opposite polarity", v1.isParallel(v2));
    }

    @Test
    public void testIsDegenerateVerticalHorizontalPerpendicular() {
        // test isDegenerate()
        assertTrue(ldegenerate.isDegenerate(), "a degenerate line");
        assertFalse(lrand1.isDegenerate(), "a non-degenerate line");
        // test isVertical()
        assertTrue(lvertical.isVertical(), "a vertical line");
        assertFalse(lhorizontal.isVertical(), "a horizontal line");
        assertFalse(lrand1.isVertical(), "a non-vertical line");
        assertFalse(ldegenerate.isVertical(), "a degenerate line");
        // test isHorizontal()
        assertFalse(lvertical.isHorizontal(), "a vertical line");
        assertTrue(lhorizontal.isHorizontal(), "a horizontal line");
        assertFalse(lrand1.isHorizontal(), "a non-horizontal line");
        assertFalse(ldegenerate.isHorizontal(), "a degenerate line");
        // test isPerpendicular()
        assertTrue(lvertical.isPerpendicular(), "a vertical line");
        assertTrue(lhorizontal.isPerpendicular(), "a horizontal line");
        assertFalse(lrand1.isPerpendicular(), "a non-perpendicular line");
        assertFalse(ldegenerate.isPerpendicular(), "a degenerate line");
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
        assertEquals(8, new LineInt(5,6,5,-2).length(), exact,
                     "vertical LineInt with negative end");
        assertEquals(l1Int.length(), Geom2D.lineLength(p1Start, p1End), exact,
                     "compare LineInt & PointInt input");
        assertEquals(l1Int.length(), Geom2D.lineLength(x1i, y1i, x2i, y2i), exact,
                     "compare LineInt & int input");
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
            assertTrue(i1.equals(i2),
                       "symmetry of intersection operations: intersection1=" + i1 + " intersection2=" + i2);
        } catch (Geom2D.LinesParallelException e) {}

        // Test rounding of non-integer values

    }

}
