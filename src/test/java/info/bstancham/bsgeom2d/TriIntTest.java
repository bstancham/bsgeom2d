package info.bstancham.bsgeom2d;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TriIntTest {

    private PointInt prand1 = Util.randPointInt();
    private PointInt prand2 = Util.randPointInt();

    private TriInt triDupVertex1 = new TriInt(prand1, prand2, prand1);
    private TriInt triDupAllVertex = new TriInt(prand1, prand1, prand1);
    private TriInt triNonDegenerate1 = new TriInt(prand1, prand2, prand1.sum(1, 0));
    private TriInt triCollinear1 = new TriInt(prand1, prand1.sum(prand2), prand1.sum(prand2.invert()));

    @Test
    public void testIsDegenerate() {
        assertTrue(triDupAllVertex.isDegenerate(), "all vertices duplicates");
        assertTrue(triDupVertex1.isDegenerate(), "duplicate vertices");
        assertTrue(triCollinear1.isDegenerate(), "collinear vertices");
        assertFalse(triNonDegenerate1.isDegenerate(), "non-degenerate triangle");
    }

}
