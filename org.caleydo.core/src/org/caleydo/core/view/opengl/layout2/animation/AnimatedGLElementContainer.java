/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.animation;

import static org.caleydo.core.view.opengl.layout2.GLElementAccessor.asLayoutElement;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementAccessor;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.IGLElementVisitor;
import org.caleydo.core.view.opengl.layout2.animation.AAnimation.EAnimationType;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IInTransition;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IOutTransition;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.AGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayout2Adapter;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
/**
 * a element container that is animated!
 *
 * @author Samuel Gratzl
 *
 */
public class AnimatedGLElementContainer extends GLElement implements IGLElementParent, Iterable<GLElement> {
	private static final int DEFAULT_DURATION_INT = 300;
	public static final int NO = 0;
	protected static final Duration DEFAULT_DURATION = new Duration(DEFAULT_DURATION_INT);
	protected static final Duration NO_DURATION = new Duration(NO);

	private IGLLayout2 layout = GLLayouts.NONE;
	// the static children
	private final List<GLElement> children = new ArrayList<>(3);

	private final Map<IGLLayoutElement, ALayoutAnimation> layoutAnimations = new HashMap<>();

	private boolean enableAnimation = true;
	private boolean dirtyAnimation = true;
	private boolean forceLayout = true;

	private Duration defaultDuration = DEFAULT_DURATION;
	private IMoveTransition defaultMoveTransition = MoveTransitions.MOVE_AND_GROW_LINEAR;
	private IInTransition defaultInTransition = InOutTransitions.SLIDE_HOR_IN;
	private IOutTransition defaultOutTransition = InOutTransitions.SLIDE_HOR_OUT;

	public AnimatedGLElementContainer() {

	}

	public AnimatedGLElementContainer(IGLLayout layout) {
		this(new GLLayout2Adapter(layout));
	}

	public AnimatedGLElementContainer(IGLLayout2 layout) {
		this.layout = layout;
	}

	private static Duration asDuration(int duration) {
		if (duration == DEFAULT_DURATION_INT)
			return DEFAULT_DURATION;
		if (duration == NO)
			return NO_DURATION;
		return new Duration(duration);
	}

	/**
	 * @param enableAnimation
	 *            setter, see {@link enableAnimation}
	 */
	public void setEnableAnimation(boolean enableAnimation) {
		if (this.enableAnimation == enableAnimation)
			return;
		this.enableAnimation = enableAnimation;
		if (!this.enableAnimation) {
			flushLayoutAnimations();
		}
	}

	/**
	 * @param animateByDefault
	 *            setter, see {@link animateByDefault}
	 */
	public AnimatedGLElementContainer setAnimateByDefault(boolean animateByDefault) {
		this.defaultDuration = animateByDefault ? DEFAULT_DURATION : NO_DURATION;
		return this;
	}

	/**
	 * @param transition
	 *            setter, see {@link defaultInTransition}
	 */
	public AnimatedGLElementContainer setDefaultInTransition(IInTransition transition) {
		this.defaultInTransition = transition == null ? InOutTransitions.SLIDE_HOR_IN : transition;
		return this;
	}

	/**
	 * @param transition
	 *            setter, see {@link defaultMoveTransition}
	 */
	public AnimatedGLElementContainer setDefaultMoveTransition(IMoveTransition transition) {
		this.defaultMoveTransition = transition == null ? MoveTransitions.MOVE_AND_GROW_LINEAR : transition;
		return this;
	}

	/**
	 * @param transition
	 *            setter, see {@link defaultOutTransition}
	 */
	public AnimatedGLElementContainer setDefaultOutTransition(IOutTransition transition) {
		this.defaultOutTransition = transition == null ? InOutTransitions.SLIDE_HOR_OUT : transition;
		return this;
	}

	/**
	 * @param defaultDuration
	 *            setter, see {@link defaultDuration}
	 */
	public AnimatedGLElementContainer setDefaultDuration(int defaultDuration) {
		this.defaultDuration = asDuration(defaultDuration);
		return this;
	}

	@Override
	public void relayout() {
		super.relayout();
		this.forceLayout = true;
	}

	/**
	 * @param layout
	 *            setter, see {@link layout}
	 */
	public final AnimatedGLElementContainer setLayout(IGLLayout2 layout) {
		if (this.layout == layout)
			return this;
		this.layout = layout;
		relayout();
		return this;
	}

	/**
	 * @param layout
	 *            setter, see {@link layout}
	 */
	public final AnimatedGLElementContainer setLayout(IGLLayout layout) {
		if (this.layout instanceof GLLayout2Adapter && ((GLLayout2Adapter) this.layout).getLayout() == layout)
			return this;
		return setLayout(new GLLayout2Adapter(layout));
	}

	/**
	 * returns the currently active children to render (either there oder animated)
	 *
	 * @return
	 */
	private Iterable<GLElement> activeChildren() {
		if (layoutAnimations.isEmpty())
			return children;
		Collection<GLElement> r = new ArrayList<>(3);
		for (Map.Entry<IGLLayoutElement, ALayoutAnimation> anim : layoutAnimations.entrySet()) {
			switch (anim.getValue().getType()) {
			case OUT:
				r.add(anim.getKey().asElement());
				break;
			default:
				break;
			}
		}
		return Iterables.concat(children, r);
	}

	@Override
	protected final void layoutImpl(int deltaTimeMs) {
		super.layoutImpl(deltaTimeMs);
		Vec2f size = getSize();
		boolean relayout = false;
		if (!enableAnimation) {
			List<IGLLayoutElement> l = new ArrayList<>(children.size());
			for (GLElement elem : children) {
				if (elem.getVisibility() != EVisibility.NONE)
					l.add(asLayoutElement(elem));
			}
			relayout = layout.doLayout(l, size.x(), size.y(), asLayoutElement(this), deltaTimeMs);
		} else if (dirtyAnimation || forceLayout) {
			List<RecordingLayoutElement> l = new ArrayList<>(children.size());
			for (GLElement elem : children) {
				if (elem.getVisibility() != EVisibility.NONE)
					l.add(new RecordingLayoutElement(asLayoutElement(elem)));
			}
			relayout = layout.doLayout(l, size.x(), size.y(), asLayoutElement(this), deltaTimeMs);

			for (RecordingLayoutElement elem : l) {
				ALayoutAnimation anim = layoutAnimations.get(elem.wrappee);
				if (anim != null) {
					updateMoveAnimation(anim, elem.wrappee, elem.before, elem.after);
					continue;
				}
				if (elem.hasChanged()) { // create a move animation
					anim = createMoveAnimation(elem.wrappee, elem.before, elem.after);
					layoutAnimations.put(elem.wrappee, anim);
				}
			}
		}
		if (relayout)
			relayout();
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);

		final Vec2f size = getSize();
		// layout updates
		for (Iterator<Map.Entry<IGLLayoutElement, ALayoutAnimation>> it = layoutAnimations.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<IGLLayoutElement, ALayoutAnimation> entry = it.next();
			ALayoutAnimation anim = entry.getValue();
			IGLLayoutElement elem = entry.getKey();
			if (anim.apply(elem, deltaTimeMs, size.x(), size.y())) { // done
				if (anim.getType() == EAnimationType.OUT)
					takeDown(elem.asElement(), false);
				it.remove();
			}
		}

		for(GLElement child : activeChildren())
			child.layout(deltaTimeMs);
	}

	public final boolean isAnimating() {
		return !layoutAnimations.isEmpty();
	}

	protected void updateMoveAnimation(ALayoutAnimation anim, IGLLayoutElement elem, Vec4f before, Vec4f after) {
		anim.init(before, after);
	}

	protected ALayoutAnimation createMoveAnimation(IGLLayoutElement elem, Vec4f before, Vec4f after) {
		final Duration duration = elem.getLayoutDataAs(Duration.class, defaultDuration);
		if (duration.getDuration() == 0) {
			DummyAnimation d = new DummyAnimation(EAnimationType.MOVE);
			d.init(before, after);
			return d;
		}
		final IMoveTransition animation = elem.getLayoutDataAs(IMoveTransition.class, defaultMoveTransition);
		MoveAnimation anim = new MoveAnimation(duration.getDuration(), animation);
		anim.init(before, after);
		return anim;
	}

	protected ALayoutAnimation createInAnimation(IGLLayoutElement elem, Duration duration,
			IInTransition animation) {
		if (duration.getDuration() == 0)
			return new DummyAnimation(EAnimationType.IN);
		return new InAnimation(duration.getDuration(), animation);
	}

	protected ALayoutAnimation createOutAnimation(IGLLayoutElement elem, Duration duration,
			IOutTransition animation) {
		if (duration.getDuration() == 0)
			return new DummyAnimation(EAnimationType.OUT);
		return new OutAnimation(duration.getDuration(), animation);
	}

	@Override
	protected void takeDown() {
		for (GLElement elem : this)
			takeDown(elem, false);
		layoutAnimations.clear();
		super.takeDown();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		for (GLElement child : this) {
			GLElementAccessor.setParent(child, this);
			GLElementAccessor.init(child, context);
		}
	}

	private void setup(GLElement child) {
		IGLElementParent ex = child.getParent();
		boolean doInit = ex == null;
		if (ex == this) {
			// internal move
			children.remove(child);
		} else if (ex != null) {
			doInit = !ex.moved(child);
		}
		GLElementAccessor.setParent(child, this);
		if (doInit && context != null)
			GLElementAccessor.init(child, context);
	}

	private void takeDown(GLElement child, boolean checkLayoutAnimations) {
		GLElementAccessor.takeDown(child);
		GLElementAccessor.setParent(child, null);
		if (checkLayoutAnimations) {
			layoutAnimations.remove(asLayoutElement(child));
		}
	}

	public final int indexOf(GLElement child) {
		return children.indexOf(child);
	}

	public final void add(GLElement child) {
		add(children.size(), child);
	}

	public final void add(int index, GLElement child) {
		add(index, child, child.getLayoutDataAs(Duration.class, defaultDuration),
				child.getLayoutDataAs(IInTransition.class, defaultInTransition));
	}

	public final void add(GLElement child, int duration) {
		add(children.size(), child, asDuration(duration),
				child.getLayoutDataAs(IInTransition.class, defaultInTransition));
	}

	public final void add(int index, GLElement child, Duration duration, IInTransition animation) {
		if (child.getParent() == this) {
			int from = indexOf(child);
			if (from < index) // as we remove before we insert
				index--;
			if (index < 0) // index can be -1 if the child was removed and added again immediately
				index = 0;
		}
		// we want to add this child now but smooth it in, so we are interested in its final position in the future at
		// the future state
		IGLLayoutElement l = asLayoutElement(child);
		this.layoutAnimations.put(l, createInAnimation(l, duration, animation));
		setup(child);
		dirtyAnimation = true;
		children.add(index, child);
		animate();
	}

	public final GLElement set(int index, GLElement child) {
		GLElement old = children.get(index);
		takeDown(old, true);

		IGLLayoutElement l = asLayoutElement(child);
		this.layoutAnimations.put(l,
				createInAnimation(l, defaultDuration,
				child.getLayoutDataAs(IInTransition.class, defaultInTransition)));
		setup(child);
		children.set(index, child);
		dirtyAnimation = true;
		animate();
		return old;
	}

	public final void remove(GLElement child) {
		remove(child, child.getLayoutDataAs(Duration.class, defaultDuration));
	}

	public final void remove(int index) {
		remove(get(index));
	}

	public final void remove(GLElement child, int duration) {
		remove(child, asDuration(duration));
	}

	public final void remove(GLElement child, Duration duration) {
		remove(child, duration, child.getLayoutDataAs(IOutTransition.class, defaultOutTransition));
	}

	public final void remove(GLElement child, Duration duration, IOutTransition animation) {
		assert child.getParent() == this;
		children.remove(child);
		if (duration.getDuration() <= 0)
			takeDown(child, true);
		else {
			IGLLayoutElement l = asLayoutElement(child);
			ALayoutAnimation anim = createOutAnimation(l, duration, animation);
			anim.init(child.getBounds(), null);
			this.layoutAnimations.put(l, anim);
		}
		dirtyAnimation = true;
		animate();
	}

	public final void clear() {
		clear(defaultDuration);
	}
	public final void clear(int duration) {
		clear(asDuration(duration));
	}

	public final void clear(Duration duration) {
		List<GLElement> seenIn = new ArrayList<>(children);
		for (GLElement s : seenIn)
			remove(s, duration);
	}

	public final GLElement get(int index) {
		return children.get(index);
	}

	public final void sortBy(Comparator<GLElement> comparator) {
		Collections.sort(children, comparator);
		relayout();
	}

	private void animate() {
		super.relayout();// super not direct to signal we want to relayout but no force
	}

	/**
	 * calls this method to set the size of a child in a managed way, as if you set the size of an element the recording
	 * can't restore the old value
	 *
	 * @param child
	 * @param w
	 * @param h
	 */
	public final void resizeChild(GLElement child, float w, float h) {
		resizeChild(child, w, h, child.getLayoutDataAs(Duration.class, defaultDuration));
	}

	public final void resizeChild(GLElement child, float w, float h, Duration duration) {
		final IMoveTransition animation = child.getLayoutDataAs(IMoveTransition.class, defaultMoveTransition);
		// final Duration duration = child.getLayoutDataAs(Duration.class, defaultDuration);
		ManualMoveAnimation anim = new ManualMoveAnimation(duration.getDuration(), animation, child.getBounds());
		child.setSize(w, h);
		layoutAnimations.put(GLElementAccessor.asLayoutElement(child), anim);
	}

	public final int size() {
		return children.size();
	}

	@Override
	public final boolean moved(GLElement child) {
		children.remove(child);
		if (true) {
			layoutAnimations.remove(asLayoutElement(child));
		}
		relayout();
		return context != null;
	}

	/**
	 * removes and applies all layout animations
	 */
	protected final void flushLayoutAnimations() {
		final Vec2f size = getSize();
		for (Map.Entry<IGLLayoutElement, ALayoutAnimation> entry : layoutAnimations.entrySet()) {
			ALayoutAnimation anim = entry.getValue();
			int r = anim.getRemaining();
			boolean done = anim.apply(entry.getKey(), r, size.x(), size.y());
			if (anim.getType() == EAnimationType.OUT)
				takeDown(entry.getKey().asElement(), false);
			assert (done);
		}
		layoutAnimations.clear();
	}

	/**
	 * repaints the children and ensures that the there is no repaint loop
	 */
	public final void repaintChildren() {
		for (GLElement child : children)
			GLElementAccessor.repaintDown(child);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		for (GLElement child : activeChildren()) {
			child.render(g);
		}
		if (!layoutAnimations.isEmpty())
			repaintAll();
	}

	/**
	 * repaints the children and ensures that the there is no repaint loop
	 */
	public final void repaintPickChildren() {
		for (GLElement child : children)
			GLElementAccessor.repaintPickDown(child);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		g.incZ();
		for (GLElement child : activeChildren())
			child.renderPick(g);
		g.decZ();
	}

	@Override
	protected boolean hasPickAbles() {
		return super.hasPickAbles() || !children.isEmpty(); // may have pickables
	}

	@Override
	public final Iterator<GLElement> iterator() {
		return Iterators.unmodifiableIterator(children.iterator());
	}

	@Override
	public <P, R> R accept(IGLElementVisitor<P, R> visitor, P para) {
		return visitor.visit(this, para);
	}

	/**
	 * wrapper to record the layout changes
	 *
	 * @author Samuel Gratzl
	 *
	 */
	private static class RecordingLayoutElement extends AGLLayoutElement {
		private final IGLLayoutElement wrappee;

		private Vec4f before;
		private Vec4f after;

		public RecordingLayoutElement(IGLLayoutElement wrappee) {
			this.wrappee = wrappee;
			this.before = wrappee.getBounds();
			this.after = before.copy();
		}

		public boolean hasChanged() {
			if (after == null || before.equals(after))
				return false;

			return !(!areValidBounds(after) && !areValidBounds(before));
		}

		@Override
		public GLElement asElement() {
			return wrappee.asElement();
		}

		@Override
		public <T> T getLayoutDataAs(Class<T> clazz, T default_) {
			return wrappee.getLayoutDataAs(clazz, default_);
		}

		@Override
		public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
			return wrappee.getLayoutDataAs(clazz, default_);
		}

		@Override
		public Vec4f getBounds() {
			return this.after.copy();
		}

		@Override
		public Rect getRectBounds() {
			return new Rect(this.after);
		}

		@Override
		public Vec4f getSetBounds() {
			return wrappee.getSetBounds();
		}

		@Override
		public void setLocation(float x, float y) {
			this.after.setX(x);
			this.after.setY(y);
		}

		@Override
		public void setSize(float w, float h) {
			this.after.setZ(w);
			this.after.setW(h);
		}
	}
}
