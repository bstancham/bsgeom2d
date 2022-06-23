package info.bstancham.bsgeom2d.testgui;

import java.awt.Graphics;

/**
 * Conway's Game of Life.
 *
 * Cellular automata...
 *
 */
public class GameOfLife extends PixelZoomMode {

    public GameOfLife() {
        zoom.set(20);
    }

    @Override
    public void paint (Graphics g) {
        super.paint(g);
    }
}
