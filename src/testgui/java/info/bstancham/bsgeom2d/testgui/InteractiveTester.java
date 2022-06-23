package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.*;
import info.bstancham.bsgeom2d.ShapeInt;
import info.bstancham.bsgeom2d.PolyInt;

/**
 * A swing-based interactive testing program for the bsgeom2d library.
 *
 * The main method launches a GUI with inbuilt help.
 *
 * Switch between the modes to test different aspects of the bsgeom2d library.
 *
 *
 *
 * TODO;
 * - CanvasPanel in separate class...
 * - CanvasFrame alternative constructor takes a CanvasPanel parameter...
 * - extend CanvasPanel to make PolygonTestPanel...
 *   ... maintains an array of Polygons...
 *   ... provides controls for switching between polygons in list...
 *   ... edit each polygon, or reset it to default values...
 */
public class InteractiveTester {

    private static void createAndShowGUI() {
        CanvasFrame frame = new CanvasFrame(1000, 800,
                                            "Interactive Tester for bsgeom2d",
                                            new ModeSwitchingPanel());
        frame.getCanvas().setModes(new CanvasMode[] {
                //new RedSquareMode(),
                new RegularPolygonMode(),
                new LineAngleMode(),
                new RelativeLeftHandSideMode(),
                new LineIntersectionMode(),
                new ValidateShapeMode(),
                new PolygonWindingMode(),
                new TriangulateShapesMode(),
                new IntersectLineAndShapeMode(),
                new IntersectShapesMode(),
                new InsideShapeMode(),
                new TiledShapeMode(),
                new TiledShapeEditMode(),
                new GameOfLife(),
            });
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("running InteractiveTester...");
        // Schedule a job for the event-dispatching thread:
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() { createAndShowGUI(); }
        });
    }
}
