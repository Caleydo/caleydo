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
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.awt.Point;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;

/**
 * basic layouting element
 *
 * @author Samuel Gratzl
 *
 */
public class GLElement {
	/**
	 * the visibility state of this element
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public enum EVisibility {
		/**
		 * will just be drawn (default)
		 */
		VISIBLE,
		/**
		 * will be drawn and can be picked
		 */
		PICKABLE,
		/**
		 * will not be drawn or pickable but will contribute to the layout
		 */
		HIDDEN,
		/**
		 * invisible and uses no space
		 */
		NONE;
	}

	/**
	 * the renderer to use for rendering indirectly
	 */
	private IGLRenderer renderer = GLRenderers.DUMMY;

	/**
	 * location of this element determined by parent layout
	 */
	private float x_layout = 0, y_layout = 0;
	/**
	 * location set by the user
	 */
	private float x_set = Float.NaN, y_set = Float.NaN;
	/**
	 * size determined by parent layout
	 */
	private float w_layout = Float.NaN, h_layout = Float.NaN;
	/**
	 * size set by the user
	 */
	private float w_set = Float.NaN, h_set = Float.NaN;

	/**
	 * padding of the content to the border
	 */
	private GLPadding padding = GLPadding.ZERO;

	/**
	 * the current visibility mode, see {@link EVisibility}
	 */
	private EVisibility visibility = EVisibility.VISIBLE;

	/**
	 * my parent element for propagating repaint and relayout requests
	 */
	protected IGLElementParent parent;

	/**
	 * global shared context of this hierarchy
	 */
	protected IGLElementContext context;

	/**
	 * layout data for the parent layout
	 */
	private Object layoutData;
	/**
	 * this element as {@link IGLLayoutElement}
	 */
	protected final IGLLayoutElement layoutElement = new LayoutElementAdapter();

	/**
	 * cache for managing display lists of rendering
	 */
	private final RenderCache cache = new RenderCache();
	/**
	 * cache for managing display lists of picking
	 */
	private final RenderCache pickCache = new RenderCache();

	/**
	 * the picking ID to use, automatically resolved by settings the visibility mode to {@link EVisibility#PICKABLE)
	 */
	private int pickingID = -1;
	/**
	 * the renderer to use for picking, default: a full sized rect
	 */
	private IGLRenderer picker = GLRenderers.TRANSPARENT_RECT;
	/**
	 * the list of picking listeners, set by {@link #onPick(IPickingListener)}
	 */
	private final PickingListenerComposite pickingListener = new PickingListenerComposite(1);

	/**
	 * indicator whether the layouting should run next time
	 */
	private boolean dirtyLayout = true;

	public GLElement() {

	}

	public GLElement(IGLRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @return the layoutData, see {@link #layoutData}
	 */
	public final Object getLayoutData() {
		return layoutData;
	}

	public final GLElement setLayoutData(Object layoutData) {
		if (this.layoutData == layoutData)
			return this;
		this.layoutData = layoutData;
		relayout();
		return this;
	}

	/**
	 * renders the current element
	 *
	 * @param g
	 */
	public final void render(GLGraphics g) {
		if (dirtyLayout)
			layout();
		if (!needToRender()) {
			cache.invalidate(context);
			return;
		}
		float x = x_layout + padding.left;
		float y = y_layout + padding.top;
		float w = w_layout - padding.left - padding.right;
		float h = h_layout - padding.top - padding.bottom;

		g.move(x, y);
		if (!cache.render(context, g)) {
			cache.begin(context, g, w, h);
			renderImpl(g, w, h);
			cache.end(context, g);
			// } else {
			// // cache visualization
			// g.color(1, 0, 1, 0.1f).incZ(1).fillRect(0, 0, w, h).incZ(-1);
		}
		g.move(-x, -y);
	}

	/**
	 * hook for rendering directly without a renderer
	 *
	 * @param g
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	protected void renderImpl(GLGraphics g, float w, float h) {
		renderer.render(g, w, h, this);
	}

	/**
	 * renders the picking part of this element
	 *
	 * @param g
	 */
	public final void renderPick(GLGraphics g) {
		if (dirtyLayout)
			layout();
		if (!needToRender()) {
			pickCache.invalidate(context);
			return;
		}
		float x = x_layout + padding.left;
		float y = y_layout + padding.top;
		float w = w_layout - padding.left - padding.right;
		float h = w_layout - padding.top - padding.bottom;

		g.move(x, y);
		if (!pickCache.render(context, g)) {
			pickCache.begin(context, g, w, h);
			boolean pushed = pickingID >= 0;
			if (pushed)
				g.pushName(this.pickingID);
			renderPickImpl(g, w, h);
			if (pushed)
				g.popName();
			pickCache.end(context, g);
		}
		g.move(-x, -y);
	}

	private boolean needToRender() {
		if (visibility != EVisibility.VISIBLE && visibility != EVisibility.PICKABLE)
			return false;
		if (w_layout <= 0 || h_layout <= 0)
			return false;
		return true;
	}

	/**
	 * hook for rendering the picking directly without a renderer
	 *
	 * @param g
	 * @param w
	 * @param h
	 */
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (visibility == EVisibility.PICKABLE) // check really pickable
			picker.render(g, w, h, this);
	}

	/**
	 * @param padding
	 *            setter, see {@link padding}
	 */
	public void setPadding(GLPadding padding) {
		if (padding == null)
			padding = GLPadding.ZERO;
		this.padding = padding;
	}

	/**
	 * @return the padding, see {@link #padding}
	 */
	public GLPadding getPadding() {
		return padding;
	}

	/**
	 * setter for {@link #picker}
	 *
	 * @param picker
	 */
	public final void setPicker(IGLRenderer picker) {
		if (this.picker == picker)
			return;
		this.picker = picker;
		repaintPick();
	}

	/**
	 * setter for {@link #renderer}
	 *
	 * @param renderer
	 */
	public final void setRenderer(IGLRenderer renderer) {
		if (this.renderer == renderer)
			return;
		this.renderer = renderer;
		repaint();
	}

	/**
	 * @param display
	 *            setter, see {@link display}
	 */
	public final GLElement setVisibility(EVisibility display) {
		if (this.visibility == display)
			return this;
		final EVisibility old = this.visibility;
		final EVisibility new_ = display;

		this.visibility = display;
		if (old == EVisibility.NONE || new_ == EVisibility.NONE)
			relayoutParent();
		if (old == EVisibility.PICKABLE) {
			//not longer pickable
			if (pickingID >= 0) {
				context.unregisterPickingListener(pickingListener);
				pickingID = -1;
			}
		} else if (new_ == EVisibility.PICKABLE) {
			// now pickable
			if (pickingID < 0 && context != null) {
				pickingID = context.registerPickingListener(pickingListener, getPickingObjectId());
			}
		}

		repaint();
		return this;
	}

	/**
	 * hook for returning the picking object id to use, default 0
	 *
	 * @return
	 */
	protected int getPickingObjectId() {
		return 0;
	}

	/**
	 * adds picking listener to this element
	 *
	 * @param customPickingListener
	 * @return
	 */
	public final GLElement onPick(IPickingListener customPickingListener) {
		this.pickingListener.add(customPickingListener);
		return this;
	}

	/**
	 * @return the visibility, see {@link #visibility}
	 */
	public final EVisibility getVisibility() {
		return visibility;
	}

	/**
	 * sets the size of this element to a fixed size, to help the {@link IGLLayout}
	 *
	 * @param w
	 * @param h
	 * @return
	 */
	public final GLElement setSize(float w, float h) {
		if (this.w_set == w && this.h_set == h)
			return this;
		this.w_set = w_layout = w;
		this.h_set = h_layout = h;
		relayoutParent();
		return this;
	}

	/**
	 * sets the location of this element to a fixed position, to help the {@link IGLLayout}
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public final GLElement setLocation(float x, float y) {
		if (this.x_set == x && this.y_set == y)
			return this;
		this.x_set = x_layout = x;
		this.y_set = y_layout = y;
		relayoutParent();
		return this;
	}

	/**
	 * returns the layouted position
	 *
	 * @return
	 */
	public final Vec2f getLocation() {
		return new Vec2f(x_layout, y_layout);
	}

	/**
	 * computes the absolute location of this element using my parent
	 *
	 * @return
	 */
	public final Vec2f getAbsoluteLocation() {
		if (parent == null) {
			return getLocation();
		} else {
			Vec2f p = parent.getAbsoluteLocation();
			p.add(getLocation());
			return p;
		}
	}

	/**
	 * converts a position to absolute pixel coordinates
	 *
	 * @param relative
	 * @return
	 */
	public final Vec2f toAbsolute(Vec2f relative) {
		relative.add(getAbsoluteLocation());
		return relative;
	}

	/**
	 * converts a position to relative to my absolute position, see {@link #getAbsoluteLocation()}
	 *
	 * @param absolute
	 * @return
	 */
	public final Vec2f toRelative(Vec2f absolute) {
		absolute.sub(getAbsoluteLocation());
		return absolute;
	}

	/**
	 * see {@link #toRelative(Vec2f)} for {@link Point}
	 *
	 * @param absolute
	 * @return
	 */
	public final Vec2f toRelative(Point absolute) {
		return toRelative(new Vec2f(absolute.x, absolute.y));
	}


	/**
	 * returns the layouted size of this element
	 *
	 * @return
	 */
	public final Vec2f getSize() {
		return new Vec2f(w_layout, h_layout);
	}

	/**
	 * combination of {@link #getLocation()} and {@link #getSize()}
	 *
	 * @return
	 */
	public final Vec4f getBounds() {
		return new Vec4f(x_layout, y_layout, w_layout, h_layout);
	}

	/**
	 * shortcut for {@link #setLocation(float, float)} and {@link #setSize(float, float)}
	 *
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public final GLElement setBounds(float x, float y, float w, float h) {
		return setLocation(x, y).setSize(w, h);
	}

	/**
	 * triggers that I should be relayouted before the next run
	 */
	protected void relayout() {
		cache.invalidate(context);
		pickCache.invalidate(context);
		this.dirtyLayout = true;
	}

	protected final void relayoutParent() {
		if (parent != null)
			parent.relayout();
	}

	/**
	 * shortcut to {@link #repaint()} and {@link #repaintAll()}
	 */
	protected final void repaintAll() {
		repaint();
		repaintPick();
	}

	/**
	 * triggers that me and my parents get repainted
	 */
	protected void repaint() {
		cache.invalidate(context);
		if (parent != null)
			parent.repaint();
	}

	/**
	 * triggers that me and my parents get repaint the picking representation
	 */
	protected void repaintPick() {
		pickCache.invalidate(context);
		if (parent != null)
			parent.repaintPick();
	}

	final void setParent(IGLElementParent parent) {
		if (this.parent == parent)
			return;
		this.parent = parent;
	}

	/**
	 * @return the parent, see {@link #parent}
	 */
	public final IGLElementParent getParent() {
		return parent;
	}

	/**
	 * setup method, when adding a child to a parent
	 * @param context
	 */
	protected void init(IGLElementContext context) {
		this.context = context;
		if (visibility == EVisibility.PICKABLE) {
			pickingID = context.registerPickingListener(pickingListener, getPickingObjectId());
		}
		context.init(this);
	}

	/**
	 * take down, after removing a child from its parent
	 */
	protected void takeDown() {
		context.takeDown(this);
		cache.takeDown(context);
		pickCache.takeDown(context);
		if (pickingID >= 0) {
			context.unregisterPickingListener(pickingListener);
			pickingID = -1;
		}
		this.parent = null;
		this.context = null;
	}

	/**
	 * trigger to layout this element
	 */
	protected void layout() {
		dirtyLayout = false;
		cache.invalidate(context);
		pickCache.invalidate(context);
	}

	private void setLayoutSize(float w, float h) {
		if (this.w_layout == w && this.h_layout == h)
			return;
		this.w_layout = w;
		this.h_layout = h;
		if (w_layout > 0 && h_layout > 0)
			layout();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Element [visibility=");
		builder.append(visibility);
		builder.append(", xywh_layout=").append(x_layout).append('/').append(y_layout).append('/').append(w_layout)
				.append('/').append(h_layout);
		builder.append(", xywh_set=").append(x_set).append('/').append(y_set).append('/').append(w_set).append('/')
				.append(h_set);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * implementation of the {@link IGLLayoutElement} spec
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private class LayoutElementAdapter implements IGLLayoutElement {
		@Override
		public void setSize(float w, float h) {
			setLayoutSize(w, h);
		}

		@Override
		public void setLocation(float x, float y) {
			GLElement.this.x_layout = x;
			GLElement.this.y_layout = y;
		}

		@Override
		public void setBounds(float x, float y, float w, float h) {
			setLocation(x, y);
			setSize(w, h);
		}

		@Override
		public float getWidth() {
			return w_layout;
		}

		@Override
		public float getHeight() {
			return h_layout;
		}

		@Override
		public float getSetWidth() {
			return w_set;
		}

		@Override
		public float getSetHeight() {
			return h_set;
		}

		@Override
		public float getSetX() {
			return x_set;
		}

		@Override
		public float getSetY() {
			return y_set;
		}

		@Override
		public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
			if (clazz.isInstance(layoutData))
				return clazz.cast(layoutData);
			return default_;
		}

		@Override
		public GLElement asElement() {
			return GLElement.this;
		}

		@Override
		public String toString() {
			return GLElement.this.toString();
		}
	}
}




