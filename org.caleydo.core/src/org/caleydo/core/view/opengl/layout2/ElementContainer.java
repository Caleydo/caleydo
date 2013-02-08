package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.layout.ILayout;
import org.caleydo.core.view.opengl.layout2.layout.ILayoutElement;
import org.caleydo.core.view.opengl.layout2.layout.Layouts;

import com.google.common.collect.Iterators;

/**
 * an element that contain other elements which are layouted by a {@link ILayout}, default is that the children won't be
 * layouted
 *
 * @author Samuel Gratzl
 *
 */
public class ElementContainer extends Element implements IElementParent, Iterable<Element> {
	private ILayout layout = Layouts.NONE;
	private final List<Element> children = new ArrayList<>(3);

	public ElementContainer() {

	}

	public ElementContainer(ILayout layout) {
		this.layout = layout;
	}

	@Override
	public void relayout() {
		super.relayout();
	}

	@Override
	public void repaint() {
		super.repaint();
	}

	@Override
	public void repaintPick() {
		super.repaintPick();
	}

	/**
	 * @param layout
	 *            setter, see {@link layout}
	 */
	public final ElementContainer setLayout(ILayout layout) {
		if (layout == this.layout)
			return this;
		this.layout = layout;
		relayout();
		return this;
	}

	@Override
	protected final void layout() {
		super.layout();
		List<ILayoutElement> l = asLayoutElements();
		Vec2f size = getSize();
		if (layout.doLayout(l, size.x(), size.y()))
			relayout();
	}

	private List<ILayoutElement> asLayoutElements() {
		List<ILayoutElement> l = new ArrayList<>(children.size());
		for (Element child : children)
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
	public final Element pack(boolean packWidth, boolean packHeight) {
		if (!packWidth && !packHeight)
			return this;
		// this.layout.pack(asLayoutElement(), this, width, height);
		float w = -1;
		float h = -1;
		for (ILayoutElement child : asLayoutElements()) {
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
		for (Element elem : this)
			takeDown(elem);
		super.takeDown();
	}

	@Override
	protected void init(IElementContext context) {
		super.init(context);
		for (Element child : this)
			child.init(context);
	}

	private void setup(Element child) {
		IElementParent ex = child.getParent();
		assert ex != this;
		if (ex != null) {
			ex.moved(child);
		}
		child.setParent(this);
		if (context != null)
			child.init(context);
	}

	private void takeDown(Element child) {
		child.takeDown();
	}

	public final void clear() {
		for (Iterator<Element> it = this.iterator(); it.hasNext();) {
			Element e = it.next();
			takeDown(e);
			it.remove();
		}
		relayout();
	}

	public final Element get(int index) {
		return children.get(index);
	}

	public final boolean add(Element child) {
		setup(child);
		boolean r = children.add(child);
		relayout();
		return r;
	}

	public final void add(Element child, Object layout) {
		add(child.setLayoutData(layout));
	}

	public final void add(int index, Element child) {
		setup(child);
		children.add(child);
		relayout();
	}

	public final void remove(Element child) {
		if (children.remove(child)) {
			takeDown(child);
			relayout();
		}
	}

	@Override
	public final void moved(Element child) {
		children.remove(child);
		relayout();
	}

	public final Element remove(int index) {
		Element e = children.get(index);
		remove(e);
		return e;
	}

	public final Element set(int index, Element element) {
		Element old = children.get(index);
		takeDown(old);
		setup(element);
		children.set(index, element);
		relayout();
		return old;
	}

	public final List<Element> asList() {
		return new AbstractList<Element>() {

			@Override
			public Element get(int index) {
				return children.get(index);
			}

			@Override
			public void add(int index, Element element) {
				ElementContainer.this.add(index, element);
			}

			@Override
			public Element set(int index, Element element) {
				return ElementContainer.this.set(index, element);
			}

			@Override
			public Element remove(int index) {
				return ElementContainer.this.remove(index);
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
		for(Element child : children)
			renderChild(child, g);
		g.decZ();
	}
	protected void renderChild(Element child, GLGraphics g) {
		child.render(g);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
		g.incZ();
		for (Element child : children)
			renderPickChild(child, g);
		g.decZ();
	}

	protected void renderPickChild(Element child, GLGraphics g) {
		child.renderPick(g);
	}

	@Override
	public final Iterator<Element> iterator() {
		return Iterators.unmodifiableIterator(children.iterator());
	}
}







