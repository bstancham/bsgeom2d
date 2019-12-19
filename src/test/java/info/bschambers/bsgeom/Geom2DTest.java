package info.bschambers.geom;

import org.junit.Test;
import static org.junit.Assert.*;

public class Geom2DTest {

    private final static double exact = 0.0;

    @Test public void testAngleConstants() {
        assertEquals(90, Math.toDegrees(Geom2D.QUARTER_TURN), exact);
        assertEquals(180, Math.toDegrees(Geom2D.HALF_TURN), exact);
        assertEquals(270, Math.toDegrees(Geom2D.THREE_QUARTER_TURN), exact);
        assertEquals(360, Math.toDegrees(Geom2D.FULL_TURN), exact);
    }

}
