/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
		public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		}
	};

	/**
	 * special layout, where every child will get the whole space, i.e. they are on top of each other
	 */
	public static final IGLLayout LAYERS = new IGLLayout() {
		@Override
		public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
			for (IGLLayoutElement child : children) {
				float x = defaultValue(child.getSetX(), 0);
				float y = defaultValue(child.getSetY(), 0);
				child.setBounds(x, y, w - x, h - y);
			}
		}
	};

	// common item cache
	private static final IGLLayout FLOW_HORIZONTAL_0 = new GLFlowLayout(true, 0, GLPadding.ZERO);
	private static final IGLLayout FLOW_HORIZONTAL_1 = new GLFlowLayout(true, 1, GLPadding.ZERO);
	private static final IGLLayout FLOW_HORIZONTAL_2 = new GLFlowLayout(true, 2, GLPadding.ZERO);
	private static final IGLLayout FLOW_VERTICAL_0 = new GLFlowLayout(false, 0, GLPadding.ZERO);
	private static final IGLLayout FLOW_VERTICAL_1 = new GLFlowLayout(false, 1, GLPadding.ZERO);
	private static final IGLLayout FLOW_VERTICAL_2 = new GLFlowLayout(false, 2, GLPadding.ZERO);

	/**
	 * horizontal flow layout, similar to the {@link RowLayout}
	 *
	 * @see GLFlowLayout
	 * @param gap
	 *            the gap in pixels between the elements
	 * @return
	 */
	public static IGLLayout flowHorizontal(float gap) {
		if (gap == 0)
			return FLOW_HORIZONTAL_0;
		if (gap == 1)
			return FLOW_HORIZONTAL_1;
		if (gap == 2)
			return FLOW_HORIZONTAL_2;
		return new GLFlowLayout(true, gap, GLPadding.ZERO);
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
		if (gap == 0)
			return FLOW_VERTICAL_0;
		if (gap == 1)
			return FLOW_VERTICAL_1;
		if (gap == 2)
			return FLOW_VERTICAL_2;
		return new GLFlowLayout(false, gap, GLPadding.ZERO);
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
		if (isDefault(v))
			return d;
		return v;
	}

	/**
	 * checks whether the given value is a default layout value
	 *
	 * @param v
	 * @return
	 */
	public static boolean isDefault(float v) {
		return v < 0 || Float.isNaN(v);
	}

	/**
	 * utility to work with decorators for Layout data
	 *
	 * @param clazz
	 *            the desired type
	 * @param value
	 *            the current value
	 * @param default_
	 *            the default value
	 * @return
	 */
	public static <T> T resolveLayoutData(Class<T> clazz, Object value, T default_) {
		if (clazz.isInstance(value))
			return clazz.cast(value);
		if (value instanceof IHasGLLayoutData) {
			return ((IHasGLLayoutData) value).getLayoutDataAs(clazz, default_);
		}
		return default_;
	}

	/**
	 * utility to work with decorators for Layout data
	 * 
	 * @param clazz
	 *            the desired type
	 * @param value
	 *            the current value
	 * @param default_
	 *            the default value
	 * @return
	 */
	public static <T> T resolveLayoutDatas(Class<T> clazz, T default_, Object... values) {
		for (Object value : values) {
			if (clazz.isInstance(value))
				return clazz.cast(value);
			if (value instanceof IHasGLLayoutData) {
				T r = ((IHasGLLayoutData) value).getLayoutDataAs(clazz, null);
				if (r != null)
					return r;
			}
		}
		return default_;
	}
}
