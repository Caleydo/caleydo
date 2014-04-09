/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import java.util.Collection;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.layout2.IGLGraphicsTracer;

/**
 * @author Samuel Gratzl
 *
 */
public class GLGraphicsTracers {
	public static final IGLGraphicsTracer DUMMY = new DummyGLGraphicsTracer();

	private static final Collection<IGLGraphicsTracer.IFactory> factories = ExtensionUtils.findImplementation(
			"org.caleydo.ui.tracer", "class", IGLGraphicsTracer.IFactory.class);

	public static IGLGraphicsTracer create(IView view) {
		for (IGLGraphicsTracer.IFactory f : factories) {
			IGLGraphicsTracer v = f.create(view);
			if (v != null)
				return v;
		}
		return DUMMY;
	}
}
