package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.CanvasMode;
import info.bstancham.bsgeom2d.testgui.gui.CanvasPanel;
import info.bstancham.bsgeom2d.testgui.gui.KeyBinding;
import info.bstancham.bsgeom2d.testgui.gui.Gfx;
// import info.bstancham.bsgeom2d.testgui.gui.GfxTransform;
import info.bstancham.bsgeom2d.PointInt;
import info.bstancham.bsgeom2d.BoxInt;
import info.bstancham.bsgeom2d.LineInt;
import info.bstancham.bsgeom2d.Geom2D;
import info.bstancham.bsgeom2d.Geom2DInt;
import info.bstancham.bsgeom2d.ShapeInt;
import info.bstancham.bsgeom2d.ShapeGroupInt;
import info.bstancham.bsgeom2d.PolyInt;
import info.bstancham.bsgeom2d.TriInt;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * pixel-zooming --- draw grid at above 3
 *
 * cursor keys do mouse-pointer adjustment, one pixel at a time
 *
 *
 *
 * pixel-zoomed painting methods (maybe move to bsutil.gui.Gfx)
 *
 */
public class PixelZoomMode extends CanvasMode {

    private int cursorX = 0;
    private int cursorY = 0;
    private int offsetX = 0;
    private int offsetY = 0;
    protected IntParam zoom;
    protected Color gridCol  = new Color(0x111133);
    protected Color tenthCol = new Color(0x333355);
    protected Color axisCol  = new Color(0x4444AA);
    private boolean showGrid = true;

    public PixelZoomMode() {
        zoom = new IntParam("pixel-zoom", '-', '=', 1, 1, 50);
        addKeyBinding(zoom.getKeyBinding());
        addKeyBinding('g', () -> "show grid " + boolStr(showGrid),
                      () -> toggleShowGrid());
    }

    protected String boolStr(boolean val) {
        return "(" + (val ? "TRUE" : "FALSE" ) + ")";
    }

    private void toggleShowGrid() {
        showGrid = !showGrid;
        getCanvas().repaint();
    }

    @Override
    public void paint(Graphics g) {

        // grid
        if (showGrid) {
            if (zoom.get() > 2) {
                int nth = 10;
                int xStart = centreX() % zoom.get();
                int yStart = centreY() % zoom.get();
                // int xCount = centreX() % (zoom.get() * nth);
                int xCount = nth - ((centreX() / zoom.get()) % nth);
                int yCount = nth - ((centreY() / zoom.get()) % nth);
                int w = sizeX();
                int h = sizeY();
                for (int n = xStart; n < w; n += zoom.get()) {
                    g.setColor((xCount++ % nth == 0 ? tenthCol : gridCol));
                    g.drawLine(n, 0, n, h);
                }
                for (int n = yStart; n < h; n += zoom.get()) {
                    g.setColor((yCount++ % nth == 0 ? tenthCol : gridCol));
                    g.drawLine(0, n, w, n);
                }
            }

            // axes
            g.setColor(axisCol);
            g.drawLine(0, centreY(), sizeX(), centreY()); // horiz
            g.drawLine(centreX(), 0, centreX(), sizeY()); // vert
        }

        // cursor
        Gfx.crosshairs(g, Color.YELLOW,
                       toScreenX(cursorX()), toScreenY(cursorY()), 50);

        // cursor co-ordinates
        int x = sizeX() - 230;
        int y = 5;
        Gfx.textBlock(g, null, Color.GRAY,
                      new String[] {
                          String.format("CURSOR CO-ORDINATES: %d, %d",
                                        cursorX(), cursorY()),
                          String.format("GRID-CENTRE OFFSET:  %d, %d",
                                        offsetX, offsetY),
                      }, x, y);
    }



    /*------------------- screen and canvas geometry -------------------*/

    public int centreX() { return canvasCentreX() + offsetX; }
    public int centreY() { return canvasCentreY() - offsetY; }

    private int canvasCentreX() { return getCanvas().getSize().width / 2; }
    private int canvasCentreY() { return getCanvas().getSize().height / 2; }

    protected int sizeX() { return getCanvas().getSize().width; }
    protected int sizeY() { return getCanvas().getSize().height; }

    /**
     * Convert screen co-ordinate to canvas co-ordinate...
     */
    public int toCanvasX(int x) {
        return (int) Math.round((x - centreX())
                                / (zoom.get()
                                   + 0.0));
    }

    public int toCanvasY(int y) {
        return (int) Math.round(((sizeY() - y)
                                 - centreY())
                                / (zoom.get()
                                   + 0.0));
    }

    protected PointInt toCanvas(PointInt p) {
        return new PointInt(toCanvasX(p.x()), toCanvasY(p.y()));
    }

    /**
     * Convert canvas co-ordinate to screen co-ordinate...
     */
    public int toScreenX(int x) {
        return centreX() + (x * zoom.get());
    }

    public int toScreenY(int y) {
        return centreY() - (y * zoom.get());
    }

    protected PointInt toScreen(PointInt p) {
        return new PointInt(toScreenX(p.x()), toScreenY(p.y()));
    }

    public int zoom() { return zoom.get(); }




    /*----------------------- mouse and keyboard -----------------------*/

    protected int cursorX() { return cursorX; }
    protected int cursorY() { return cursorY; }

    protected PointInt cursorPoint() { return new PointInt(cursorX(), cursorY()); }
    // protected PointInt cursorPoint() {
    //     return new PointInt(cursorX(), cursorY());
    // }

    protected void setCursorPos(PointInt p) {
        setCursorPos(p.x(), p.y());
    }

    protected void setCursorPos(int x, int y) {
        cursorX = x;
        cursorY = y;
    }

    @Override
    public void mouseMoved(int x, int y) {
        super.mouseMoved(x, y);
        cursorX = toCanvasX(x);
        cursorY = toCanvasY(y);
        getCanvas().updateAndRepaint();
        // System.out.println("mouse pos: " + mouseX() + ", " + mouseY());
    }

    /**
     * Shifts grid-centre...
     */
    @Override
    public void mouseDragged(int x, int y) {
        // System.out.println("mouse pos: " + mouseX() + ", " + mouseY());
        // System.out.println("mouse dragged: " + x + ", " + y);
        shiftGrid(x - mouseX(), -(y - mouseY()));
        super.mouseMoved(x, y);
    }
    private void shiftGrid(int x, int y) {
        // System.out.println("... shift grid: " + x + ", " + y);
        offsetX += x;
        offsetY += y;
        getCanvas().repaint();
    }

    @Override
    public void pressedLeft() {
        cursorX -= 1;
        getCanvas().updateAndRepaint();
    }

    @Override
    public void pressedRight() {
        cursorX += 1;
        getCanvas().updateAndRepaint();
    }

    @Override
    public void pressedUp() {
        cursorY += 1;
        getCanvas().updateAndRepaint();
    }

    @Override
    public void pressedDown() {
        cursorY -= 1;
        getCanvas().updateAndRepaint();
    }





    /*------------- static painting methods (MOVE TO GFX) --------------*/

    //// LINES ////

    protected void line(Graphics g, LineInt l) {
        line(g, l.startX(), l.startY(), l.endX(), l.endY());
    }

    protected void line(Graphics g, PointInt start, PointInt end) {
        line(g, start.x(), start.y(), end.x(), end.y());
    }

    // need some sort of canvas-context object to handle scaling...
    // ... include interface in Gfx...
    protected void line(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(toScreenX(x1), toScreenY(y1),
                   toScreenX(x2), toScreenY(y2));
    }

    /**
     * Draws line with arrowhead at end.<br>
     * Does not draw arrowhead if line is degenerate.
     */
    protected void arrow(Graphics g, LineInt l) {
        line(g, l);
        if (!l.isDegenerate())
            arrowhead(g, toScreen(l.end()), -l.angle() + Geom2D.HALF_TURN, 30);
    }

    /**
     * @param p Screen co-ordinate for end of arrowhead.
     *
     */
    protected void arrowhead(Graphics g, PointInt p, double angle, int size) {
        double headAngle = Math.PI * 0.9;
        PointInt head1 = Geom2D.circlePoint(angle + headAngle, size).toInt();
        PointInt head2 = Geom2D.circlePoint(angle - headAngle, size).toInt();
        g.drawLine(p.x(), p.y(), p.sumX(head1), p.sumY(head1));
        g.drawLine(p.x(), p.y(), p.sumX(head2), p.sumY(head2));
    }

    protected void sideIndicatorLine(Graphics g, LineInt l, double angle) {
        arrow(g, l);
        // indicator arrow
        PointInt mid = Geom2DInt.midPoint(toScreen(l.start()), toScreen(l.end()));
        PointInt p = Geom2D.circlePoint(-l.angle() + angle, 60).toInt();
        LineInt side = new LineInt(mid.x(), mid.y(),
                                   mid.x() + p.x(), mid.y() + p.y());
        g.drawLine(side.startX(), side.startY(), side.endX(), side.endY());
        arrowhead(g,  side.end(), side.angle(), 15);
    }

    protected void leftSideIndicatorLine(Graphics g, LineInt l) {
        sideIndicatorLine(g, l, -Geom2D.QUARTER_TURN);
    }

    protected void rightSideIndicatorLine(Graphics g, LineInt l) {
        sideIndicatorLine(g, l, -Geom2D.THREE_QUARTER_TURN);
    }

    //// ANGLES ////

    protected void drawAngleTurned(Graphics g, PointInt a, PointInt b, PointInt c) {
        double refAngle = Geom2D.lineAngle(a, b);
        double angleTurned = Geom2D.angleTurned(a, b, c);
        g.setColor((angleTurned > 0 ? Color.GREEN : Color.RED));
        // continuation line
        PointInt cont = Geom2D.circlePoint(-(refAngle + Geom2D.HALF_TURN), 40).toInt();
        g.drawLine(toScreenX(b.x()),
                   toScreenY(b.y()),
                   toScreenX(b.x()) + cont.x(),
                   toScreenY(b.y()) + cont.y());
        // arc
        drawAngle(g, b, 30,
                  (int) -Math.toDegrees(refAngle) + 90,
                  (int) Math.toDegrees(angleTurned));
    }

    protected void drawAngle(Graphics g, PointInt p, int radius, int startAngle, int angle) {
        if (angle % 90 == 0) {
            int rAngle = 0;
            while (rAngle < Math.abs(angle)) {
                int adjusted = (angle > 0 ? startAngle + 90 + rAngle
                                           : startAngle - rAngle);
                drawRightAngle(g, p, radius, adjusted);
                rAngle += 90;
            }
            // for (int i = 0; i <= angle; i += 90)
        } else {
            g.drawArc(toScreenX(p.x()) - radius,
                      toScreenY(p.y()) - radius,
                      radius * 2, radius * 2, startAngle, angle);
        }
    }

    /**
     * Draw right angle as square.
     *
     * @TODO: fix!
     */
    protected void drawRightAngle(Graphics g, PointInt p, int radius, int startAngle) {
        // System.out.println("right angle: starting at " + startAngle);
        PointInt centre = new PointInt(toScreenX(p.x()), toScreenY(p.y()));
        PointInt p1 = Geom2D.circlePoint(Math.toRadians(startAngle), radius).toInt();
        PointInt p2 = Geom2D.circlePoint(Math.toRadians(startAngle + 90), radius).toInt();
        PointInt p3 = p1.sum(p2);
        PointInt a = centre.sum(p1);
        PointInt b = centre.sum(p2);
        PointInt c = centre.sum(p3);
        // drawCrosshairs(g, a, 10);
        // drawCrosshairs(g, b, 10);
        // drawCrosshairs(g, c, 10);
        g.drawLine(a.x(), a.y(), c.x(), c.y());
        g.drawLine(b.x(), b.y(), c.x(), c.y());
    }



    //// POLYGONS ////

    protected void drawPoly(Graphics g, PolyInt s) {
        for (LineInt l : s.getEdges()) line(g, l);
    }

    protected void fillPoly(Graphics g, PolyInt s) {
        int[] xs = new int[s.numVertices()];
        int[] ys = new int[s.numVertices()];
        for (int i = 0; i < s.numVertices(); i++) {
            xs[i] = toScreenX(s.vertex(i).x());
            ys[i] = toScreenY(s.vertex(i).y());
        }
        g.fillPolygon(xs, ys, s.numVertices());
    }

    protected void drawPolyWithWinding(Graphics g, PolyInt s) {
        for (int i = 0; i <= s.numVertices() - 1; i++) {
            int iPrev = (i < 1 ? s.numVertices() - 1 : i - 1);
            int iNext = (i < s.numVertices() - 1 ? i + 1 : 0);
            g.setColor(Color.MAGENTA);
            line(g, s.edge(i));
            if (!(s.edge(i).isDegenerate()))
                drawAngleTurned(g, s.vertex(iPrev),
                                   s.vertex(i),
                                   s.vertex(iNext));
        }
    }

    //// SHAPES ////

    protected void drawShape(Graphics g, ShapeGroupInt s) {
        for (ShapeInt sub : s)
            for (PolyInt poly : sub)
                drawPoly(g, poly);
    }

    protected void fillShape(Graphics g, ShapeGroupInt s) {
        for (ShapeInt sub : s)
            fillSubShape(g, sub);
    }

    protected void fillSubShape(Graphics g, ShapeInt s) {
        fillPoly(g, s.outline());
        if (s.isPerforated()) {
            Color fg = g.getColor();
            g.setColor(getCanvas().backgroundColor());
            Iterator<PolyInt> holesIter = s.holesIterator();
            while (holesIter.hasNext()) {
                fillPoly(g, holesIter.next());
            }
            g.setColor(fg);
        }
    }

    protected void drawShapeWithWinding(Graphics g, ShapeGroupInt s) {
        for (ShapeInt sub : s)
            for (PolyInt poly : sub)
                drawPolyWithWinding(g, poly);
    }

    //// MISC ////

    protected void drawBox(Graphics g, BoxInt b) {
        line(g, b.left(), b.top(), b.right(), b.top());
        line(g, b.left(), b.bottom(), b.right(), b.bottom());
        line(g, b.left(), b.top(), b.left(), b.bottom());
        line(g, b.right(), b.top(), b.right(), b.bottom());
        // diagonals
        line(g, b.left(), b.top(), b.right(), b.bottom());
        line(g, b.left(), b.bottom(), b.right(), b.top());
    }

    protected void drawVertices(Graphics g, PointInt[] vertices, int size) {
        for (PointInt p : vertices)
            drawCrosshairs(g, p, size);
    }

    protected void drawCrosshairs(Graphics g, PointInt p, int size) {
        Gfx.crosshairs(g, toScreenX(p.x()), toScreenY(p.y()), size);
    }

   protected void drawTriangle(Graphics g, TriInt tri) {
        line(g, tri.a(), tri.b());
        line(g, tri.b(), tri.c());
        line(g, tri.c(), tri.a());
    }




    /*---------------- params (move somewhere sensible) ----------------*/

    public static class ListParam<T> {
        private ArrayList<T> items = new ArrayList<>();
        private int index = 0;
        private char decrChar;
        private char incrChar;
        private String name;
        private CanvasMode mode;

        public ListParam(char decr, char incr, String name, CanvasMode mode) {
            decrChar = decr;
            incrChar = incr;
            this.name = name;
            this.mode = mode;
        }

        public T current() { return items.get(index); }

        // @SuppressWarnings("varargs")
        @SafeVarargs
        public final void add(T ... newItems) {
            for (T item : newItems) items.add(item);
        }

        public void incr(int amt) {
            index += amt;
            if (index < 0) index = items.size() - 1;
            if (index >= items.size()) index = 0;
        }

        public KeyBinding[] getKeyBindings() {
            return new KeyBinding[] {
                new KeyBinding(decrChar, "decrement " + name,
                               () -> { incr(-1); mode.getCanvas().updateAndRepaint(); }),
                new KeyBinding(incrChar, "increment " + name,
                               () -> { incr(1); mode.getCanvas().updateAndRepaint(); })
            };
        }
    }



    /*--------------------------- INFO-BLOCK ---------------------------*/

    /**
     * TODO: move InfoBlock class to Gfx...
     */
    class InfoBlock {

        private ArrayList<String> lines = new ArrayList<>();
        private ArrayList<Color> colors = new ArrayList<>();
        private Color lastColor = Color.GRAY;
        private int step = 15;
        private int longest = 0;
        private String separatorTag = "<SEPARATOR>";

        public void reset() {
            lines = new ArrayList<String>();
            colors = new ArrayList<Color>();
            longest = 0;
        }

        public void add(String ... newLines) {
            add(lastColor, newLines);
        }

        public void add(Color newColor, String ... newLines) {
            for (String line : newLines) {
                lines.add(line);
                colors.add(newColor);
                lastColor = newColor;
                longest = Math.max(longest, line.length());
            }
        }

        public void addSeparator(Color c) {
            lines.add(separatorTag);
            colors.add(c);
        }

        /**
         * Paints all info in bottom-left of screen.
         */
        public void paint(Graphics g) {
            int y = sizeY() - 20 - (step * lines.size());
            for (int i = 0; i < lines.size(); i++)
                Gfx.textBlock(g, null, colors.get(i),
                              (lines.get(i) == separatorTag ? separator() : lines.get(i)),
                              10, y + (i * step));
        }

        private String separator() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < longest; i++) sb.append('-');
            return sb.toString();
        }
    }

}
