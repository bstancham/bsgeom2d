package info.bschambers.geom;

import org.junit.Test;
import static org.junit.Assert.*;

public class TriIntTest {

    private PointInt prand1 = Util.randPointInt();
    private PointInt prand2 = Util.randPointInt();

    private TriInt triDupVertex1 = new TriInt(prand1, prand2, prand1);
    private TriInt triDupAllVertex = new TriInt(prand1, prand1, prand1);
    private TriInt triNonDegenerate1 = new TriInt(prand1, prand2, prand1.sum(1, 0));
    private TriInt triCollinear1 = new TriInt(prand1, prand1.sum(prand2), prand1.sum(prand2.invert()));

    @Test
    public void testIsDegenerate() {
        assertTrue("all vertices duplicates", triDupAllVertex.isDegenerate());
        assertTrue("duplicate vertices", triDupVertex1.isDegenerate());
        assertTrue("collinear vertices", triCollinear1.isDegenerate());
        assertFalse("non-degenerate triangle", triNonDegenerate1.isDegenerate());
    }

}
