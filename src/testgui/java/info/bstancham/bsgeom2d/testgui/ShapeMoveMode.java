package info.bstancham.bsgeom2d.testgui;

/**
 * Adds cursor control for moving shapes on screen.
 */
public abstract class ShapeMoveMode extends ShapeTestMode {

    public ShapeMoveMode(int numberOfDisplayShapes) {
        super(numberOfDisplayShapes);
    }

    @Override
    public void update() {
        if (editMode) {
            setCurrentVertex(cursorX(), cursorY());
        } else {
            shapeWrapper().setPosition(cursorX(), cursorY());
        }
    }
}
