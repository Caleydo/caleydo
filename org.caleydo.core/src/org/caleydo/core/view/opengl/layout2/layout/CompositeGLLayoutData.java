/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;


/**
 * composite variant of a chained layout data object,
 * 
 * @author Samuel Gratzl
 * 
 */
public class CompositeGLLayoutData implements IHasGLLayoutData {
	private final Object[] elems;

	private CompositeGLLayoutData(Object... elems) {
		this.elems = elems;
	}

	public static CompositeGLLayoutData combine(Object... elems) {
		return new CompositeGLLayoutData(elems);
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		for (Object elem : elems) {
			T v = GLLayouts.resolveLayoutData(clazz, elem, null);
			if (v != null)
				return v;
		}
		return default_;
	}

}
