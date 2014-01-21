/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.IScrollBar.IScrollBarCallback;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;

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
	/**
	 * in which direction a mouse wheel changes a scrollbar
	 */
	private final EDimension mouseWheelScrolls;
	private int mouseWheelsScrollsPickingId = -1;

	private final float scrollBarWidth;

	/**
	 * whether scrolling should be enabled
	 */
	private boolean enabled = true;

	/**
	 * whether the viewport should be automatically reseted, if scrolling isn't needed anymore
	 */
	private boolean autoResetViewport = true;

	/**
	 * a custom min size provider otherwise the content will be used
	 */
	private IHasMinSize minSizeProvider = null;

	public ScrollingDecorator(GLElement content, IScrollBar horizontal, IScrollBar vertical, float scrollBarWidth) {
		this(content, horizontal, vertical, scrollBarWidth, null);
	}

	public ScrollingDecorator(GLElement content, IScrollBar horizontal, IScrollBar vertical, float scrollBarWidth,
			EDimension mouseWheelsScrolls) {
		super(content);
		this.scrollBarWidth = scrollBarWidth;
		mouseWheelScrolls = mouseWheelsScrolls;
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

	/**
	 * factory method for creating a {@link ScrollingDecorator}
	 * 
	 * @param content
	 *            what
	 * @param scrollBarWidth
	 *            size of the scrollbar
	 * @param mouseWheelsScrolls
	 *            in which direction should the mouse wheel scroll, null is allowed
	 * @return
	 */
	public static ScrollingDecorator wrap(GLElement content, float scrollBarWidth, EDimension mouseWheelsScrolls) {
		return new ScrollingDecorator(content, new ScrollBar(true), new ScrollBar(false), scrollBarWidth,
				mouseWheelsScrolls);
	}

	public static ScrollingDecorator wrap(GLElement content, float scrollBarWidth) {
		return wrap(content, scrollBarWidth, null);
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		if (horizontal != null)
			horizontal.pickingId = context.registerPickingListener(horizontal.scrollBar);
		if (vertical != null)
			vertical.pickingId = context.registerPickingListener(vertical.scrollBar);
		if (mouseWheelScrolls != null && mouseWheelScrolls.select(horizontal, vertical) != null)
			mouseWheelsScrollsPickingId = context.registerPickingListener(new IPickingListener() {
				@Override
				public void pick(Pick pick) {
					if (pick.getPickingMode() == PickingMode.MOUSE_WHEEL)
						mouseWheelScrolls.select(horizontal, vertical).scrollBar.pick(pick);
				}
			});
	}

	@Override
	protected void takeDown() {
		if (horizontal != null)
			context.unregisterPickingListener(horizontal.pickingId);
		if (vertical != null)
			context.unregisterPickingListener(vertical.pickingId);
		if (mouseWheelsScrollsPickingId != -1) {
			context.unregisterPickingListener(mouseWheelsScrollsPickingId);
			mouseWheelsScrollsPickingId = -1;
		}
		super.takeDown();
	}

	/**
	 * @param minSizeProvider
	 *            setter, see {@link minSizeProvider}
	 */
	public void setMinSizeProvider(IHasMinSize minSizeProvider) {
		this.minSizeProvider = minSizeProvider;
		relayout();
	}

	/**
	 * @param autoResetViewport
	 *            setter, see {@link autoResetViewport}
	 */
	public void setAutoResetViewport(boolean autoResetViewport) {
		this.autoResetViewport = autoResetViewport;
		if (this.autoResetViewport)
			relayout();
	}

	/**
	 * @param enabled
	 *            setter, see {@link enabled}
	 */
	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled)
			return;
		this.enabled = enabled;
		relayout();
	}

	/**
	 * @return the enabled, see {@link #enabled}
	 */
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	protected void layoutContent(IGLLayoutElement layout, float w, float h, int deltaTimeMs) {
		if (!enabled) {
			layout.setBounds(0, 0, w, h);
			return;
		}
		Vec2f minSize = getMinSize(layout);
		Vec2f size = getSize();
		Vec2f contentSize = new Vec2f();
		Vec2f offset = content.getLocation();
		offset.setX(-offset.x());
		offset.setY(-offset.y());

		if (!autoResetViewport && offset.x() != 0) {
			minSize.setX(Math.max(size.x() + offset.x(), minSize.x()));
		}
		if (!autoResetViewport && offset.y() != 0) {
			minSize.setY(Math.max(size.y() + offset.y(), minSize.y()));
		}

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
		if (minSizeProvider != null)
			return minSizeProvider.getMinSize();
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
		if (!enabled) {
			if (pick)
				renderPickContent(g, w, h);
			else
				content.render(g);
			return;
		}

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
			renderPickContent(g, w, h);
		else
			content.render(g);

		if (doVer || doHor) {
			gl.glPopAttrib();
		}
	}

	private void renderPickContent(GLGraphics g, float w, float h) {
		if (mouseWheelsScrollsPickingId >= 0)
			g.pushName(mouseWheelsScrollsPickingId).fillRect(0, 0, w, h);
		content.renderPick(g);
		if (mouseWheelsScrollsPickingId >= 0)
			g.popName();
	}

	/**
	 * @return the applied clipping area
	 */
	public Rect getClipingArea() {
		Vec2f loc = content.getLocation();
		Vec2f size = getSize();

		if (!enabled)
			return new Rect(0, 0, size.x(), size.y());

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
