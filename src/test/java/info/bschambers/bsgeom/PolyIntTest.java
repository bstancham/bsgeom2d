package info.bschambers.geom;

import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class PolyIntTest {

    private PolyInt quad1 = new PolyInt(new PointInt( 1, -2),
                                        new PointInt( 9, -3),
                                        new PointInt(-4, 12),
                                        new PointInt(-2, -8));
    private PolyInt quad1CycledVerts = new PolyInt(new PointInt(-4, 12),
                                                   new PointInt(-2, -8),
                                                   new PointInt( 1, -2),
                                                   new PointInt( 9, -3));
    private PolyInt quad1Mod = new PolyInt(new PointInt( 1, -2),
                                           new PointInt( 9, -3),
                                           new PointInt(-4, 12),
                                           new PointInt(-2, -8));
    private PolyInt poly1 = new PolyInt(new PointInt( 0,  0),
                                        new PointInt(10,  0),
                                        new PointInt(16,  6),
                                        new PointInt(16, 10),
                                        new PointInt(20, 10),
                                        new PointInt(25, 15),
                                        new PointInt(35, 35),
                                        new PointInt(25, 35),
                                        new PointInt(17, 19),
                                        new PointInt( 3, 19),
                                        new PointInt( 3, 16),
                                        new PointInt( 5, 16),
                                        new PointInt( 5, 12),
                                        new PointInt( 3, 12),
                                        new PointInt( 3,  4),
                                        new PointInt( 0,  4));

    private LineInt outsidePoly1Bounds = new LineInt(36, 20, 41, 9);
    private LineInt outsidePoly1 = new LineInt(4, 20, 17, 22);
    private LineInt oneEndOnPoly1Vertex = new LineInt(4, 20, 17, 19);
    private LineInt oneEndOnPoly1Edge = new LineInt(4, 20, 18, 21);
    private LineInt insidePoly1 = new LineInt(6, 18, 16, 11);
    private LineInt oneEndOutsidePoly1 = new LineInt(4, 20, 6, 18);
    private LineInt poly1Edge1 = poly1.edge(4);
    private LineInt intersect6Poly1 = new LineInt(0, -2, 6, 27);

    @Test
    public void testEquals() {
        assertEquals("compare to self", quad1, quad1);
        assertNotEquals("compare to null", quad1, null);
        assertNotEquals("compare to wrong class", quad1, new PointInt(25, 34));
        assertNotEquals("polygon with different number of vertices", quad1, poly1);
        assertNotEquals("self, with winding order reversed", quad1, quad1.reverseVertexOrder());
        assertEquals("winding order reversed twice", quad1, quad1.reverseVertexOrder().reverseVertexOrder());
        assertEquals("self, with vertex order cycled", quad1, quad1CycledVerts);
        assertEquals("self, with one modified vertex", quad1, quad1Mod);
    }

    @Test
    public void testContainsLine() {
        assertFalse("outside of bounding box", poly1.contains(outsidePoly1Bounds));
        assertFalse("one vertex inside polygon, other outside of bounding box",
                    poly1.contains(new LineInt(5, 2, 41, 9)));
        assertFalse("inside bounding box, outside polygon", poly1.contains(outsidePoly1));
        assertFalse("one vertex inside, other outside of polygon",
                    poly1.contains(oneEndOutsidePoly1));
        assertTrue("line fully inside polygon", poly1.contains(insidePoly1));
        assertTrue("line inside polygon, one end on a polygon vertex",
                   poly1.contains(new LineInt(6, 18, 16, 10)));
        assertTrue("line is same as a polygon edge", poly1.contains(poly1Edge1));

        // assertFalse("line is sub-segment of polygon edge", poly1.contains(new LineInt()));
        // assertFalse("line is on polygon edge - polygon edge has collinear vertices", poly1.contains(new LineInt()));

        //assertFalse("line outside, both ends on a polygon vertex", poly1.contains(new LineInt(3, 19, 0, 4)));
    }

    @Test
    public void splitLineAtIntersections() {
        LineInt[] parts = poly1.splitLineAtIntersections(outsidePoly1);
        assertEquals("line outside polygon - num parts", 1, parts.length);
        assertEquals("line outside polygon - returns original line", outsidePoly1, parts[0]);

        parts = poly1.splitLineAtIntersections(oneEndOnPoly1Vertex);
        assertEquals("one end on polygon vertex - num parts" + Arrays.toString(parts), 1, parts.length);
        assertEquals("one end on polygon vertex - returns original line", oneEndOnPoly1Vertex, parts[0]);

        parts = poly1.splitLineAtIntersections(oneEndOnPoly1Edge);
        assertEquals("one end on polygon edge - num parts" + Arrays.toString(parts), 1, parts.length);
        assertEquals("one end on polygon edge - returns original line", oneEndOnPoly1Edge, parts[0]);

        parts = poly1.splitLineAtIntersections(insidePoly1);
        assertEquals("line inside polygon - num parts", 1, parts.length);
        assertEquals("line inside polygon - returns original line", insidePoly1, parts[0]);

        parts = poly1.splitLineAtIntersections(oneEndOutsidePoly1);
        assertEquals("one end outside polygon - num parts", 2, parts.length);

        parts = poly1.splitLineAtIntersections(poly1Edge1);
        assertEquals("line same as polygon edge - num parts", 1, parts.length);
        assertEquals("line same as polygon edge - returns original line", poly1Edge1, parts[0]);

        // parts = poly1.splitLineAtIntersections(intersect6Poly1);
        // assertEquals("multiple intersections - num parts", 7, parts.length);



        // parts = poly1.splitLineAtIntersections(oneEndOutsidePoly1);
        // assertEquals("both ends on polygon vertices - internal intersections - num parts", 2, parts.length);

        // parts = poly1.splitLineAtIntersections(oneEndOutsidePoly1);
        // assertEquals("line is segment of polygon edge - num parts", 2, parts.length);

        // parts = poly1.splitLineAtIntersections(oneEndOutsidePoly1);
        // assertEquals("line on polygon edge, but longer - num parts", 2, parts.length);

        // parts = poly1.splitLineAtIntersections(oneEndOutsidePoly1);
        // assertEquals("line on collinear polygon vertices which form an edge - num parts", 2, parts.length);

        // parts = poly1.splitLineAtIntersections(oneEndOutsidePoly1);
        // assertEquals("line on collinear polygon vertices which don't form an edge - num parts", 2, parts.length);

    }

}
