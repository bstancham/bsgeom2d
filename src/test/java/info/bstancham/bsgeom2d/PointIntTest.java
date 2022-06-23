package info.bstancham.bsgeom2d;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PointIntTest {

    private PointInt p1 = new PointInt(3, 42);
    private PointInt p1b = new PointInt(3, 42);
    private PointInt p2 = new PointInt(-3, 42);
    private PointInt p3 = new PointInt(0, 0);
    private PointDbl p1Double = new PointDbl(3, 42);

    @Test
    public void testEquals() {
        assertTrue(p1.equals(p1), "compare with self");
        assertFalse(p1.equals(null), "compare with null object");
        assertFalse(p1.equals(p1Double), "compare with object of different class");
        assertTrue(p1.equals(p1b), "PointInt with same co-ords");
        assertFalse(p1.equals(p2), "PointInt with different co-ords");
        assertFalse(p1.equals(p3), "PointInt with co-ords 0,0");
    }

    @Test
    public void testToDouble() {
        PointDbl d = p2.toDouble();
        assertTrue(p2.x() == d.x(), "compare x co-ords");
        assertTrue(p2.y() == d.y(), "compare y co-ords");
    }

}
