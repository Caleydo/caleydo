/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import java.util.List;

/**
 * adapter of a {@link IGLLayout} to an {@link IGLLayout2}
 *
 * @author Samuel Gratzl
 *
 */
public final class GLLayout2Adapter implements IGLLayout2 {
	private final IGLLayout layout;

	public GLLayout2Adapter(IGLLayout layout) {
		this.layout = layout;
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent) {
		layout.doLayout(children, w, h);
		return false;
	}

	/**
	 * @return the layout, see {@link #layout}
	 */
	public IGLLayout getLayout() {
		return layout;
	}
}
