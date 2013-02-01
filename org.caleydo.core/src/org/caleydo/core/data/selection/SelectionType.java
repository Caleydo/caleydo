/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.selection;

import java.util.ArrayList;

/**
 * <p>
 * A SelectionType is an object associated with a particular set of elements. The members of the set typically are
 * determined by user selection.
 * </p>
 * <p>
 * There are two types of Selection types: the default types ({@link #NORMAL}, {@link #MOUSE_OVER}, {@link #SELECTION}
 * and {@link #DESELECTED}) as well as user-defined types.
 * </p>
 * <p>
 * A Selection has the following properties, all of which have default values, so it is only necessary to define those
 * values that are of interest:
 * </p>
 * <ul>
 * <li><b>{@link #color}</b> either as and int[] or as a float[]. No matter which one is used the other is calculated.</li>
 * <li><b>visibility</b> determines whether elements of this type should be visible</li>
 * <li><b></b></li>
 * <li><b>connectivity</b> determines whether an element should be connected via connection lines or not</li>
 * <li><b>line width</b> provides the line width that should be used when line based visualizations are rendered</li>
 * <li><b>priority</b> the priority is used to determine the Z value. The priority has to be between 0 and 1. Elements
 * of higher priority are rendered further on top.</li>
 * <li><b>is managed</b> selections can be managed via the RCP Selection Browser view. If a selection should be managed
 * this way is determined by this flag.</li>
 * </ul>
 *
 * @author Alexander Lex
 */
public class SelectionType implements Comparable<SelectionType> {

	/** a name for the selection type, human readable */
	private String type = "Not set";
	/** the color the selection type should be rendered in */
	private float[] color = new float[] { 0, 0, 0, 1 };
	/** the color equivalent to float color in int space (0-255) */
	private int[] intColor = new int[] { 0, 0, 0 };
	/**
	 * flag that determines whether an element of this selection type should be visible or not
	 */
	private boolean isVisible = true;

	/** line rendering views should use this width for this selection type */
	private float lineWidth = 0.3f;
	/**
	 * a priority determining which selection type should be rendered on top in case of multi-selections. The valid
	 * range is 0-1 where {@link #NORMAL} has 0, {@link #MOUSE_OVER} 1 and {@link #SELECTION} 0.99
	 */
	private Float priority = 0.1f;

	/**
	 * flag that determines whether a particular selection type should be managed by the {@link RcpSelectionBrowserView}
	 * . only managed types will appear in the selection browser view. default is false. can be changed on demand via
	 * the setter method.
	 */
	private boolean isManaged = false;

	public static final SelectionType NORMAL = new SelectionType("Normal", new float[] { 0, 0, 0, 1 }, 0.3f, true, 0);

	public static final SelectionType MOUSE_OVER = new SelectionType("MouseOver", new int[] { 249, 196, 79 }, 1, true,
			0.99f);

	public static final SelectionType SELECTION = new SelectionType("Selected", new int[] { 236, 112, 20 }, 1, true, 1f);

	public static final SelectionType DESELECTED = new SelectionType("Deselected", new float[] { 0, 0, 0, 1 }, 1,
			false, 0);

	public static final SelectionType LEVEL_HIGHLIGHTING = new SelectionType("LevelHighlighting", new int[] { 255, 255,
			0 }, 1, true, 1f);

	private static ArrayList<SelectionType> defaultTypes = new ArrayList<SelectionType>();

	/**
	 * Default constructor, use getters and setters to initialize
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
	 *            <p>
	 *            a priority determining which selection type should be rendered on top in case of multi-selections. The
	 *            valid range is 0-1 where {@link #NORMAL} has 0, {@link #MOUSE_OVER} 1 and {@link #SELECTION} 0.99.
	 *            </p>
	 *            <p>
	 *            The best way to apply this to actual render hight is to specify a render height constant and multiply
	 *            it by the priority, e.g., <code>float z = SELECTION_Z * selectionType.getPriority();</code>
	 */
	public SelectionType(String type, float[] color, float lineWidth, boolean isVisible, float priority) {
		this.type = type;
		setColor(color);
		setIntColor(convertFloatColor(color));
		this.lineWidth = lineWidth;
		this.isVisible = isVisible;
		checkPiority(priority);
		this.priority = priority;
	}

	/**
	 * Convenience constructor for batch initialization, same as
	 * {@link #SelectionType(String, float[], boolean, boolean, float)} except for that the color space is defined in
	 * integers (0-255) instead of floats and contains only 3 values. The float color space is calculated automatically
	 * and the transparency is assumed to be 1. It is possible to use
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
	 *            a priority determining which selection type should be rendered on top in case of multi-selections. The
	 *            valid range is 0-1 where {@link #NORMAL} has 0, {@link #MOUSE_OVER} 1 and {@link #SELECTION} 0.99
	 */
	public SelectionType(String type, int[] intColor, int lineWidth, boolean isVisible, float priority) {
		this.type = type;
		setIntColor(intColor);
		setColor(convertIntColor(intColor));
		this.lineWidth = lineWidth;
		this.isVisible = isVisible;
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
			throw new IllegalArgumentException("Color has to contain exactly 4 float values, but was: " + color);
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
			throw new IllegalArgumentException("intColor has to contain exactly 3 int values, but was: " + intColor);
		this.intColor = intColor;
	}

	/**
	 * @param lineWidth
	 *            the the width of lines that should be used for this selection type in line rendering views
	 */
	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * @return the the width of lines that should be used for this selection type in line rendering views
	 */
	public float getLineWidth() {
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
	 * Returns the priority of the SelectionType, which tells you on which level it should be rendered in case of
	 * multiple selections. For conventions see {@link #priority}
	 *
	 * @return the priority, between 1 and 0
	 */
	public float getPriority() {
		return priority;
	}

	/**
	 * Returns the priority of the SelectionType, which tells you on which level it should be rendered in case of
	 * multiple selections. For conventions see {@link #priority}
	 *
	 * @return the priority, between 1 and 0
	 */
	public void setPriority(float priority) {
		checkPiority(priority);
		this.priority = priority;
	}

	private void checkPiority(float priority) {
		if (priority > 1 || priority < 0)
			throw new IllegalArgumentException("Argument value not valid, must be in the range between 0 and 1");
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
			defaultTypes.add(LEVEL_HIGHLIGHTING);
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
		if (NORMAL.equals(selectionType) || MOUSE_OVER.equals(selectionType) || SELECTION.equals(selectionType)
				|| DESELECTED.equals(selectionType))
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

	/**
	 * @param isManaged
	 *            setter, see {@link #isManaged}
	 */
	public void setManaged(boolean isManaged) {
		this.isManaged = isManaged;
	}

	/**
	 * @return the isManaged, see {@link #isManaged}
	 */
	public boolean isManaged() {
		return isManaged;
	}

	/**
	 * We use the type string to hash the selection types
	 */
	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public int compareTo(SelectionType comparisonTarget) {
		return priority.compareTo(comparisonTarget.priority);
	}

	@Override
	public boolean equals(Object obj) {
		return type.equals(((SelectionType) obj).type);
	}

}
