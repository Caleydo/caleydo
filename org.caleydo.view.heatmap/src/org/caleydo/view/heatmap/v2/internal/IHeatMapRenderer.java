/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;

/**
 * @author Samuel Gratzl
 *
 */
public interface IHeatMapRenderer {
	/**
	 *
	 */
	void takeDown();

	void update(IGLElementContext context, List<Integer> dimensions, List<Integer> records, GLElement parent);

	void render(GLGraphics g, float w, float h, ISpacingLayout recordSpacing, ISpacingLayout dimensionSpacing);

}
