/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * The LayoutManager is responsible for rendering all the elements specified in its set {@link #template}. It contains a
 * reference to the view frustum and initializes the calculation of spacing once the view frustum is changed.
 *
 * <h2>Animation</h2>
 * <p>
 * Layout support animation for adding, removing content as well as for transitioning between multiple states.
 * </p>
 * <p>
 * To <b>animate adding content</b> simply call {@link #recordPostTranslationState()} after you called
 * {@link #updateLayout()}.
 * </p>
 * <p>
 * If you want to do an animation from one state of the layout to another one you first have to record the previous
 * state using {@link #recordPreTransitionState()} and then, after you changed the layout and called
 * {@link #updateLayout()} call {@link #recordPostTranslationState()} again.
 * </p>
 *
 * @author Alexander Lex
 */
public class LayoutManager {

	private ViewFrustum viewFrustum;

	/** The entry point to the recursively defined layout */
	private ElementLayout baseElementLayout;

	private final PixelGLConverter pixelGLConverter;

	/**
	 * List of display list indices that refer to display lists of @link {@link ALayoutRenderer}s that have been
	 * destroyed.
	 */
	private Queue<Integer> displayListsToDelete = new ConcurrentLinkedQueue<Integer>();

	/**
	 * Determines whether the {@link ALayoutRenderer}s called by this {@link LayoutManager} should make use of display
	 * lists (if implemented). Note that if {@link #useDisplayLists} is set to true, the {@link #render(GL2)} method
	 * must not be part of any external display list, otherwise the GL behavior is not defined.
	 */
	private boolean useDisplayLists = false;

	/** Map keeping track of all layouts that were removed in the previous update cycle */
	private HashMap<Integer, TransitionSpecification> mapLayoutIDToTransitionSpecification = new HashMap<>();

	/** Flag determining whether a removal transition is currently active */
	private boolean isTransitionActive = false;

	public LayoutManager(ViewFrustum viewFrustum, PixelGLConverter pixelGLConverter) {
		if (viewFrustum == null || pixelGLConverter == null)
			throw new IllegalArgumentException("Arguments viewFrustum or pixelGLConverter were null");
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
	 * Set a static layout configuration which contains an ElementLayout which is accessible using
	 * {@link LayoutConfiguration#getBaseElementLayout()}, which is set as the {@link #baseElementLayout} of this
	 * {@link LayoutManager}.
	 */
	@Deprecated
	public void setStaticLayoutConfiguration(LayoutConfiguration layoutConfiguration) {
		layoutConfiguration.setStaticLayouts();
		setBaseElementLayout(layoutConfiguration.getBaseElementLayout());
	}

	/**
	 * Recursively update the whole layout of this renderer. The dimensions are extracted from the viewFrustum provided
	 * in the constructor. Since the viewFrustum is passed by reference, changes to the viewFrustum, e.g. by a reshape
	 * of the window are reflected. FIXME: this should be split into two different methods, one for updating the layout
	 * due to size changes, and one for updating the layout through to new elements
	 */
	public void updateLayout() {
		if (baseElementLayout == null)
			return;
		float totalWidth = viewFrustum.getRight() - viewFrustum.getLeft();
		float totalHeight = viewFrustum.getTop() - viewFrustum.getBottom();

		// should we do this here? we could integrate this with another
		// traversal
		baseElementLayout.setLayoutManager(this);
		calculateScales(0, 0, totalWidth, totalHeight);

		baseElementLayout.updateSpacings();
		// recordPostTranslationState();
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
		if (isTransitionActive) {
			if (mapLayoutIDToTransitionSpecification.isEmpty())
				isTransitionActive = false;
			for (TransitionSpecification tSpec : mapLayoutIDToTransitionSpecification.values()) {
				tSpec.getLayout().isTransitionActive = true;

				tSpec.getLayout().render(gl);
				isTransitionActive = tSpec.getLayout().isTransitionActive;
			}
			if (!isTransitionActive) {
				mapLayoutIDToTransitionSpecification.clear();
			}
		}
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
	 * Deletes the display lists of all {@link ALayoutRenderer}s of this LayoutManager. This method must be called when
	 * the <code>LayoutManager</code> is no longer used.
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

		baseElementLayout.calculateScales(totalWidth, totalHeight, dynamicSizeUnitsX, dynamicSizeUnitsY);
		if (baseElementLayout instanceof ALayoutContainer)
			((ALayoutContainer) baseElementLayout).calculateTransforms(bottom, left, totalHeight, totalWidth);
	}

	/**
	 * Set the base element layout - which is the topmost layout containing all other element layouts
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
	 * Adds the index of a display list that shall be deleted in the next render cycle. This method is intended to be
	 * used by {@link ALayoutRenderer}s that will be destroyed only.
	 *
	 * @param displayListIndex
	 */
	protected void addDisplayListToDelete(int displayListIndex) {
		displayListsToDelete.add(displayListIndex);
	}

	/** Records the state of the layout before it's changed for animations between the states */
	public void recordPreTransitionState() {
		baseElementLayout.recordPreTransitionState();

	}

	/** Records the state after the layout change to execute the animation */
	public void recordPostTranslationState() {
		baseElementLayout.recordPostTranslationState();

		for (TransitionSpecification tSpec : mapLayoutIDToTransitionSpecification.values()) {
			tSpec.setPostTransitionDelete(tSpec.getLayout().removalDirection);
		}
		isTransitionActive = true;
	}

	/**
	 * Registers the transitons with the {@link #mapLayoutIDToTransitionSpecification}. This is done for all elements on
	 * {@link #recordPreTransitionState()}
	 */
	void registerTransitionSpecification(TransitionSpecification tSpec) {
		mapLayoutIDToTransitionSpecification.put(tSpec.getID(), tSpec);
	}

	/**
	 * Here layouts that are retained after the change remove themselves from the transition manager so that only those
	 * that were deleted remain. This is done throug {@link #recordPostTranslationState()}
	 */
	void unRegisterTransitionSpecification(TransitionSpecification tSpec) {
		mapLayoutIDToTransitionSpecification.remove(tSpec.getID());
	}

	/**
	 * @return the viewFrustum, see {@link #viewFrustum}
	 */
	public ViewFrustum getViewFrustum() {
		return viewFrustum;
	}

}
