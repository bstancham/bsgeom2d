package info.bschambers.bsgeom2d;

import java.util.Random;

public final class Util {

    private static Random rand = new Random();

    public final static double EXACT = 0.0;
    public final static double ALMOST_EXACT = 0.000000000001;

    /**
     * @return A random integer between -1000 and 1000.
     */
    public static int randInt() {
        return rand.nextInt(2000) - 1000;
    }

    public static PointInt randPointInt() {
        return new PointInt(randInt(), randInt());
    }

    public static LineInt randLineInt() {
        return new LineInt(randPointInt(), randPointInt());
    }

}
