package org.caleydo.core.view.opengl.layout2.layout;

import java.util.List;

import org.caleydo.core.view.opengl.layout.ColumnLayout;
import org.caleydo.core.view.opengl.layout.RowLayout;

/**
 * factory class for {@link ILayout}s
 *
 * @author Samuel Gratzl
 *
 */
public class Layouts {
	/**
	 * this layout does exactly nothing
	 */
	public static final ILayout NONE = new ILayout() {
		@Override
		public boolean doLayout(List<ILayoutElement> children, float w, float h) {
			return false;
		}
	};

	/**
	 * special layout, where every child will get the whole space, i.e. they are on top of each other
	 */
	public static final ILayout LAYERS = new ILayout() {
		@Override
		public boolean doLayout(List<ILayoutElement> children, float w, float h) {
			for (ILayoutElement child : children) {
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
	 * @see FlowLayout
	 * @param gap
	 *            the gap in pixels between the elements
	 * @return
	 */
	public static ILayout flowHorizontal(float gap) {
		return new FlowLayout(true, gap);
	}

	/**
	 * vertical flow layout, similar to the {@link ColumnLayout}
	 *
	 * @see FlowLayout
	 * @param gap
	 *            the gap in pixels between the elements
	 * @return
	 */
	public static ILayout flowVertical(float gap) {
		return new FlowLayout(false, gap);
	}

	private static float defaultValue(float v, float d) {
		if (v < 0 || Float.isNaN(v))
			return d;
		return v;
	}
}