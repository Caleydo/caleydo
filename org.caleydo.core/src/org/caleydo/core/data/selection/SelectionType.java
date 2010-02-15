package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Arrays;

import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * <p>
 * A SelectionType is an object associated with a particular set of elements. The members of the set typically
 * are determined by user selection.
 * </p>
 * <p>
 * There are two types of Selection types: the default types ({@link #NORMAL}, {@link #MOUSE_OVER},
 * {@link #SELECTION} and {@link #DESELECTED}) as well as user-defined types.
 * </p>
 * 
 * TODO add info about user defined types
 * 
 * @author Alexander Lex
 */
public class SelectionType {

	private String type = "Not set";
	private float[] color = new float[] { 0, 0, 0, 1 };
	private boolean isVisible = true;
	private boolean isConnected = false;

	public static final SelectionType NORMAL =
		new SelectionType("Normal", new float[] { 0, 0, 0, 1 }, true, false);
	public static final SelectionType MOUSE_OVER =
		new SelectionType("MouseOver", GeneralRenderStyle.MOUSE_OVER_COLOR, true, true);
	public static final SelectionType SELECTION =
		new SelectionType("Selected", GeneralRenderStyle.SELECTED_COLOR, true, false);
	public static final SelectionType DESELECTED =
		new SelectionType("Deselected", new float[] { 0, 0, 0, 1 }, false, false);

	private static ArrayList<SelectionType> defaultTypes = new ArrayList<SelectionType>();

	public SelectionType() {
	}

	public SelectionType(String type, float[] color, boolean isVisible, boolean isConnected) {
		this.type = type;
		this.color = color;
		this.isVisible = isVisible;
		this.isConnected = isConnected;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	@Override
	public String toString() {
		return type;
	}

	public static synchronized ArrayList<SelectionType> getDefaultTypes() {
		if (defaultTypes.isEmpty()) {
			defaultTypes.add(NORMAL);
			defaultTypes.add(MOUSE_OVER);
			defaultTypes.add(SELECTION);
			defaultTypes.add(DESELECTED);
		}
		return defaultTypes;
	}

}
