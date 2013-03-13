package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.picking.Pick;

public class ScrollBar implements IScrollBar {
	private final boolean isHorizontal;
	private boolean hovered = false;
	private float offset;
	private float view;
	private float total;
	private IScrollBarCallback callback;

	public ScrollBar(boolean isHorizontal) {
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

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.color(Color.LIGHT_GRAY).fillRect(0, 0, w, h);
		if (isHorizontal) {
			g.color(hovered ? Color.DARK_GRAY : Color.GRAY).fillRect(getOffset(w), 0, getSize(w), h);
		} else {
			g.color(hovered ? Color.DARK_GRAY : Color.GRAY).fillRect(0, getOffset(h), w, getSize(h));
		}
	}

	private float getOffset(float total) {
		return offset * total / this.total;
	}

	private float getSize(float total) {
		return view * total / this.total;
	}

	@Override
	public void renderPick(GLGraphics g, float w, float h, GLElement parent) {
		g.fillRect(0, 0, w, h);
	}

	@Override
	public void pick(Pick pick) {
		if (pick.isAnyDragging() && !pick.isDoDragging())
			return;
		if (callback == null)
			return;
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			hovered = true;
			break;
		case CLICKED:
			Vec2f relative = callback.toRelative(pick.getPickedPoint());
			float t = callback.getTotal(this);
			float v = this.total / t;
			if (isHorizontal)
				v *= relative.x();
			else
				v *= relative.y();
			if (v < offset || v > (offset + view)) {
				v = Math.min(total - view, Math.max(0, v));
				callback.onScrollBarMoved(this, v);
			} else
				pick.setDoDragging(true);
			callback.repaint();
			break;
		case DRAGGED:
			if (!pick.isDoDragging())
				return;
			float vd = this.total / callback.getTotal(this);
			if (isHorizontal)
				vd *= pick.getDx();
			else
				vd *= pick.getDy();
			callback.onScrollBarMoved(this, offset + vd);
			break;
		case MOUSE_RELEASED:
			break;
		case MOUSE_OUT:
			hovered = false;
			callback.repaint();
			break;
		default:
			break;
		}
	}

}