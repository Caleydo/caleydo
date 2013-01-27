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
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;

/**
 * <p>
 * Layouts (i.e {@link ElementLayout}s, {@link Column}s or {@link Row}s can have renderers associated with them. These
 * Renderers display the content in a form that has to be specified in a sub-class of this one.
 * </p>
 * <p>
 * Each Layout may have up to three renderers. One for the background, the main renderer and one for the foreground,
 * which are also rendered in this sequence.
 * </p>
 * <p>
 * There are two main ways to use renderers: either by letting a sub-part of an {@link AGLView} be rendered by them.
 * Then typically, an instance of this view is passed to a sub-class to collaborate closely (possibly using package
 * private access) with the renderer.
 * </p>
 * <p>
 * Alternatively, whole views can be rendered in a renderer. For this, use the specialized {@link ViewLayoutRenderer}
 * class.
 * </p>
 * <p>
 * Every ALayoutRenderer renders from (0, 0) to (x, y). An ALayoutRenderer does not take care of any spacings on the
 * sides.
 * </p>
 *
 * <p>
 * If a specific ALayoutRenderer wants its content to be rendered in a display list, {@link #permitsWrappingDisplayLists()} must
 * return true. However, the use of display lists also depends on whether the {@link LayoutManager} of the associated
 * <code>ElementLayout</code> permits display lists.
 * </p>
 *
 * @author Alexander Lex
 * @author Christian Partl
 */
public abstract class ALayoutRenderer {
	protected float x; // width would be a better name
	protected float y;

	protected ElementLayout elementLayout;
	protected LayoutManager layoutManager;
	/**
	 * Determines whether a display list index has been generated for this renderer.
	 */
	private boolean hasDisplayListIndex = false;
	/**
	 * The index of the display list for this renderer.
	 */
	private int displayListIndex;
	/**
	 * Determines whether the display list for this renderer should be rebuilt.
	 */
	protected boolean isDisplayListDirty = true;

	// protected PixelGLConverter pixelGLConverter;

	public ALayoutRenderer() {

	}

	/**
	 * Rendering method.
	 *
	 * @param gl
	 */
	public final void render(GL2 gl) {
		boolean displayListsAllowedByLayoutManager = false;
		if (layoutManager != null) {
			displayListsAllowedByLayoutManager = layoutManager.isUseDisplayLists();
		}

		if (displayListsAllowedByLayoutManager && !hasDisplayListIndex && permitsWrappingDisplayLists()) {
			displayListIndex = gl.glGenLists(1);
			hasDisplayListIndex = true;
		}

		prepare();

		if (isDisplayListDirty && permitsWrappingDisplayLists() && displayListsAllowedByLayoutManager) {
			gl.glNewList(displayListIndex, GL2.GL_COMPILE);
			renderContent(gl);
			gl.glEndList();
			isDisplayListDirty = false;
		}

		if (permitsWrappingDisplayLists() && displayListsAllowedByLayoutManager) {
			gl.glCallList(displayListIndex);
		} else {
			renderContent(gl);
		}

	}

	/**
	 * Destroys the renderer releasing all associated resources including e.g. display lists.
	 *
	 * @param gl
	 */
	public void destroy(GL2 gl) {
		if (hasDisplayListIndex) {
			gl.glDeleteLists(displayListIndex, 1);
			hasDisplayListIndex = false;
			displayListIndex = -1;
		}
	}

	/**
	 * Renders the content. This content is rendered in a display list, if this renderer uses display lists (
	 * {@link #permitsWrappingDisplayLists()}) and and the {@link LayoutManager} grants display list rendering (
	 * {@link LayoutManager#isUseDisplayLists()}).
	 *
	 * @param gl
	 */
	protected abstract void renderContent(GL2 gl);

	/**
	 * @return True, if the renderer makes use of display lists, false otherwise.
	 */
	protected abstract boolean permitsWrappingDisplayLists();

	/**
	 * Method that is called in every render cycle before {@link #renderContent(GL2)} is invoked. This method is
	 * intended to be overridden for renderers that need to do some processing in every render cycle, which can not be
	 * done in {@link #renderContent(GL2)} if display lists are used.
	 */
	protected void prepare() {

	}

	/**
	 * Set the limits of this renderer. The view must render within only these limits.
	 *
	 * @param x
	 * @param y
	 */
	public void setLimits(float x, float y) {
		this.x = x;
		this.y = y;
		setDisplayListDirty();
	}

	/** Calculate spacing if required */
	protected void updateSpacing() {

	}

	protected void setElementLayout(ElementLayout elementLayout) {
		this.elementLayout = elementLayout;
		layoutManager = elementLayout.getLayoutManager();
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

	/**
	 * Sets the display list of this renderer dirty.
	 */
	public void setDisplayListDirty() {
		this.isDisplayListDirty = true;
	}

	/**
	 * @return the isDisplayListDirty, see {@link #isDisplayListDirty}
	 */
	public boolean isDisplayListDirty() {
		return isDisplayListDirty;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (layoutManager != null && hasDisplayListIndex)
				layoutManager.addDisplayListToDelete(displayListIndex);
		} finally {
			super.finalize();
		}
	}

}
