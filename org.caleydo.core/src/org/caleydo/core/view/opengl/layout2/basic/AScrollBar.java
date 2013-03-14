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
package org.caleydo.core.view.opengl.layout2.basic;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

public abstract class AScrollBar implements IScrollBar {
	protected final boolean isHorizontal;
	protected boolean hovered = false;
	protected float offset;
	protected float view;
	protected float total;
	protected IScrollBarCallback callback;

	public AScrollBar(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	@Override
	public void setCallback(IScrollBarCallback callback) {
		this.callback = callback;
	}

	@Override
	public float setBounds(float offset, float view, float total) {
		this.offset = Math.min(total - view, Math.max(0, offset));
		this.view = view;
		this.total = total;
		return this.offset;
	}

	protected float getOffset(float total) {
		return offset * total / this.total;
	}

	protected float getSize(float total) {
		return view * total / this.total;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.color(Color.LIGHT_GRAY).fillRect(0, 0, w, h);
		if (isHorizontal) {
			g.color(hovered ? Color.DARK_GRAY : Color.GRAY).fillRect(getOffset(w), 0, getSize(w), h);
		} else {
			g.color(hovered ? Color.DARK_GRAY : Color.GRAY).fillRect(0, getOffset(h), w, getSize(h));
		}
	}

	@Override
	public void renderPick(GLGraphics g, float w, float h, GLElement parent) {
		g.fillRect(0, 0, w, h);
	}
}