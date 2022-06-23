package info.bstancham.bsgeom2d.testgui;

import info.bstancham.bsgeom2d.testgui.gui.*;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Displays regular polygons.
 */
public class RegularPolygonMode extends CanvasMode {

    private int radius = 200;
    private int radius2 = 50;
    private int numSides = 5;
    private int painterIndex = 0;
    private PolyPainter[] painters = new PolyPainter[] {
        new RegularPolygon(),
        new RegularSpindle(),
        new RegularStar(),
        new Gear(),
        new Sunbeam(),
        new RegularMultiPolygon()
    };
    private double skew = 0;
    private double skewStep = 0.01;
    private Color polyColor = Color.WHITE;

    public RegularPolygonMode() {
        addKeyBinding('s',
                      () -> "change shape (" + getPainter().getClass().getSimpleName() + ")",
                      () -> { painterIndex++;
                              if (painterIndex >= painters.length) painterIndex = 0;
                              getCanvas().repaint(); });

        addKeyBinding('o', 'p',
                      () -> "-/+ number of sides (" + numSides + ")",
                      () -> { numSides--;
                              if (numSides < 3) numSides = 3;
                              getCanvas().repaint(); },
                      () -> { numSides++;
                              getCanvas().repaint(); });

        addKeyBinding('l', ';',
                      () -> "-/+ radius (" + radius + ")",
                      () -> { radius--; getCanvas().repaint(); },
                      () -> { radius++; getCanvas().repaint(); });

        addKeyBinding('.', '/',
                      () -> "-/+ inner radius (" + radius2 + ")",
                      () -> { radius2--; getCanvas().repaint(); },
                      () -> { radius2++; getCanvas().repaint(); });

        addKeyBinding('[', ']',
                      () -> "-/+ inner radius skew (" + skew + ")",
                      () -> { skew -= skewStep; getCanvas().repaint(); },
                      () -> { skew += skewStep; getCanvas().repaint(); });

        addKeyBinding('c', () -> "random colour",
                      () -> { polyColor = Gfx.randomColor();
                              getCanvas().repaint(); });
    }

    @Override
    public void mouseMoved(int x, int y) {
        super.mouseMoved(x, y);
        getCanvas().repaint();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(polyColor);
        getPainter().paint(g, getCanvas().centreX(), getCanvas().centreY());
        getPainter().paint(g, mouseX(), mouseY());
    }

    private PolyPainter getPainter() { return painters[painterIndex]; }

    private interface PolyPainter {
        public void paint(Graphics g, int x, int y);
    }

    private class RegularPolygon implements PolyPainter {
        public void paint(Graphics g, int x, int y) {
            Gfx.regularPolygon(g, x, y, numSides, radius);
        }
    }

    private class RegularMultiPolygon extends RegularPolygon {
        public void paint(Graphics g, int x, int y) {
            int numSteps = 5;
            int step = (radius - radius2) / numSteps;
            for (int i = 0; i <= numSteps; i++) {
                Gfx.regularPolygon(g, x, y, numSides, radius - (i * step));
            }
        }
    }

    private class RegularSpindle implements PolyPainter {
        public void paint(Graphics g, int x, int y) {
            double step = (Math.PI * 2) / numSides;
            for (int i = 0; i < numSides; i++) {
                int x1 = (int) (x + Math.sin(i * step) * radius);
                int y1 = (int) (y + Math.cos(i * step) * radius);
                int x2 = (int) (x + Math.sin((i * step) + skew) * radius2);
                int y2 = (int) (y + Math.cos((i * step) + skew) * radius2);
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    private class RegularStar implements PolyPainter {
        public void paint(Graphics g, int x, int y) {
            double step = (Math.PI * 2) / numSides;
            for (int i = 0; i < numSides; i++) {
                int x1 = (int) (x + Math.sin(i * step) * radius);
                int y1 = (int) (y + Math.cos(i * step) * radius);
                int x2 = (int) (x + Math.sin(((i + 0.5) * step) + skew) * radius2);
                int y2 = (int) (y + Math.cos(((i + 0.5) * step) + skew) * radius2);
                int x3 = (int) (x + Math.sin((i + 1) * step) * radius);
                int y3 = (int) (y + Math.cos((i + 1) * step) * radius);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x2, y2, x3, y3);
            }
        }
    }

    private class Gear implements PolyPainter {
        public void paint(Graphics g, int x, int y) {
            double step = (Math.PI * 2) / numSides;
            double offset = 0;
            for (int i = 0; i < numSides; i++) {
                int x1 = (int) (x + Math.sin((i + offset - 0.125) * step) * radius);
                int y1 = (int) (y + Math.cos((i + offset - 0.125) * step) * radius);
                int x2 = (int) (x + Math.sin((i + offset + 0.125) * step) * radius);
                int y2 = (int) (y + Math.cos((i + offset + 0.125) * step) * radius);
                int x3 = (int) (x + Math.sin(((i + offset + 0.375) * step) + skew) * radius2);
                int y3 = (int) (y + Math.cos(((i + offset + 0.375) * step) + skew) * radius2);
                int x4 = (int) (x + Math.sin(((i + offset + 0.625) * step) + skew) * radius2);
                int y4 = (int) (y + Math.cos(((i + offset + 0.625) * step) + skew) * radius2);
                int x5 = (int) (x + Math.sin((i + offset + 0.875) * step) * radius);
                int y5 = (int) (y + Math.cos((i + offset + 0.875) * step) * radius);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x2, y2, x3, y3);
                g.drawLine(x3, y3, x4, y4);
                g.drawLine(x4, y4, x5, y5);
            }
        }
    }

    private class Sunbeam implements PolyPainter {
        public void paint(Graphics g, int x, int y) {
            double step = (Math.PI * 2) / numSides;
            double offset = -0.25;
            for (int i = 0; i < numSides; i++) {
                int x1 = (int) (x + Math.sin((i + offset) * step) * radius);
                int y1 = (int) (y + Math.cos((i + offset) * step) * radius);
                int x2 = (int) (x + Math.sin((i + offset + 0.5) * step) * radius);
                int y2 = (int) (y + Math.cos((i + offset + 0.5) * step) * radius);
                int x3 = (int) (x + Math.sin(((i + offset + 0.5) * step) + skew) * radius2);
                int y3 = (int) (y + Math.cos(((i + offset + 0.5) * step) + skew) * radius2);
                int x4 = (int) (x + Math.sin(((i + offset + 1) * step) + skew) * radius2);
                int y4 = (int) (y + Math.cos(((i + offset + 1) * step) + skew) * radius2);
                int x5 = (int) (x + Math.sin((i + offset + 1) * step) * radius);
                int y5 = (int) (y + Math.cos((i + offset + 1) * step) * radius);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x2, y2, x3, y3);
                g.drawLine(x3, y3, x4, y4);
                g.drawLine(x4, y4, x5, y5);
            }
        }
    }
}
