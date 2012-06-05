package setvis;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 * Defines a group of rectangles which are connected with lines.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class Group {

	/**
	 * The list of rectangles.
	 */
	public final Rectangle2D[] rects;

	/**
	 * The list of guiding lines.
	 */
	public final Line2D[] lines;

	/**
	 * Creates a group consisting of rectangles and lines.
	 * 
	 * @param rects
	 *            The rectangles.
	 * @param lines
	 *            The lines guiding the set outlines. May be <code>null</code>.
	 */
	public Group(final Collection<Rectangle2D> rects,
			final Collection<Line2D> lines) {
		this.rects = rects.toArray(new Rectangle2D[rects.size()]);
		this.lines = lines != null ? lines.toArray(new Line2D[lines.size()])
				: null;
	}

	/**
	 * Creates a group consisting of rectangles.
	 * 
	 * @param rects
	 *            The rectangles.
	 */
	public Group(final Collection<Rectangle2D> rects) {
		this(rects, null);
	}

}
