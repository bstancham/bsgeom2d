package info.bstancham.bsgeom2d;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(quad1, quad1, "compare to self");
        assertNotEquals(quad1, null, "compare to null");
        assertNotEquals(quad1, new PointInt(25, 34), "compare to wrong class");
        assertNotEquals(quad1, poly1, "polygon with different number of vertices");
        assertNotEquals(quad1, quad1.reverseVertexOrder(), "self, with winding order reversed");
        assertEquals(quad1, quad1.reverseVertexOrder().reverseVertexOrder(),
                     "winding order reversed twice");
        assertEquals(quad1, quad1CycledVerts, "self, with vertex order cycled");
        assertEquals(quad1, quad1Mod, "self, with one modified vertex");
    }

    @Test
    public void testContainsLine() {
        assertFalse(poly1.contains(outsidePoly1Bounds), "outside of bounding box");
        assertFalse(poly1.contains(new LineInt(5, 2, 41, 9)),
                    "one vertex inside polygon, other outside of bounding box");
        assertFalse(poly1.contains(outsidePoly1), "inside bounding box, outside polygon");
        assertFalse(poly1.contains(oneEndOutsidePoly1),
                    "one vertex inside, other outside of polygon");
        assertTrue(poly1.contains(insidePoly1), "line fully inside polygon");
        assertTrue(poly1.contains(new LineInt(6, 18, 16, 10)),
                   "line inside polygon, one end on a polygon vertex");
        assertTrue(poly1.contains(poly1Edge1), "line is same as a polygon edge");

        // assertFalse("line is sub-segment of polygon edge", poly1.contains(new LineInt()));
        // assertFalse("line is on polygon edge - polygon edge has collinear vertices", poly1.contains(new LineInt()));

        //assertFalse("line outside, both ends on a polygon vertex", poly1.contains(new LineInt(3, 19, 0, 4)));
    }

    @Test
    public void splitLineAtIntersections() {
        LineInt[] parts = poly1.splitLineAtIntersections(outsidePoly1);
        assertEquals(1, parts.length, "line outside polygon - num parts");
        assertEquals(outsidePoly1, parts[0], "line outside polygon - returns original line");

        parts = poly1.splitLineAtIntersections(oneEndOnPoly1Vertex);
        assertEquals(1, parts.length, "one end on polygon vertex - num parts" + Arrays.toString(parts));
        assertEquals(oneEndOnPoly1Vertex, parts[0], "one end on polygon vertex - returns original line");

        parts = poly1.splitLineAtIntersections(oneEndOnPoly1Edge);
        assertEquals(1, parts.length, "one end on polygon edge - num parts" + Arrays.toString(parts));
        assertEquals(oneEndOnPoly1Edge, parts[0], "one end on polygon edge - returns original line");

        parts = poly1.splitLineAtIntersections(insidePoly1);
        assertEquals(1, parts.length, "line inside polygon - num parts");
        assertEquals(insidePoly1, parts[0], "line inside polygon - returns original line");

        parts = poly1.splitLineAtIntersections(oneEndOutsidePoly1);
        assertEquals(2, parts.length, "one end outside polygon - num parts");

        parts = poly1.splitLineAtIntersections(poly1Edge1);
        assertEquals(1, parts.length, "line same as polygon edge - num parts");
        assertEquals(poly1Edge1, parts[0], "line same as polygon edge - returns original line");

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
