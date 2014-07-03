/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.renderer;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * abstraction of a rendering element,
 *
 * the idea is that renderers are constant and don't change over time
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLRenderer {
	/**
	 * performs the actual rendering using the given data
	 */
	void render(GLGraphics g, float w, float h, GLElement parent);
}

