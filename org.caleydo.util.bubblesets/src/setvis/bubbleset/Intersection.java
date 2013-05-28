package setvis.bubbleset;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Stores the location and type of an intersection. Provides utility methods for
 * finding intersections.
 * 
 * @author Christopher Collins
 */
public final class Intersection extends Point2D.Double {

	/** the serial version UID */
	private static final long serialVersionUID = 8788447626238369821L;

	public static enum State {
		Point, Parallel, Coincident, None
	}

	private final State state;

	public Intersection(final double x, final double y) {
		super(x, y);
		state = State.Point;
	}

	public Intersection(final State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	/**
	 * Calculate the intersection of two line segments.
	 * 
	 * @param a
	 *            The first line.
	 * @param b
	 *            The second line.
	 * @return an Intersection item storing the type of intersection and the
	 *         exact point if any was found
	 */
	public static final Intersection intersectLineLine(final Line2D a,
			final Line2D b) {
		Intersection result;

		final double ua_t = (b.getX2() - b.getX1()) * (a.getY1() - b.getY1())
				- (b.getY2() - b.getY1()) * (a.getX1() - b.getX1());
		final double ub_t = (a.getX2() - a.getX1()) * (a.getY1() - b.getY1())
				- (a.getY2() - a.getY1()) * (a.getX1() - b.getX1());
		final double u_b = (b.getY2() - b.getY1()) * (a.getX2() - a.getX1())
				- (b.getX2() - b.getX1()) * (a.getY2() - a.getY1());

		if (u_b != 0) {
			final double ua = ua_t / u_b;
			final double ub = ub_t / u_b;

			if (0 <= ua && ua <= 1 && 0 <= ub && ub <= 1) {
				result = new Intersection(a.getX1() + ua
						* (a.getX2() - a.getX1()), a.getY1() + ua
						* (a.getY2() - a.getY1()));
			} else {
				result = new Intersection(State.None);
			}
		} else {
			if (ua_t == 0 || ub_t == 0) {
				result = new Intersection(State.Coincident);
			} else {
				result = new Intersection(State.Parallel);
			}
		}

		return result;
	}

	/**
	 * Find the fraction along the line a that line b intersects, closest to P1
	 * on line a. This is slightly faster than determining the actual
	 * intersection coordinates.
	 * 
	 * @param a
	 *            The first line
	 * @param b
	 *            The second line
	 * @return the smallest fraction along the line that indicates an
	 *         intersection point
	 */
	public static final double fractionAlongLineA(final Line2D a, final Line2D b) {
		final double ua_t = (b.getX2() - b.getX1()) * (a.getY1() - b.getY1())
				- (b.getY2() - b.getY1()) * (a.getX1() - b.getX1());
		final double ub_t = (a.getX2() - a.getX1()) * (a.getY1() - b.getY1())
				- (a.getY2() - a.getY1()) * (a.getX1() - b.getX1());
		final double u_b = (b.getY2() - b.getY1()) * (a.getX2() - a.getX1())
				- (b.getX2() - b.getX1()) * (a.getY2() - a.getY1());

		if (u_b != 0) {
			final double ua = ua_t / u_b;
			final double ub = ub_t / u_b;

			if (0 <= ua && ua <= 1 && 0 <= ub && ub <= 1) {
				return ua;
			}
		}
		return java.lang.Double.MAX_VALUE;
	}

	/**
	 * Find the fraction along the given line that the rectangle intersects,
	 * closest to the center of the line. This is slightly faster than
	 * determining the actual intersection coordinates.
	 * 
	 * @param bounds
	 *            The rectangle
	 * @param line
	 *            The line
	 * @return the smallest fraction along the line that indicates an
	 *         intersection point
	 */
	public static final double fractionToLineCenter(final Rectangle2D bounds,
			final Line2D line) {
		double minDistance = java.lang.Double.MAX_VALUE;
		double testDistance = 0;
		int countIntersections = 0;

		// top
		testDistance = fractionAlongLineA(
				line,
				new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds
						.getMaxX(), bounds.getMinY()));
		testDistance -= 0.5;
		testDistance = Math.abs(testDistance);
		if ((testDistance >= 0) && (testDistance <= 1)) {
			countIntersections++;
			if (testDistance < minDistance) {
				minDistance = testDistance;
			}
		}

		// left
		testDistance = fractionAlongLineA(
				line,
				new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds
						.getMinX(), bounds.getMaxY()));
		testDistance -= 0.5;
		testDistance = Math.abs(testDistance);
		if ((testDistance >= 0) && (testDistance <= 1)) {
			countIntersections++;
			if (testDistance < minDistance) {
				minDistance = testDistance;
			}
		}
		if (countIntersections == 2) {
			return minDistance; // max 2 intersections
		}

		// bottom
		testDistance = fractionAlongLineA(
				line,
				new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds
						.getMaxX(), bounds.getMaxY()));
		testDistance -= 0.5;
		testDistance = Math.abs(testDistance);
		if ((testDistance >= 0) && (testDistance <= 1)) {
			countIntersections++;
			if (testDistance < minDistance) {
				minDistance = testDistance;
			}
		}
		if (countIntersections == 2) {
			return minDistance; // max 2 intersections
		}

		// right
		testDistance = fractionAlongLineA(
				line,
				new Line2D.Double(bounds.getMaxX(), bounds.getMinY(), bounds
						.getMaxX(), bounds.getMaxY()));
		testDistance -= 0.5;
		testDistance = Math.abs(testDistance);
		if ((testDistance >= 0) && (testDistance <= 1)) {
			countIntersections++;
			if (testDistance < minDistance) {
				minDistance = testDistance;
			}
		}

		// if no intersection, return -1
		if (countIntersections == 0) {
			return -1;
		}

		return minDistance;
	}

	/**
	 * Find the fraction along the given line that the rectangle intersects,
	 * closest to P1 on the line. This is slightly faster than determining the
	 * actual intersection coordinates.
	 * 
	 * @param bounds
	 *            The rectangle
	 * @param line
	 *            The line
	 * @return the smallest fraction along the line that indicates an
	 *         intersection point
	 */
	public static final double fractionToLineEnd(final Rectangle2D bounds,
			final Line2D line) {
		double minDistance = java.lang.Double.MAX_VALUE;
		double testDistance = 0;
		int countIntersections = 0;

		// top
		testDistance = fractionAlongLineA(
				line,
				new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds
						.getMaxX(), bounds.getMinY()));
		if ((testDistance >= 0) && (testDistance <= 1)) {
			countIntersections++;
			if (testDistance < minDistance) {
				minDistance = testDistance;
			}
		}

		// left
		testDistance = fractionAlongLineA(
				line,
				new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds
						.getMinX(), bounds.getMaxY()));
		if ((testDistance >= 0) && (testDistance <= 1)) {
			countIntersections++;
			if (testDistance < minDistance) {
				minDistance = testDistance;
			}
		}
		if (countIntersections == 2) {
			return minDistance; // max 2 intersections
		}

		// bottom
		testDistance = fractionAlongLineA(
				line,
				new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds
						.getMaxX(), bounds.getMaxY()));
		if ((testDistance >= 0) && (testDistance <= 1)) {
			countIntersections++;
			if (testDistance < minDistance) {
				minDistance = testDistance;
			}
		}
		if (countIntersections == 2) {
			return minDistance; // max 2 intersections
		}

		// right
		testDistance = fractionAlongLineA(
				line,
				new Line2D.Double(bounds.getMaxX(), bounds.getMinY(), bounds
						.getMaxX(), bounds.getMaxY()));
		if ((testDistance >= 0) && (testDistance <= 1)) {
			countIntersections++;
			if (testDistance < minDistance) {
				minDistance = testDistance;
			}
		}

		// if no intersection, return -1
		if (countIntersections == 0) {
			return -1;
		}

		return minDistance;
	}

	/**
	 * Tests intersection of the given line segment with all sides of the given
	 * rectangle.
	 * 
	 * @param line
	 *            the line to test
	 * @param bounds
	 *            the rectangular bounds to test each side of
	 * @param intersections
	 *            an array of at least 4 intersections where the intersections
	 *            will be stored as top, left, bottom, right
	 * @return the number of intersection points found (doesn't count
	 *         coincidental lines)
	 */
	public static final int testIntersection(final Line2D line,
			final Rectangle2D bounds, final Intersection[] intersections) {

		int countIntersections = 0;

		// top
		intersections[0] = intersectLineLine(
				line,
				new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds
						.getMaxX(), bounds.getMinY()));
		if (intersections[0].state == State.Point) {
			countIntersections++;
		}

		// left
		intersections[1] = intersectLineLine(
				line,
				new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds
						.getMinX(), bounds.getMaxY()));
		if (intersections[1].state == State.Point) {
			countIntersections++;
		}

		// CAN'T STOP HERE: NEED ALL INTERSECTIONS TO BE FILLED IN
		// if (countIntersections == 2) return countIntersections; // max 2
		// intersections

		// bottom
		intersections[2] = intersectLineLine(
				line,
				new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds
						.getMaxX(), bounds.getMaxY()));
		if (intersections[2].state == State.Point) {
			countIntersections++;
		}

		// right
		intersections[3] = intersectLineLine(
				line,
				new Line2D.Double(bounds.getMaxX(), bounds.getMinY(), bounds
						.getMaxX(), bounds.getMaxY()));
		if (intersections[3].state == State.Point) {
			countIntersections++;
		}

		return countIntersections;
	}

}
