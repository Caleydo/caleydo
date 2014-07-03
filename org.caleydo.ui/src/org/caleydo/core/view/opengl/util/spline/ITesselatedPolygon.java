/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.spline;

import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * a generic definition of a polygon that should be tesselated
 * 
 * @author Samuel Gratzl
 * 
 */
public interface ITesselatedPolygon {
	/**
	 * draws the outline of this polygon
	 *
	 * @param g
	 */
	void draw(GLGraphics g);

	/**
	 * fills the polygon using the given {@link TesselationRenderer}
	 *
	 * @param g
	 * @param renderer
	 */
	void fill(GLGraphics g, TesselationRenderer renderer);

	/**
	 * @return the number of vertices
	 */
	int size();
}
