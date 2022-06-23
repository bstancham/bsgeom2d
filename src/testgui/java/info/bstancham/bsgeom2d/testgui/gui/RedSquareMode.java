package info.bstancham.bsgeom2d.testgui.gui;

import java.awt.Color;
import java.awt.Graphics;

/**
 * A simple class which demonstrates ways to use the CanvasMode features.
 */
public class RedSquareMode extends CanvasMode {

    private int squareX = 50;
    private int squareY = 50;
    private int squareW = 20;
    private int squareH = 20;
    private Color fillColor = Color.RED;
    private Color outlineColor = Color.WHITE;
    private String[] instructions = new String[] {
        "Click and drag to move the square."
    };

    public RedSquareMode() {
        addKeyBinding(new KeyBinding('c', "new random colour", () -> {
                    fillColor = Gfx.randomColor();
                    outlineColor = Gfx.randomColor();
                    // System.out.println("... new colour: fill=" + fillColor + " outline=" + outlineColor);
                    getCanvas().repaint();
        }));
    }

    @Override
    public String[] getUserInstructions() { return instructions; }

    @Override
    public void paint(Graphics g) {
        // Draw some stuff in the background
        //g.drawString("RedSquareMode",10,20);
        g.setColor(Color.YELLOW);
        g.drawLine(squareX, squareY, mouseX(), mouseY());
        // g.drawOval(200, 30, 100, 150);
        // paint the red square
        g.setColor(fillColor);
        g.fillRect(squareX,squareY,squareW,squareH);
        g.setColor(outlineColor);
        g.drawRect(squareX,squareY,squareW,squareH);
    }

    @Override
    public void mousePressed(int x, int y) {
        moveSquare(x, y);
        getCanvas().repaint();
    }

    @Override
    public void mouseDragged(int x, int y) {
        moveSquare(x, y);
        getCanvas().repaint();
    }

    private void moveSquare(int x, int y) {
        int offset = 1;
        if (squareX != x || squareY != y) {
            // repaint old position
            getCanvas().repaint(squareX, squareY, squareW + offset, squareH + offset);
            squareX = x;
            squareY = y;
            // return epaint new position
            getCanvas().repaint(squareX, squareY, squareW + offset, squareH + offset);
        }
    }
}
