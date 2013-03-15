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
	public static final int DEFAULT_DURATION = 300;
	public static final int NO = 0;

	private IGLLayout layout = GLLayouts.NONE;
	// the static children
	private final List<GLElement> children = new ArrayList<>(3);

	private final Map<IGLLayoutElement, ALayoutAnimation> layoutAnimations = new HashMap<>();

	private boolean enableAnimation = true;
	private boolean dirtyAnimation = true;
	private boolean forceLayout = true;

	private int defaultDuration = DEFAULT_DURATION;
	private IMoveTransition defaultMoveTransition = MoveTransitions.MOVE_AND_GROW_LINEAR;
	private IInTransition defaultInTransition = InOutTransitions.SLIDE_HOR_IN;
	private IOutTransition defaultOutTransition = InOutTransitions.SLIDE_HOR_OUT;

	public AnimatedGLElementContainer() {

	}

	public AnimatedGLElementContainer(IGLLayout layout) {
		this.layout = layout;
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
		this.defaultDuration = animateByDefault ? 300 : 0;
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
	protected final void layoutImpl() {
		super.layoutImpl();
		Vec2f size = getSize();
		if (!enableAnimation) {
			List<IGLLayoutElement> l = new ArrayList<>(children.size());
			for (GLElement elem : children) {
				if (elem.getVisibility() != EVisibility.NONE)
					l.add(asLayoutElement(elem));
			}
			layout.doLayout(l, size.x(), size.y());
		} else if (dirtyAnimation || forceLayout) {
			List<RecordingLayoutElement> l = new ArrayList<>(children.size());
			for (GLElement elem : children) {
				if (elem.getVisibility() != EVisibility.NONE)
					l.add(new RecordingLayoutElement(asLayoutElement(elem)));
			}
			layout.doLayout(l, size.x(), size.y());

			for (RecordingLayoutElement elem : l) {
				ALayoutAnimation anim = layoutAnimations.get(elem.wrappee);
				if (anim != null) {
					updateMoveAnimation(anim, elem.wrappee, elem.before, elem.after);
					continue;
				}
				if (elem.hasChanged()) { // create a move animation
					anim = createMoveAnimation(elem.wrappee, elem.before, elem.after, defaultDuration);
					layoutAnimations.put(elem.wrappee, anim);
				}
			}
		}
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

	protected void updateMoveAnimation(ALayoutAnimation anim, IGLLayoutElement elem, Vec4f before, Vec4f after) {
		anim.init(before, after);
	}

	protected ALayoutAnimation createMoveAnimation(IGLLayoutElement elem, Vec4f before, Vec4f after, int duration) {
		if (defaultDuration == 0) {
			DummyAnimation d = new DummyAnimation(EAnimationType.MOVE);
			d.init(before, after);
			return d;
		}
		final IMoveTransition animation = elem.getLayoutDataAs(IMoveTransition.class, defaultMoveTransition);
		MoveAnimation anim = new MoveAnimation(duration, animation);
		anim.init(before, after);
		return anim;
	}

	protected ALayoutAnimation createInAnimation(IGLLayoutElement elem, int duration,
			IInTransition animation) {
		if (defaultDuration == 0)
			return new DummyAnimation(EAnimationType.IN);
		return new InAnimation(duration, animation);
	}

	protected ALayoutAnimation createOutAnimation(IGLLayoutElement elem, int duration,
			IOutTransition animation) {
		if (defaultDuration == 0)
			return new DummyAnimation(EAnimationType.OUT);
		return new OutAnimation(duration, animation);
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
		for (GLElement child : this)
			GLElementAccessor.init(child, context);
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
		GLElementAccessor.setParent(child, this);
		if (doInit && context != null)
			GLElementAccessor.init(child, context);
	}

	private void takeDown(GLElement child, boolean checkLayoutAnimations) {
		GLElementAccessor.takeDown(child);
		if (checkLayoutAnimations) {
			layoutAnimations.remove(asLayoutElement(child));
		}
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
		add(children.size(), child, duration, child.getLayoutDataAs(IInTransition.class, defaultInTransition));
	}

	public final void add(int index, GLElement child, int duration, IInTransition animation) {
		if (child.getParent() == this) {
			int from = indexOf(child);
			if (from < index) // as we remove before we insert
				index--;
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
		remove(child, defaultDuration);
	}

	public final void remove(int index) {
		remove(get(index));
	}

	public final void remove(GLElement child, int duration) {
		remove(child, duration, child.getLayoutDataAs(IOutTransition.class, defaultOutTransition));
	}

	public final void remove(GLElement child, int duration, IOutTransition animation) {
		assert child.getParent() == this;
		children.remove(child);
		if (duration <= 0)
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

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		for (GLElement child : activeChildren()) {
			child.render(g);
		}
		if (!layoutAnimations.isEmpty())
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
	protected boolean hasPickAbles() {
		return !children.isEmpty(); // may have pickables
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
			return after != null && !before.equals(after) && !(!areValidBounds(after) && !areValidBounds(before));
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
