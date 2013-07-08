/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout.util;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * @author Samuel Gratzl
 *
 */
public final class Renderers {
	private Renderers() {

	}

	public static LabelRenderer createLabel(ILabelProvider label, AGLView view) {
		return new LabelRenderer(view, view.getTextRenderer(), label);
	}
}
