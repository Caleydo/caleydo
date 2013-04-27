package setvis.shape;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import setvis.SetOutline;

/**
 * Generates a {@link Shape} with a b-spline generated outline. The generated
 * shapes may be a bit exactly defined, causing performance issues. Setting the
 * granularity can work against this but can also lead to not smooth results.
 * When performance is an issue and the outlines do not have to be very smooth
 * {@link BezierShapeGenerator} are a better alternative.
 * 
 * The {@link BSplineShapeGenerator} can be used as a decorator for other
 * generators.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class BSplineShapeGenerator extends RoundShapeGenerator {

    // since the basic function is fixed this value should not be changed
    private static final int ORDER = 3;

    private static final int START_INDEX = ORDER - 1;

    private static final int REL_END = 1;

    private static final int REL_START = REL_END - ORDER;

    private final AbstractShapeGenerator parent;

    private int granularity = 6;

    /**
     * Creates a new {@link BSplineShapeGenerator} with the given set outline
     * creator.
     * 
     * @param outline
     *            The set outline generator.
     */
    public BSplineShapeGenerator(final SetOutline outline) {
        this(new PolygonShapeGenerator(outline));
    }

    public BSplineShapeGenerator(final AbstractShapeGenerator parent) {
        super(parent.getSetOutline(), true);
        this.parent = parent;
    }

    /**
     * Sets the granularity.
     * 
     * @param granularity
     *            The granularity is the number of line segments per base point.
     */
    public void setGranularity(final int granularity) {
        this.granularity = granularity;
    }

    /**
     * @return The granularity is the number of line segments per base point.
     */
    public int getGranularity() {
        return granularity;
    }

    @Override
    public Shape convertToShape(final Point2D[] points, final boolean closed) {
        // covering special cases
        if (points.length < 3) {
            return parent.convertToShape(points, closed);
        }
        // actual b-spline calculation
        final List<Point2D> list = new LinkedList<Point2D>();
        final int count = points.length + ORDER - 1;
        final double g = granularity;
        final Point2D start = calcPoint(points, START_INDEX - (closed ? 0 : 2),
                0, closed);
        list.add(start);
        for (int i = START_INDEX - (closed ? 0 : 2); i < count
                + (closed ? 0 : 2); ++i) {
            for (int j = 1; j <= granularity; ++j) {
                list.add(calcPoint(points, i, j / g, closed));
            }
        }
        return parent.convertToShape(list.toArray(new Point2D[list.size()]),
                closed);
    }

    private static double basicFunction(final int i, final double t) {
        // the basis function for a cubic B spline
        switch (i) {
        case -2:
            return (((-t + 3) * t - 3) * t + 1) / 6;
        case -1:
            return (((3 * t - 6) * t) * t + 4) / 6;
        case 0:
            return (((-3 * t + 3) * t + 3) * t + 1) / 6;
        case 1:
            return (t * t * t) / 6;
        default:
            throw new InternalError();
        }
    }

    // evaluates a point on the B spline
    private Point2D calcPoint(final Point2D[] points, final int i,
            final double t, final boolean closed) {
        double px = 0;
        double py = 0;
        for (int j = REL_START; j <= REL_END; j++) {
            final Point2D p = points[closed ? getRelativeIndex(i, j,
                    points.length) : Math.max(0,
                            Math.min(points.length - 1, i + j))];
            final double bf = basicFunction(j, t);
            px += bf * p.getX();
            py += bf * p.getY();
        }
        return new Point2D.Double(px, py);
    }

    public AbstractShapeGenerator getParent() {
        return parent;
    }

}
