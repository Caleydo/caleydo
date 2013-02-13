package org.caleydo.core.view.opengl.layout2.layout;

import java.util.List;

import org.caleydo.core.view.opengl.layout.ColumnLayout;
import org.caleydo.core.view.opengl.layout.RowLayout;

/**
 * factory class for {@link IGLLayout}s
 *
 * @author Samuel Gratzl
 *
 */
public class GLLayouts {
	/**
	 * this layout does exactly nothing
	 */
	public static final IGLLayout NONE = new IGLLayout() {
		@Override
		public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
			return false;
		}
	};

	/**
	 * special layout, where every child will get the whole space, i.e. they are on top of each other
	 */
	public static final IGLLayout LAYERS = new IGLLayout() {
		@Override
		public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
			for (IGLLayoutElement child : children) {
				float x = defaultValue(child.getSetX(), 0);
				float y = defaultValue(child.getSetY(), 0);
				child.setBounds(x, y, w - x, h - y);
			}
			return false;
		}
	};

	/**
	 * horizontal flow layout, similar to the {@link RowLayout}
	 *
	 * @see GLFlowLayout
	 * @param gap
	 *            the gap in pixels between the elements
	 * @return
	 */
	public static IGLLayout flowHorizontal(float gap) {
		return new GLFlowLayout(true, gap);
	}

	/**
	 * vertical flow layout, similar to the {@link ColumnLayout}
	 *
	 * @see GLFlowLayout
	 * @param gap
	 *            the gap in pixels between the elements
	 * @return
	 */
	public static IGLLayout flowVertical(float gap) {
		return new GLFlowLayout(false, gap);
	}

	/**
	 * returns the default value if the value to check is lower than 0 or NaN
	 *
	 * @param v
	 *            the value to check
	 * @param d
	 *            the default value
	 * @return
	 */
	public static float defaultValue(float v, float d) {
		if (v < 0 || Float.isNaN(v))
			return d;
		return v;
	}
}