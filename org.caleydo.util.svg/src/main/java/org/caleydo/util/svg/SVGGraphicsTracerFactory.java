/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.util.svg;

import java.io.File;

import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.layout2.IGLGraphicsTracer;
import org.caleydo.core.view.opengl.layout2.IGLGraphicsTracer.IFactory;

/**
 * @author Samuel Gratzl
 *
 */
public class SVGGraphicsTracerFactory implements IFactory {
	static int createFor = -1;
	static File saveAs;

	@Override
	public IGLGraphicsTracer create(IView view) {
		if (saveAs == null || createFor != view.getID())
			return null;
		createFor = -1;
		File f = saveAs;
		saveAs = null;
		return new SVGGraphicsTracer(f);
	}

	public static void enable(IView view, File f) {
		createFor = view.getID();
		saveAs = f;
	}
}
