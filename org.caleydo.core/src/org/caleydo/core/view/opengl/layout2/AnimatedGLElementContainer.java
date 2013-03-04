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
import java.util.List;
import java.util.PriorityQueue;

import org.caleydo.core.view.opengl.layout2.animation.AAnimation;
import org.caleydo.core.view.opengl.layout2.animation.AAnimation.EAnimationType;
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

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

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

	// active animations sorted by their remaining time
	private final PriorityQueue<AAnimation> animations = new PriorityQueue<>(3);

	private boolean dirtyAnimation = true;
	private boolean forceLayout = true;

	private long startTime = -1;

	private boolean animateByDefault = true;

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
		this.animateByDefault = animateByDefault;
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
		this.defaultMoveTransition = transition == null ? MoveTransitions.MOVE_AND_GROW_LINEAR
				: transition;
		return this;
	}

	/**
	 * @param transition
	 *            setter, see {@link defaultOutTransition}
	 */
	public AnimatedGLElementContainer setDefaultOutTransition(IOutTransition transition) {
		this.defaultOutTransition = transition == null ? InOutTransitions.SLIDE_HOR_OUT
				: transition;
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
		if (this.animations.isEmpty() || time == 0)
			return children;
		List<GLElement> r = new ArrayList<>(children);
		for (AAnimation anim : animations) {
			switch (anim.getType()) {
			case MOVE:
				break;
			case IN:
				if (anim.getStopAt() < time) { // was added
					r.add(anim.getAnimatedElement());
				}
				break;
			case OUT:
				if (anim.getStopAt() < time) { // was removed
					r.remove(anim.getAnimatedElement());
				}
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
		for (AAnimation anim : animations) {
			switch (anim.getType()) {
			case OUT:
				if (anim.isRunning()) { // was removed
					r.add(anim.getAnimatedElement());
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
			List<AAnimation> tmp = Lists.newArrayList(animations);

			Collection<GLElement> elems = asSeenIn(0);
			List<RecordingLayoutElement> l = new ArrayList<>(elems.size());
			for (GLElement elem : elems) {
				if (elem.getVisibility() != EVisibility.NONE)
					l.add(new RecordingLayoutElement(elem.layoutElement));
			}
			layout.doLayout(l, size.x(), size.y());

			outer: for (RecordingLayoutElement elem : l) {
				for (Iterator<AAnimation> ita = tmp.iterator(); ita.hasNext();) {
					AAnimation anim = ita.next();
					if (anim.getAnimated() == elem.wrappee && anim instanceof ALayoutAnimation) { // match
						((ALayoutAnimation) anim).init(elem.before, elem.after);
						ita.remove();
						continue outer;
					}
				}
				if (elem.hasChanged()) { // create a move animation
					ALayoutAnimation anim = createMoveAnimation(elem);
					anim.init(elem.before, elem.after);
					animations.add(anim);
				}
			}
		}
	}

	private ALayoutAnimation createMoveAnimation(RecordingLayoutElement elem) {
		if (!animateByDefault) {
			return new DummyAnimation(EAnimationType.MOVE, elem.wrappee);
		}
		final IDuration duration = elem.getLayoutDataAs(IDuration.class, Durations.DEFAULT);
		final IMoveTransition animation = elem.getLayoutDataAs(IMoveTransition.class,
 defaultMoveTransition);
		MoveAnimation anim = new MoveAnimation(0, duration, elem.wrappee, animation);
		return anim;
	}

	private void doAnimation() {
		if (animations.isEmpty())
			return;

		Vec2f size = getSize();
		int delta = nextDelta();

		for (Iterator<AAnimation> it = animations.iterator(); it.hasNext();) {
			AAnimation anim = it.next();
			if (anim.apply(delta, size.x(), size.y())) { // done
				GLElement elem = anim.getAnimatedElement();
				switch (anim.getType()) {
				case MOVE:
					break;
				case IN:
					break;
				case OUT:
					takeDown(elem);
					break;
				case STYLE:
					break;
				}
				it.remove();
			}
		}
		if (animations.isEmpty()) {
			startTime = -1; // stop animation
		}

		repaintAll();
	}

	private int nextDelta() {
		long old = startTime;
		startTime = System.currentTimeMillis();
		if (old < 0) // first
			old = startTime;
		int delta = (int) (startTime - old);
		return delta;
	}

	@Override
	protected void takeDown() {
		for (GLElement elem : this)
			takeDown(elem);
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
			//internal move
			children.remove(child);
		} else if (ex != null) {
			doInit = ex.moved(child);
		}
		child.setParent(this);
		if (doInit && context != null)
			child.init(context);
	}

	private void takeDown(GLElement child) {
		child.takeDown();
	}

	public final void add(GLElement child) {
		add(child, animateByDefault ? 300 : 0);
	}

	public final void add(GLElement child, int duration) {
		add(child, duration, child.getLayoutDataAs(IInTransition.class, defaultInTransition));
	}

	/**
	 * animated adding of an element
	 *
	 * @param child
	 */
	public final void add(GLElement child, int duration, IInTransition animation) {
		int startIn = 0;
		assert child.getParent() == null; // no parent yet
		// we want to add this child now but smooth it in, so we are interested in its final position in the future at
		// the future state
		this.animations.add(new InAnimation(startIn, Durations.fix(duration), child.layoutElement, animation));
		setup(child);
		dirtyAnimation = true;
		if (startIn == 0)
			children.add(child);
		animate();
	}


	public final void remove(GLElement child) {
		remove(child, animateByDefault ? 300 : 0);
	}

	public final void remove(GLElement child, int duration) {
		remove(child, duration, child.getLayoutDataAs(IOutTransition.class, defaultOutTransition));
	}

	public final void remove(GLElement child, int duration, IOutTransition animation) {
		int startIn = 0;
		assert child.getParent() == this;
		this.animations.add(new OutAnimation(startIn, Durations.fix(duration), child.layoutElement, animation));
		if (startIn == 0)
			children.remove(child);
		dirtyAnimation = true;
		animate();
	}

	public final void clear() {
		clear(animateByDefault ? 300 : 0);
	}

	public final void clear(int duration) {
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
		// FIXME
		children.remove(child);
		relayout();
		return context != null;
	}

	public final void animate(GLElement elem, int duration, IStyleAnimation anim) {
		this.animations.add(new StyleAnimation(0, Durations.fix(duration), elem.layoutElement, anim));
		animate();

	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		doAnimation();
		super.renderImpl(g, w, h);
		g.incZ();
		for (GLElement child : activeChildren())
			child.render(g);
		g.decZ();
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
