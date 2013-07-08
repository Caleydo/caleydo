/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.builder.ElementLayoutBuilder;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;

/**
 * Utility for creating the most common forms of layouts/renderers.
 *
 * @author Samuel Gratzl
 * 
 */
public final class ElementLayouts {
	private ElementLayouts() {

	}

	public static ElementLayoutBuilder create() {
		return new ElementLayoutBuilder();
	}

	public static ElementLayout createXSpacer(int width) {
		return create().width(width).height(1).build();
	}


	public static ElementLayout createButton(AGLView view, Button button) {
		return createButton(view, button, 16, 16, 0.02f);
	}

	public static ElementLayout createButton(AGLView view, Button button, int width, int height, float z) {
		return create().width(width).height(height)
				.render(new ButtonRenderer.Builder(view, button).zCoordinate(z).build()).build();
	}

	public static ElementLayout wrap(ALayoutRenderer renderer, int width) {
		return create().width(width).render(renderer).build();
	}

	public static ElementLayout scrollAlbe(AGLView view, ElementLayout content) {
		ScrolledElementLayout l = new ScrolledElementLayout(view, content);
		return l;
	}
}
