/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * A sub-class for {@link ALayoutRenderer} intended to render whole {@link AGLView}s. The main contract here, is that
 * the view renders within its view frustum, which is updated according to the size of the layout in the
 * {@link #setLimits(float, float)} method.
 *
 * @author Alexander Lex
 */
public class ViewLayoutRenderer extends ALayoutRenderer {

	protected AGLView view;

	// protected boolean useAbsoluteScreenCoordinateViewFrustum = false;

	public ViewLayoutRenderer() {

	}

	/**
	 * Constructor taking an {@link AGLView} to be rendered by this renderer.
	 *
	 * @param view
	 */
	public ViewLayoutRenderer(AGLView view) {
		this.view = view;
	}

	@Override
	public void setLimits(float x, float y) {
		if (layoutManager == null)
			return;
		float left = layoutManager.getViewFrustum().getLeft() + elementLayout.getTranslateX();
		float bottom = layoutManager.getViewFrustum().getBottom() + elementLayout.getTranslateY();

		if (Float.compare(this.x, x) == 0 && Float.compare(this.y, y) == 0
				&& Float.compare(view.getViewFrustum().getWidth(), x) == 0
				&& Float.compare(view.getViewFrustum().getHeight(), y) == 0
				&& Float.compare(view.getViewFrustum().getLeft(), left) == 0
				&& Float.compare(view.getViewFrustum().getBottom(), bottom) == 0)
			return;
		super.setLimits(x, y);

		ViewFrustum viewFrustum = view.getViewFrustum();

		if (viewFrustum == null) {
			viewFrustum = new ViewFrustum();
			viewFrustum.setProjectionMode(CameraProjectionMode.ORTHOGRAPHIC);
		}

		// if (useAbsoluteScreenCoordinateViewFrustum) {
		viewFrustum.setLeft(left);
		viewFrustum.setBottom(bottom);
		viewFrustum.setRight(left + x);
		viewFrustum.setTop(bottom + y);
		// } else {
		// viewFrustum.setLeft(0);
		// viewFrustum.setBottom(0);
		// viewFrustum.setRight(x);
		// viewFrustum.setTop(y);
		// }
		view.setFrustum(viewFrustum);
		view.setDisplayListDirty();
	}

	/**
	 * @param view
	 *            setter, see {@link #view}
	 */
	public void setView(AGLView view) {
		this.view = view;
	}

	/**
	 * @return the view, see {@link #view}
	 */
	public AGLView getView() {
		return view;
	}

	@Override
	public int getMinHeightPixels() {
		return view.getMinPixelHeight();
	}

	@Override
	public int getMinWidthPixels() {
		return view.getMinPixelWidth();
	}

	@Override
	protected void renderContent(GL2 gl) {
		view.displayRemote(gl);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	// /**
	// * @param useAbsoluteScreenCoordinateViewFrustum
	// * setter, see {@link useAbsoluteScreenCoordinateViewFrustum}
	// */
	// public void setUseAbsoluteScreenCoordinateViewFrustum(boolean useAbsoluteScreenCoordinateViewFrustum) {
	// this.useAbsoluteScreenCoordinateViewFrustum = useAbsoluteScreenCoordinateViewFrustum;
	// }

}
