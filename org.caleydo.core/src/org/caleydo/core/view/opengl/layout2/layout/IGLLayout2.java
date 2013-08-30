/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import java.util.List;

/**
 * improved version of {@link IGLLayout}
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLLayout2 {
	/**
	 * performs layouting
	 * 
	 * @param children
	 *            the children to layout
	 * @param w
	 * @param h
	 * @param parent
	 *            the parent element
	 * @param deltaTimeMs
	 *            the delta time between the last call and the current call
	 * @return whether a relayout is needed
	 */
	boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs);
}
