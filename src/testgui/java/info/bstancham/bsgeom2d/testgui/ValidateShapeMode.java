package info.bstancham.bsgeom2d.testgui;
import java.awt.Graphics;
import java.awt.Color;

public class ValidateShapeMode extends ShapeMoveMode {

    private InfoBlock info = new InfoBlock();
    private boolean showBounds = false;
    private boolean showLineDir = false;

    public ValidateShapeMode() {
        super(1);
        zoom.set(10);

        addKeyBinding('b', () -> "show bounding box " + boolStr(showBounds),
                      () -> toggleBoundingBox());
    }

    private void toggleBoundingBox() {
        showBounds = !showBounds;
        softUpdateAndRepaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.MAGENTA);
        drawShape(g, shape());
        g.setColor(Color.WHITE);
        drawVertices(g, shape().getVertices(), 10);

        if (showBounds) {
            g.setColor(Color.CYAN);
            drawBox(g, shape().boundingBox());
        }
            
        // info
        info.reset();
        // info.add("   lines: " + shape().numLines(), Color.RED);
        // info.add("fragments: " + shape().numOutlines(), Color.RED);
        info.add(Color.RED, " vertices: " + shape().numVertices());
        info.paint(g);
    }
    
}
