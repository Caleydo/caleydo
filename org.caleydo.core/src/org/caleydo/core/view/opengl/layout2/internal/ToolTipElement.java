/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import org.caleydo.core.util.color.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * the default element to render a string tooltip
 *
 * @author Samuel Gratzl
 *
 */
final class TooltipElement extends GLElement {
	private final String text;

	public TooltipElement(String text) {
		this.text = text;
		this.setLocation(5, 5);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		float textWidth = Math.min(g.text.getTextWidth(this.text, 12), w);
		g.color(0.9f, 0.9f, 0.9f).fillRect(0, 0, textWidth + 3, 15);
		g.textColor(Color.BLACK).drawText(text, 1, 1, textWidth + 1, 12);
	}
}
