/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
