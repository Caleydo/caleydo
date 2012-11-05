package setvis.gui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import setvis.SetOutline;
import setvis.bubbleset.BubbleSet;
import setvis.ch.ConvexHull;

/**
 * Enumerates the types of outline generators.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public enum OutlineType {

    /** The bubble set generator. */
    BUBBLE_SETS("Bubble Sets", BubbleSet.class),

    /** A convex hull generator. */
    CONVEX_HULL("Convex Hull", ConvexHull.class),

    ;

    /** A readable name. */
    private String name;

    /** The associated class. */
    private final Class<? extends SetOutline> assocClass;

    /**
     * Creates an outline type.
     * 
     * @param name
     *            A readable name.
     * @param assocClass
     *            The associated class. It is used to reverse lookup types from
     *            {@link SetOutline} objects.
     */
    private OutlineType(final String name,
            final Class<? extends SetOutline> assocClass) {
        this.name = name;
        this.assocClass = assocClass;
    }

    @Override
    public String toString() {
        return name;
    }

    /** The lookup map. */
    private static final Map<Class<?>, OutlineType> MAP = new HashMap<Class<?>, OutlineType>();

    /** Initializing the map. */
    static {
        for (final OutlineType type : values()) {
            MAP.put(type.assocClass, type);
        }
    }

    /**
     * Finds the type of the given object.
     * 
     * @param set
     *            The outline object to find the type for.
     * @return The type of the given object.
     */
    public static OutlineType getFor(final SetOutline set) {
        return MAP.get(set.getClass());
    }

    /**
     * Creates an outline configuration panel for the given canvas.
     * 
     * @param canvas
     *            The canvas.
     * @return The panel or <code>null</code> if no panel is needed.
     */
    public AbstractOutlineConfiguration createOutlineConfiguration(
            final Canvas canvas) {
        switch (this) {
        case BUBBLE_SETS:
            return createBubbleSetConfiguration(canvas);
        default:
            break;
        }
        return null;
    }

    /**
     * Creates a bubble set configuration panel for the given canvas.
     * 
     * @param canvas
     *            The canvas.
     * @return The bubble set configuration panel.
     */
    private AbstractOutlineConfiguration createBubbleSetConfiguration(
            final Canvas canvas) {
        return new AbstractOutlineConfiguration(canvas, this) {

            // the serial version uid
            private static final long serialVersionUID = -4099593260786691472L;

            /** The granularity of floating point sliders. */
            private static final double GRANULARITY = 0.5;

            /** The slider for the number of routing iterations. */
            private final JSlider routingIt = new JSlider(10, 1000);

            /** The slider for the number of marching iterations. */
            private final JSlider marchingIt = new JSlider(1, 100);

            /** The slider for the size of the pixel groups. */
            private final JSlider pixelGroup = new JSlider(1, 10);

            /** The slider for edge radius number 0. */
            private final JSlider edgeR0 = new JSlider(1, 200);

            /** The slider for edge radius number 1. */
            private final JSlider edgeR1 = new JSlider(1, 200);

            /** The slider for node radius number 0. */
            private final JSlider nodeR0 = new JSlider(1, 200);

            /** The slider for node radius number 1. */
            private final JSlider nodeR1 = new JSlider(1, 200);

            /** The slider for the morphing buffer. */
            private final JSlider morphingSlider = new JSlider(1, 200);

            /** The slider for skipped points. */
            private final JSlider skip = new JSlider(1, 30);

            /** The label for corresponding slider. */
            private final JLabel routingItLabel = new JLabel();

            /** The label for corresponding slider. */
            private final JLabel marchingItLabel = new JLabel();

            /** The label for corresponding slider. */
            private final JLabel pixelGroupLabel = new JLabel();

            /** The label for corresponding slider. */
            private final JLabel edgeR0Label = new JLabel();

            /** The label for corresponding slider. */
            private final JLabel edgeR1Label = new JLabel();

            /** The label for corresponding slider. */
            private final JLabel nodeR0Label = new JLabel();

            /** The label for corresponding slider. */
            private final JLabel nodeR1Label = new JLabel();

            /** The label for corresponding slider. */
            private final JLabel morphingSliderLabel = new JLabel();

            /** The label for corresponding slider. */
            private final JLabel skipLabel = new JLabel();

            /**
             * Whether this component is in update mode. When this component is
             * in the update mode, the changes are assumed to be outside and no
             * changes to the underlying bubble set are made.
             */
            private boolean textUpdate = true;

            @Override
            protected void doFillContent() {
                // definings the change listeners
                final ChangeListener routeMarchListener = new ChangeListener() {

                    @Override
                    public void stateChanged(final ChangeEvent e) {
                        if (textUpdate) {
                            return;
                        }
                        final BubbleSet bubble = (BubbleSet) getOutline();
                        // for changing iterations we have to create a new
                        // bubble set object
                        final BubbleSet newBubble = new BubbleSet(routingIt
                                .getValue(), marchingIt.getValue(), bubble
                                .getPixelGroup(), bubble.getEdgeR0(), bubble
                                .getEdgeR1(), bubble.getNodeR0(), bubble
                                .getNodeR1(), bubble.getMorphBuffer(), bubble
                                .getSkip());
                        canvas.setShapeAndOutline(newBubble, ShapeType
                                .getFor(canvas.getShapeCreator()));
                    }

                };
                routingIt.addChangeListener(routeMarchListener);
                marchingIt.addChangeListener(routeMarchListener);
                final ChangeListener change = new ChangeListener() {

                    @Override
                    public void stateChanged(final ChangeEvent e) {
                        if (textUpdate) {
                            return;
                        }
                        // it is enough to adjust the settings in the current
                        // bubble set object
                        final BubbleSet bubble = (BubbleSet) getOutline();
                        bubble.setSkip(skip.getValue());
                        bubble.setPixelGroup(pixelGroup.getValue());
                        bubble.setMorphBuffer(morphingSlider.getValue()
                                * GRANULARITY);
                        bubble.setEdgeR0(edgeR0.getValue() * GRANULARITY);
                        bubble.setEdgeR1(edgeR1.getValue() * GRANULARITY);
                        bubble.setNodeR0(nodeR0.getValue() * GRANULARITY);
                        bubble.setNodeR1(nodeR1.getValue() * GRANULARITY);
                        changed();
                    }
                };
                skip.addChangeListener(change);
                pixelGroup.addChangeListener(change);
                morphingSlider.addChangeListener(change);
                edgeR0.addChangeListener(change);
                edgeR1.addChangeListener(change);
                nodeR0.addChangeListener(change);
                nodeR1.addChangeListener(change);
                // adding the components
                addHor(new JLabel("Routing Iterations:"), routingIt,
                        routingItLabel);
                addHor(new JLabel("Marching Iterations:"), marchingIt,
                        marchingItLabel);
                addHor(new JLabel("Pixel Group:"), pixelGroup, pixelGroupLabel);
                addHor(new JLabel("Edge Radius 0:"), edgeR0, edgeR0Label);
                addHor(new JLabel("Edge Radius 1:"), edgeR1, edgeR1Label);
                addHor(new JLabel("Node Radius 0:"), nodeR0, nodeR0Label);
                addHor(new JLabel("Node Radius 1:"), nodeR1, nodeR1Label);
                addHor(new JLabel("Morphing Buffer:"), morphingSlider,
                        morphingSliderLabel);
                addHor(new JLabel("Skip points:"), skip, skipLabel);
            }

            @Override
            public void somethingChanged() {
                // update all the components
                // going into updating mode, that we get no interference with
                // the change listeners
                textUpdate = true;
                final BubbleSet bubble = (BubbleSet) getOutline();
                final int s = bubble.getSkip();
                skip.setValue(s);
                skipLabel.setText("" + s);
                final int pg = bubble.getPixelGroup();
                pixelGroup.setValue(pg);
                pixelGroupLabel.setText("" + pg);
                final int rit = bubble.getMaxRoutingIterations();
                routingIt.setValue(rit);
                routingItLabel.setText("" + rit);
                final int mit = bubble.getMaxMarchingIterations();
                marchingIt.setValue(mit);
                marchingItLabel.setText("" + mit);
                final double mb = bubble.getMorphBuffer();
                morphingSlider.setValue((int) (mb / GRANULARITY));
                morphingSliderLabel.setText("" + mb);
                final double e0 = bubble.getEdgeR0();
                edgeR0.setValue((int) (e0 / GRANULARITY));
                edgeR0Label.setText("" + e0);
                final double e1 = bubble.getEdgeR1();
                edgeR1.setValue((int) (e1 / GRANULARITY));
                edgeR1Label.setText("" + e1);
                final double n0 = bubble.getNodeR0();
                nodeR0.setValue((int) (n0 / GRANULARITY));
                nodeR0Label.setText("" + n0);
                final double n1 = bubble.getNodeR1();
                nodeR1.setValue((int) (n1 / GRANULARITY));
                nodeR1Label.setText("" + n1);
                // out of update mode
                textUpdate = false;
            }

        };
    }

    public static void creationText(final SetOutline outline,
            final StringBuilder sb) {
        sb.append("new ");
        sb.append(outline.getClass().getSimpleName());
        sb.append("(");
        if (outline instanceof BubbleSet) {
            final BubbleSet b = (BubbleSet) outline;
            if (BubbleSet.defaultEdgeR0 == b.getEdgeR0()
                    && BubbleSet.defaultEdgeR1 == b.getEdgeR1()
                    && BubbleSet.defaultMaxMarchingIterations == b
                    .getMaxMarchingIterations()
                    && BubbleSet.defaultMaxRoutingIterations == b
                    .getMaxRoutingIterations()
                    && BubbleSet.defaultMorphBuffer == b.getMorphBuffer()
                    && BubbleSet.defaultNodeR0 == b.getNodeR0()
                    && BubbleSet.defaultNodeR1 == b.getNodeR1()
                    && BubbleSet.defaultPixelGroup == b.getPixelGroup()
                    && BubbleSet.defaultSkip == b.getSkip()) {
                sb.append(")");
                return;
            }
            sb.append(b.getMaxRoutingIterations());
            sb.append(CREATION_SEPARATOR);
            sb.append(b.getMaxMarchingIterations());
            sb.append(CREATION_SEPARATOR);
            sb.append(b.getPixelGroup());
            sb.append(CREATION_SEPARATOR);
            sb.append(b.getEdgeR0());
            sb.append(CREATION_SEPARATOR);
            sb.append(b.getEdgeR1());
            sb.append(CREATION_SEPARATOR);
            sb.append(b.getNodeR0());
            sb.append(CREATION_SEPARATOR);
            sb.append(b.getNodeR1());
            sb.append(CREATION_SEPARATOR);
            sb.append(b.getMorphBuffer());
            sb.append(CREATION_SEPARATOR);
            sb.append(b.getSkip());
        }
        sb.append(")");
    }

    private static final String CREATION_SEPARATOR = ", ";

}
