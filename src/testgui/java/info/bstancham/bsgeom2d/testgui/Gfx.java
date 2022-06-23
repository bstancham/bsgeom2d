package info.bstancham.bsgeom2d.testgui.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Static painting methods...
 *
 * ... note on transform...
 */
public final class Gfx {

    /*----------------------------- COLOR ------------------------------*/

    /**
     * @return A new random color generated using RGB values between 0
     * and 255.
     */
    public static Color randomColor() {
        return new Color((int) (Math.random() * 255),
                         (int) (Math.random() * 255),
                         (int) (Math.random() * 255));
    }

    /**
     * @param c The input color.
     * @return A copy of the input color, with the RGB values inverted.
     */
    public static Color invertColor(Color c) {
        return new Color(255 - c.getRed(),
                         255 - c.getGreen(),
                         255 - c.getBlue());
    }

    /**
     * @param c The input color.
     * @param amount The amount to adjust brightness by. Value of 1.0
     * will be just the same as the input color. Value may be larger
     * than 1.0.
     * @return A new color based on the input color, but with relative
     * brightness adjusted.
     */
    public static Color relativeBrightness(Color c, double amount) {
        int rVal = (int)(c.getRed() * amount);   if (rVal > 255) { rVal = 255; }
        int gVal = (int)(c.getGreen() * amount); if (gVal > 255) { gVal = 255; }
        int bVal = (int)(c.getBlue() * amount);  if (bVal > 255) { bVal = 255; }
        return new Color(rVal, gVal, bVal);
    }



    /*----------------------------- SHAPES -----------------------------*/

    public static void crosshairs(Graphics g, Color col, int x, int y, int size) {
        g.setColor(col);
        crosshairs(g, x, y, size);
    }

    public static void crosshairs(Graphics g, int x, int y, int size) {
        g.drawLine(x - size/2, y, x + size/2, y); // horiz
        g.drawLine(x, y - size/2, x, y + size/2); // vert
    }

    public static void circle(Graphics g, Color col, int x, int y, int size) {
        g.setColor(col);
        g.drawOval(x - size/2, y - size/2, size, size);
    }

    public static void regularPolygon(Graphics g, int x, int y, int numSides, int radius) {
        double step = (Math.PI * 2) / numSides;
        for (int i = 0; i < numSides; i++) {
            int x1 = (int) (x + Math.sin(i * step) * radius);
            int y1 = (int) (y + Math.cos(i * step) * radius);
            int x2 = (int) (x + Math.sin((i + 1) * step) * radius);
            int y2 = (int) (y + Math.cos((i + 1) * step) * radius);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    // public static void line(Graphics g, LineInt ln) {
    // }



    /*------------------------------ TEXT ------------------------------*/

    /**
     * The font used by most text drawing methods.
     */
    public static final Font FIXED_FONT = new Font("monospaced", Font.PLAIN, 12);

    /**
     * Passes a single string to the textBlock method.
     */
    public static void textBlock(Graphics g, Color bgCol, Color textCol,
                                 String text, int x, int y) {
        textBlock(g, bgCol, textCol, new String[] { text }, x, y);
    }

    /**
     * Paints a block of text from an array of strings.
     *
     * If you don't want the background box to be painted, make the
     * bgCol parameter null.
     *
     * @param disp A <code>Display</code> instance.

     * @param bgCol The background color. If this is null, no
     * background will be drawn.
     * @param textCol The text color.
     * @param text The text to be printed.
     * @param x The position of the top left corner of the text box.
     * @param y The position of the top left corner of the text box.
     */
    public static void textBlock(Graphics g, Color bgCol, Color textCol,
                                 String[] text, int x, int y) {
        int vertStep = 20;
        int horizPad = 10;
        int vertPad = 10;
        // do we need to paint the box?
        if (bgCol != null) {
            // calculate box dimensions from text array
            int longest = 0;
            for (String line : text)
                if (line.length() > longest)
                    longest = line.length();
            int xDim = (int)(longest * 7) + (horizPad * 2);
            int yDim = text.length * vertStep;
            // paint box
            g.setColor(bgCol);
            g.fillRect(x, y, xDim, yDim + (vertPad * 2));
        }
        // paint text
        g.setColor(textCol);
        g.setFont(FIXED_FONT);
        int yPos = y + (int)(vertStep * 0.66);
        for (String line : text) {
            g.drawString(line, x + horizPad, yPos + vertPad);
            yPos += vertStep;
        }
    }




    /*------------------------ ISOMETRIC BLOCKS ------------------------*/

    /**
     * Paints an isometric cube at the specified position.
     */
    public static void isoCube(Graphics g, GfxTransform trans, Color col,
                               Color outline, int x, int y, int z) {
        isoTopFace(g, trans, col, outline, x, y, z);
        isoLeftFace(g, trans, col, outline, x, y, z);
        isoRightFace(g, trans, col, outline, x, y, z);
    }

    /**
     * Paints a placemarker for an unknown type of block.
     */
    public static void isoBlockUnknown(Graphics g, GfxTransform trans,
                                       int x, int y, int z) {
        int xPos = trans.getIsoX(x, y, z);
        int yPos = trans.getIsoY(x, y, z);
        g.setColor(Color.RED);
        g.drawString("?", xPos, yPos);
    }

    //// XY TRIANGLE BLOCKS ////

    public static void isoTriX0Y1(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoDiagSquareFaceX0Y1(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceX0Y1(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX0Y0(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoTopFace(g, trans , col, outlineCol, x,y,z);
        isoTriHalfFaceX0Y0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX1Y1(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoRightFace(g, trans, col, outlineCol, x,y,z);
        isoDiagSquareFaceX1Y1(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceX1Y1(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX1Y0(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoTopFace(g, trans, col, outlineCol, x,y,z);
        isoRightFace(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceX1Y0(g, trans, col, outlineCol, x,y,z);
    }


    //// YZ TRIANGLE BLOCKS ////

    public static void isoTriY1Z0(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoDiagSquareFaceZ0Y1(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceY1Z0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriY0Z0(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoTopFace(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceY0Z0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriY1Z1(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoLeftFace(g, trans, col, outlineCol, x,y,z);
        isoDiagSquareFaceZ1Y1(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceY1Z1(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriY0Z1(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoTopFace(g, trans, col, outlineCol, x,y,z);
        isoLeftFace(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceY0Z1(g, trans, col, outlineCol, x,y,z);
    }



    //// ZX TRIANGLE BLOCKS ////

    public static void isoTriZ0X1(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoRightFace(g, trans, col, outlineCol, x,y,z);
	isoTriHalfFaceZ0X1(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriZ0X0(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoDiagSquareFaceZ0X0(g, trans, col, outlineCol, x,y,z);
	isoTriHalfFaceZ0X0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriZ1X1(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
        isoLeftFace(g, trans, col, outlineCol, x,y,z);
        isoRightFace(g, trans, col, outlineCol, x,y,z);
	isoTriHalfFaceZ1X1(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriZ1X0(Graphics g, GfxTransform trans,
                                  Color col, Color outlineCol, int x, int y, int z) {
	isoLeftFace(g, trans, col, outlineCol, x,y,z);
	isoTriHalfFaceZ1X0(g, trans, col, outlineCol, x,y,z);
    }



    //// XYZ TRIANGLE BLOCKS ////

    public static void isoTriX0Y0Z0(Graphics g, GfxTransform trans,
                                    Color col, Color outlineCol, int x, int y, int z) {
	isoTriHalfFaceZ0X0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX0Y0Z1(Graphics g, GfxTransform trans,
                                    Color col, Color outlineCol, int x, int y, int z) {
	isoTriHalfFaceZ1X0(g, trans, col, outlineCol, x,y,z);
	isoTriHalfFaceX0Y0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX0Y1Z0(Graphics g, GfxTransform trans,
                                    Color col, Color outlineCol, int x, int y, int z) {
	isoTriFaceX0Y1Z0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX0Y1Z1(Graphics g, GfxTransform trans,
                                    Color col, Color outlineCol, int x, int y, int z) {
	isoTriHalfFaceX0Y1(g, trans, col, outlineCol, x,y,z);
	isoTriFaceX0Y1Z1(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX1Y0Z0(Graphics g, GfxTransform trans,
                                    Color col, Color outlineCol, int x, int y, int z) {
	isoTriHalfFaceZ0X1(g, trans, col, outlineCol, x,y,z);
	isoTriHalfFaceY0Z0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX1Y0Z1(Graphics g, GfxTransform trans,
                                    Color col, Color outlineCol, int x, int y, int z) {
	isoTriHalfFaceZ1X1(g, trans, col, outlineCol, x,y,z);
	isoTriHalfFaceX1Y0(g, trans, col, outlineCol, x,y,z);
	isoTriHalfFaceY0Z1(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX1Y1Z0(Graphics g, GfxTransform trans,
                                    Color col, Color outlineCol, int x, int y, int z) {
	isoTriHalfFaceY1Z0(g, trans, col, outlineCol, x,y,z);
	isoTriFaceX1Y1Z0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX1Y1Z1(Graphics g, GfxTransform trans,
                                    Color col, Color outlineCol, int x, int y, int z) {
	isoTriHalfFaceX1Y1(g, trans, col, outlineCol, x,y,z);
	isoTriHalfFaceY1Z1(g, trans, col, outlineCol, x,y,z);
    }



    //// XYZ-MAX TRIANGLE BLOCKS ////

    public static void isoTriX0Y0Z0max(Graphics g, GfxTransform trans,
                                       Color col, Color outlineCol, int x, int y, int z) {
        isoTopFace(g, trans, col, outlineCol, x, y, z);
        isoTriHalfFaceY0Z0(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceX0Y0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX0Y0Z1max(Graphics g, GfxTransform trans,
                                       Color col, Color outlineCol, int x, int y, int z) {
        isoTopFace(g, trans, col, outlineCol, x, y, z);
        isoLeftFace(g, trans, col, outlineCol, x, y, z);
        isoTriHalfFaceY0Z1(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX0Y1Z0max(Graphics g, GfxTransform trans,
                                       Color col, Color outlineCol, int x, int y, int z) {
        isoTriHalfFaceZ0X0(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceX0Y1(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceY1Z0(g, trans, col, outlineCol, x,y,z);
        isoTriFaceX0Y1Z0max(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX0Y1Z1max(Graphics g, GfxTransform trans,
                                       Color col, Color outlineCol, int x, int y, int z) {
        isoLeftFace(g, trans, col, outlineCol, x, y, z);
        isoTriHalfFaceZ1X0(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceY1Z1(g, trans, col, outlineCol, x,y,z);
        isoTriFaceX0Y1Z1max(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX1Y0Z0max(Graphics g, GfxTransform trans,
                                       Color col, Color outlineCol, int x, int y, int z) {
        isoTopFace(g, trans, col, outlineCol, x, y, z);
        isoRightFace(g, trans, col, outlineCol, x, y, z);
        isoTriHalfFaceX1Y0(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX1Y0Z1max(Graphics g, GfxTransform trans,
                                       Color col, Color outlineCol, int x, int y, int z) {
        isoCube(g, trans, col, outlineCol, x, y, z);
    }
    public static void isoTriX1Y1Z0max(Graphics g, GfxTransform trans,
                                       Color col, Color outlineCol, int x, int y, int z) {
        isoRightFace(g, trans, col, outlineCol, x, y, z);
        isoTriHalfFaceX1Y1(g, trans, col, outlineCol, x,y,z);
        isoTriHalfFaceZ0X1(g, trans, col, outlineCol, x,y,z);
        isoTriFaceX1Y1Z0max(g, trans, col, outlineCol, x,y,z);
    }
    public static void isoTriX1Y1Z1max(Graphics g, GfxTransform trans,
                                       Color col, Color outlineCol, int x, int y, int z) {
        isoLeftFace(g, trans, col, outlineCol, x, y, z);
        isoRightFace(g, trans, col, outlineCol, x, y, z);
        isoTriHalfFaceZ1X1(g, trans, col, outlineCol, x,y,z);
    }





    /**
     * Face painting engine for isometric block methods.
     */
    private static void isoFacePolygon(Graphics g, GfxTransform trans,
                                       Color col, Color outlineCol, double brightness,
                                       int x, int y, int z,
                                       int[] xCoords, int[] yCoords) {
        int xPos = trans.getIsoX(x, y, z);
        int yPos = trans.getIsoY(x, y, z);
        //// POLYGON CO-ORDINATES
        int[] xVals = makeCoords(trans.getUnitSize(), xPos, xCoords);
        int[] yVals = makeCoords(trans.getUnitSize(), yPos, yCoords);
        //// DRAW FILLED POLYGON
        g.setColor(relativeBrightness(col, brightness));
        g.fillPolygon(xVals, yVals, xCoords.length);
        //// DRAW OUTLINES
        g.setColor(outlineCol);
        g.drawPolygon(xVals, yVals, xCoords.length);
    }

    private static int[] makeCoords(int scale, int offset, int[] vals) {
	int[] out = new int[vals.length];
	for (int i = 0; i < vals.length; i++)
	    out[i] = makeCoord(scale, offset, vals[i]);
	return out;
    }
    private static int makeCoord(int scale, int offset, int val) {
	return (val * scale) + offset;
    }



    //// SQUARE FACES ////

    private static void isoTopFace(Graphics g, GfxTransform trans,
                                Color col, Color outline, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outline, 1.0, x,y,z,
                       new int[] { -1, 0, 1, 0 },
                       new int[] { -1, -2, -1, 0 });
    }

    private static void isoLeftFace(Graphics g, GfxTransform trans,
                                Color col, Color outline, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outline, 0.25, x,y,z,
                       new int[] { -1, 0, 0, -1 },
                       new int[] { -1, 0, 1, 0 });
    }

    private static void isoRightFace(Graphics g, GfxTransform trans,
                                Color col, Color outline, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outline, 0.5, x,y,z,
                       new int[] { 0, 1, 1, 0 },
                       new int[] { 0, -1, 0, 1 });
    }

    //// DIAGONAL FACES ////

    private static void isoDiagSquareFaceX0Y1(Graphics g, GfxTransform trans,
                                          Color col, Color outlineCol, int x, int y, int z) {
        isoFacePolygon(g, trans, col, outlineCol, 0.375, x,y,z,
                       new int[] { 0, -1, 0, 1 },
                       new int[] { 1, -1, -2, 0 });
    }

    private static void isoDiagSquareFaceX1Y1(Graphics g, GfxTransform trans,
                                          Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 1.4, x,y,z,
                       new int[] { 1, 0, -1, 0 },
                       new int[] { -1, -1, 0, 0 });
    }

    private static void isoDiagSquareFaceZ0Y1(Graphics g, GfxTransform trans,
                                          Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.375, x,y,z,
                       new int[] { 0, 1, 0, -1 },
                       new int[] { 1, -1, -2, 0 });
    }

    private static void isoDiagSquareFaceZ1Y1(Graphics g, GfxTransform trans,
                                          Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 1.4, x,y,z,
                       new int[] { -1, 0, 1, 0 },
                       new int[] { -1, -1, 0, 0 });
    }

    private static void isoDiagSquareFaceZ0X0(Graphics g, GfxTransform trans,
                                          Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.5, x,y,z,
                       new int[] { -1, 1, 1, -1 },
                       new int[] { -1, -1, 0, 0 });
    }

    //// XY TRIANGLE HALF-FACES ////

    private static void isoTriHalfFaceX1Y0(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.25, x,y,z,
                       new int[] { 0, -1, 0 },
                       new int[] { 1, -1, 0 });
    }

    private static void isoTriHalfFaceX1Y1(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.25, x,y,z,
                       new int[] { 0, 0, -1 },
                       new int[] { 1, 0, 0 });
    }

    private static void isoTriHalfFaceX0Y0(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.25, x,y,z,
                       new int[] { 0, -1, -1 },
                       new int[] { 0, -1, 0 });
    }

    private static void isoTriHalfFaceX0Y1(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.25, x,y,z,
                       new int[] { 0, -1, -1 },
                       new int[] { 1, -1, 0 });
    }

    //// ZY TRIANGLE HALF-FACES ////

    private static void isoTriHalfFaceY0Z1(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.5, x,y,z,
                       new int[] { 0, 1, 0 },
                       new int[] { 1, -1, 0 });
    }

    private static void isoTriHalfFaceY1Z1(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.5, x,y,z,
                       new int[] { 0, 0, 1 },
                       new int[] { 1, 0, 0 });
    }

    private static void isoTriHalfFaceY0Z0(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.5, x,y,z,
                       new int[] { 0, 1, 1 },
                       new int[] { 0, -1, 0 });
    }

    private static void isoTriHalfFaceY1Z0(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.5, x,y,z,
                       new int[] { 0, 1, 1 },
                       new int[] { 1, -1, 0 });
    }




    //// ZX TRIANGLE HALF-FACES ////

    private static void isoTriHalfFaceZ0X1(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 1.0, x,y,z,
                       new int[] { 0, 0, 1 },
                       new int[] { 0, -2, -1 });
    }

    private static void isoTriHalfFaceZ0X0(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 1.0, x,y,z,
                       new int[] { -1, 0, 1 },
                       new int[] { -1, -2, -1 });
    }

    private static void isoTriHalfFaceZ1X1(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 1.0, x,y,z,
                       new int[] { -1, 0, 1 },
                       new int[] { -1, 0, -1 });
    }

    private static void isoTriHalfFaceZ1X0(Graphics g, GfxTransform trans,
                                           Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 1.0, x,y,z,
                       new int[] { 0, -1, 0 },
                       new int[] { 0, -1, -2 });
    }



    //// XYZ TRIANGLE FACES ////

    private static void isoTriFaceX0Y1Z0(Graphics g, GfxTransform trans,
                                         Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.5, x,y,z,
                       new int[] { -1, 0, 1 },
                       new int[] { 0, -2, 0 });
    }

    private static void isoTriFaceX0Y1Z1(Graphics g, GfxTransform trans,
                                         Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 1.25, x,y,z,
                       new int[] { 0, 0, -1 },
                       new int[] { 1, -1, -1 });
    }

    private static void isoTriFaceX1Y1Z0(Graphics g, GfxTransform trans,
                                         Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.5, x,y,z,
                       new int[] { 0, 0, 1 },
                       new int[] { 1, -1, -1 });
    }



    //// XYZ MAX TRIANGLE FACES ////

    private static void isoTriFaceX0Y1Z0max(Graphics g, GfxTransform trans,
                                            Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.5, x,y,z,
                       new int[] { -1, 0, 1 },
                       new int[] { -1, 1, -1 });
    }

    private static void isoTriFaceX0Y1Z1max(Graphics g, GfxTransform trans,
                                            Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 1.25, x,y,z,
                       new int[] { 0, 1, 0 },
                       new int[] { 0, 0, -2 });
    }

    private static void isoTriFaceX1Y1Z0max(Graphics g, GfxTransform trans,
                                            Color col, Color outlineCol, int x, int y, int z) {
	isoFacePolygon(g, trans, col, outlineCol, 0.5, x,y,z,
                       new int[] { 0, 0,-1 },
                       new int[] { 0,-2, 0 });
    }

}
