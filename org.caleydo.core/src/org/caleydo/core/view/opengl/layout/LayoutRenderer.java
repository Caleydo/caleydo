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

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;

/**
 * <p>
 * Layouts (i.e {@link ElementLayout}s, {@link Column}s or {@link Row}s can have
 * renderers associated with them. These Renderers display the content in a form
 * that has to be specified in a sub-class of this one.
 * </p>
 * <p>
 * Each Layout may have up to three renderers. One for the background, the main
 * renderer and one for the foreground, which are also rendered in this
 * sequence.
 * </p>
 * <p>
 * There are two main ways to use renderers: either by letting a sub-part of an
 * {@link AGLView} be rendered by them. Then typically, an instance of this view
 * is passed to a sub-class to collaborate closely (possibly using package
 * private access) with the renderer.
 * </p>
 * <p>
 * Alternatively, whole views can be rendered in a renderer. For this, use the
 * specialized {@link ViewLayoutRenderer} class.
 * </p>
 * <p>
 * Every LayoutRenderer renders from (0, 0) to (x, y). An LayoutRenderer does
 * not take care of any spacings on the sides.
 * </p>
 * 
 * @author Alexander Lex
 */
public class LayoutRenderer {
	protected float x;
	protected float y;
	protected boolean debugMode = true;

	protected ElementLayout elementLayout;
	protected LayoutManager layoutManger;

	// protected PixelGLConverter pixelGLConverter;

	/**
	 * To be overridden in a sub-class.
	 * 
	 * @param gl
	 */
	public void render(GL2 gl) {
	}

	/**
	 * Set the limits of this renderer. The view must render within only these
	 * limits.
	 * 
	 * @param x
	 * @param y
	 */
	public void setLimits(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** Calculate spacing if required */
	protected void updateSpacing() {

	}

	public void setElementLayout(ElementLayout elementLayout) {
		this.elementLayout = elementLayout;
		layoutManger = elementLayout.getLayoutManager();
		// pixelGLConverter = layoutManger.getPixelGLConverter();
	}

	/**
	 * To be overridden by subclass if needed.
	 * 
	 * @return The minimum height in pixels required by the renderer.
	 */
	public int getMinHeightPixels() {
		return 0;
	}

	/**
	 * To be overridden by subclass if needed.
	 * 
	 * @return The minimum width in pixels required by the renderer.
	 */
	public int getMinWidthPixels() {
		return 0;
	}

	protected PixelGLConverter getPixelGLConverter() {
		return layoutManger.getPixelGLConverter();
	}

}
