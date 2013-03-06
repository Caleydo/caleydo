package setvis.bubbleset;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

import setvis.SetOutline;
import setvis.bubbleset.Intersection.State;

/**
 * Generates isocontour set outlines.
 * 
 * The website of the bubble sets: <a
 * href="http://faculty.uoit.ca/collins/research/bubblesets/"
 * >http://faculty.uoit.ca/collins/research/bubblesets/</a>
 * 
 * @author Christopher Collins
 * 
 */
public class BubbleSet implements SetOutline {

    class Item implements Comparable<Item> {
        Rectangle2D rectangle;
        double centroidDistance;

        public double getX() {
            return rectangle.getX();
        }

        public double getY() {
            return rectangle.getY();
        }

        public double getCenterX() {
            return rectangle.getCenterX();
        }

        public double getCenterY() {
            return rectangle.getCenterY();
        }

        public double getWidth() {
            return rectangle.getWidth();
        }

        public double getHeight() {
            return rectangle.getHeight();
        }

        public Rectangle2D getBounds2D() {
            return rectangle.getBounds2D();
        }

        @Override
        public int compareTo(final Item compare) {
            if (centroidDistance < compare.centroidDistance) {
                return -1;
            }
            if (centroidDistance > compare.centroidDistance) {
                return 0;
            }
            return 0;
        }
    }

    public static final int defaultMaxRoutingIterations = 100;

    /** The maximum number of passes through all nodes to attempt edge rerouting */
    private int maxRoutingIterations = defaultMaxRoutingIterations;

    public static final int defaultMaxMarchingIterations = 20;

    /**
     * The maximum number of passes of marching squares while trying to ensure
     * connectedness
     */
    private int maxMarchingIterations = defaultMaxMarchingIterations;

    public static final int defaultPixelGroup = 4;

    /**
     * The size of square super pixels used for calculations (larger results in
     * lower resolution contours and faster calculations).
     */
    private int pixelGroup = defaultPixelGroup;

    public static final double defaultEdgeR0 = 10;

    /**
     * The radius for the contour around a virtual edge -- the point at which
     * the energy is 1
     */
    private double edgeR0 = defaultEdgeR0;

    public static final double defaultEdgeR1 = 20;

    /**
     * The radius at which potential reaches zero -- the extent of energy
     * contribution by the virtual edge
     */
    private double edgeR1 = defaultEdgeR1;

    public static final double defaultNodeR0 = 15;

    /**
     * the radius for the contour around a single node -- the point at which the
     * energy is 1
     */
    private double nodeR0 = defaultNodeR0;

    public static final double defaultNodeR1 = 50;

    /**
     * the radius at which potential reaches zero -- the extent of energy
     * contribution by the node
     */
    private double nodeR1 = defaultNodeR1;

    public static final double defaultMorphBuffer = defaultNodeR0;

    /**
     * The distance to route edges around a node
     */
    private double morphBuffer = defaultMorphBuffer;

    public static final int defaultSkip = 8;

    /**
     * Number of points to skip in the marching squares when making the contour.
     * Larger numbers create smoother, but less precise curves.
     */
    private int skip = defaultSkip;

    /**
     * Whether to use optimized data structures.
     */
    private boolean useOptimizedDataStructures = true;

    /**
     * Create a new BubbleSet calculation instance with the given parameters.
     * 
     * @param routingIterations
     *            the maximum number of passes through all items to attempt edge
     *            rerouting
     * @param marchingIterations
     *            the maximum number of passes of marching squares while trying
     *            to ensure connectedness
     * @param pixelGroup
     *            the size of square super pixels used for calculations (larger
     *            results in lower resolution contours and faster calculations).
     * @param edgeR0
     *            the radius for the contour around a virtual edge -- the point
     *            at which the energy is 1
     * @param edgeR1
     *            the extent of energy contribution by a virtual edge -- the
     *            radius at which potential reaches 0
     * @param nodeR0
     *            the radius for the contour around a rectangle -- the point at
     *            which the energy is 1
     * @param nodeR1
     *            the extent of energy contribution by a rectangle -- the radius
     *            at which potential reaches 0
     * @param morphBuffer
     *            the buffer distance to route the bubble set around interfering
     *            rectangles; usually nodeR0 is a good value
     * @param skip
     *            the number of points to skip in the marching squares when
     *            making the contour. Larger numbers create smoother, but less
     *            precise curves
     */
    public BubbleSet(final int routingIterations, final int marchingIterations,
            final int pixelGroup, final double edgeR0, final double edgeR1,
            final double nodeR0, final double nodeR1, final double morphBuffer,
            final int skip) {
        maxRoutingIterations = routingIterations;
        maxMarchingIterations = marchingIterations;
        this.pixelGroup = pixelGroup;
        this.edgeR0 = edgeR0;
        this.edgeR1 = edgeR1;
        this.nodeR0 = nodeR0;
        this.nodeR1 = nodeR1;
        this.morphBuffer = morphBuffer;
        this.skip = skip;        
    }

    /**
     * Create a new BubbleSet calculation instance with default parameters.
     */
    public BubbleSet() {
        // use default paramters
    }

    public void setParameter(final int routingIterations, final int marchingIterations,
            final int pixelGroup, final double edgeR0, final double edgeR1,
            final double nodeR0, final double nodeR1, final double morphBuffer,
            final int skip) {
        maxRoutingIterations = routingIterations;
        maxMarchingIterations = marchingIterations;
        this.pixelGroup = pixelGroup;
        this.edgeR0 = edgeR0;
        this.edgeR1 = edgeR1;
        this.nodeR0 = nodeR0;
        this.nodeR1 = nodeR1;
        this.morphBuffer = morphBuffer;
        this.skip = skip;        
    }
    
    protected boolean useVirtualEdges=true;
    public void useVirtualEdges(boolean value)
    {
    	useVirtualEdges=value;
    }
    // FIELDS BELOW ARE USED BY THE ALGORITHM AND GENERALLY SHOULD NOT BE
    // CHANGED (HENCE NO GET/SET)

    /**
     * The energy threshold for marching squares
     */
    private double threshold = 1;

    /**
     * The default energy contribution for rectangles in the set.
     */
    private double nodeInfluenceFactor = 1;

    /**
     * The default energy contribution for virtual edges in the set.
     */
    private double edgeInfluenceFactor = 1;

    /**
     * The default energy contribution for rectangles not in the set.
     */
    private double negativeNodeInfluenceFactor = -0.8;

    /**
     * The array of energy values calculated over the active area.
     */
    private double[][] potentialArea;

    /**
     * The bounds which contain all set members + a buffer, in screen
     * coordinates. This bounding box is guaranteed to contain the bubble set.
     */
    private Rectangle2D activeRegion;

    Deque<Line2D> virtualEdges = new ArrayDeque<Line2D>();

    /**
     * The energy threshold value used to calculate the most recent contour.
     */
    private double lastThreshold;

    /**
     * Test point equality within a threshold.
     * 
     * @param p1
     *            the first point
     * @param p2
     *            the second point
     * @param delta
     *            the threshold distance
     * @return true if and only if the distance between p1 and p2 is less than
     *         delta
     */
    public static boolean doublePointsEqual(final Point2D p1, final Point2D p2,
            final double delta) {
        return (p1.distance(p2) < delta);
    }

    @Override
    public Point2D[] createOutline(final Rectangle2D[] members,
            final Rectangle2D[] nonMembers) {
        return createOutline(members, nonMembers, null);
    }

    /**
     * Calculate the Bubble Set using energy and marching squares with edge
     * routing.
     */
    @Override
    public Point2D[] createOutline(final Rectangle2D[] members,
            final Rectangle2D[] nonMembers, final Line2D[] edges) {

        if (members.length == 0) {
            return new Point2D[0];
        }

        final Item[] memberItems = new Item[members.length];
        for (int i = 0; i < members.length; i++) {
            memberItems[i] = new Item();
            memberItems[i].rectangle = members[i];
        }

        // calculate and store virtual edges
        virtualEdges.clear();
        if(useVirtualEdges){
        	calculateVirtualEdges(memberItems, nonMembers);
        }

        if (edges != null) {
            virtualEdges.addAll(Arrays.asList(edges));
        }

        // cycle through members of aggregate adding to bounds of influence
        for (int memberIndex = 0; memberIndex < members.length; memberIndex++) {
            if (activeRegion == null) {
                // clone because we don't want to change bounds of items, but we
                // need to start with item bounds (not empty bounds because 0,0
                // may not be in area of influence
                activeRegion = (Rectangle2D) memberItems[memberIndex]
                        .getBounds2D().clone();
            } else {
                activeRegion.add(memberItems[memberIndex].getBounds2D());
            }
        }

        // add the bounds of the virtual edges to the active area
        final Iterator<Line2D> lines = virtualEdges.iterator();
        while (lines.hasNext()) {
            activeRegion.add(lines.next().getBounds2D());
        }

        // bounds contains a rectangle with all the nodes in the aggregate
        // within it (convex hull) expand bounds by the maximum radius on all
        // sides
        activeRegion.setRect(activeRegion.getX() - Math.max(edgeR1, nodeR1)
                - morphBuffer, activeRegion.getY() - Math.max(edgeR1, nodeR1)
                - morphBuffer,
                activeRegion.getWidth() + 2 * Math.max(edgeR1, nodeR1) + 2
                * morphBuffer,
                activeRegion.getHeight() + 2 * Math.max(edgeR1, nodeR1) + 2
                * morphBuffer);
        potentialArea = new double[(int) (Math.ceil(activeRegion.getWidth()
                / pixelGroup))][(int) (Math.ceil(activeRegion.getHeight()
                        / pixelGroup))];

        // estimate length of contour to be the perimeter of the rectangular
        // aggregate bounds (tested, it's a good approx)
        final int estLength = ((int) activeRegion.getWidth() + (int) activeRegion
                .getHeight()) * 2;
        final ArrayList<Point2D> surface = useOptimizedDataStructures ? new FastList<Point2D>(
                estLength) : new ArrayList<Point2D>(estLength);

                // store defaults and adjust globals so that changes are visible to
                // calculateSurface method
                final double tempThreshold = threshold;
                final double tempNegativeNodeInfluenceFactor = negativeNodeInfluenceFactor;
                final double tempNodeInfluenceFactor = nodeInfluenceFactor;
                final double tempEdgeInfluenceFactor = edgeInfluenceFactor;

                int iterations = 0;

                // add the aggregate and all it's members and virtual edges
                fillPotentialArea(activeRegion, memberItems, nonMembers, potentialArea);

                // try to march, check if surface contains all items
                while ((!calculateContour(surface, activeRegion, members, nonMembers,
                        potentialArea)) && (iterations < maxMarchingIterations)) {
                    surface.clear();
                    iterations++;

                    // reduce negative influences first; this will allow the surface to
                    // pass without making it fatter all around (which raising the
                    // threshold does)
                    if (iterations <= maxMarchingIterations / 2) {
                        threshold *= 0.95f;
                        nodeInfluenceFactor *= 1.2;
                        edgeInfluenceFactor *= 1.2;
                        fillPotentialArea(activeRegion, memberItems, nonMembers,
                                potentialArea);
                    }

                    // after half the iterations, start increasing positive energy and
                    // lowering the threshold

                    if (iterations > maxMarchingIterations / 2) {
                        if (negativeNodeInfluenceFactor != 0) {
                            threshold *= 0.95f;
                            negativeNodeInfluenceFactor *= 0.8;
                            fillPotentialArea(activeRegion, memberItems, nonMembers,
                                    potentialArea);
                        }
                    }
                }

                lastThreshold = threshold;
                threshold = tempThreshold;
                negativeNodeInfluenceFactor = tempNegativeNodeInfluenceFactor;
                nodeInfluenceFactor = tempNodeInfluenceFactor;
                edgeInfluenceFactor = tempEdgeInfluenceFactor;

                // start with global SKIP value, but decrease skip amount if there
                // aren't enough points in the surface
                int thisSkip = skip;
                // prepare viz attribute array
                int size = surface.size();

                if (thisSkip > 1) {
                    size = surface.size() / thisSkip;
                    // if we reduced too much (fewer than three points in reduced
                    // surface) reduce skip and try again
                    while ((size < 3) && (thisSkip > 1)) {
                        thisSkip--;
                        size = surface.size() / thisSkip;
                    }
                }

                // add the offset of the active area to the coordinates
                final float xcorner = (float) activeRegion.getX();
                final float ycorner = (float) activeRegion.getY();

                final Point2D[] fhull = new Point2D[size];

                // copy hull values
                for (int i = 0, j = 0; j < size; j++, i += thisSkip) {
                    fhull[j] = new Point2D.Double(surface.get(i).getX() + xcorner,
                            surface.get(i).getY() + ycorner);
                }

                // getting rid of unused memory
                // preventing a memory leak
                activeRegion = null;
                potentialArea = null;

                return fhull;
    }

    /**
     * Fill the surface using marching squares, return true if and only if all
     * items in the given aggregate are contained inside rectangle specified by
     * the extents of the surface. This does not guarantee the surface will
     * contain all items, but it is a fast approximation.
     * 
     * @param contour
     *            the surface to fill
     * @param bounds
     *            the bounds of the space being calculated, in screen
     *            coordinates
     * @param members
     *            the items which should be insider the contour
     * @param nonMembers
     *            the items which should be outside the contour
     * @param potentialArea
     *            the energy field corresponding to the given aggregate and
     *            bounds
     * @return true if and only if marching squares successfully found a surface
     *         containing all elements in the aggregate
     */
    public boolean calculateContour(final ArrayList<Point2D> contour,
            final Rectangle2D bounds, final Rectangle2D[] members,
            final Rectangle2D[] nonMembers, final double[][] potentialArea) {

        // if no surface could be found stop
        if (!MarchingSquares.calculateContour(contour, potentialArea,
                pixelGroup, threshold)) {
            return false;
        }

        final boolean[] containment = testContainment(contour, bounds, members,
                nonMembers);

        return containment[0];
    }

    /**
     * Test containment of items in the bubble set.
     * 
     * @param contour
     *            the points on the surface
     * @param bounds
     *            the bounds of influence used to calculate the surface
     * @param members
     *            the set members which should be inside the contour
     * @param nonMembers
     *            the interference items which should be outside the contour
     * @return an array where the first element indicates if the set contains
     *         all required items and the second element indicates if the set
     *         contains extra items
     */
    public boolean[] testContainment(final ArrayList<Point2D> contour,
            final Rectangle2D bounds, final Rectangle2D[] members,
            final Rectangle2D[] nonMembers) {
        // precise bounds checking
        // copy hull values
        final Path2D g = new Path2D.Double();
        // start with global SKIP value, but decrease skip amount if there
        // aren't enough points in the surface
        int thisSkip = skip;
        // prepare viz attribute array
        int size = contour.size();
        if (thisSkip > 1) {
            size = contour.size() / thisSkip;
            // if we reduced too much (fewer than three points in reduced
            // surface) reduce skip and try again
            while ((size < 3) && (thisSkip > 1)) {
                thisSkip--;
                size = contour.size() / thisSkip;
            }
        }

        final float xcorner = (float) bounds.getX();
        final float ycorner = (float) bounds.getY();

        // simulate the surface we will eventually draw, using straight segments
        // (approximate, but fast)
        for (int i = 0; i < size - 1; i++) {
            if (i == 0) {
                g.moveTo((float) contour.get(i * thisSkip).getX() + xcorner,
                        (float) contour.get(i * thisSkip).getY() + ycorner);
            } else {
                g.lineTo((float) contour.get(i * thisSkip).getX() + xcorner,
                        (float) contour.get(i * thisSkip).getY() + ycorner);
            }
        }

        g.closePath();

        boolean containsAll = true;
        boolean containsExtra = false;

        for (final Rectangle2D item : members) {
            // check rough bounds
            containsAll = (containsAll)
                    && (g.getBounds().contains(item.getBounds().getCenterX(),
                            item.getBounds().getCenterY()));
            // check precise bounds if rough passes
            containsAll = (containsAll)
                    && (g.contains(item.getBounds().getCenterX(), item
                            .getBounds().getCenterY()));
        }
        for (final Rectangle2D item : nonMembers) {
            // check rough bounds
            if (g.getBounds().contains(item.getBounds().getCenterX(),
                    item.getBounds().getCenterY())) {
                // check precise bounds if rough passes
                if (g.contains(item.getBounds().getCenterX(), item.getBounds()
                        .getCenterY())) {
                    containsExtra = true;
                }
            }
        }
        return new boolean[] { containsAll, containsExtra };
    }

    /**
     * Fill the given area with energy, with values modulated by the preset
     * energy function parameters (radial extent, positive and negative
     * influences for included and excluded nodes and edges).
     * 
     * @param activeArea
     *            the bounding box which contains all set members; the bubble
     *            set will be within this region
     * 
     * @param members
     *            the rectangles to include
     * 
     * @param nonMembers
     *            the rectangles to exclude
     * 
     * @param potentialArea
     *            the energy field to fill in
     */
    public void fillPotentialArea(final Rectangle2D activeArea,
            final Item[] members, final Rectangle2D[] nonMembers,
            final double[][] potentialArea) {

        double influenceFactor = 0;

        // add all positive energy (included items) first, as negative energy
        // (morphing) requires all positives to be already set

        if (nodeInfluenceFactor != 0) {
            for (final Item item : members) {
                // add node energy
                influenceFactor = nodeInfluenceFactor;
                final double nodeRDiff = nodeR0 - nodeR1;
                // using inverse a for numerical stability
                final double inva = nodeRDiff * nodeRDiff;
                calculateRectangleInfluence(potentialArea, influenceFactor
                        / inva, nodeR1, new Rectangle2D.Double(item.getX()
                                - activeArea.getX(), item.getY() - activeArea.getY(),
                                item.getWidth(), item.getHeight()));
            } // end processing node items of this aggregate
        } // end processing positive node energy

        if (edgeInfluenceFactor != 0) {
            // add the influence of all the virtual edges
            influenceFactor = edgeInfluenceFactor;
            final double a = 1 / ((edgeR0 - edgeR1) * (edgeR0 - edgeR1));

            if (virtualEdges.size() > 0) {
                calculateLinesInfluence(potentialArea, a * influenceFactor,
                        edgeR1, virtualEdges, activeArea);
            }
        }

        // calculate negative energy contribution for all other visible items
        // within bounds
        if (negativeNodeInfluenceFactor != 0) {
            for (final Rectangle2D item : nonMembers) {
                // if item is within influence bounds, add potential
                if (activeArea.intersects(item.getBounds())) {
                    // subtract influence
                    influenceFactor = negativeNodeInfluenceFactor;
                    final double nodeRDiff = nodeR0 - nodeR1;
                    // using inverse a for numerical stability
                    final double inva = nodeRDiff * nodeRDiff;
                    calculateRectangleInfluence(
                            potentialArea,
                            influenceFactor / inva,
                            nodeR1,
                            new Rectangle2D.Double(item.getX()
                                    - activeArea.getX(), item.getY()
                                    - activeArea.getY(), item.getWidth(), item
                                    .getHeight()));
                }
            }
        }
    }

    private static void calculateCentroidDistances(final Item[] items) {
        double totalx = 0;
        double totaly = 0;
        double nodeCount = 0;

        for (final Item item : items) {
            totalx += item.getCenterX();
            totaly += item.getCenterY();
            nodeCount++;
        }

        totalx /= nodeCount;
        totaly /= nodeCount;

        for (final Item item : items) {
            final double diffX = totalx - item.getCenterX();
            final double diffY = totaly - item.getCenterY();
            item.centroidDistance = Math.sqrt(diffX * diffX + diffY * diffY);
        }
    }

    /**
     * Visit all items in the set, connecting them to their the best neighbour
     * which is also in the set (see connectItem() for detail on selection of
     * best neighbour). Stores the connections in the virtualEdges collection
     * for each item in the set.
     * 
     * @param items
     *            set members to connect to one another
     * @param nonMembers
     *            other on screen rectangles to avoid
     */
    private void calculateVirtualEdges(final Item[] items,
            final Rectangle2D[] nonMembers) {
    	final Deque<Item> visited = new ArrayDeque<Item>();

        calculateCentroidDistances(items);
        Arrays.sort(items);

        for (final Item item : items) {
            virtualEdges.addAll(connectItem(items, nonMembers, item, visited));
            visited.add(item);
        }
    }

    /**
     * Find the sequence of virtual edges which connect a given item to its best
     * unvisited neighbour within the set. Considerations for selection of best
     * neighbour include distance and number of intervening non-set items on the
     * straight line between the item and the candidate neighbour.
     * 
     * @param memberItems
     *            the set to seach for nearest connections
     * @param nonMembers
     *            the rectangular regions to avoid
     * @param item
     *            the item to find the best neighbour for
     * @param visited
     *            the already connected items within the set
     * @return a set of lines which connects the given item to it's set while
     *         avoiding interfering items
     */
    private Deque<Line2D> connectItem(final Item[] memberItems,
            final Rectangle2D[] nonMembers, final Item item,
            final Collection<Item> visited) {

        Item closestNeighbour = null;
        Deque<Line2D> scannedLines = new ArrayDeque<Line2D>();
        final Deque<Line2D> linesToCheck = new ArrayDeque<Line2D>();

        final Iterator<Item> neighbourIterator = visited.iterator();
        double minLength = Double.MAX_VALUE;
        // discover the nearest neighbour with minimal interference items
        while (neighbourIterator.hasNext()) {
            double numberInterferenceItems = 0;
            final Item neighbourItem = neighbourIterator.next();
            final double distance = Point2D.distance(item.getCenterX(),
                    item.getCenterY(), neighbourItem.getCenterX(),
                    neighbourItem.getCenterY());

            final Line2D completeLine = new Line2D.Double(item.getCenterX(),
                    item.getCenterY(), neighbourItem.getCenterX(),
                    neighbourItem.getCenterY());

            // augment distance by number of interfering items
            numberInterferenceItems = countInterferenceItems(nonMembers,
                    completeLine);

            // TODO is there a better function to consider interference in
            // nearest-neighbour checking? This is hacky
            if ((distance * (numberInterferenceItems + 1) < minLength)) {
                closestNeighbour = neighbourItem;
                minLength = distance * (numberInterferenceItems + 1);
            }
        }

        // if there is a visited closest neighbour, add straight line between
        // them to the positive energy to ensure connected clusters
        if (closestNeighbour != null) {
            final Line2D completeLine = new Line2D.Double(item.getCenterX(),
                    item.getCenterY(), closestNeighbour.getCenterX(),
                    closestNeighbour.getCenterY());

            // route the edge around intersecting nodes not in set
            linesToCheck.push(completeLine);

            boolean hasIntersection = true;
            int iterations = 0;
            final Intersection[] intersections = new Intersection[4];
            int numIntersections = 0;
            while (hasIntersection && iterations < maxRoutingIterations) {
                hasIntersection = false;
                while (!hasIntersection && !linesToCheck.isEmpty()) {
                    final Line2D line = linesToCheck.pop();

                    // resolve intersections in order along edge
                    final Rectangle2D closestItem = getCenterItem(nonMembers,
                            line);

                    if (closestItem != null) {
                        numIntersections = Intersection.testIntersection(line,
                                closestItem.getBounds(), intersections);

                        // 2 intersections = line passes through item
                        if (numIntersections == 2) {
                            double tempMorphBuffer = morphBuffer;

                            Point2D movePoint = rerouteLine(line,
                                    closestItem.getBounds(), tempMorphBuffer,
                                    intersections, true);

                            // test the movePoint already exists
                            boolean foundFirst = (pointExists(movePoint,
                                    linesToCheck.iterator()) || pointExists(
                                            movePoint, scannedLines.iterator()));
                            boolean pointInside = isPointInsideNonMember(
                                    movePoint, nonMembers);

                            // prefer first corner, even if buffer becomes
                            // very small
                            while ((!foundFirst) && (pointInside)
                                    && (tempMorphBuffer >= 1)) {
                                // try a smaller buffer
                                tempMorphBuffer /= 1.5;
                                movePoint = rerouteLine(line,
                                        closestItem.getBounds(),
                                        tempMorphBuffer, intersections, true);
                                foundFirst = (pointExists(movePoint,
                                        linesToCheck.iterator()) || pointExists(
                                                movePoint, scannedLines.iterator()));
                                pointInside = isPointInsideNonMember(movePoint,
                                        nonMembers);
                            }

                            if ((movePoint != null) && (!foundFirst)
                                    && (!pointInside)) {
                                // add 2 rerouted lines to check
                                linesToCheck.push(new Line2D.Double(line
                                        .getP1(), movePoint));
                                linesToCheck.push(new Line2D.Double(movePoint,
                                        line.getP2()));
                                // indicate intersection found
                                hasIntersection = true;
                            }

                            // if we didn't find a valid point around the
                            // first corner, try the second
                            if (!hasIntersection) {
                                tempMorphBuffer = morphBuffer;

                                movePoint = rerouteLine(line,
                                        closestItem.getBounds(),
                                        tempMorphBuffer, intersections, false);
                                boolean foundSecond = (pointExists(movePoint,
                                        linesToCheck.iterator()) || pointExists(
                                                movePoint, scannedLines.iterator()));
                                pointInside = isPointInsideNonMember(movePoint,
                                        nonMembers);

                                // if both corners have been used, stop;
                                // otherwise gradually reduce buffer and try
                                // second corner
                                while ((!foundSecond) && (pointInside)
                                        && (tempMorphBuffer >= 1)) {
                                    // try a smaller buffer
                                    tempMorphBuffer /= 1.5;
                                    movePoint = rerouteLine(line,
                                            closestItem.getBounds(),
                                            tempMorphBuffer, intersections,
                                            false);
                                    foundSecond = (pointExists(movePoint,
                                            linesToCheck.iterator()) || pointExists(
                                                    movePoint, scannedLines.iterator()));
                                    pointInside = isPointInsideNonMember(
                                            movePoint, nonMembers);
                                }

                                if ((movePoint != null) && (!foundSecond)) {
                                    // add 2 rerouted lines to check
                                    linesToCheck.push(new Line2D.Double(line
                                            .getP1(), movePoint));
                                    linesToCheck.push(new Line2D.Double(
                                            movePoint, line.getP2()));
                                    // indicate intersection found
                                    hasIntersection = true;
                                }
                            }
                        }
                    } // end check of closest item

                    // no intersection found, mark this line as completed
                    if (!hasIntersection) {
                        scannedLines.push(line);
                    }

                    iterations++;
                } // end inner loop - out of lines or found an intersection
            } // end outer loop - no more intersections or out of iterations

            // finalize any that were not rerouted (due to running out of
            // iterations) or if we aren't morphing
            while (!linesToCheck.isEmpty()) {
                scannedLines.push(linesToCheck.pop());
            }

            // try to merge consecutive lines if possible
            while (!scannedLines.isEmpty()) {
                final Line2D line1 = scannedLines.pop();
                if (!scannedLines.isEmpty()) {
                    final Line2D line2 = scannedLines.pop();
                    final Line2D mergeLine = new Line2D.Double(line1.getP1(),
                            line2.getP2());
                    // resolve intersections in order along edge
                    final Rectangle2D closestItem = getCenterItem(nonMembers,
                            mergeLine);
                    // merge most recent line and previous line
                    if (closestItem == null) {
                        scannedLines.push(mergeLine);
                    } else {
                        linesToCheck.push(line1);
                        scannedLines.push(line2);
                    }
                } else {
                    linesToCheck.push(line1);
                }
            }
            scannedLines = linesToCheck;
        }
        return scannedLines;
    }

    /**
     * Check if a point is inside the bounds of any of the given rectangles.
     * 
     * @param point
     *            the point to check, in screen coordinates
     * @param nonMembers
     *            the rectangles to scan
     * @return true if this point is within the rectangular bounding box of at
     *         least one tuple; false otherwise
     */
    private static boolean isPointInsideNonMember(final Point2D point,
            final Rectangle2D[] nonMembers) {
        for (final Rectangle2D testRectangle : nonMembers) {
            if (testRectangle.contains(point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a given point is already an endpoint of any of the given
     * lines.
     * 
     * @param pointToCheck
     *            the point to check
     * @param linesIterator
     *            the lines to scan the endpoints of
     * @return true if the given point is the endpoint of at least one line;
     *         false otherwise
     */
    public boolean pointExists(final Point2D pointToCheck,
            final Iterator<Line2D> linesIterator) {
        boolean found = false;

        while ((linesIterator.hasNext()) && (!found)) {
            final Line2D checkEndPointsLine = linesIterator.next();
            // check with some tolerance for rounding errors
            if (doublePointsEqual(checkEndPointsLine.getP1(), pointToCheck,
                    1e-3)) {
                found = true;
            }
            if (doublePointsEqual(checkEndPointsLine.getP2(), pointToCheck,
                    1e-3)) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Add radial (circular) contribution of a point source to all points in a
     * given area.
     * 
     * @param potentialArea
     *            the area to fill with influence values
     * @param factor
     *            the influence factor of this point source
     * @param r1
     *            the radius at which contribution becomes 0
     * @param pointx
     *            the x-coordinate of the point source
     * @param pointy
     *            the y-coordinate of the point source
     */
    public void calculatePointInfluence(final double[][] potentialArea,
            final double factor, final double r1, final double pointx,
            final double pointy) {
        double tempX = 0, tempY = 0, distance = 0;

        // for every point in potentialArea, calculate distance to point and add
        // influence
        for (int x = 0; x < potentialArea.length; x++) {
            for (int y = 0; y < potentialArea[x].length; y++) {
                tempX = x * pixelGroup;
                tempY = y * pixelGroup;
                distance = Point2D.distance(tempX, tempY, pointx, pointy);
                // only influence if less than r1
                final double dr = distance - r1;
                if (dr < 0) {
                    potentialArea[x][y] += factor * dr * dr;
                }
            }
        }
    }

    /**
     * Add a contribution of a line source to all points in a given area. For
     * every point in the given area, the distance to the closest point on the
     * line is calculated and this distance is input into the gradient influence
     * function, then added to the potentialArea.
     * 
     * @param potentialArea
     *            the area to fill with influence values
     * @param influenceFactor
     *            the influence factor of the line in the area
     * @param r1
     *            the radius at which contribution becomes 0
     * @param line
     *            the line source
     */
    public void calculateLineInfluence(final double[][] potentialArea,
            final double influenceFactor, final double r1, final Line2D line) {
        double tempX, tempY, distance = 0;

        final Rectangle2D r = line.getBounds2D();
        // calculate the subregion of potential area which may be affected by
        // this line
        final int startX = Math.min(
                Math.max(0, (int) ((r.getX() - r1) / pixelGroup)),
                potentialArea.length - 1);
        final int startY = Math.min(
                Math.max(0, (int) ((r.getY() - r1) / pixelGroup)),
                potentialArea[startX].length - 1);
        final int endX = Math.min(potentialArea.length - 1, Math.max(0,
                (int) ((r.getX() + r.getWidth() + r1) / pixelGroup)));
        final int endY = Math.min(potentialArea[startX].length, Math.max(0,
                (int) ((r.getY() + r.getHeight() + r1) / pixelGroup)));

        // for every point in active region of potentialArea, calculate distance
        // to nearest point
        // on line and add influence
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                tempX = x * pixelGroup;
                tempY = y * pixelGroup;

                distance = line.ptSegDist(tempX, tempY);

                // only influence if less than r1
                final double dr = distance - r1;
                if (dr < 0) {
                    potentialArea[x][y] += influenceFactor * dr * dr;
                }
            }
        }
    }

    /**
     * Finds the item in the iterator whose rectangular bounds intersect the
     * line closest to the center and the item is not in the given aggregate.
     * Note that despite the shape of the rendered VisualItem, the rectangular
     * bounds are used as for this check.
     * 
     * @param items
     *            the items to test for intersection with the line
     * @param testLine
     *            the line to test for intersection with the items
     * @return the closest item or null if there are no intersections.
     */
    public Rectangle2D getCenterItem(final Rectangle2D[] items,
            final Line2D testLine) {
        double minDistance = Double.MAX_VALUE;
        Rectangle2D closestItem = null;

        for (final Rectangle2D interferenceItem : items) {
            if (interferenceItem.intersectsLine(testLine)) {
                final double distance = Intersection.fractionToLineCenter(
                        interferenceItem, testLine);
                // find closest intersection
                if ((distance != -1) && (distance < minDistance)) {
                    closestItem = interferenceItem;
                    minDistance = distance;
                }
            }
        }
        return closestItem;
    }

    private static int countInterferenceItems(
            final Rectangle2D[] interferenceItems, final Line2D testLine) {
        int count = 0;
        for (final Rectangle2D interferenceItem : interferenceItems) {
            if (interferenceItem.intersectsLine(testLine)) {
                if (Intersection.fractionToLineCenter(
                        interferenceItem.getBounds(), testLine) != -1) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Add a contribution of a line source to all points in a given area. For
     * every point in the given area, the distance to the closest point on the
     * line is calculated and this distance is input into the gradient influence
     * function, then added to the potentialArea.
     * 
     * @param potentialArea
     *            the area to fill with influence values
     * @param influenceFactor
     *            the influence factor of the line in the area
     * @param r1
     *            the radius where influence drops to zero
     * @param lines
     *            the lines to add
     * @param activeRegion
     *            the active area of this bubble set
     */
    public void calculateLinesInfluence(final double[][] potentialArea,
            final double influenceFactor, final double r1,
            final Deque<Line2D> lines, final Rectangle2D activeRegion) {

        double tempX, tempY, distanceSq = 0;
        double minDistanceSq = Double.MAX_VALUE;

        Rectangle2D r = null;

        // calculate active region for the lines
        for (final Line2D line : lines) {
            if (r == null) {
                r = (Rectangle2D) line.getBounds2D().clone();
            } else {
                r.add(line.getBounds2D());
            }
        }

        if (r == null) {
            return;
        }

        // offset the rectangle by the bubble set bounds to put into 0,0 space
        // for potentialArea
        r.setFrame(r.getX() - activeRegion.getX(),
                r.getY() - activeRegion.getY(), r.getWidth(), r.getHeight());

        // find the affected subregion of potentialArea
        final int startX = Math.min(
                Math.max(0, (int) ((r.getX() - r1) / pixelGroup)),
                potentialArea.length - 1);
        final int startY = Math.min(
                Math.max(0, (int) ((r.getY() - r1) / pixelGroup)),
                potentialArea[startX].length - 1);
        final int endX = Math.min(potentialArea.length - 1, Math.max(0,
                (int) ((r.getX() + r.getWidth() + r1) / pixelGroup)));
        final int endY = Math.min(potentialArea[startX].length, Math.max(0,
                (int) ((r.getY() + r.getHeight() + r1) / pixelGroup)));

        // for every point in active part of potentialArea, calculate distance
        // to nearest point on line and add influence
        for (int x = startX; x < endX; ++x) {
            for (int y = startY; y < endY; ++y) {

                // if we are adding negative energy, skip if not already
                // positive; positives have already been added first, and adding
                // negative to <=0 will have no affect on surface
                if ((influenceFactor < 0) && (potentialArea[x][y] <= 0)) {
                    continue;
                }

                // convert back to screen coordinates
                tempX = x * pixelGroup + activeRegion.getX();
                tempY = y * pixelGroup + activeRegion.getY();

                minDistanceSq = Double.POSITIVE_INFINITY;
                for (final Line2D line : lines) {
                    // use squared distance for comparison
                    distanceSq = line.ptSegDistSq(tempX, tempY);
                    if (distanceSq < minDistanceSq) {
                        minDistanceSq = distanceSq;
                    }
                }

                // use the real minimal distance here (with Math.sqrt)
                // only influence if less than r1
                final double mdr = Math.sqrt(minDistanceSq) - r1;
                if (mdr < 0) {
                    potentialArea[x][y] += influenceFactor * mdr * mdr;
                }
            }
        }
    }

    /**
     * Add a contribution of a rectangle source to all points in a given area.
     * For every point in the given area, the distance to the closest point on
     * the rectangle is calculated and this distance is input into the gradient
     * influence function, then added to the potentialArea.
     * 
     * @param potentialArea
     *            the area to fill with influence values
     * @param influenceFactor
     *            the influence factor of the line in the area
     * @param r1
     *            the radius where influence drops to zero
     * @param rect
     *            the rectangle source to add to the set
     */
    public void calculateRectangleInfluence(final double[][] potentialArea,
            final double influenceFactor, final double r1,
            final Rectangle2D rect) {
        double tempX, tempY, distance = 0;

        // find the affected subregion of potentialArea
        final int startX = Math.min(
                Math.max(0, (int) ((rect.getX() - r1) / pixelGroup)),
                potentialArea.length - 1);
        final int startY = Math.min(
                Math.max(0, (int) ((rect.getY() - r1) / pixelGroup)),
                potentialArea[startX].length - 1);
        final int endX = Math.min(potentialArea.length - 1, Math.max(0,
                (int) ((rect.getX() + rect.getWidth() + r1) / pixelGroup)));
        final int endY = Math.min(potentialArea[startX].length, Math.max(0,
                (int) ((rect.getY() + rect.getHeight() + r1) / pixelGroup)));

        // for every point in active subregion of potentialArea, calculate
        // distance to nearest point on rectangle and add influence
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {

                // if we are adding negative energy, skip if not already
                // positive; positives have already been added first, and adding
                // negative to <=0 will have no affect on surface
                if ((influenceFactor < 0) && (potentialArea[x][y] <= 0)) {
                    continue;
                }

                // convert back to screen coordinates
                tempX = x * pixelGroup;
                tempY = y * pixelGroup;

                // test current point to see if it is inside rectangle
                if (rect.contains(tempX, tempY)) {
                    distance = 0;
                } else {
                    // which edge of rectangle is closest
                    final int outcode = rect.outcode(tempX, tempY);
                    // top
                    if ((outcode & Rectangle2D.OUT_TOP) == Rectangle2D.OUT_TOP) {
                        // and left
                        if ((outcode & Rectangle2D.OUT_LEFT) == Rectangle2D.OUT_LEFT) {
                            // linear distance from upper left corner
                            distance = Point2D.distance(tempX, tempY,
                                    rect.getMinX(), rect.getMinY());
                        } else {
                            // and right
                            if ((outcode & Rectangle2D.OUT_RIGHT) == Rectangle2D.OUT_RIGHT) {
                                // linear distance from upper right corner
                                distance = Point2D.distance(tempX, tempY,
                                        rect.getMaxX(), rect.getMinY());
                            } else {
                                // distance from top line segment
                                distance = Line2D.ptSegDist(rect.getMinX(),
                                        rect.getMinY(), rect.getMaxX(),
                                        rect.getMinY(), tempX, tempY);
                            }
                        }
                    } else {
                        // bottom
                        if ((outcode & Rectangle2D.OUT_BOTTOM) == Rectangle2D.OUT_BOTTOM) {
                            // and left
                            if ((outcode & Rectangle2D.OUT_LEFT) == Rectangle2D.OUT_LEFT) {
                                // linear distance from lower left corner
                                distance = Point2D.distance(tempX, tempY,
                                        rect.getMinX(), rect.getMaxY());
                            } else {
                                // and right
                                if ((outcode & Rectangle2D.OUT_RIGHT) == Rectangle2D.OUT_RIGHT) {
                                    // linear distance from lower right corner
                                    distance = Point2D.distance(tempX, tempY,
                                            rect.getMaxX(), rect.getMaxY());
                                } else {
                                    // distance from bottom line segment
                                    distance = Line2D.ptSegDist(rect.getMinX(),
                                            rect.getMaxY(), rect.getMaxX(),
                                            rect.getMaxY(), tempX, tempY);
                                }
                            }
                        } else {
                            // left only
                            if ((outcode & Rectangle2D.OUT_LEFT) == Rectangle2D.OUT_LEFT) {
                                // linear distance from left edge
                                distance = Line2D.ptSegDist(rect.getMinX(),
                                        rect.getMinY(), rect.getMinX(),
                                        rect.getMaxY(), tempX, tempY);
                            } else {
                                // right only
                                if ((outcode & Rectangle2D.OUT_RIGHT) == Rectangle2D.OUT_RIGHT) {
                                    // linear distance from right edge
                                    distance = Line2D.ptSegDist(rect.getMaxX(),
                                            rect.getMinY(), rect.getMaxX(),
                                            rect.getMaxY(), tempX, tempY);
                                }
                            }
                        }
                    }
                }
                // only influence if less than r1
                final double dr = distance - r1;
                if (dr < 0) {
                    potentialArea[x][y] += influenceFactor * dr * dr;
                }
            }
        }
    }

    /**
     * Add a contribution of an arbitrary area made of straight line segments to
     * all points in a given area. For every point in the given area, the
     * distance to the closest point on the area boundary is calculated and this
     * distance is input into the gradient influence function, then added to the
     * potentialArea.
     * 
     * @param potentialArea
     *            the area to fill with influence values
     * @param influenceFactor
     *            the influence factor of the line in the area
     * @param r1
     *            the radius at which energy drops to zero
     * @param a
     *            the area to add positive influence (in world coordinates)
     * @param activeArea
     *            the bounds of the calculation region
     */
    public void calculateAreaInfluence(final double[][] potentialArea,
            final double influenceFactor, final double r1, final Area a,
            final Rectangle2D activeArea) {
        double tempX, tempY, distance = 0;

        // create a deque of the lines
        final Deque<Line2D> lines = new ArrayDeque<Line2D>();
        final PathIterator pathIterator = a.getPathIterator(null);

        final double[] current = new double[6];
        final double[] previous = new double[6];
        double[] start = null;

        int segmentType;

        while (!pathIterator.isDone()) {
            segmentType = pathIterator.currentSegment(current);

            if (start == null) {
                start = new double[6];
                System.arraycopy(current, 0, start, 0, current.length);
            }

            if (segmentType == PathIterator.SEG_LINETO) {
                lines.add(new Line2D.Double(previous[0], previous[1],
                        current[0], current[1]));
            }
            if (segmentType == PathIterator.SEG_CLOSE) {
                lines.add(new Line2D.Double(previous[0], previous[1], start[0],
                        start[1]));
            }
            System.arraycopy(current, 0, previous, 0, current.length);

            pathIterator.next();
        }

        // go around edges
        calculateLinesInfluence(potentialArea, influenceFactor, r1, lines,
                activeArea);

        final int startX = Math.min(
                Math.max(0, (int) ((activeArea.getX() - r1) / pixelGroup)),
                potentialArea.length - 1);
        final int startY = Math.min(
                Math.max(0, (int) ((activeArea.getY() - r1) / pixelGroup)),
                potentialArea[startX].length - 1);
        final int endX = Math
                .min(potentialArea.length - 1,
                        Math.max(
                                0,
                                (int) ((activeArea.getX()
                                        + activeArea.getWidth() + r1) / pixelGroup)));
        final int endY = Math
                .min(potentialArea[startX].length,
                        Math.max(
                                0,
                                (int) ((activeArea.getY()
                                        + activeArea.getHeight() + r1) / pixelGroup)));

        // for every point in potentialArea, calculate distance to nearest point
        // on rectangle
        // and add influence
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                // if we are adding negative energy, skip if not already
                // positive
                // positives have already been added first, and adding negative
                // to <=0 will have no affect on surface

                if ((influenceFactor < 0) && (potentialArea[x][y] <= 0)) {
                    continue;
                }

                tempX = x * pixelGroup + activeArea.getX();
                tempY = y * pixelGroup + activeArea.getY();

                // inside
                if (a.contains(tempX, tempY)) {
                    distance = 0;
                } else {
                    distance = Double.MAX_VALUE;
                }
                // only influence if less than r1
                final double dr = distance - r1;
                if (dr < 0) {
                    potentialArea[x][y] += influenceFactor * dr * dr;
                }
            }
        }
    }

    /**
     * Paint the most recently calculated energy function into the given
     * graphics context. Used to visualize the energy calculation for debugging
     * purposes.
     * 
     * @param g2d
     *            the graphics context to draw into
     */

    public void paintPotential(final Graphics2D g2d) {
        if (potentialArea == null) {
            return;
        }

        // draw energy field
        int tempX, tempY;
        for (int x = 0; x < potentialArea.length - 1; x++) {
            for (int y = 0; y < potentialArea[x].length - 1; y++) {
                tempX = x * pixelGroup + (int) activeRegion.getX();
                tempY = y * pixelGroup + (int) activeRegion.getY();

                if (potentialArea[x][y] < 0) {
                    g2d.setColor(new Color(20, 20, 150, (int) Math.min(255,
                            Math.abs((potentialArea[x][y] * 40)))));
                } else {
                    g2d.setColor(new Color(150, 20, 20, (int) Math.min(255,
                            Math.abs((potentialArea[x][y] * 40)))));
                }
                if (potentialArea[x][y] == lastThreshold) {
                    g2d.setColor(new Color(0, 0, 0, 120));
                }
                g2d.fillRect(tempX, tempY, pixelGroup, pixelGroup);
            }
        }
    }

    /**
     * Find an appropriate split point in the line to wrap the line around the
     * given rectangle.
     * 
     * @param line
     *            the line to split
     * @param rectangle
     *            the rectangle which intersects the line exactly twice
     * @param rerouteBuffer
     *            the buffer to place between the selected reroute corner and
     *            the new point
     * @param intersections
     *            the intersections of the line with each of the rectangle edges
     * @param wrapNormal
     *            whether to wrap around the closest corner (if true) or the
     *            opposite corner (if false)
     * @return the position of the new endpoint
     */
    public Point2D rerouteLine(final Line2D line, final Rectangle2D rectangle,
            final double rerouteBuffer, final Intersection[] intersections,
            final boolean wrapNormal) {

        final Intersection topIntersect = intersections[0];
        final Intersection leftIntersect = intersections[1];
        final Intersection bottomIntersect = intersections[2];
        final Intersection rightIntersect = intersections[3];

        // wrap around the most efficient way
        if (wrapNormal) {
            // left side
            if (leftIntersect.getState() == State.Point) {
                if (topIntersect.getState() == State.Point) {
                    // triangle, must go around top left
                    return new Point2D.Double(rectangle.getMinX()
                            - rerouteBuffer, rectangle.getMinY()
                            - rerouteBuffer);
                }
                if (bottomIntersect.getState() == State.Point) {
                    // triangle, must go around bottom left
                    return new Point2D.Double(rectangle.getMinX()
                            - rerouteBuffer, rectangle.getMaxY()
                            + rerouteBuffer);
                }
                // else through left to right, calculate areas
                final double totalArea = rectangle.getHeight()
                        * rectangle.getWidth();
                // top area
                final double topArea = rectangle.getWidth()
                        * (((leftIntersect.getY() - rectangle.getY()) + (rightIntersect
                                .getY() - rectangle.getY())) / 2);
                if (topArea < totalArea / 2) {
                    // go around top (the side which would make a greater
                    // movement)
                    if (leftIntersect.getY() > rightIntersect.getY()) {
                        // top left
                        return new Point2D.Double(rectangle.getMinX()
                                - rerouteBuffer, rectangle.getMinY()
                                - rerouteBuffer);
                    }
                    // top right
                    return new Point2D.Double(rectangle.getMaxX()
                            + rerouteBuffer, rectangle.getMinY()
                            - rerouteBuffer);
                }
                // go around bottom
                if (leftIntersect.getY() < rightIntersect.getY()) {
                    // bottom left
                    return new Point2D.Double(rectangle.getMinX()
                            - rerouteBuffer, rectangle.getMaxY()
                            + rerouteBuffer);
                }
                // bottom right
                return new Point2D.Double(rectangle.getMaxX() + rerouteBuffer,
                        rectangle.getMaxY() + rerouteBuffer);
            }
            // right side
            if (rightIntersect.getState() == State.Point) {
                if (topIntersect.getState() == State.Point) {
                    // triangle, must go around top right
                    return new Point2D.Double(rectangle.getMaxX()
                            + rerouteBuffer, rectangle.getMinY()
                            - rerouteBuffer);
                }
                if (bottomIntersect.getState() == State.Point) {
                    // triangle, must go around bottom right
                    return new Point2D.Double(rectangle.getMaxX()
                            + rerouteBuffer, rectangle.getMaxY()
                            + rerouteBuffer);
                }
            }
            // else through top to bottom, calculate areas
            final double totalArea = rectangle.getHeight()
                    * rectangle.getWidth();
            final double leftArea = rectangle.getHeight()
                    * (((topIntersect.getX() - rectangle.getX()) + (rightIntersect
                            .getX() - rectangle.getX())) / 2);
            if (leftArea < totalArea / 2) {
                // go around left
                if (topIntersect.getX() > bottomIntersect.getX()) {
                    // top left
                    return new Point2D.Double(rectangle.getMinX()
                            - rerouteBuffer, rectangle.getMinY()
                            - rerouteBuffer);
                }
                // bottom left
                return new Point2D.Double(rectangle.getMinX() - rerouteBuffer,
                        rectangle.getMaxY() + rerouteBuffer);
            }
            // go around right
            if (topIntersect.getX() < bottomIntersect.getX()) {
                // top right
                return new Point2D.Double(rectangle.getMaxX() + rerouteBuffer,
                        rectangle.getMinY() - rerouteBuffer);
            }
            // bottom right
            return new Point2D.Double(rectangle.getMaxX() + rerouteBuffer,
                    rectangle.getMaxY() + rerouteBuffer);
        }
        // wrap around opposite (usually because the first move caused a
        // problem)
        if (leftIntersect.getState() == State.Point) {
            if (topIntersect.getState() == State.Point) {
                // triangle, must go around bottom right
                return new Point2D.Double(rectangle.getMaxX() + rerouteBuffer,
                        rectangle.getMaxY() + rerouteBuffer);
            }
            if (bottomIntersect.getState() == State.Point) {
                // triangle, must go around top right
                return new Point2D.Double(rectangle.getMaxX() + rerouteBuffer,
                        rectangle.getMinY() - rerouteBuffer);
            }
            // else through left to right, calculate areas
            final double totalArea = rectangle.getHeight()
                    * rectangle.getWidth();
            final double topArea = rectangle.getWidth()
                    * (((leftIntersect.getY() - rectangle.getY()) + (rightIntersect
                            .getY() - rectangle.getY())) / 2);
            if (topArea < totalArea / 2) {
                // go around bottom (the side which would make a lesser
                // movement)
                if (leftIntersect.getY() > rightIntersect.getY()) {
                    // bottom right
                    return new Point2D.Double(rectangle.getMaxX()
                            + rerouteBuffer, rectangle.getMaxY()
                            + rerouteBuffer);
                }
                // bottom left
                return new Point2D.Double(rectangle.getMinX() - rerouteBuffer,
                        rectangle.getMaxY() + rerouteBuffer);
            }
            // go around top
            if (leftIntersect.getY() < rightIntersect.getY()) {
                // top right
                return new Point2D.Double(rectangle.getMaxX() + rerouteBuffer,
                        rectangle.getMinY() - rerouteBuffer);
            }
            // top left
            return new Point2D.Double(rectangle.getMinX() - rerouteBuffer,
                    rectangle.getMinY() - rerouteBuffer);
        }
        if (rightIntersect.getState() == State.Point) {
            if (topIntersect.getState() == State.Point) {
                // triangle, must go around bottom left
                return new Point2D.Double(rectangle.getMinX() - rerouteBuffer,
                        rectangle.getMaxY() + rerouteBuffer);
            }
            if (bottomIntersect.getState() == State.Point) {
                // triangle, must go around top left
                return new Point2D.Double(rectangle.getMinX() - rerouteBuffer,
                        rectangle.getMinY() - rerouteBuffer);
            }
        }
        // else through top to bottom, calculate areas
        final double totalArea = rectangle.getHeight() * rectangle.getWidth();
        final double leftArea = rectangle.getHeight()
                * (((topIntersect.getX() - rectangle.getX()) + (rightIntersect
                        .getX() - rectangle.getX())) / 2);
        if (leftArea < totalArea / 2) {
            // go around right
            if (topIntersect.getX() > bottomIntersect.getX()) {
                // bottom right
                return new Point2D.Double(rectangle.getMaxX() + rerouteBuffer,
                        rectangle.getMaxY() + rerouteBuffer);
            }
            // top right
            return new Point2D.Double(rectangle.getMaxX() + rerouteBuffer,
                    rectangle.getMinY() - rerouteBuffer);
        }
        // go around left
        if (topIntersect.getX() < bottomIntersect.getX()) {
            // bottom left
            return new Point2D.Double(rectangle.getMinX() - rerouteBuffer,
                    rectangle.getMaxY() + rerouteBuffer);
        }
        // top left
        return new Point2D.Double(rectangle.getMinX() - rerouteBuffer,
                rectangle.getMinY() - rerouteBuffer);
    }

    // ///////////////////////
    // GETTERS / SETTERS
    // ///////////////////////

    /**
     * @return the maximum number of routing iterations.
     */
    public int getMaxRoutingIterations() {
        return maxRoutingIterations;
    }

    /**
     * @return the maximum number of marching iterations.
     */
    public int getMaxMarchingIterations() {
        return maxMarchingIterations;
    }

    /**
     * @return the size of square super pixels used for calculations (larger
     *         results in lower resolution contours and faster calculations).
     */
    public int getPixelGroup() {
        return pixelGroup;
    }

    /**
     * @param pixelGroup
     *            the size of square super pixels used for calculations (larger
     *            results in lower resolution contours and faster calculations).
     */
    public void setPixelGroup(final int pixelGroup) {
        this.pixelGroup = pixelGroup;
    }

    /**
     * @return the edgeR0
     */
    public double getEdgeR0() {
        return edgeR0;
    }

    /**
     * @param edgeR0
     *            the edgeR0 to set
     */
    public void setEdgeR0(final double edgeR0) {
        this.edgeR0 = edgeR0;
    }

    /**
     * @return the edgeR1
     */
    public double getEdgeR1() {
        return edgeR1;
    }

    /**
     * @param edgeR1
     *            the edgeR1 to set
     */
    public void setEdgeR1(final double edgeR1) {
        this.edgeR1 = edgeR1;
    }

    /**
     * @return the nodeR0
     */
    public double getNodeR0() {
        return nodeR0;
    }

    /**
     * @param nodeR0
     *            the nodeR0 to set
     */
    public void setNodeR0(final double nodeR0) {
        this.nodeR0 = nodeR0;
    }

    /**
     * @return the nodeR1
     */
    public double getNodeR1() {
        return nodeR1;
    }

    /**
     * @param nodeR1
     *            the nodeR1 to set
     */
    public void setNodeR1(final double nodeR1) {
        this.nodeR1 = nodeR1;
    }

    /**
     * @return the morphBuffer
     */
    public double getMorphBuffer() {
        return morphBuffer;
    }

    /**
     * @param morphBuffer
     *            the morphBuffer to set
     */
    public void setMorphBuffer(final double morphBuffer) {
        this.morphBuffer = morphBuffer;
    }

    /**
     * @return the number of points to skip in the marching squares when making
     *         the contour
     */
    public int getSkip() {
        return skip;
    }

    /**
     * @param skip
     *            the number of points to skip in the marching squares when
     *            making the contour
     */
    public void setSkip(final int skip) {
        this.skip = skip;
    }

    /**
     * @return whether this bubble set uses optimized data structures.
     */
    public boolean useOptimizedDataStructures() {
        return useOptimizedDataStructures;
    }

    /**
     * @param useOptimizedDataStructures
     *            whether to use optimized data structures
     */
    public void setUseOptimizedDataStructures(
            final boolean useOptimizedDataStructures) {
        this.useOptimizedDataStructures = useOptimizedDataStructures;
    }

}
