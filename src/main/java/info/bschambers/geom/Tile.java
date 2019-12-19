package info.bschambers.geom;

/**
 * Immutable data type representing a square tile which can have a limited number of
 * shapes based on which of the four edges exist.<br/>
 *
 * Tile dimension is 2*2 units (not 1*1) because a centre point is needed for some of the
 * possible shapes.<br/>
 *
 * 14 combinations possible.
 */
public class Tile {

    // five possible vertices
    public static final PointInt centre   = new PointInt(1, 1);
    public static final PointInt topLeft  = new PointInt(0, 2);
    public static final PointInt botLeft  = new PointInt(0, 0);
    public static final PointInt botRight = new PointInt(2, 0);
    public static final PointInt topRight = new PointInt(2, 2);

    // eight possible facets
    public static final TriInt facetTop      = new TriInt(centre,   topRight, topLeft);
    public static final TriInt facetBot      = new TriInt(centre,   botLeft,  botRight);
    public static final TriInt facetLeft     = new TriInt(centre,   topLeft,  botLeft);
    public static final TriInt facetRight    = new TriInt(centre,   botRight, topRight);
    public static final TriInt facetTopLeft  = new TriInt(topLeft,  botLeft,  topRight);
    public static final TriInt facetBotLeft  = new TriInt(botLeft,  botRight, topLeft);
    public static final TriInt facetTopRight = new TriInt(topRight, topLeft,  botRight);
    public static final TriInt facetBotRight = new TriInt(botRight, topRight, botLeft);

    // four possible perimeter edges...
    // ... extra internal edges will be needed, but these are the only
    // ones which need to be compared to one-another...
    public final boolean top;
    public final boolean bot;
    public final boolean left;
    public final boolean right;

    // if number of perimeter edges is odd, then we will need to use
    // the centre point
    private final int numEdges;
    private final boolean oddNumEdges;

    public Tile(boolean top, boolean bot, boolean left, boolean right) {
        this.top = top;
        this.bot = bot;
        this.left = left;
        this.right = right;
        this.numEdges = countNumEdges();
        this.oddNumEdges = (numEdges % 2 == 0 ? false : true);
    }

    private int countNumEdges() {
        int i = 0;
        i += (top   ? 1 :0);
        i += (bot   ? 1 :0);
        i += (left  ? 1 :0);
        i += (right ? 1 :0);
        return i;
    }

    public int numEdges() { return numEdges; }

    public int numFacets() {
        return (oddNumEdges ? numEdges : numEdges / 2);
    }

    public TriInt[] getFacets() {
        if (numEdges == 0) return new TriInt[0];
        if (numEdges == 4) return new TriInt[] { facetTopLeft, facetBotRight };
        if (oddNumEdges)   return getFacetsOdd();
        else               return getFacetsEven();
    }

    private TriInt[] getFacetsOdd() {
        TriInt[] facets = new TriInt[numFacets()];
        int index = 0;
        index += addIf(top,   facets, index, facetTop);
        index += addIf(bot,   facets, index, facetBot);
        index += addIf(left,  facets, index, facetLeft);
        index += addIf(right, facets, index, facetRight);
        return facets;
    }

    private TriInt[] getFacetsEven() {
        TriInt[] facets = new TriInt[numFacets()];
        int index = 0;
        index += addIf(top && left,  facets, index, facetTopLeft);
        index += addIf(top && right, facets, index, facetTopRight);
        index += addIf(bot && left,  facets, index, facetBotLeft);
        index += addIf(bot && right, facets, index, facetBotRight);
        return facets;
    }

    private int addIf(boolean b, TriInt[] facets, int index, TriInt f) {
        if (b) {
            facets[index] = f;
            return 1;
        }
        return 0;
    }

    public Tile subtract(Tile tile) {
        return new Tile(subtract(top, tile.top),
                              subtract(bot, tile.bot),
                              subtract(left, tile.left),
                              subtract(right, tile.right));
    }

    public Tile intersect(Tile tile) {
        return new Tile(top && tile.top,
                        bot && tile.bot,
                        left && tile.left,
                        right && tile.right);
    }

    public Tile union(Tile tile) {
        return new Tile(top || tile.top,
                        bot || tile.bot,
                        left || tile.left,
                        right || tile.right);
    }

    private boolean subtract(boolean b1, boolean b2) {
        return (b2 ? false : b1);
    }

}
