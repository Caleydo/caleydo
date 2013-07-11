/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import static org.caleydo.core.view.opengl.layout2.IPopupLayer.FLAG_BORDER;
import static org.caleydo.core.view.opengl.layout2.IPopupLayer.FLAG_CLOSEABLE;
import static org.caleydo.core.view.opengl.layout2.IPopupLayer.FLAG_COLLAPSABLE;
import static org.caleydo.core.view.opengl.layout2.IPopupLayer.FLAG_MOVEABLE;
import static org.caleydo.core.view.opengl.layout2.IPopupLayer.FLAG_RESIZEABLE;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.util.List;

import org.caleydo.core.util.color.Color;
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
import org.caleydo.core.view.opengl.layout2.renderer.RoundedRectRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.eclipse.swt.SWT;

/**
 * a specific popup
 *
 * @author Samuel Gratzl
 *
 */
class PopupElement extends GLElementContainer implements IGLLayout, IGLRenderer, GLButton.ISelectionCallback {
	private static final int FLAG_HAS_HEADER = FLAG_MOVEABLE | FLAG_COLLAPSABLE;
	private final GLElement content;
	private final int flags;
	private int headerPickingId = -1;
	private boolean collapsed = false;
	private IPickingListener l = new IPickingListener() {
		@Override
		public void pick(Pick pick) {
			onTitlePicked(pick);
		}
	};

	private Vec2f expandedSized;

	public PopupElement(GLElement content, Vec4f bounds, int flags) {
		this.content = content;
		this.flags = flags;
		setLayout(this);
		this.add(content);
		GLButton close = new GLButton();
		close.setRenderer(this);
		close.setCallback(this);
		close.setTooltip("close this popup");
		close.setHoverEffect(GLRenderers.drawRoundedRect(Color.WHITE));
		close.setzDelta(0.5f);
		close.setVisibility(isFlagSet(FLAG_CLOSEABLE) ? EVisibility.PICKABLE : EVisibility.HIDDEN);
		this.add(close);

		GLElement resize = new Resize();
		resize.setzDelta(0.5f);
		resize.setVisibility(isFlagSet(FLAG_RESIZEABLE) ? EVisibility.PICKABLE : EVisibility.HIDDEN);
		this.add(resize);

		if (bounds != null)
			this.setBounds(bounds.x(), bounds.y(), bounds.z(), bounds.w() + (isFlagSet(FLAG_HAS_HEADER) ? 8 : 0));
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
	 * @param collapsed
	 *            setter, see {@link collapsed}
	 */
	public void toggleCollapsed() {
		this.collapsed = !collapsed;
		if (this.collapsed) {
			expandedSized = getSize();
			setSize(expandedSized.x(), 8);
		} else {
			setSize(expandedSized.x(), expandedSized.y());
		}
		relayoutParent();
		relayout();
	}

	/**
	 * @param pick
	 */
	protected void onTitlePicked(Pick pick) {
		if (pick.isAnyDragging() && !pick.isDoDragging())
			return;
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			context.getSWTLayer().setCursor(SWT.CURSOR_HAND);
			break;
		case DOUBLE_CLICKED:
			if (isFlagSet(FLAG_COLLAPSABLE)) {
				toggleCollapsed();
			}
			break;
		case CLICKED:
			pick.setDoDragging(true);
			break;
		case MOUSE_OUT:
			if (!pick.isDoDragging())
				context.getSWTLayer().resetCursor();
			break;
		case MOUSE_RELEASED:
			if (!pick.isDoDragging())
				return;
			context.getSWTLayer().resetCursor();
			break;
		case DRAGGED:
			float dx = pick.getDx();
			float dy = pick.getDy();
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
		boolean hasHeader = isFlagSet(FLAG_HAS_HEADER);
		float offset = isFlagSet(FLAG_BORDER) ? 1 : 0;
		if (collapsed)
			body.hide();
		else
			body.setBounds(offset, (hasHeader ? 8 : offset), w - offset * 2, h - (hasHeader ? 8 : offset * 2));
		IGLLayoutElement close = children.get(1);
		close.setBounds(w - 8, -4, 14, 14);
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
		if (isFlagSet(FLAG_HAS_HEADER)) {
			g.color(Color.BLACK);
			RoundedRectRenderer.render(g, 0, 0, w, 8, 3, 2, RoundedRectRenderer.FLAG_FILL
					| RoundedRectRenderer.FLAG_TOP);
		}
		super.renderImpl(g, w, h);
		if (isFlagSet(FLAG_BORDER)) {
			// g.color(Color.LIGHT_GRAY).fillRoundedRect(0, 0, w, h, 3);
			g.color(Color.BLACK);
			RoundedRectRenderer.render(g, 0, 0, w, h, 3, 2, RoundedRectRenderer.FLAG_TOP);
		}
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (isFlagSet(FLAG_HAS_HEADER)) {
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
		g.color(Color.BLACK).fillRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
		g.color(Color.WHITE);
		g.lineWidth(2);
		g.drawLine(3, 3, w - 3, h - 3);
		g.drawLine(w - 3, 3, 3, h - 3);
		g.lineWidth(1);
	}

	/**
	 * @param dx
	 * @param dy
	 */
	protected void resize(float dx, float dy) {
		Vec2f s = this.getSize();
		setSize(s.x() + dx, s.y() + dy);
		relayout();
	}

	class Resize extends PickableGLElement {
		public Resize() {
			setPicker(GLRenderers.DUMMY);
		}

		@Override
		protected void onMouseOver(Pick pick) {
			if (pick.isAnyDragging())
				return;
			context.getSWTLayer().setCursor(SWT.CURSOR_SIZESE);
		}

		@Override
		protected void onMouseOut(Pick pick) {
			if (pick.isAnyDragging())
				return;
			context.getSWTLayer().resetCursor();
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
