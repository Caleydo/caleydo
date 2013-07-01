/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.Frame;

import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.view.opengl.canvas.internal.IGLCanvasFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Samuel Gratzl
 *
 */
public class AWTGLCanvasFactory implements IGLCanvasFactory {

	@Override
	public AWTGLCanvas create(GLCapabilitiesImmutable caps, Composite parent) {
		GLCanvas canvas = new GLCanvas(caps);
		Composite composite = new Composite(parent, SWT.EMBEDDED);
		Frame frameGL = SWT_AWT.new_Frame(composite);
		frameGL.add(canvas);
		return new AWTGLCanvas(canvas, composite);
	}
}
