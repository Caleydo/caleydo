/**
 * 
 */
package setvis.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import setvis.shape.AbstractShapeGenerator;

/**
 * The main window of the application.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class MainWindow extends JFrame implements CanvasListener {

    // serial version uid
    private static final long serialVersionUID = -7857037941409543268L;

    /**
     * The underlying canvas.
     */
    private final CanvasComponent canvas;

    /**
     * The underlying side bar.
     */
    private final SideBar sideBar;

    /**
     * Creates the main window.
     * 
     * @param shaper
     *            The shape generator for the outlines.
     */
    public MainWindow(final AbstractShapeGenerator shaper) {
        super("Set visualization");
        canvas = new CanvasComponent(shaper);
        sideBar = new SideBar(canvas);
        canvas.addCanvasListener(this);
        final JPanel pane = new JPanel(new BorderLayout());
        pane.add(canvas, BorderLayout.CENTER);
        pane.add(sideBar, BorderLayout.EAST);
        add(pane);
        setPreferredSize(new Dimension(970, 760));
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void canvasChanged(final int changes) {
        sideBar.somethingChanged(changes);
    }

    /**
     * @return The canvas represented in this window.
     */
    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public void dispose() {
        canvas.removeCanvasListener(this);
        super.dispose();
    }

}
