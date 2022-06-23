package info.bstancham.bsgeom2d.testgui.gui;

/**
 * - scale
 * - centre-offset
 * - flip y-dimension
 */
public class GfxTransform {

    private int unitSize = 4;
    private int centerX = 0;
    private int centerY = 0;
    private int width = 100;
    private int height = 100;

    // public GfxTransform(int width, int height) {
    //     this.width = width;
    //     this.height = height;
    //     centerX = width / 2;
    //     centerY = height / 2;
    // }

    /**
     * The unit size variable affects many painting methods.    private int centerX = 0;

     * @return The current unit size.
     */
    public int getUnitSize() { return unitSize; }
    public void setUnitSize(int val) { unitSize = val; }

    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }
    public void setCenterX(int val) { centerX = val; }
    public void setCenterY(int val) { centerY = val; }
    public void setCenter(int x, int y) {
        centerX = x;
        centerY = y;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setSize(int x, int y) {
        width = x;
        height = y;
    }

    /**
     * @return The 2D X co-ordinate for isometric projection of the
     * input co-ordinates.
     */
    public int getIsoX(int x, int y, int z) {
        return getCenterX() - (x * getUnitSize()) + (z * getUnitSize());
    }

    /**
     * @return The 2D Y co-ordinate for isometric projection of the
     * input co-ordinates.
     */
    public int getIsoY(int x, int y, int z) {
        return getCenterY() - (y * getUnitSize()) - (z * getUnitSize()) - (x * getUnitSize());
    }



    // public int zoom();
    // public int toCanvasX(int x);
    // public int toCanvasY(int y);
    // public int toScreenX(int x);
    // public int toScreenY(int y);
}

// public class GfxTransform {

//     private int zoom = 1;

//     private IntFunc centreX = IntFunc.val(0);
//     private IntFunc centreY = IntFunc.val(0);

//     public void setCentreXFunc(IntFunc f) { centreX = f; }
//     public void setCentreYFunc(IntFunc f) { centreY = f; }

//     public int centreX() { return centreX.get(); }
//     public int centreY() { return centreY.get(); }

//     /**
//      * Convert screen co-ordinate to canvas co-ordinate...
//      */
//     protected int toCanvasX(int x) {
//         return (x - centreX()) / zoom.get();
//     }

//     protected int toCanvasY(int y) {
//         return (y - centreY()) / zoom.get();
//     }

//     /**
//      * Convert canvas co-ordinate to screen co-ordinate...
//      */
//     protected int toScreenX(int x) {
//         return centreX() + (x * zoom.get());
//     }

//     protected int toScreenY(int y) {
//         return centreY() + (y * zoom.get());
//     }

//     // protected PointInt toCanvasPt(PointInt p) {
//     //     return new PointInt(toCanvasX(p.x()), toCanvasY(p.y()));
//     // }

//     // protected PointInt toScreenPt(PointInt p) {
//     //     return new PointInt(toScreenX(p.x()), toScreenY(p.y()));
//     // }





//     // public int getScale() { return scale.get(); }


//     // /**
//     //  * Convert screen co-ordinate into canvas co-ordinate.
//     //  */
//     // public int toCanvasX(int x) {}
//     // public int toCanvasY(int x) {}

//     // /**
//     //  * Convert canvas co-ordinate into screen co-ordinate.
//     //  */
//     // public int toScreenX(int x) {}
//     // public int toScreenY(int x) {}















//     // protected int centreX() { return getCanvas().getSize().width / 2; }
//     // protected int centreY() { return getCanvas().getSize().height / 2; }
//     // protected int sizeX() { return getCanvas().getSize().width; }
//     // protected int sizeY() { return getCanvas().getSize().height; }




// }
