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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * The LayoutManager is responsible for rendering all the elements specified in
 * its set {@link #template}. It contains a reference to the view frustum and
 * initializes the calculation of spacing once the view frustum is changed.
 *
 * @author Alexander Lex
 */
public class LayoutManager {

	private ViewFrustum viewFrustum;

	/** The entry point to the recursively defined layout */
	private ElementLayout baseElementLayout;

	private final PixelGLConverter pixelGLConverter;

	/**
	 * List of display list indices that refer to display lists of @link
	 * {@link ALayoutRenderer}s that have been destroyed.
	 */
	private Queue<Integer> displayListsToDelete = new ConcurrentLinkedQueue<Integer>();

	/**
	 * Determines whether the {@link ALayoutRenderer}s called by this
	 * {@link LayoutManager} should make use of display lists (if implemented).
	 * Note that if {@link #useDisplayLists} is set to true, the
	 * {@link #render(GL2)} method must not be part of any external display
	 * list, otherwise the GL behavior is not defined.
	 */
	private boolean useDisplayLists = false;

	public LayoutManager(ViewFrustum viewFrustum, PixelGLConverter pixelGLConverter) {
		if (viewFrustum == null || pixelGLConverter == null)
			throw new IllegalArgumentException(
					"Arguments viewFrustum or pixelGLConverter were null");
		this.viewFrustum = viewFrustum;
		this.pixelGLConverter = pixelGLConverter;
	}

	public PixelGLConverter getPixelGLConverter() {
		return pixelGLConverter;
	}

	public void setViewFrustum(ViewFrustum viewFrustum) {
		this.viewFrustum = viewFrustum;

		updateLayout();
	}

	/**
	 * Set a static layout configuration which contains an ElementLayout which
	 * is accessible using {@link LayoutConfiguration#getBaseElementLayout()},
	 * which is set as the {@link #baseElementLayout} of this
	 * {@link LayoutManager}.
	 */
	@Deprecated
	public void setStaticLayoutConfiguration(LayoutConfiguration layoutConfiguration) {
		layoutConfiguration.setStaticLayouts();
		setBaseElementLayout(layoutConfiguration.getBaseElementLayout());
	}

	/**
	 * Recursively update the whole layout of this renderer. The dimensions are
	 * extracted from the viewFrustum provided in the constructor. Since the
	 * viewFrustum is passed by reference, changes to the viewFrustum, e.g. by a
	 * reshape of the window are reflected. FIXME: this should be split into two
	 * different methods, one for updating the layout due to size changes, and
	 * one for updating the layout through to new elements
	 */
	public void updateLayout() {

		float totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		float totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		// template.getBaseLayoutElement().destroy();

		// if (layoutConfiguration != null) {
		// layoutConfiguration.setStaticLayouts();
		// setBaseElementLayout(layoutConfiguration.getBaseElementLayout());
		// }
		// should we do this here? we could integrate this with another
		// traversal
		baseElementLayout.setLayoutManager(this);
		calculateScales(viewFrustum.getLeft(), viewFrustum.getBottom(), totalWidth,
				totalHeight);

		baseElementLayout.updateSpacings();
	}

	/**
	 * Sets the display lists of all associated {@link ALayoutRenderer}s dirty.
	 */
	public void setRenderingDirty() {
		baseElementLayout.setRenderingDirty();
	}

	/**
	 * Recursively render the layout of all elements
	 *
	 * @param gl
	 */
	public void render(GL2 gl) {
		baseElementLayout.render(gl);

		deleteDisplayListsOfDestroyedRenderers(gl);
	}

	/**
	 * Deletes the display lists of {@link #displayListsToDelete}.
	 *
	 * @param gl
	 */
	protected void deleteDisplayListsOfDestroyedRenderers(GL2 gl) {

		// Free display lists
		Integer i = null;
		while ((i = displayListsToDelete.poll()) != null) {
			gl.glDeleteLists(i, 1);
		}
	}

	/**
	 * Deletes the display lists of all {@link ALayoutRenderer}s of this
	 * LayoutManager. This method must be called when the
	 * <code>LayoutManager</code> is no longer used.
	 *
	 * @param gl
	 */
	public void destroy(GL2 gl) {
		deleteDisplayListsOfDestroyedRenderers(gl);

		if (baseElementLayout != null)
			baseElementLayout.destroy(gl);
	}

	/**
	 * Calculate the size and positions of the layout elements in the template
	 *
	 * @param totalWidth
	 * @param totalHeight
	 */
	void calculateScales(float bottom, float left, float totalWidth, float totalHeight) {
		baseElementLayout.setTranslateX(left);
		baseElementLayout.setTranslateY(bottom);

		int dynamicSizeUnitsX = baseElementLayout.getDynamicSizeUnitsX();
		int dynamicSizeUnitsY = baseElementLayout.getDynamicSizeUnitsY();

		baseElementLayout.calculateScales(totalWidth, totalHeight, dynamicSizeUnitsX,
				dynamicSizeUnitsY);
		if (baseElementLayout instanceof ALayoutContainer)
			((ALayoutContainer) baseElementLayout).calculateTransforms(bottom, left,
					totalHeight, totalWidth);
	}

	/**
	 * Set the base element layout - which is the topmost layout containing all
	 * other element layouts
	 *
	 * @param baseElementLayout
	 */
	public void setBaseElementLayout(ElementLayout baseElementLayout) {
		this.baseElementLayout = baseElementLayout;
	}

	/**
	 * @param useDisplayLists
	 *            setter, see {@link #useDisplayLists}
	 */
	public void setUseDisplayLists(boolean useDisplayLists) {
		this.useDisplayLists = useDisplayLists;
	}

	/**
	 * @return the useDisplayLists, see {@link #useDisplayLists}
	 */
	public boolean isUseDisplayLists() {
		return useDisplayLists;
	}

	/**
	 * Adds the index of a display list that shall be deleted in the next render
	 * cycle. This method is intended to be used by {@link ALayoutRenderer}s that
	 * will be destroyed only.
	 *
	 * @param displayListIndex
	 */
	protected void addDisplayListToDelete(int displayListIndex) {
		displayListsToDelete.add(displayListIndex);
	}
}
