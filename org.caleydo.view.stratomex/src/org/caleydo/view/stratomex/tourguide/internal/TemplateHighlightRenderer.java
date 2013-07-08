/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.tourguide.internal;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;

/**
 * @author Samuel Gratzl
 *
 */
public class TemplateHighlightRenderer extends ColorRenderer {
	public TemplateHighlightRenderer() {
		super(new float[] { 0.95f, .95f, .95f, 1.f });
		setBorderColor(Color.DARK_GRAY.getRGBA());
		setDrawBorder(true);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
