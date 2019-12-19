package info.bschambers.bsgeom2d;

import org.junit.Test;
import static org.junit.Assert.*;

public class PointDblTest {

    private PointDbl p1 = new PointDbl(3.7, 42.00145);
    private PointDbl p1b = new PointDbl(3.7, 42.00145);
    private PointDbl p2 = new PointDbl(-3.31, 42.00145);
    private PointDbl p3 = new PointDbl(0.0, 0.0);
    private PointInt p1Int = new PointInt(3, 42);

    @Test
    public void testEquals() {
        assertTrue("compare with self", p1.equals(p1));
        assertFalse("compare with null object", p1.equals(null));
        assertFalse("compare with object of different class", p1.equals(p1Int));
        assertTrue("PointInt with same co-ords", p1.equals(p1b));
        assertFalse("PointInt with different co-ords", p1.equals(p2));
        assertFalse("PointInt with co-ords 0,0", p1.equals(p3));
    }

    @Test
    public void testToInt() {
        PointInt pi = p2.toInt();
        assertTrue("compare rounded-down x co-ords", (int) p2.x() == pi.x());
        assertTrue("compare rounded-down y co-ords", (int) p2.y() == pi.y());
    }

    // // fails if exception is not thrown
    // @Test(expected=NullPointerException.class)
    // public void testNullPointer() {
    //     greet.throwNull();
    // }
}
