/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal;

import javax.media.opengl.GLCapabilitiesImmutable;

import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Samuel Gratzl
 *
 */
public interface IGLCanvasFactory {
	IGLCanvas create(GLCapabilitiesImmutable caps, Composite parent);
}
