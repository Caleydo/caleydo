/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.swt;


import javax.media.opengl.GLCapabilitiesImmutable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.opengl.swt.GLCanvas;

/**
 * @author Samuel Gratzl
 *
 */
public class SWTGLCanvasFactory extends ASWTBasedCanvasFactory {

	@Override
	public SWTGLCanvas create(GLCapabilitiesImmutable caps, Composite parent) {
		// parent = new Composite(parent, SWT.NO_BACKGROUND);
		// parent.setLayout(new FillLayout());
		GLCanvas canvas = new GLCanvas(parent, SWT.NO_BACKGROUND, caps, null, null);
		return new SWTGLCanvas(canvas);
	}
}
