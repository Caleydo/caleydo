package setvis.gui;

import java.util.HashMap;
import java.util.Map;

import setvis.shape.AbstractShapeGenerator;
import setvis.shape.BSplineShapeGenerator;
import setvis.shape.BezierShapeGenerator;
import setvis.shape.PolygonShapeGenerator;
import setvis.shape.ShapeSimplifier;

/**
 * Enumerates the types of shape generators.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * 
 */
public enum ShapeType {

    /** Direct line shape generator. */
    LINES("Direct Lines", PolygonShapeGenerator.class),

    /** Bezier line shape generator. */
    BEZIER("Bezier Curves", BezierShapeGenerator.class),

    /** B-Splines shape generator. */
    BSPLINE("B-Splines", BSplineShapeGenerator.class),

    ;

    /** The associated class. */
    private final Class<? extends AbstractShapeGenerator> assocClass;

    /** A readable name. */
    private final String name;

    /**
     * Creates an outline type.
     * 
     * @param name
     *            A readable name.
     * @param assocClass
     *            The associated class. It is used to reverse lookup types from
     *            {@link AbstractShapeGenerator} objects.
     */
    private ShapeType(final String name,
            final Class<? extends AbstractShapeGenerator> assocClass) {
        this.name = name;
        this.assocClass = assocClass;
    }

    @Override
    public String toString() {
        return name;
    }

    /** The lookup map. */
    private static final Map<Class<?>, ShapeType> MAP = new HashMap<Class<?>, ShapeType>();

    /** Initializing the map. */
    static {
        for (final ShapeType type : values()) {
            MAP.put(type.assocClass, type);
        }
    }

    /**
     * Finds the type of the given object.
     * 
     * @param shaper
     *            The shape generator object to find the type for.
     * @return The type of the given object.
     */
    public static ShapeType getFor(final AbstractShapeGenerator shaper) {
        return MAP.get(shaper.getClass());
    }

    public static void creationText(final AbstractShapeGenerator shaper,
            final StringBuilder sb) {
        if (shaper instanceof BezierShapeGenerator) {
            final BezierShapeGenerator b = (BezierShapeGenerator) shaper;
            creationPrefix(shaper, sb);
            OutlineType.creationText(shaper.getSetOutline(), sb);
            if (b.hasMaxRadius() && b.isClockwise()) {
                creationPostfix(sb);
                return;
            }
            creationSeparator(sb);
            sb.append(b.isClockwise());
            creationSeparator(sb);
            sb.append(b.hasMaxRadius());
            creationPostfix(sb);
        } else if (shaper instanceof BSplineShapeGenerator) {
            final BSplineShapeGenerator b = (BSplineShapeGenerator) shaper;
            final AbstractShapeGenerator parent = b.getParent();
            creationPrefix(shaper, sb);
            if (parent instanceof PolygonShapeGenerator) {
                OutlineType.creationText(shaper.getSetOutline(), sb);
                creationPostfix(sb);
                return;
            }
            creationText(parent, sb);
            creationPostfix(sb);
        } else if (shaper instanceof ShapeSimplifier) {
            final ShapeSimplifier s = (ShapeSimplifier) shaper;
            final AbstractShapeGenerator parent = s.getParent();
            if (s.isDisabled()) {
                creationText(parent, sb);
                return;
            }
            creationPrefix(shaper, sb);
            creationText(parent, sb);
            if (s.getTolerance() != 0.0) {
                creationSeparator(sb);
                sb.append(s.getTolerance());
            }
            creationPostfix(sb);
            return;
        } else {
            creationPrefix(shaper, sb);
            OutlineType.creationText(shaper.getSetOutline(), sb);
            creationPostfix(sb);
        }
    }

    private static void creationPrefix(final AbstractShapeGenerator shaper,
            final StringBuilder sb) {
        sb.append("new ");
        sb.append(shaper.getClass().getSimpleName());
        sb.append("(");
    }

    private static void creationSeparator(final StringBuilder sb) {
        sb.append(", ");
    }

    private static void creationPostfix(final StringBuilder sb) {
        sb.append(")");
    }

}
