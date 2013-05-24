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
	private static final float MIN_WINDOW = 8;
	protected final boolean isHorizontal;
	protected boolean hovered = false;
	protected float offset;
	protected float window;
	protected float size;
	protected IScrollBarCallback callback;

	public AScrollBar(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	@Override
	public void setCallback(IScrollBarCallback callback) {
		this.callback = callback;
	}

	@Override
	public float setBounds(float offset, float window, float size) {
		this.offset = Math.min(size - window, Math.max(0, offset));
		this.window = window;
		this.size = size;
		return this.offset;
	}

	protected final float clamp(float newOffset) {
		return Math.min(size - window, Math.max(0, newOffset));
	}

	@Override
	public float getOffset() {
		return offset;
	}

	@Override
	public float getWindow() {
		return window;
	}

	@Override
	public float getSize() {
		return size;
	}

	private float[] map(float total) {
		float scale = total / this.size;
		float w = window * scale;
		if (w < MIN_WINDOW) { // need to scale
			float missing = MIN_WINDOW - w;
			total -= missing;
			scale = total / this.size;
			w = window * scale + missing;
		}
		return new float[] { offset * scale, w, scale };
	}

	private float unmap(float[] mapped, float v) {
		return v / mapped[2];
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.color(Color.LIGHT_GRAY).fillRect(0, 0, w, h);
		float total = isHorizontal ? w : h;
		g.color(hovered ? Color.DARK_GRAY : Color.GRAY);
		float[] s = map(total);

		if (isHorizontal) {
			g.fillRect(s[0], 0, s[1], h);
		} else {
			g.fillRect(0, s[0], w, s[1]);
		}
	}

	protected final boolean jump(float mousePos) {
		float total = callback.getHeight(this);
		float[] s = map(total);

		// normal drag
		if (mousePos >= s[0] && mousePos <= (s[0] + s[1]))
			return false;

		// jump
		float v = unmap(s, mousePos);
		callback.onScrollBarMoved(this, clamp(v));
		return true;
	}

	protected final void drag(float mouseDelta) {
		float[] s = map(callback.getHeight(this));
		float vd = unmap(s, mouseDelta);
		callback.onScrollBarMoved(this, clamp(offset + vd));
	}

	@Override
	public void renderPick(GLGraphics g, float w, float h, GLElement parent) {
		g.fillRect(0, 0, w, h);
	}
}
