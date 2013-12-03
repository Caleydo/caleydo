/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway.v2.internal;

import org.caleydo.core.view.ARcpGLElementViewPart;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.view.pathway.v2.internal.serial.SerializedPathwayView;

/**
 *
 * @author Christian
 *
 */
public class RcpGLPathwayView extends ARcpGLElementViewPart {

	public RcpGLPathwayView() {
		super(SerializedPathwayView.class);
	}

	@Override
	protected AGLElementView createView(IGLCanvas canvas) {
		return new GLPathwayView(glCanvas);
	}
}
