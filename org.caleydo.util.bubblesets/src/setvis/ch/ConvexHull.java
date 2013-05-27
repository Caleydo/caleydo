/**
 * 
 */
package setvis.ch;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import setvis.SetOutline;
import setvis.VecUtil;

/**
 * Calculates a convex hull outline, ignoring the non members. The convex hull
 * is computed with the Graham's Scan algorithm.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public class ConvexHull implements SetOutline {

    @Override
    public Point2D[] createOutline(final Rectangle2D[] members,
            final Rectangle2D[] nonMembers, final Line2D[] lines) {
        return createOutline(members, nonMembers);
    }

    @Override
    public Point2D[] createOutline(final Rectangle2D[] members,
            final Rectangle2D[] nonMembers) {
        if (members.length == 0) {
            return new Point2D[0];
        }
        final Set<Point2D> all = new HashSet<Point2D>();
        Point2D ref = null;
        for (final Rectangle2D r : members) {
            final Point2D p = new Point2D.Double(r.getMinX(), r.getMaxY());
            if (ref == null || p.getY() > ref.getY()
                    || (p.getY() == ref.getY() && p.getX() < ref.getX())) {
                ref = p;
            }
            all.add(p);
            all.add(new Point2D.Double(r.getMaxX(), r.getMinY()));
            all.add(new Point2D.Double(r.getMaxX(), r.getMaxY()));
            all.add(new Point2D.Double(r.getMinX(), r.getMinY()));
        }
        final Point2D[] points = all.toArray(new Point2D[all.size()]);
        sortPolar(points, ref);
        return grahamScan(points);
    }

    /**
     * Sorts the array by polar coordinates in reference to {@code ref}.
     * 
     * @param all
     *            The array to sort.
     * @param ref
     *            The reference point for the polar coordinates.
     */
    private static void sortPolar(final Point2D[] all, final Point2D ref) {
        final Comparator<Point2D> cmp = new Comparator<Point2D>() {
            @Override
            public int compare(final Point2D o1, final Point2D o2) {
                if (VecUtil.isNull(ref, o1)) {
                    return -1;
                }
                if (VecUtil.isNull(ref, o2)) {
                    return 1;
                }
                return -VecUtil.relTo(ref, o1, o2);
            }
        };
        Arrays.sort(all, cmp);
    }

    /**
     * Performs the graham scan on a sorted array.
     * 
     * @param all
     *            The sorted array.
     * @return The convex hull vertices.
     */
    private static Point2D[] grahamScan(final Point2D[] all) {
        final LinkedList<Point2D> res = new LinkedList<Point2D>();
        res.addFirst(all[0]);
        res.addFirst(all[1]);
        int i = 2;
        final int n = all.length;
        while (i < n) {
            final Point2D p1 = res.get(0);
            final Point2D si = all[i];
            if (res.size() > 1) {
                final Point2D p2 = res.get(1);
                if (VecUtil.isNull(p2, p1) || VecUtil.relTo(p2, p1, si) <= 0) {
                    res.removeFirst();
                    continue;
                }
            }
            res.addFirst(si);
            ++i;
        }
        return res.toArray(new Point2D[res.size()]);
    }

}
