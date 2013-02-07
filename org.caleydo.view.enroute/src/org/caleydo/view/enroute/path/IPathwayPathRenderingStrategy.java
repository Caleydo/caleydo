/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.enroute.path;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Interface for different strategies that render a pathway path.
 *
 * @author Christian Partl
 *
 */
public interface IPathwayPathRenderingStrategy {

	/**
	 * Renders the path for a {@link APathwayPathRenderer}.
	 *
	 * @param gl
	 * @param glu
	 */
	public void render(GL2 gl, GLU glu);

	/**
	 * @return The minimum height that is required by the strategy to render the path.
	 */
	public int getMinHeightPixels();

	/**
	 * @return The minimum width that is required by the strategy to render the path.
	 */
	public int getMinWidthPixels();

}
