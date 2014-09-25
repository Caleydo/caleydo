/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.renderer;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * composite pattern for {@link IGLRenderer}
 * 
 * @author Samuel Gratzl
 * 
 */
public class GLRendererComposite extends ArrayList<IGLRenderer> implements IGLRenderer {
	private static final long serialVersionUID = -2280716329162030636L;

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		for (IGLRenderer r : this)
			r.render(g, w, h, parent);
	}
}
