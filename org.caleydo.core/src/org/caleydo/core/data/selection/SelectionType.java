package org.caleydo.core.data.selection;

import java.util.ArrayList;

/**
 * <p>
 * A SelectionType is an object associated with a particular set of elements. The members of the set typically
 * are determined by user selection.
 * </p>
 * <p>
 * There are two types of Selection types: the default types ({@link #NORMAL}, {@link #MOUSE_OVER},
 * {@link #SELECTION} and {@link #DESELECTED}) as well as user-defined types.
 * </p>
 * TODO add info about user defined types
 * 
 * @author Alexander Lex
 */
public class SelectionType {

	/** a name for the selection type, human readable */
	private String type = "Not set";
	/** the color the selection type should be rendered in */
	private float[] color = new float[] { 0, 0, 0, 1 };
	/** the color equivalent to float color in int space (0-255) */
	private int[] intColor = new int[] { 0, 0, 0 };
	/** flag that determines whether an element of this selection type should be visible or not */
	private boolean isVisible = true;
	/** flag that determines whether connection lines should be drawn to this selection type or not */
	private boolean isConnected = false;
	/** line rendering views should use this width for this selection type */
	private int lineWidth = 1;
	/**
	 * a priority determining which selection type should be rendered on top in case of multi-selections. The
	 * valid range is 0-1 where {@link #NORMAL} has 0, {@link #MOUSE_OVER} 1 and {@link #SELECTION} 0.99
	 */
	private float priority = 0.1f;

	public static final SelectionType NORMAL =
		new SelectionType("Normal", new float[] { 0, 0, 0, 1 }, 1, true, false, 0);
	public static final SelectionType MOUSE_OVER =
		new SelectionType("MouseOver", new int[] { 116, 169, 207 }, 3, true, true, 1);
	public static final SelectionType SELECTION =
		new SelectionType("Selected", new int[] { 5, 112, 176 }, 3, true, false, 0.99f);
	public static final SelectionType DESELECTED =
		new SelectionType("Deselected", new float[] { 0, 0, 0, 1 }, 1, false, false, 0);

	private static ArrayList<SelectionType> defaultTypes = new ArrayList<SelectionType>();

	/**
	 * Default constructur, use getters and setters to initialize
	 */
	public SelectionType() {
	}

	/**
	 * Convenience constructor for batch initialization
	 * 
	 * @param type
	 *            a name for the selection type, human readable
	 * @param color
	 *            the color the selection type should be rendered in
	 * @param lineWidth
	 *            the width of lines that should be used for this selection type in line rendering views
	 * @param isVisible
	 *            flag that determines whether an element of this selection type should be visible or not
	 * @param isConnected
	 *            flag that determines whether connection lines should be drawn to this selection type or not
	 * @param priority
	 *            a priority determining which selection type should be rendered on top in case of
	 *            multi-selections. The valid range is 0-1 where {@link #NORMAL} has 0, {@link #MOUSE_OVER} 1
	 *            and {@link #SELECTION} 0.99
	 */
	public SelectionType(String type, float[] color, int lineWidth, boolean isVisible, boolean isConnected,
		float priority) {
		this.type = type;
		setColor(color);
		setIntColor(convertFloatColor(color));
		this.lineWidth = lineWidth;
		this.isVisible = isVisible;
		this.isConnected = isConnected;
		checkPiority(priority);
		this.priority = priority;
	}

	/**
	 * Convenience constructor for batch initialization, same as
	 * {@link #SelectionType(String, float[], boolean, boolean, float)} except for that the color space is
	 * defined in integers (0-255) instead of floats and contains only 3 values. The float color space is
	 * calculated automatically and the transparency is assumed to be 1. It is possible to use
	 * 
	 * @param type
	 *            a name for the selection type, human readable
	 * @param color
	 *            the color the selection type should be rendered in in integers (0-255)
	 * @param lineWidth
	 *            the width of lines that should be used for this selection type in line rendering views
	 * @param isVisible
	 *            flag that determines whether an element of this selection type should be visible or not
	 * @param isConnected
	 *            flag that determines whether connection lines should be drawn to this selection type or not
	 * @param priority
	 *            a priority determining which selection type should be rendered on top in case of
	 *            multi-selections. The valid range is 0-1 where {@link #NORMAL} has 0, {@link #MOUSE_OVER} 1
	 *            and {@link #SELECTION} 0.99
	 */
	public SelectionType(String type, int[] intColor, int lineWidth, boolean isVisible, boolean isConnected,
		float priority) {
		this.type = type;
		setIntColor(intColor);
		setColor(convertIntColor(intColor));
		this.lineWidth = lineWidth;
		this.isVisible = isVisible;
		this.isConnected = isConnected;
		checkPiority(priority);
		this.priority = priority;
	}

	/**
	 * Returns the {@link #type} of the selection type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the {@link #type} of the selection type
	 * 
	 * @param type
	 *            the type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the {@link #color} of the selection type
	 * 
	 * @return the color
	 */
	public float[] getColor() {
		return color;
	}

	/**
	 * Sets the {@link #color} of the selection type
	 * 
	 * @param color
	 *            the color
	 */
	public void setColor(float[] color) {
		if (color.length != 4)
			throw new IllegalArgumentException("Color has to contain exactly 4 float values, but was: "
				+ color);
		this.color = color;
	}

	/**
	 * Returns the int color array of length 3 (0-255)
	 * 
	 * @return
	 */
	public int[] getIntColor() {
		return intColor;
	}

	/**
	 * Sets the int color for this selection type (0-255). Does not automatically calculate the float values;
	 * 
	 * @param intColor
	 */
	public void setIntColor(int[] intColor) {
		if (intColor.length != 3)
			throw new IllegalArgumentException("intColor has to contain exactly 3 int values, but was: "
				+ intColor);
		this.intColor = intColor;
	}

	/**
	 * @param lineWidth
	 *            the the width of lines that should be used for this selection type in line rendering views
	 */
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * @return the the width of lines that should be used for this selection type in line rendering views
	 */
	public int getLineWidth() {
		return lineWidth;
	}

	/**
	 * Tells you whether a selection type should be rendered visible or not
	 * 
	 * @return true if the selection type should be visible, else false
	 * @see #isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Set whether a selection type should be rendered visible or not
	 * 
	 * @param isVisible
	 *            true if the selection type should be visible, else false
	 * @see #isVisible
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Tells you whether a selection type should be connected with Visual Links or not
	 * 
	 * @return true if the selection type should be connected, else false
	 * @see #isConnected
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * Set whether a selection type should be connected with Visual Links or not
	 * 
	 * @param isConnected
	 *            true if the selection type should be connected, else false
	 * @see #isConnected
	 */
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	/**
	 * Returns the priority of the SelectionType, which tells you on which level it should be rendered in case
	 * of multiple selections. For conventions see {@link #priority}
	 * 
	 * @return the priority, between 1 and 0
	 */
	public float getPriority() {
		return priority;
	}

	/**
	 * Returns the priority of the SelectionType, which tells you on which level it should be rendered in case
	 * of multiple selections. For conventions see {@link #priority}
	 * 
	 * @return the priority, between 1 and 0
	 */
	public void setPriority(float priority) {
		checkPiority(priority);
		this.priority = priority;
	}

	private void checkPiority(float priority) {
		if (priority > 1 || priority < 0)
			throw new IllegalArgumentException(
				"Argument value not valid, must be in the range between 0 and 1");
	}

	@Override
	public String toString() {
		return type;
	}

	/**
	 * Get a list of all default types
	 * 
	 * @return the list of default types
	 */
	public static synchronized ArrayList<SelectionType> getDefaultTypes() {
		if (defaultTypes.isEmpty()) {
			defaultTypes.add(NORMAL);
			defaultTypes.add(MOUSE_OVER);
			defaultTypes.add(SELECTION);
			defaultTypes.add(DESELECTED);
		}
		return defaultTypes;
	}

	/**
	 * Check whether the provided type is a default type.
	 * 
	 * @param selectionType
	 *            a type to be checked whether it is default
	 * @return true if selectionType is a default type, else false
	 */
	public static boolean isDefaultType(SelectionType selectionType) {
		if (NORMAL.equals(selectionType) || MOUSE_OVER.equals(selectionType)
			|| SELECTION.equals(selectionType) || DESELECTED.equals(defaultTypes))
			return true;

		return false;
	}

	private static int[] convertFloatColor(float[] color) {
		int[] intColor = new int[3];
		intColor[0] = (int) (color[0] * 255 / color[3]);
		intColor[1] = (int) (color[0] * 255 / color[3]);
		intColor[2] = (int) (color[0] * 255 / color[3]);
		return intColor;
	}

	private static float[] convertIntColor(int[] intColor) {
		float[] color = new float[4];
		color[0] = ((float) intColor[0]) / 255;
		color[1] = ((float) intColor[1]) / 255;
		color[2] = ((float) intColor[2]) / 255;
		color[3] = 1;
		return color;
	}

}
