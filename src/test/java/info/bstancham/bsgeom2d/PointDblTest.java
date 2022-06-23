package info.bstancham.bsgeom2d;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PointDblTest {

    private PointDbl p1 = new PointDbl(3.7, 42.00145);
    private PointDbl p1b = new PointDbl(3.7, 42.00145);
    private PointDbl p2 = new PointDbl(-3.31, 42.00145);
    private PointDbl p3 = new PointDbl(0.0, 0.0);
    private PointInt p1Int = new PointInt(3, 42);

    @Test
    public void testEquals() {
        assertTrue(p1.equals(p1), "compare with self");
        assertFalse(p1.equals(null), "compare with null object");
        assertFalse(p1.equals(p1Int), "compare with object of different class");
        assertTrue(p1.equals(p1b), "PointInt with same co-ords");
        assertFalse(p1.equals(p2), "PointInt with different co-ords");
        assertFalse(p1.equals(p3), "PointInt with co-ords 0,0");
    }

    @Test
    public void testToInt() {
        PointInt pi = p2.toInt();
        assertTrue((int) p2.x() == pi.x(), "compare rounded-down x co-ords");
        assertTrue((int) p2.y() == pi.y(), "compare rounded-down y co-ords");
    }

    // // fails if exception is not thrown
    // @Test(expected=NullPointerException.class)
    // public void testNullPointer() {
    //     greet.throwNull();
    // }
}
