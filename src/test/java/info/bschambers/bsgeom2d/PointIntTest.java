package info.bschambers.bsgeom2d;

import org.junit.Test;
import static org.junit.Assert.*;

public class PointIntTest {

    private PointInt p1 = new PointInt(3, 42);
    private PointInt p1b = new PointInt(3, 42);
    private PointInt p2 = new PointInt(-3, 42);
    private PointInt p3 = new PointInt(0, 0);
    private PointDbl p1Double = new PointDbl(3, 42);

    @Test
    public void testEquals() {
        assertTrue("compare with self", p1.equals(p1));
        assertFalse("compare with null object", p1.equals(null));
        assertFalse("compare with object of different class", p1.equals(p1Double));
        assertTrue("PointInt with same co-ords", p1.equals(p1b));
        assertFalse("PointInt with different co-ords", p1.equals(p2));
        assertFalse("PointInt with co-ords 0,0", p1.equals(p3));
    }

    @Test
    public void testToDouble() {
        PointDbl d = p2.toDouble();
        assertTrue("compare x co-ords", p2.x() == d.x());
        assertTrue("compare y co-ords", p2.y() == d.y());
    }

}
