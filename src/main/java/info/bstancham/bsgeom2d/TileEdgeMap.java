package info.bstancham.bsgeom2d;

import java.util.ArrayList;
import java.util.List;

public class TileEdgeMap {

    // edge lines
    private static final LineInt edgeTop   = new LineInt(Tile.topRight, Tile.topLeft);
    private static final LineInt edgeBot   = new LineInt(Tile.botLeft,  Tile.botRight);
    private static final LineInt edgeLeft  = new LineInt(Tile.topLeft,  Tile.botLeft);
    private static final LineInt edgeRight = new LineInt(Tile.botRight, Tile.topRight);

    private static final LineInt edgeTopLeft  = new LineInt(Tile.topLeft,  Tile.centre);
    private static final LineInt edgeBotLeft  = new LineInt(Tile.botLeft,  Tile.centre);
    private static final LineInt edgeTopRight = new LineInt(Tile.topRight, Tile.centre);
    private static final LineInt edgeBotRight = new LineInt(Tile.botRight, Tile.centre);
    private static final LineInt edgeDiagTLBR = new LineInt(Tile.topLeft,  Tile.botRight);
    private static final LineInt edgeDiagBLTR = new LineInt(Tile.botLeft,  Tile.topRight);

    // six possible internal edges
    private boolean internalTopLeft  = false;
    private boolean internalBotLeft  = false;
    private boolean internalTopRight = false;
    private boolean internalBotRight = false;
    private boolean internalDiagTLBR = false;
    private boolean internalDiagBLTR = false;

    private Tile tile;

    public TileEdgeMap(Tile tile) {
        this.tile = tile;
        // set up internal edges
        if (tile.numEdges() == 3) {
            internalTopLeft  = true;
            internalBotLeft  = true;
            internalTopRight = true;
            internalBotRight = true;
        } else if (tile.numEdges() == 2) {
            if ((tile.top && tile.right) ||
                (tile.bot && tile.left))
                internalDiagTLBR = true;
            else
                internalDiagBLTR = true;
        } else if (tile.numEdges() == 1) {
            if (tile.top) {
                internalTopLeft = true;
                internalTopRight = true;
            } else if (tile.bot) {
                internalBotLeft = true;
                internalBotRight = true;
            } else if (tile.left) {
                internalTopLeft = true;
                internalBotLeft = true;
            } else if (tile.right) {
                internalTopRight = true;
                internalBotRight = true;
            }
        }
    }

    public Tile get() { return tile; }

    public LineInt[] getEdges() {
        List<LineInt> edges = new ArrayList<LineInt>();
        if (tile.top)   edges.add(edgeTop);
        if (tile.bot)   edges.add(edgeBot);
        if (tile.left)  edges.add(edgeLeft);
        if (tile.right) edges.add(edgeRight);
        if (internalTopLeft)  edges.add(edgeTopLeft);
        if (internalTopRight) edges.add(edgeTopRight);
        if (internalBotLeft)  edges.add(edgeBotLeft);
        if (internalBotRight) edges.add(edgeBotRight);
        if (internalDiagBLTR) edges.add(edgeDiagBLTR);
        if (internalDiagTLBR) edges.add(edgeDiagTLBR);
        return edges.toArray(new LineInt[edges.size()]);
    }

    public void subtractAbove(Tile aboveMe) {
        tile = new Tile((aboveMe.bot ? false : tile.top),
                        tile.bot,
                        tile.left,
                        tile.right);
    }

    public void subtractBelow(Tile belowMe) {
        tile = new Tile(tile.top,
                        (belowMe.top ? false : tile.bot),
                        tile.left,
                        tile.right);
    }

    public void subtractLeft(Tile leftOfMe) {
        tile = new Tile(tile.top,
                        tile.bot,
                        (leftOfMe.right ? false : tile.left),
                        tile.right);
    }

    public void subtractRight(Tile rightOfMe) {
        tile = new Tile(tile.top,
                        tile.bot,
                        tile.left,
                        (rightOfMe.left ? false : tile.right));
    }

}
