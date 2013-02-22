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

import gleem.linalg.Vec4f;

import java.awt.Color;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IPopupLayer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * @author Samuel Gratzl
 *
 */
class PopupElement extends GLElementContainer implements IGLLayout, GLButton.ISelectionCallback, IGLRenderer {
	private final GLElement content;

	public PopupElement(GLElement content, Vec4f bounds, int flags) {
		this.content = content;
		setLayout(this);
		this.add(content);
		this.setBounds(bounds.x(), bounds.y(), bounds.w(), bounds.z());
		if ((flags & IPopupLayer.FLAG_CLOSEABLE) != 0) {
			GLButton close = new GLButton();
			close.setRenderer(this);
			close.setCallback(this);
			close.setzDelta(0.5f);
			this.add(close);
		}
		if ((flags & IPopupLayer.FLAG_RESIZEABLE) != 0) {
			// TODO
		}
		if ((flags & IPopupLayer.FLAG_MOVEABLE) != 0) {
			// TODO
		}
		setVisibility(EVisibility.PICKABLE); // as a barrier to the underlying
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement body = children.get(0);
		body.setBounds(3, 3, w - 6, h - 6);
		if (children.size() > 1) {
			IGLLayoutElement b = children.get(1);
			b.setBounds(w - 8, -2, 12, 12);
		}
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		((PopupLayer) getParent()).hide(content);
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.color(Color.LIGHT_GRAY).fillRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
		g.color(Color.BLACK);
		g.drawLine(2, 2, w - 2, h - 2);
		g.drawLine(w - 2, 2, 2, h - 2);
	}

	/**
	 * @return the content, see {@link #content}
	 */
	public GLElement getContent() {
		return content;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(Color.LIGHT_GRAY).fillRoundedRect(0, 0, w, h, 3);
		g.color(Color.BLACK).drawRoundedRect(0, 0, w, h, 3);
		super.renderImpl(g, w, h);
	}
}
