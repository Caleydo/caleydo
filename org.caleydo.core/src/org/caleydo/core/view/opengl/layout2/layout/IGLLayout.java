/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.layout;

import java.util.List;

/**
 * abstraction of a layout algorithm
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLLayout {
	/**
	 * performs the actual layout of the given children within the given width and height
	 *
	 * @param children
	 *            the active children of the element container
	 * @param w
	 *            the width of the container
	 * @param h
	 *            the height of the container
	 */
	void doLayout(List<? extends IGLLayoutElement> children, float w, float h);
}
