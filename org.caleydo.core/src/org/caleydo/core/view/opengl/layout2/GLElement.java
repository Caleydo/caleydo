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
import java.awt.geom.Rectangle2D;

import org.caleydo.core.view.opengl.layout2.layout.AGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.layout.IHasGLLayoutData;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;

import com.google.common.base.Predicate;

/**
 * basic layouting element
 *
 * @author Samuel Gratzl
 *
 */
public class GLElement implements IHasGLLayoutData {
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
	 * location and size of this element determined by parent layout
	 */
	private final Rectangle2D.Float bounds_layout = new Rectangle2D.Float(0, 0, Float.NaN, Float.NaN);
	/**
	 * location and size set by the user
	 */
	private final Rectangle2D.Float bounds_set = new Rectangle2D.Float(Float.NaN, Float.NaN, Float.NaN, Float.NaN);

	/**
	 * the current visibility mode, see {@link EVisibility}
	 */
	private EVisibility visibility = EVisibility.VISIBLE;

	/**
	 * my parent element for propagating repaint and relayout requests
	 */
	private IGLElementParent parent;

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
	private IGLRenderer picker = GLRenderers.RECT;
	/**
	 * the list of picking listeners, set by {@link #onPick(IPickingListener)}
	 */
	private final PickingListenerComposite pickingListener = new PickingListenerComposite(1);

	/**
	 * indicator whether the layouting should run next time
	 */
	private boolean dirtyLayout = true;

	/**
	 * the z value to add before the value should be rendered
	 */
	private float zDelta = 0;

	public GLElement() {

	}

	public GLElement(IGLRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * @param zDelta
	 *            setter, see {@link zDelta}
	 */
	public GLElement setzDelta(float zDelta) {
		if (this.zDelta == zDelta)
			return this;
		this.zDelta = zDelta;
		repaintAll();
		return this;
	}

	public final GLElement setLayoutData(Object layoutData) {
		if (this.layoutData == layoutData)
			return this;
		this.layoutData = layoutData;
		relayout();
		return this;
	}

	@Override
	public final <T> T getLayoutDataAs(Class<T> clazz, T default_) {
		return GLLayouts.resolveLayoutData(clazz, layoutData, default_);
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
			cache.invalidate(context.getDisplayListPool());
			return;
		}
		float x = bounds_layout.x;
		float y = bounds_layout.y;
		float w = bounds_layout.width;
		float h = bounds_layout.height;

		g.incZ(zDelta);
		g.move(x, y);
		if (!cache.render(context.getDisplayListPool(), g.gl)) {
			cache.begin(context.getDisplayListPool(), g.gl, w, h);
			renderImpl(g, w, h);
			cache.end(context.getDisplayListPool(), g.gl);
			// } else {
			// // cache visualization
			// g.color(1, 0, 1, 0.1f).incZ(1).fillRect(0, 0, w, h).incZ(-1);
		}
		g.move(-x, -y);
		g.incZ(-zDelta);
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
			pickCache.invalidate(context.getDisplayListPool());
			return;
		}
		float x = bounds_layout.x;
		float y = bounds_layout.y;
		float w = bounds_layout.width;
		float h = bounds_layout.height;

		g.incZ(zDelta);
		g.move(x, y);
		if (!pickCache.render(context.getDisplayListPool(), g.gl)) {
			pickCache.begin(context.getDisplayListPool(), g.gl, w, h);
			boolean pushed = pickingID >= 0;
			if (pushed)
				g.pushName(this.pickingID);
			renderPickImpl(g, w, h);
			if (pushed)
				g.popName();
			pickCache.end(context.getDisplayListPool(), g.gl);
		}
		g.move(-x, -y);
		g.incZ(-zDelta);
	}

	private boolean needToRender() {
		if (visibility != EVisibility.VISIBLE && visibility != EVisibility.PICKABLE)
			return false;
		float x = bounds_layout.x;
		float y = bounds_layout.y;
		float w = bounds_layout.width;
		float h = bounds_layout.height;
		if (w <= 0 || h <= 0 || Float.isNaN(w) || Float.isNaN(h) || Float.isNaN(x) || Float.isNaN(y))
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
	 * setter for {@link #picker}
	 *
	 * @param picker
	 */
	public final GLElement setPicker(IGLRenderer picker) {
		if (this.picker == picker)
			return this;
		this.picker = picker;
		repaintPick();
		return this;
	}

	/**
	 * setter for {@link #renderer}
	 *
	 * @param renderer
	 */
	public final GLElement setRenderer(IGLRenderer renderer) {
		if (this.renderer == renderer)
			return this;
		this.renderer = renderer;
		repaint();
		return this;
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
				context.unregisterPickingListener(pickingID);
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
		if (this.bounds_set.width == w && this.bounds_set.height == h)
			return this;
		this.bounds_set.width = bounds_layout.width = w;
		this.bounds_set.height = bounds_layout.height = h;
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
		if (this.bounds_set.x == x && this.bounds_set.y == y)
			return this;
		this.bounds_set.x = bounds_layout.x = x;
		this.bounds_set.y = bounds_layout.y = y;
		relayoutParent();
		return this;
	}

	/**
	 * returns the layouted position
	 *
	 * @return
	 */
	public final Vec2f getLocation() {
		return new Vec2f(bounds_layout.x, bounds_layout.y);
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
		return new Vec2f(bounds_layout.width, bounds_layout.height);
	}

	/**
	 * combination of {@link #getLocation()} and {@link #getSize()}
	 *
	 * @return
	 */
	public final Vec4f getBounds() {
		return new Vec4f(bounds_layout.x, bounds_layout.y, bounds_layout.width, bounds_layout.height);
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
	public void relayout() {
		repaintAll();
		this.dirtyLayout = true;
	}

	protected final void relayoutParent() {
		if (parent != null)
			parent.relayout();
	}

	/**
	 * shortcut to {@link #repaint()} and {@link #repaintAll()}
	 */
	public final void repaintAll() {
		repaint();
		repaintPick();
	}

	/**
	 * triggers that me and my parents get repainted
	 */
	public void repaint() {
		if (context != null)
			cache.invalidate(context.getDisplayListPool());
		if (parent != null)
			parent.repaint();
	}

	/**
	 * triggers that me and my parents get repaint the picking representation
	 */
	public void repaintPick() {
		if (context != null)
			pickCache.invalidate(context.getDisplayListPool());
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
	 * finds a parent in the hierarchy that satisfies the given predicate
	 *
	 * @param satisfies
	 * @return
	 */
	protected final IGLElementParent findParent(Predicate<IGLElementParent> satisfies) {
		IGLElementParent act = parent;
		while (act != null && !(satisfies.apply(act))) {
			act = parent.getParent();
		}
		return act;
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
		cache.takeDown(context.getDisplayListPool());
		pickCache.takeDown(context.getDisplayListPool());
		if (pickingID >= 0) {
			context.unregisterPickingListener(pickingID);
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
		if (context != null) {
			cache.invalidate(context.getDisplayListPool());
			pickCache.invalidate(context.getDisplayListPool());
		}
	}

	private void setLayoutSize(float w, float h) {
		if (this.bounds_layout.width == w && this.bounds_layout.height == h)
			return;
		this.bounds_layout.width = w;
		this.bounds_layout.height = h;
		if (bounds_layout.width > 0 && bounds_layout.height > 0)
			layout();
	}

	public <P, R> R accept(IGLElementVisitor<P, R> visitor, P para) {
		return visitor.visit(this, para);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [visibility=");
		builder.append(visibility);
		builder.append(", xywh_layout=").append(this.bounds_layout.x).append('/').append(this.bounds_layout.y)
				.append('/').append(this.bounds_layout.width).append('/').append(this.bounds_layout.height);
		builder.append(", xywh_set=").append(this.bounds_set.x).append('/').append(this.bounds_set.y).append('/')
				.append(this.bounds_set.width).append('/').append(this.bounds_set.height);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * implementation of the {@link IGLLayoutElement} spec
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private class LayoutElementAdapter extends AGLLayoutElement {
		@Override
		public void setSize(float w, float h) {
			setLayoutSize(w, h);
		}

		@Override
		public void setLocation(float x, float y) {
			bounds_layout.x = x;
			bounds_layout.y = y;
		}

		@Override
		public Vec4f getBounds() {
			return GLElement.this.getBounds();
		}

		@Override
		public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
			return GLElement.this.getLayoutDataAs(clazz, default_);
		}

		@Override
		public GLElement asElement() {
			return GLElement.this;
		}

		@Override
		public String toString() {
			return GLElement.this.toString();
		}

		@Override
		public Vec4f getSetBounds() {
			return new Vec4f(bounds_set.x, bounds_set.y, bounds_set.width, bounds_set.height);
		}
	}
}




