package setvis.bubbleset;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * 
 * @author Christopher Collins
 */
public final class MarchingSquares {

    private MarchingSquares() {
        // no constructor
    }

    private static enum Direction {
        N, S, E, W
    }

    // the direction of movement for marching squares
    private static Direction direction;

    private static double threshold;

    public static final boolean calculateContour(
            final ArrayList<Point2D> contour, final double[][] potentialArea,
            final int step, final double t) {
        // find a first point on the contour
        boolean marched = false;

        // set starting direction for conditional states (6 & 9)
        direction = Direction.S;

        // set the threshold
        threshold = t;

        for (int x = 0; x < potentialArea.length && !marched; x++) {
            final double[] potLine = potentialArea[x];
            for (int y = 0; y < potLine.length && !marched; y++) {
                // check invalid state condition
                if (test(potLine[y]) && getState(potentialArea, x, y) != 15) {
                    marched = march(contour, potentialArea, x, y, step);
                }
            }
        }
        return marched;
    }

    /**
     * 2-D Marching squares algorithm. March around a given area to find an
     * iso-energy contour.
     * 
     * @param contour
     *            the surface to fill with iso-energy points
     * @param potentialArea
     *            the area, filled with potential values
     * @param xpos
     *            the current x-position in the area
     * @param ypos
     *            the current y-position in the area
     * @param step
     *            the resolution of the calculation in pixels
     * @return true iff a continuous contour is found
     * 
     */
    private static final boolean march(final ArrayList<Point2D> contour,
            final double[][] potentialArea, final int xpos, final int ypos,
            final int step) {
        int x = xpos;
        int y = ypos;
        for (;;) { // iterative version of the end recursion
            final Point2D p = new Point2D.Float((float) x * step, (float) y
                    * step);

            // check if we're back where we started
            if (contour.contains(p)) {
                if (!contour.get(0).equals(p)) {
                    // encountered a loop but haven't returned to start; will
                    // change
                    // direction using conditionals and continue
                } else {
                    // back to start
                    return true;
                }
            } else {
                contour.add(p);
            }

            final int state = getState(potentialArea, x, y);
            // x, y are upper left of 2X2 marching square

            switch (state) {
            case -1:
                throw new IllegalStateException("Marched out of bounds");
            case 0:
            case 3:
            case 2:
            case 7:
                direction = Direction.E;
                break;
            case 12:
            case 14:
            case 4:
                direction = Direction.W;
                break;
            case 6:
                direction = (direction == Direction.N) ? Direction.W
                        : Direction.E;
                break;
            case 1:
            case 13:
            case 5:
                direction = Direction.N;
                break;
            case 9:
                direction = (direction == Direction.E) ? Direction.N
                        : Direction.S;
                break;
            case 10:
            case 8:
            case 11:
                direction = Direction.S;
                break;
            default:
                throw new IllegalStateException(
                        "Marching squares invalid state: " + state);
            }

            switch (direction) {
            case N:
                --y; // up
                break;
            case S:
                ++y; // down
                break;
            case W:
                --x; // left
                break;
            case E:
                ++x; // right
                break;
            default:
                throw new IllegalStateException(
                        "Marching squares invalid state: " + state);
            }
        }
    }

    /**
     * Tests whether a given value meets the threshold specified for marching
     * squares.
     * 
     * @param test
     *            the value to test
     * @return whether the test value passes
     */
    private static final boolean test(final double test) {
        return (test > threshold);
    }

    /**
     * 2-D Marching Squares algorithm. Given a position and an area of potential
     * energy values, calculate the current marching squares state by testing
     * neighbouring squares.
     * 
     * @param potentialArea
     *            the area filled with potential energy values
     * @param x
     *            the current x-position in the area
     * @param y
     *            the current y-position in the area
     * @return an int value representing a marching squares state
     */
    private static final int getState(final double[][] potentialArea,
            final int x, final int y) {
        int dir = 0;
        try {
            dir += (test(potentialArea[x][y]) ? 1 << 0 : 0);
            dir += (test(potentialArea[x + 1][y]) ? 1 << 1 : 0);
            dir += (test(potentialArea[x][y + 1]) ? 1 << 2 : 0);
            dir += (test(potentialArea[x + 1][y + 1]) ? 1 << 3 : 0);
        } catch (final ArrayIndexOutOfBoundsException e) {
            System.err.println("Marched out of bounds: " + x + " " + y
                    + " bounds: " + potentialArea.length + " "
                    + potentialArea[0].length);
            return -1;
        }
        return dir;
    }

}
