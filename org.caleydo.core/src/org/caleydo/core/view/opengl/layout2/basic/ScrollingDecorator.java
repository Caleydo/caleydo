/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;

import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.IScrollBar.IScrollBarCallback;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * wrapper element that enables the use of scrollbars.
 *
 * by convention the min size is part of the layout data
 *
 * @author Samuel Gratzl
 *
 */
public final class ScrollingDecorator extends AGLElementDecorator implements IScrollBarCallback {
	private final ScrollBarImpl vertical;
	private final ScrollBarImpl horizontal;

	private final float scrollBarWidth;

	public ScrollingDecorator(GLElement content, IScrollBar horizontal, IScrollBar vertical, float scrollBarWidth) {
		super(content);
		this.scrollBarWidth = scrollBarWidth;
		this.horizontal = horizontal != null ? new ScrollBarImpl(horizontal) : null;
		if (horizontal != null) {
			horizontal.setCallback(this);
			horizontal.setWidth(scrollBarWidth);
		}
		this.vertical = vertical != null ? new ScrollBarImpl(vertical) : null;
		if (vertical != null) {
			vertical.setCallback(this);
			vertical.setWidth(scrollBarWidth);
		}
	}

	public static ScrollingDecorator wrap(GLElement content, float scrollBarWidth) {
		return new ScrollingDecorator(content, new ScrollBar(true), new ScrollBar(false), scrollBarWidth);
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		if (horizontal != null)
			horizontal.pickingId = context.registerPickingListener(horizontal.scrollBar);
		if (vertical != null)
			vertical.pickingId = context.registerPickingListener(vertical.scrollBar);
	}

	@Override
	protected void takeDown() {
		if (horizontal != null)
			context.unregisterPickingListener(horizontal.pickingId);
		if (vertical != null)
			context.unregisterPickingListener(vertical.pickingId);
		super.takeDown();
	}

	@Override
	protected void layoutContent(IGLLayoutElement layout, float w, float h) {
		Vec2f minSize = getMinSize(layout);
		Vec2f size = getSize();
		Vec2f contentSize = new Vec2f();
		Vec2f offset = content.getLocation();
		offset.setX(-offset.x());
		offset.setY(-offset.y());

		boolean needHor = horizontal != null && size.x() < minSize.x();
		if (needHor)
			size.setY(size.y() - scrollBarWidth);
		boolean needVer = vertical != null && size.y() < minSize.y();
		if (needVer) {
			size.setX(size.x() - scrollBarWidth);
			if (!needHor) {
				needHor = horizontal != null && size.x() < minSize.x();
				if (needHor) {
					size.setY(size.y() - scrollBarWidth);
				}
			}
		}

		if (needHor) {
			contentSize.setX(minSize.x());
			horizontal.needIt = true;
			offset.setX(horizontal.scrollBar.setBounds(offset.x(), size.x(), minSize.x()));
		} else {
			if (horizontal != null)
				horizontal.needIt = false;
			contentSize.setX(size.x());
			offset.setX(0);
		}
		if (needVer) {
			contentSize.setY(minSize.y());
			vertical.needIt = true;
			offset.setY(vertical.scrollBar.setBounds(offset.y(), size.y(), minSize.y()));
		} else {
			if (vertical != null)
				vertical.needIt = false;
			contentSize.setY(size.y());
			offset.setY(0);
		}
		layout.setLocation(-offset.x(), -offset.y());
		layout.setSize(contentSize.x(), contentSize.y());
	}


	/**
	 * @param layout
	 * @return
	 */
	private Vec2f getMinSize(IGLLayoutElement layout) {
		IHasMinSize minSize = layout.getLayoutDataAs(IHasMinSize.class, null);
		if (minSize != null)
			return minSize.getMinSize();
		return layout.getLayoutDataAs(Vec2f.class, new Vec2f(0, 0));
	}

	@Override
	public float getHeight(IScrollBar scrollBar) {
		if (horizontal != null && horizontal.scrollBar == scrollBar)
			return getSize().x();
		else
			return getSize().y();
	}

	@Override
	public void onScrollBarMoved(IScrollBar scrollBar, float value) {
		Vec2f loc = content.getLocation();
		if (horizontal != null && horizontal.scrollBar == scrollBar) {
			content.setLocation(-value, loc.y());
		} else {
			content.setLocation(loc.x(), -value);
		}
		repaint();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		doRender(g, w, h, false);
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		doRender(g, w, h, true);
		super.renderPickImpl(g, w, h);
	}

	protected void doRender(GLGraphics g, float w, float h, boolean pick) {
		final boolean doHor = needHor();
		final boolean doVer = needVer();
		final GL2 gl = g.gl;

		if (doVer || doHor) {
			gl.glPushAttrib(GL2.GL_ENABLE_BIT);
		}
		if (doHor) {
			g.move(0, h - scrollBarWidth);
			g.pushName(horizontal.pickingId);
			if (pick)
				horizontal.scrollBar.renderPick(g, doVer ? w - scrollBarWidth : w, scrollBarWidth, this);
			else
				horizontal.scrollBar.render(g, doVer ? w - scrollBarWidth : w, scrollBarWidth, this);
			g.popName();
			g.move(0, -h + scrollBarWidth);
		}
		if (doVer) {
			g.move(w - scrollBarWidth, 0);
			g.pushName(vertical.pickingId);
			if (pick)
				vertical.scrollBar.renderPick(g, scrollBarWidth, doHor ? h - scrollBarWidth : h, this);
			else
				vertical.scrollBar.render(g, scrollBarWidth, doHor ? h - scrollBarWidth : h, this);
			g.popName();
			g.move(-w + scrollBarWidth, 0);
		}
		if (doHor) {
			double[] clipPlane1 = new double[] { 1.0, 0.0, 0.0, 0 };
			double[] clipPlane3 = new double[] { -1.0, 0.0, 0.0, doVer ? w - scrollBarWidth : w };
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE0, clipPlane1, 0);
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE1, clipPlane3, 0);
			gl.glEnable(GL2ES1.GL_CLIP_PLANE0);
			gl.glEnable(GL2ES1.GL_CLIP_PLANE1);
		}
		if (doVer) {
			double[] clipPlane2 = new double[] { 0.0, 1.0, 0.0, 0 };
			double[] clipPlane4 = new double[] { 0.0, -1.0, 0.0, doHor ? h - scrollBarWidth : h };
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE2, clipPlane2, 0);
			gl.glClipPlane(GL2ES1.GL_CLIP_PLANE3, clipPlane4, 0);
			gl.glEnable(GL2ES1.GL_CLIP_PLANE2);
			gl.glEnable(GL2ES1.GL_CLIP_PLANE3);
		}
		if (pick)
			content.renderPick(g);
		else
			content.render(g);

		if (doVer || doHor) {
			gl.glPopAttrib();
		}
	}

	/**
	 * @return the applied clipping area
	 */
	public Rect getClipingArea() {
		Vec2f loc = content.getLocation();
		Vec2f size = getSize();

		Rect r = new Rect();
		r.x(-loc.x());
		r.y(-loc.y());
		r.width(needVer() ? size.x() - scrollBarWidth : size.x());
		r.height(needHor() ? size.y() - scrollBarWidth : size.y());
		return r;
	}

	protected boolean needHor() {
		return horizontal != null && horizontal.needIt;
	}

	protected boolean needVer() {
		return vertical != null && vertical.needIt;
	}


	@Override
	public boolean moved(GLElement child) {
		return false;
	}

	private class ScrollBarImpl {
		private final IScrollBar scrollBar;
		private int pickingId;
		private boolean needIt;

		public ScrollBarImpl(IScrollBar scrollBar) {
			this.scrollBar = scrollBar;
		}
	}

	/**
	 * contract for delivering a min size, alternative provide a Vec2f in the layout data
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IHasMinSize {
		Vec2f getMinSize();
	}
}
