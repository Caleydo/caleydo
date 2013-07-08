/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.newt;

import javax.media.opengl.GLCapabilitiesImmutable;

import org.caleydo.core.view.opengl.canvas.internal.swt.ASWTBasedCanvasFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.swt.NewtCanvasSWT;

/**
 * @author Samuel Gratzl
 *
 */
public class NEWTGLCanvasFactory extends ASWTBasedCanvasFactory {

	@Override
	public NEWTGLCanvas create(GLCapabilitiesImmutable caps, Composite parent) {
		GLWindow canvas = GLWindow.create(caps);
		NewtCanvasSWT composite = NewtCanvasSWT.create(parent, SWT.NO_BACKGROUND, canvas);
		return new NEWTGLCanvas(canvas, composite);
	}

}
