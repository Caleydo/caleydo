package setvis.gui;

import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import setvis.SetOutline;

/**
 * An outline configuration is a panel that holds components to configure an
 * outline generator object. It is typically associated with a given
 * {@link OutlineType}.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public abstract class AbstractOutlineConfiguration extends JPanel {

	// the serial version uid
	private static final long serialVersionUID = -5356999538568674877L;

	/**
	 * The associated {@link OutlineType}.
	 */
	private final OutlineType type;

	/**
	 * The canvas to propagate changes to.
	 */
	protected final Canvas canvas;

	/**
	 * The outline generator object to configure.
	 */
	private SetOutline outline;

	/**
	 * Indicates whether the content of this panel is present.
	 */
	private boolean filled;

	/**
	 * Creates an outline configuration panel. It is initially empty and must be
	 * filled with {@link #fillContent()}.
	 * 
	 * @param canvas
	 *            The canvas to propagate changes to.
	 * @param type
	 *            The associated type.
	 */
	public AbstractOutlineConfiguration(final Canvas canvas,
			final OutlineType type) {
		filled = false;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.canvas = canvas;
		this.type = type;
	}

	/**
	 * Inserts the content of the panel. Successive calls to this method will
	 * fill the panel just once.
	 */
	public void fillContent() {
		if (filled) {
			return;
		}
		doFillContent();
		filled = true;
	}

	/**
	 * The actual filling of the panel. Do not call this method directly. Call
	 * {@link #fillContent()} instead.
	 */
	protected abstract void doFillContent();

	/**
	 * Adds a series of components in a horizontal manner. This method may not
	 * be called outside the constructor.
	 * 
	 * @param comps
	 *            The components.
	 */
	protected void addHor(final JComponent... comps) {
		final JPanel hor = new JPanel();
		hor.setLayout(new BoxLayout(hor, BoxLayout.X_AXIS));
		boolean first = true;
		for (final JComponent c : comps) {
			if (first) {
				first = false;
			} else {
				hor.add(Box.createRigidArea(new Dimension(5, 5)));
			}
			hor.add(c);
		}
		add(hor);
	}

	/**
	 * Propagates to the canvas that something regarding the outline generator
	 * has changed.
	 */
	public void changed() {
		canvas.fireCanvasChange(CanvasListener.GENERATORS);
	}

	/**
	 * @param outline
	 *            Sets the outline generator to configure.
	 */
	public void setOutline(final SetOutline outline) {
		this.outline = outline;
	}

	/**
	 * @return The current outline generator.
	 */
	public SetOutline getOutline() {
		return outline;
	}

	/**
	 * @return The associated type.
	 */
	public OutlineType getType() {
		return type;
	}

	/**
	 * Is called when something has changed from the outside.
	 */
	public abstract void somethingChanged();

}
