/**
 * 
 */
package setvis;

import setvis.bubbleset.BubbleSet;
import setvis.gui.Canvas;
import setvis.gui.MainWindow;
import setvis.shape.AbstractShapeGenerator;
import setvis.shape.BSplineShapeGenerator;

/**
 * Starts the main application.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public final class Main {

    private Main() {
        // no constructor
    }

    /**
     * Starts the main application.
     * 
     * @param args
     *            Arguments are ignored.
     */
    public static void main(final String[] args) {
        final SetOutline setOutline = new BubbleSet();
        ((BubbleSet)setOutline).useVirtualEdges(false);
        final AbstractShapeGenerator shaper = new BSplineShapeGenerator(
                setOutline);
        final MainWindow mw = new MainWindow(shaper);
        final Canvas canvas = mw.getCanvas();
        // a simple example item set
        final double w = canvas.getCurrentItemWidth();
        final double h = canvas.getCurrentItemHeight();
        canvas.addItem(0, 86.0, 141.0, w, h);
        canvas.addItem(0, 53.0, 306.0, w, h);
        canvas.addEdge(0,86.0, 141.0, 53.0, 306.0);
        canvas.addItem(0, 202.0, 256.0, w, h);
        canvas.addGroup();
        canvas.addItem(1, 85.0, 219.0, w, h);
        canvas.addItem(1, 296.0, 194.0, w, h);
        canvas.addItem(1, 211.0, 328.0, w, h);
        canvas.translateScene(112.0, 106.0);
        canvas.setDefaultView();
        mw.setVisible(true);
    }

}
