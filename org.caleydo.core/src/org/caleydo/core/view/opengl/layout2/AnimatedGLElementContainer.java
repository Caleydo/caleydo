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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.animation.AAnimation;
import org.caleydo.core.view.opengl.layout2.animation.AAnimation.EAnimationType;
import org.caleydo.core.view.opengl.layout2.animation.ACustomAnimation;
import org.caleydo.core.view.opengl.layout2.animation.AElementAnimation;
import org.caleydo.core.view.opengl.layout2.animation.ALayoutAnimation;
import org.caleydo.core.view.opengl.layout2.animation.DummyAnimation;
import org.caleydo.core.view.opengl.layout2.animation.Durations;
import org.caleydo.core.view.opengl.layout2.animation.Durations.IDuration;
import org.caleydo.core.view.opengl.layout2.animation.InAnimation;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IInTransition;
import org.caleydo.core.view.opengl.layout2.animation.InOutTransitions.IOutTransition;
import org.caleydo.core.view.opengl.layout2.animation.MoveAnimation;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions.IMoveTransition;
import org.caleydo.core.view.opengl.layout2.animation.OutAnimation;
import org.caleydo.core.view.opengl.layout2.animation.StyleAnimation;
import org.caleydo.core.view.opengl.layout2.animation.StyleAnimations.IStyleAnimation;
import org.caleydo.core.view.opengl.layout2.layout.AGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * a element container that is animated!
 *
 * @author Samuel Gratzl
 *
 */
public class AnimatedGLElementContainer extends GLElement implements IGLElementParent, Iterable<GLElement> {
	private IGLLayout layout = GLLayouts.NONE;
	// the static children
	private final List<GLElement> children = new ArrayList<>(3);

	private final List<ALayoutAnimation> layoutAnimations = new LinkedList<>();
	private final List<ACustomAnimation> customAnimations = new LinkedList<>();
	private final List<StyleAnimation> styleAnimations = new LinkedList<>();

	private boolean dirtyAnimation = true;
	private boolean forceLayout = true;

	private long startTime = -1;

	private IDuration defaultDuration = Durations.DEFAULT;
	private IMoveTransition defaultMoveTransition = MoveTransitions.MOVE_AND_GROW_LINEAR;
	private IInTransition defaultInTransition = InOutTransitions.SLIDE_HOR_IN;
	private IOutTransition defaultOutTransition = InOutTransitions.SLIDE_HOR_OUT;

	public AnimatedGLElementContainer() {

	}

	public AnimatedGLElementContainer(IGLLayout layout) {
		this.layout = layout;
	}

	/**
	 * @param animateByDefault
	 *            setter, see {@link animateByDefault}
	 */
	public AnimatedGLElementContainer setAnimateByDefault(boolean animateByDefault) {
		this.defaultDuration = animateByDefault ? Durations.DEFAULT : Durations.NO;
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
	public AnimatedGLElementContainer setDefaultDuration(IDuration defaultDuration) {
		this.defaultDuration = defaultDuration;
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
	public final AnimatedGLElementContainer setLayout(IGLLayout layout) {
		if (layout == this.layout)
			return this;
		this.layout = layout;
		relayout();
		return this;
	}

	/**
	 * @return the layout, see {@link #layout}
	 */
	public IGLLayout getLayout() {
		return layout;
	}

	/**
	 * returns a list of children that are there at a specific time, including added and removed elements through
	 * animations
	 *
	 * @param time
	 * @return
	 */
	private List<GLElement> asSeenIn(int time) {
		if (this.layoutAnimations.isEmpty() || time == 0)
			return children;
		List<GLElement> r = new ArrayList<>(children);
		for (AAnimation anim : layoutAnimations) {
			switch (anim.getType()) {
			case IN:
				if (anim.getStopAt() < time) { // was added
					r.add(((AElementAnimation) anim).getAnimatedElement());
				}
				break;
			case OUT:
				if (anim.getStopAt() < time) { // was removed
					r.remove(((AElementAnimation) anim).getAnimatedElement());
				}
				break;
			default:
				break;
			}
		}
		return r;
	}

	/**
	 * returns the currently active children to render (either there oder animated)
	 *
	 * @return
	 */
	private Iterable<GLElement> activeChildren() {
		Collection<GLElement> r = new ArrayList<>(children);
		for (AAnimation anim : layoutAnimations) {
			switch (anim.getType()) {
			case OUT:
				if (anim.isRunning()) { // was removed
					r.add(((AElementAnimation) anim).getAnimatedElement());
				}
				break;
			default:
				break;
			}
		}
		return r;
	}

	@Override
	protected final void layout() {
		super.layout();
		Vec2f size = getSize();

		if (dirtyAnimation || forceLayout) {
			List<ALayoutAnimation> tmp = new LinkedList<>(layoutAnimations);

			Collection<GLElement> elems = asSeenIn(0);
			List<RecordingLayoutElement> l = new ArrayList<>(elems.size());
			for (GLElement elem : elems) {
				if (elem.getVisibility() != EVisibility.NONE)
					l.add(new RecordingLayoutElement(elem.layoutElement));
			}
			layout.doLayout(l, size.x(), size.y());

			outer: for (RecordingLayoutElement elem : l) {
				for (Iterator<ALayoutAnimation> ita = tmp.iterator(); ita.hasNext();) {
					ALayoutAnimation anim = ita.next();
					if (anim.getAnimated() == elem.wrappee) { // match
						anim.init(elem.before, elem.after);
						ita.remove();
						continue outer;
					}
				}
				if (elem.hasChanged()) { // create a move animation
					ALayoutAnimation anim = createMoveAnimation(elem.wrappee, elem.before, elem.after);
					layoutAnimations.add(anim);
				}
			}
		}
	}

	private int nextDelta() {
		long old = startTime;
		startTime = System.currentTimeMillis();
		if (old < 0) // first
			old = startTime;
		int delta = (int) (startTime - old);
		return delta;
	}

	protected ALayoutAnimation createMoveAnimation(IGLLayoutElement elem, Vec4f before, Vec4f after) {
		if (defaultDuration == Durations.NO) {
			DummyAnimation d = new DummyAnimation(EAnimationType.MOVE, elem);
			d.init(before, after);
			return d;
		}
		final IDuration duration = elem.getLayoutDataAs(IDuration.class, defaultDuration);
		final IMoveTransition animation = elem.getLayoutDataAs(IMoveTransition.class, defaultMoveTransition);
		MoveAnimation anim = new MoveAnimation(0, duration, elem, animation);
		anim.init(before, after);
		return anim;
	}

	protected ALayoutAnimation createInAnimation(IGLLayoutElement elem, int startIn, IDuration duration,
			IInTransition animation) {
		if (defaultDuration == Durations.NO)
			return new DummyAnimation(EAnimationType.IN, elem);
		return new InAnimation(startIn, duration, elem, animation);
	}

	protected ALayoutAnimation createOutAnimation(IGLLayoutElement elem, int startIn, IDuration duration,
			IOutTransition animation) {
		if (defaultDuration == Durations.NO)
			return new DummyAnimation(EAnimationType.OUT, elem);
		return new OutAnimation(startIn, duration, elem, animation);
	}

	@Override
	protected void takeDown() {
		for (GLElement elem : this)
			takeDown(elem, false);
		layoutAnimations.clear();
		styleAnimations.clear();
		customAnimations.clear();
		super.takeDown();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		for (GLElement child : this)
			child.init(context);
	}

	private void setup(GLElement child) {
		IGLElementParent ex = child.getParent();
		boolean doInit = ex == null;
		if (ex == this) {
			// internal move
			children.remove(child);
		} else if (ex != null) {
			doInit = ex.moved(child);
		}
		child.setParent(this);
		if (doInit && context != null)
			child.init(context);
	}

	private void takeDown(GLElement child, boolean checkLayoutAnimations) {
		child.takeDown();
		removeAnimationsOf(child, checkLayoutAnimations);
	}

	private void removeAnimationsOf(GLElement child, boolean checkLayoutAnimations) {
		removeStyleAnimationsOf(child);
		if (checkLayoutAnimations) {
			for (Iterator<ALayoutAnimation> it = layoutAnimations.iterator(); it.hasNext();)
				if (it.next().getAnimatedElement() == child)
					it.remove();
		}
	}

	protected final void removeStyleAnimationsOf(GLElement child) {
		for (Iterator<StyleAnimation> it = styleAnimations.iterator(); it.hasNext();)
			if (it.next().getAnimatedElement() == child)
				it.remove();
	}

	public final int indexOf(GLElement child) {
		return children.indexOf(child);
	}

	public final void add(GLElement child) {
		add(child, defaultDuration);
	}

	public final void add(int index, GLElement child) {
		add(index, child, defaultDuration, child.getLayoutDataAs(IInTransition.class, defaultInTransition));
	}

	public final void add(GLElement child, int duration) {
		add(child, Durations.fix(duration));
	}

	public final void add(GLElement child, IDuration duration) {
		add(children.size(), child, duration, child.getLayoutDataAs(IInTransition.class, defaultInTransition));
	}

	/**
	 * animated adding of an element
	 *
	 * @param child
	 */
	public final void add(GLElement child, int duration, IInTransition animation) {
		add(children.size(), child, Durations.fix(duration), animation);
	}

	public final void add(int index, GLElement child, IDuration duration, IInTransition animation) {
		int startIn = 0;
		if (child.getParent() == this) {
			int from = indexOf(child);
			if (from < index) // as we remove before we insert
				index--;
		}
		// we want to add this child now but smooth it in, so we are interested in its final position in the future at
		// the future state
		this.layoutAnimations.add(createInAnimation(child.layoutElement, startIn, duration, animation));
		setup(child);
		dirtyAnimation = true;
		if (startIn == 0) // TODO
			children.add(index, child);
		animate();
	}

	public final GLElement set(int index, GLElement child) {
		GLElement old = children.get(index);
		takeDown(old, true);
		this.layoutAnimations.add(createInAnimation(child.layoutElement, 0, defaultDuration,
				child.getLayoutDataAs(IInTransition.class, defaultInTransition)));
		setup(child);
		children.set(index, child);
		dirtyAnimation = true;
		animate();
		return old;
	}

	public final void remove(GLElement child) {
		remove(child, defaultDuration);
	}

	public final void remove(int index) {
		remove(get(index));
	}

	public final void remove(GLElement child, IDuration duration) {
		remove(child, duration, child.getLayoutDataAs(IOutTransition.class, defaultOutTransition));
	}

	public final void remove(GLElement child, IDuration duration, IOutTransition animation) {
		int startIn = 0;
		assert child.getParent() == this;
		this.layoutAnimations.add(createOutAnimation(child.layoutElement, startIn, duration, animation));
		if (startIn == 0) // TODO
			children.remove(child);
		dirtyAnimation = true;
		animate();
	}

	public final void clear() {
		clear(defaultDuration);
	}

	public final void clear(IDuration duration) {
		List<GLElement> seenIn = new ArrayList<>(asSeenIn(0));
		for (GLElement s : seenIn)
			remove(s, duration);
	}

	public final GLElement get(int index) {
		return asSeenIn(0).get(index);
	}

	public final void sortBy(Comparator<GLElement> comparator) {
		Collections.sort(children, comparator);
		relayout();
	}

	private void animate() {
		super.relayout();// super not direct to signal we want to relayout but no force
	}

	public final int size() {
		return children.size();
	}

	@Override
	public final boolean moved(GLElement child) {
		children.remove(child);
		removeAnimationsOf(child, true);
		relayout();
		return context != null;
	}

	public final void animate(GLElement elem, int duration, IStyleAnimation anim) {
		animate(elem, 0, duration, anim);
	}

	public final void animate(GLElement elem, int startIn, int duration, IStyleAnimation anim) {
		this.styleAnimations.add(new StyleAnimation(startIn, Durations.fix(duration), elem.layoutElement, anim));
		repaint();
	}

	public final void animate(ACustomAnimation anim) {
		this.customAnimations.add(anim);
		repaint();
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);

		final Vec2f size = getSize();
		final int delta = nextDelta();

		// layout updates
		List<GLElement> extras = new ArrayList<>(3);
		for (Iterator<ALayoutAnimation> it = layoutAnimations.iterator(); it.hasNext();) {
			ALayoutAnimation anim = it.next();
			if (anim.apply(delta, size.x(), size.y())) { // done
				GLElement elem = anim.getAnimatedElement();
				switch (anim.getType()) {
				case MOVE:
					break;
				case IN:
					break;
				case OUT:
					takeDown(elem, false);
					break;
				default:
					throw new IllegalStateException();
				}
				it.remove();
			} else if (anim.getType() == EAnimationType.OUT && anim.isRunning())
				extras.add(anim.getAnimatedElement());
		}

		// custom animations
		for (Iterator<ACustomAnimation> it = customAnimations.iterator(); it.hasNext();) {
			ACustomAnimation anim = it.next();
			if (anim.apply(g, delta, size.x(), size.y())) { // done
				it.remove();
			}
		}

		// style animations + renderings
		Iterable<GLElement> activeChildren = Iterables.concat(children, extras);
		g.incZ();
		if (styleAnimations.isEmpty()) {
			for (GLElement child : activeChildren) {
				child.render(g);
			}
		} else {
			outer: for (GLElement child : activeChildren) {
				for (Iterator<StyleAnimation> it = styleAnimations.iterator(); it.hasNext();) {
					StyleAnimation anim = it.next();
					if (anim.getAnimatedElement() == child) {
						if (anim.apply(g, delta))
							it.remove();
						continue outer;
					}
				}
				child.render(g);
			}
		}
		g.decZ();

		if (layoutAnimations.isEmpty() && styleAnimations.isEmpty() && customAnimations.isEmpty()) {
			startTime = -1; // stop animation
		}
		repaintAll();
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
	public final Iterator<GLElement> iterator() {
		return Iterators.unmodifiableIterator(children.iterator());
	}

	@Override
	public <P, R> R accept(IGLElementVisitor<P, R> visitor, P para) {
		return visitor.visit(this, para);
	}

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
			return after != null && !before.equals(after);
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
		public Vec4f getBounds() {
			return this.after.copy();
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
