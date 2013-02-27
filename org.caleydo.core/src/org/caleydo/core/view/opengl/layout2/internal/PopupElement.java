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

import static org.caleydo.core.view.opengl.layout2.IPopupLayer.FLAG_BORDER;
import static org.caleydo.core.view.opengl.layout2.IPopupLayer.FLAG_CLOSEABLE;
import static org.caleydo.core.view.opengl.layout2.IPopupLayer.FLAG_MOVEABLE;
import static org.caleydo.core.view.opengl.layout2.IPopupLayer.FLAG_RESIZEABLE;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.awt.Color;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.eclipse.swt.SWT;

/**
 * @author Samuel Gratzl
 *
 */
class PopupElement extends GLElementContainer implements IGLLayout, IGLRenderer, GLButton.ISelectionCallback {
	private final GLElement content;
	private final int flags;
	private int headerPickingId = -1;
	private IPickingListener l = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onTitlePicked(pick);
		}
	};

	public PopupElement(GLElement content, Vec4f bounds, int flags) {
		this.content = content;
		this.flags = flags;
		setLayout(this);
		this.add(content);
		boolean hasHeader = false;
		if (isFlagSet(FLAG_MOVEABLE)) {
			hasHeader = true;
		}

		GLButton close = new GLButton();

		close.setRenderer(this);
		close.setCallback(this);
		close.setzDelta(0.5f);
		close.setVisibility(isFlagSet(FLAG_CLOSEABLE) ? EVisibility.PICKABLE : EVisibility.HIDDEN);
		this.add(close);

		GLElement resize = new Resize();
		resize.setzDelta(0.5f);
		resize.setVisibility(isFlagSet(FLAG_RESIZEABLE) ? EVisibility.PICKABLE : EVisibility.HIDDEN);
		this.add(resize);

		if (bounds != null)
			this.setBounds(bounds.x(), bounds.y(), bounds.z() + 6, bounds.w() + 3 + (hasHeader ? 8 : 3));
		setVisibility(EVisibility.PICKABLE); // as a barrier to the underlying
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		if (isFlagSet(FLAG_MOVEABLE))
			headerPickingId = context.registerPickingListener(this.l);
	}

	@Override
	protected void takeDown() {
		if (isFlagSet(FLAG_MOVEABLE))
			context.unregisterPickingListener(headerPickingId);
		headerPickingId = -1;
		super.takeDown();
	}

	/**
	 * @param pick
	 */
	protected void onTitlePicked(Pick pick) {
		if (pick.isAnyDragging() && !pick.isDoDragging())
			return;
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			context.setCursor(SWT.CURSOR_HAND);
			break;
		case CLICKED:
			pick.setDoDragging(true);
			break;
		case MOUSE_OUT:
			if (!pick.isDoDragging())
				context.setCursor(-1);
			break;
		case MOUSE_RELEASED:
			if (!pick.isDoDragging())
				return;
			context.setCursor(-1);
			break;
		case DRAGGED:
			int dx = pick.getDx();
			int dy = pick.getDy();
			Vec2f l = getLocation();
			setLocation(l.x() + dx, l.y() + dy);
			break;
		default:
			break;
		}
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement body = children.get(0);
		boolean moveAble = isFlagSet(FLAG_MOVEABLE);
		body.setBounds(3, (moveAble ? 8 : 3), w - 6, h - 3 - (moveAble ? 8 : 3));
		IGLLayoutElement close = children.get(1);
		close.setBounds(w - 8, -2, 12, 12);
		IGLLayoutElement resize = children.get(2);
		resize.setBounds(w - 12, h - 12, 12, 12);
	}

	/**
	 * @return the content, see {@link #content}
	 */
	public GLElement getContent() {
		return content;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (isFlagSet(FLAG_MOVEABLE)) {
			g.color(Color.LIGHT_GRAY).renderRoundedRect(true, 0, 0, w, 8, 3, 2, true, true, false, false);
		}
		if (isFlagSet(FLAG_BORDER)) {
			// g.color(Color.LIGHT_GRAY).fillRoundedRect(0, 0, w, h, 3);
			g.color(Color.BLACK).drawRoundedRect(0, 0, w, h, 3);
		}
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (isFlagSet(FLAG_MOVEABLE)) {
			g.incZ();
			g.pushName(headerPickingId);
			g.fillRect(0, 0, w, 8);
			g.popName();
			g.decZ();
		}
		super.renderPickImpl(g, w, h);
	}

	private boolean isFlagSet(int f) {
		return (flags & f) != 0;
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		((PopupLayer) getParent()).hide(content);
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.color(new Color(210, 210, 210)).fillRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
		g.color(Color.BLACK);
		g.drawLine(2, 2, w - 2, h - 2);
		g.drawLine(w - 2, 2, 2, h - 2);
	}

	/**
	 * @param dx
	 * @param dy
	 */
	protected void resize(int dx, int dy) {
		Vec2f s = this.getSize();
		setSize(s.x() + dx, s.y() + dy);
		relayout();
	}

	class Resize extends PickableGLElement {
		public Resize() {
			setPicker(GLRenderers.DUMMY);
		}

		@Override
		protected void onClicked(Pick pick) {
			if (pick.isAnyDragging())
				return;
			pick.setDoDragging(true);
		}

		@Override
		protected void onDragged(Pick pick) {
			if (!pick.isDoDragging())
				return;
			resize(pick.getDx(), pick.getDy());

		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(Color.BLACK);
			g.drawLine(0, h, w, 0);
			g.drawLine(3, h, w, 3);
			g.drawLine(6, h, w, 6);
			super.renderImpl(g, w, h);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			super.renderPickImpl(g, w, h);
			g.color(Color.RED).fillPolygon(new Vec2f(0, h), new Vec2f(w, 0), new Vec2f(w, h));
		}
	}
}