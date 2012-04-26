/**
 * 
 */
package setvis;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * An interface for generating an outline for a set.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public interface SetOutline {

	/**
	 * Creates an outline for the set of rectangles given by {@code members}
	 * avoiding the rectangles of {@code nonMembers}.
	 * 
	 * @param members
	 *            The rectangles to include.
	 * @param nonMembers
	 *            The rectangles to avoid.
	 * @return The vertices of the outline.
	 */
	Point2D[] createOutline(Rectangle2D[] members, Rectangle2D[] nonMembers);

	/**
	 * Creates an outline for the set of rectangles given by {@code members}
	 * avoiding the rectangles of {@code nonMembers} and guided by the lines of
	 * {@code lines}.
	 * 
	 * @param members
	 *            The rectangles to include.
	 * @param nonMembers
	 *            The rectangles to avoid.
	 * @param lines
	 *            The lines guiding the outline creation. The implementation is
	 *            free to ignore them. <code>null</code> values are permitted.
	 * @return The vertices of the outline.
	 */
	Point2D[] createOutline(Rectangle2D[] members, Rectangle2D[] nonMembers,
			Line2D[] lines);
	


}
