package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterators;

/**
 * a simple container that implements a selector behavior, i.e. only one child will be rendered, e.g. for implementing
 * level of detail
 * 
 * @author Samuel Gratzl
 * 
 */
public abstract class GLElementSelector extends GLElement implements IGLElementParent, Iterable<GLElement> {
	private final List<GLElement> children = new ArrayList<>(3);

	public GLElementSelector() {

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
		Vec2f size = getSize();
		for (GLElement elem : this) {
			elem.layoutElement.setBounds(0, 0, size.x(), size.y());
		}
	}

	public final GLElement getSelected(float w, float h) {
		int index = select(w, h);
		if (index < 0)
			return null;
		if (index >= children.size())
			index = children.size() - 1;
		return children.get(index);
	}

	/**
	 * returns the index of the selected gl element child to render
	 * @param w
	 * @param h
	 * @return or null if no
	 */
	protected abstract int select(float w, float h);

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
		child.setParent(this);
		if (context != null)
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
				GLElementSelector.this.add(index, element);
			}

			@Override
			public GLElement set(int index, GLElement element) {
				return GLElementSelector.this.set(index, element);
			}

			@Override
			public GLElement remove(int index) {
				return GLElementSelector.this.remove(index);
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
		GLElement selected = getSelected(w, h);
		if (selected != null)
			selected.render(g);
	}
	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		GLElement selected = getSelected(w, h);
		if (selected != null)
			selected.renderPick(g);
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







