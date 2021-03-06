/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * @author Samuel Gratzl
 *
 */
public class GLLayoutDatas {
	private GLLayoutDatas() {

	}

	/**
	 * combines multiple elements to a single layout data composite object
	 *
	 * @param elems
	 * @return
	 */
	public static CompositeGLLayoutData combine(Object... elems) {
		return new CompositeGLLayoutData(elems);
	}

	/**
	 * converter function to a layout data object
	 *
	 * @param clazz
	 * @param default_
	 * @return
	 */
	public static <T> Function<IHasGLLayoutData, T> toLayoutData(final Class<T> clazz, final Supplier<T> default_) {
		return new Function<IHasGLLayoutData, T>() {
			@Override
			public T apply(IHasGLLayoutData arg0) {
				if (arg0 == null)
					return null;
				return arg0.getLayoutDataAs(clazz, default_);
			}
		};
	}

	public static class CompositeGLLayoutData implements IHasGLLayoutData {
		private final Object[] elems;

		private CompositeGLLayoutData(Object... elems) {
			this.elems = elems;
		}

		@Override
		public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
			return getLayoutDataAs(clazz, Suppliers.ofInstance(default_));
		}

		@Override
		public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
			return GLLayouts.resolveLayoutDatas(clazz, default_, elems);

		}
	}

	/**
	 * @return
	 */
	public static <T> Supplier<T> throwInvalidException() {
		return new Supplier<T>() {
			@Override
			public T get() {
				throw new IllegalStateException("never call this supplier");
			}
		};
	}
}
