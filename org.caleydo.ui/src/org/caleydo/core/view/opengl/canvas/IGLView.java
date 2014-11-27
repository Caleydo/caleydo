/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import org.caleydo.core.view.IView;

/**
 * abstraction between IView and {@link AGLView}
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLView extends IView {

	/**
	 * returns the {@link IGLCanvas} used by this view
	 *
	 * @return
	 */
	IGLCanvas getParentGLCanvas();

	/**
	 * enables / disables this view
	 *
	 * @param visible
	 */
	void setVisible(boolean visible);

}
