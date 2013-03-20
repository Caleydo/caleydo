package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

import com.google.common.collect.Iterators;

/**
 * an element that contain other elements which are layouted by a {@link IGLLayout}, default is that the children won't be
 * layouted
 *
 * @author Samuel Gratzl
 *
 */
public class GLElementContainer extends GLElement implements IGLElementParent, Iterable<GLElement> {
	private IGLLayout layout = GLLayouts.NONE;
	private final List<GLElement> children = new ArrayList<>(3);

	public GLElementContainer() {

	}

	public GLElementContainer(IGLLayout layout) {
		this.layout = layout;
	}

	/**
	 * @param layout
	 *            setter, see {@link layout}
	 */
	public final GLElementContainer setLayout(IGLLayout layout) {
		if (layout == this.layout)
			return this;
		this.layout = layout;
		relayout();
		return this;
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);
		for (GLElement child : this)
			child.layout(deltaTimeMs);
	}

	@Override
	protected final void layoutImpl() {
		super.layoutImpl();
		List<IGLLayoutElement> l = asLayoutElements();
		Vec2f size = getSize();
		layout.doLayout(l, size.x(), size.y());
	}

	private List<IGLLayoutElement> asLayoutElements() {
		List<IGLLayoutElement> l = new ArrayList<>(children.size());
		for (GLElement child : children)
			if (child.getVisibility() != EVisibility.NONE)
				l.add(child.layoutElement);
		return Collections.unmodifiableList(l);
	}

	/**
	 * packs the width and or height of this element by the maximal value of its children
	 *
	 * @param packWidth
	 *            perform packing in x direction
	 * @param packHeight
	 *            perform packing in y direction
	 * @return
	 */
	public final GLElement pack(boolean packWidth, boolean packHeight) {
		if (!packWidth && !packHeight)
			return this;
		// this.layout.pack(asLayoutElement(), this, width, height);
		float w = -1;
		float h = -1;
		for (IGLLayoutElement child : asLayoutElements()) {
			float cw = child.getSetWidth();
			if (!Float.isNaN(cw) && cw > 0)
				w = Math.max(w, cw);
			float ch = child.getSetHeight();
			if (!Float.isNaN(ch) && ch > 0)
				h = Math.max(h, ch);
		}
		Vec2f ori = this.getSize();
		if (w < 0 || !packWidth)
			w = ori.x();
		if (h < 0 || !packHeight)
			h = ori.y();
		setSize(w, h);
		return this;
	}

	@Override
	protected void takeDown() {
		for (GLElement elem : this)
			elem.takeDown();
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

	public final void clear() {
		int size = this.size();
		for (Iterator<GLElement> it = children.iterator(); it.hasNext();) {
			GLElement e = it.next();
			e.takeDown();
			it.remove();
		}
		if (size > 0) // had deleted something
			relayout();
	}

	public final GLElement get(int index) {
		return children.get(index);
	}

	public final boolean add(GLElement child) {
		setup(child);
		boolean r = children.add(child);
		relayout();
		return r;
	}

	public final void add(GLElement child, Object layout) {
		add(child.setLayoutData(layout));
	}

	public final void add(int index, GLElement child) {
		if (child.getParent() == this) {
			int from = indexOf(child);
			if (from < index) // as we remove before we insert
				index--;
		}
		setup(child);
		children.add(index, child);
		relayout();
	}

	public final int indexOf(GLElement child) {
		return children.indexOf(child);
	}

	public final boolean remove(GLElement child) {
		if (children.remove(child)) {
			child.takeDown();
			relayout();
			return true;
		}
		return false;
	}

	public final int size() {
		return children.size();
	}

	public final boolean isEmpty() {
		return children.isEmpty();
	}

	public final void sortBy(Comparator<GLElement> comparator) {
		Collections.sort(children, comparator);
		relayout();
	}

	@Override
	public final boolean moved(GLElement child) {
		children.remove(child);
		relayout();
		return context != null;
	}

	public final GLElement remove(int index) {
		GLElement e = children.get(index);
		remove(e);
		return e;
	}

	public final GLElement set(int index, GLElement element) {
		GLElement old = children.get(index);
		old.takeDown();
		setup(element);
		children.set(index, element);
		relayout();
		return old;
	}

	public final List<GLElement> asList() {
		return new AbstractList<GLElement>() {

			@Override
			public GLElement get(int index) {
				return children.get(index);
			}

			@Override
			public void add(int index, GLElement element) {
				GLElementContainer.this.add(index, element);
			}

			@Override
			public GLElement set(int index, GLElement element) {
				return GLElementContainer.this.set(index, element);
			}

			@Override
			public GLElement remove(int index) {
				return GLElementContainer.this.remove(index);
			}

			@Override
			public int size() {
				return children.size();
			}

		};
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		g.incZ();
		for(GLElement child : children)
			child.render(g);
		g.decZ();
	}
	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		g.incZ();
		for (GLElement child : children)
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
}







