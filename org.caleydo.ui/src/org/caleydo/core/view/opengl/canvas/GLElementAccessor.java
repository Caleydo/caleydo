/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * utility to access package protection
 * 
 * @author Samuel Gratzl
 * 
 */
public class GLElementAccessor {
	public static PixelGLConverter createPixelGLConverter(ViewFrustum viewFrustum, IGLCanvas canvas) {
		return new PixelGLConverter(viewFrustum, canvas);
	}
}
