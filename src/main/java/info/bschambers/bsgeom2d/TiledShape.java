package info.bschambers.bsgeom2d;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable data type which represents a shape made out of square tiles.
 */
public class TiledShape {

    public static final Tile EMPTY    = new Tile(false, false, false, false);
    public static final Tile SQUARE   = new Tile(true,  true,  true,  true);
    public static final Tile TOPLEFT  = new Tile(false, true,  false, true);
    public static final Tile TOPRIGHT = new Tile(false, true,  true,  false);
    public static final Tile BOTLEFT  = new Tile(true,  false, false, true);
    public static final Tile BOTRIGHT = new Tile(true,  false, true,  false);

    private static final char CODE_EMPTY    = '.';
    private static final char CODE_SQUARE   = 'x';
    private static final char CODE_TOPLEFT  = 'l';
    private static final char CODE_TOPRIGHT = 'r';
    private static final char CODE_BOTLEFT  = 'e';
    private static final char CODE_BOTRIGHT = 'i';

    private final int dim;

    /**
     * ... bottom left corner of the bounding box...
     */
    private final PointInt pos;
    private final Tile[] tiles;
    private final int unitSize = 2;

    // values to be cached after calculated
    private TriInt[] triangles = null;

    public TiledShape(int dimension, char[] symbols) {
        this(dimension, toTileArray(symbols));
    }

    public TiledShape(int dimension, Tile[] tiles) {
        this(dimension, tiles, new PointInt(0, 0));
    }

    public TiledShape(int dimension, Tile[] tiles, PointInt position) {
        this.dim = dimension;
        this.tiles = tiles;
        this.pos = position;
    }

    private static Tile[] toTileArray(char[] symbols) {
        Tile[] newTiles = new Tile[symbols.length];
        for (int i = 0; i < symbols.length; i++)
            // newTiles[i] = Tile.getBySymbol(symbols[i]);
            newTiles[i] = getTileBySymbol(symbols[i]);
        return newTiles;
    }

    private static Tile getTileBySymbol(char c) {
        if (c == CODE_EMPTY)    return EMPTY;
        if (c == CODE_SQUARE)   return SQUARE;
        if (c == CODE_TOPLEFT)  return TOPLEFT;
        if (c == CODE_TOPRIGHT) return TOPRIGHT;
        if (c == CODE_BOTLEFT)  return BOTLEFT;
        if (c == CODE_BOTRIGHT) return BOTRIGHT;
        throw new IllegalArgumentException("symbol '" + c + "' not recognised");
    }

    public int dimension() { return dim; }

    public PointInt position() { return pos; }

    public TiledShape translate(int x, int y) {
        return new TiledShape(dim, tiles, pos.sum(x, y));
    }

    // public Iterator<Tile> iterator() {
    //     return new TileIterator();
    // }

    // private class TileIterator implements Iterator<Tile> {
    //     private int index = 0;
    //     public boolean hasNext() { return index < tiles.length; }
    //     public Tile next() { return tiles[index++]; }
    // }

    public TriInt[] triangulation() {
        PointInt unitPos = pos.multiply(unitSize, unitSize);
        if (triangles == null) {
            int count = 0;
            for (Tile t : tiles)
                count += t.numFacets();
            triangles = new TriInt[count];
            int i = 0;
            int x = 0;
            int y = dim - 1;
            for (Tile t : tiles) {
                for (TriInt facet : t.getFacets()) {
                    triangles[i] = facet.translate(unitPos.sum(x * unitSize,
                                                               y * unitSize));
                    i++;
                }
                x++;
                if (x >= dim) {
                    x = 0;
                    y--;
                }
            }
        }
        return triangles;
    }

    public TiledShape subtract(TiledShape shape) {
        Tile[] newTiles = new Tile[tiles.length];
        for (int i = 0; i < tiles.length; i++)
            newTiles[i] = tiles[i].subtract(shape.getTileAt(xCoord(i),
                                                            yCoord(i)));
        return new TiledShape(dim, newTiles, pos);
    }

    public TiledShape intersect(TiledShape shape) {
        Tile[] newTiles = new Tile[tiles.length];
        for (int i = 0; i < tiles.length; i++)
            newTiles[i] = tiles[i].intersect(shape.getTileAt(xCoord(i),
                                                            yCoord(i)));
        return new TiledShape(dim, newTiles, pos);
    }

    public TiledShape union(TiledShape shape) {
        Tile[] newTiles = new Tile[tiles.length];
        for (int i = 0; i < tiles.length; i++)
            newTiles[i] = tiles[i].union(shape.getTileAt(xCoord(i),
                                                         yCoord(i)));
        return new TiledShape(dim, newTiles, pos);
    }

    private int xCoord(int i) {
        return pos.x() + (i % dim);
    }

    private int yCoord(int i) {
        return pos.y() + (dim - 1) - (i / dim);
    }

    private Tile getTileAt(int x, int y) {
        int xp = x - pos.x();
        int yp = y - pos.y();
        if (xp < 0 || yp < 0 ||
            xp >= dim || yp >= dim) return EMPTY;
        // int yAdjust = (dim * dim) - 1;
        int yAdjust = dim - 1 - yp;
        // return tiles[(yAdjust - (yp * dim)) + xp];
        return tiles[(yAdjust * dim) + xp];
    }

    private Tile tileLeft(int i) {
        if (i % dim <= 0) return EMPTY;
        return tiles[i - 1];
    }

    private Tile tileRight(int i) {
        if (i % dim >= dim - 1) return EMPTY;
        return tiles[i + 1];
    }

    private Tile tileAbove(int i) {
        if (i < dim) return EMPTY;
        return tiles[i - dim];
    }

    private Tile tileBelow(int i) {
        // if (i >= (dim * dim) - dim - 1) return EMPTY;
        if (i >= (dim * dim) - dim) return EMPTY;
        return tiles[i + dim];
    }

    /**
     * @return All of the lines in this shape... not in winding order...
     */
    public LineInt[] getLines() {
        List<LineInt> edges = new ArrayList<>();

        // compare each tile with all it's neighbors, and subtract edges
        for (int i = 0; i < tiles.length; i++) {
            TileEdgeMap tem = new TileEdgeMap(tiles[i]);
            tem.subtractLeft(tileLeft(i));
            tem.subtractRight(tileRight(i));
            tem.subtractAbove(tileAbove(i));
            tem.subtractBelow(tileBelow(i));
            for (LineInt ln : tem.getEdges())
                edges.add(ln.translate(xCoord(i) * unitSize, yCoord(i) * unitSize));
        }

        return edges.toArray(new LineInt[edges.size()]);
    }

}
