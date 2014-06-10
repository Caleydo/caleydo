/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.remote;

import java.util.List;

import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;

/**
 * Interface for accessing views that remotely render other views.
 *
 * @author Marc Streit
 */
public interface IGLRemoteRenderingView extends IView {

	public IGLCanvas getParentGLCanvas();

	/**
	 * Retrieves all the contained views from a given view. The correct
	 * implementation of this method is essential for the destruction of remote
	 * rendered views.
	 *
	 * @return list of views contained in the given view.
	 */
	public List<AGLView> getRemoteRenderedViews();

	public ViewFrustum getViewFrustum();

	/**
	 * Get the {@link PixelGLConverter} from the parent view, which needs the
	 * top-level's view frustum.
	 *
	 * @return
	 */
	public PixelGLConverter getPixelGLConverter();
}
