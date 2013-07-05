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
package org.caleydo.core.view.opengl.layout;

import javax.media.opengl.GL2;

/**
 * Interface for renderers that display content within a specified rectangle.
 *
 * @author Christian Partl
 *
 */
public interface IRenderer {

	/**
	 * Sets the rectangle that shall be used as drawing area for the renderer. The specified values are interpreted as
	 * GL coordinates.
	 *
	 * @param minX
	 * @param minY
	 * @param maxX
	 * @param maxY
	 */
	public void setLimits(float minX, float minY, float maxX, float maxY);

	/**
	 * Method that shall be called before {@link IRenderer#render(GL2)} in every display cycle. All calculations
	 * required prior rendering shall be performed here.
	 *
	 * @return True, if the rendered content needs to be updated, false otherwise. This allows to determine,e.g.,
	 *         whether a wrapping display list can be called or needs to be rebuilt.
	 */
	public boolean prepare();

	/**
	 * Renders the content.
	 *
	 * @param gl
	 */
	public void render(GL2 gl);

	/**
	 * @return True, if the displayable content of this renderer is permitted to be part of a wrapping display list.
	 *         False, if this is not the case, e.g., if the renderer uses display lists internally.
	 */
	public boolean permitsWrappingDisplayLists();

	/**
	 * @return Minimum height in pixels required by the renderer to display its content properly.
	 */
	public int getMinHeightPixels();

	/**
	 * @return Minimum width in pixels required by the renderer to display its content properly.
	 */
	public int getMinWidthPixels();

	/**
	 * Disposes all resources that are held by this renderer, e.g., display lists, picking listeners, etc.
	 * 
	 * @param gl
	 */
	public void destroy(GL2 gl);

}
