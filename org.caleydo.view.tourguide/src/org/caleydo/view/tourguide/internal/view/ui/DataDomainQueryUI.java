package org.caleydo.view.tourguide.internal.view.ui;

import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLElementSelector;
import org.caleydo.core.view.opengl.layout2.IGLElementVisitor;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.TableDataDomainQuery;

import com.google.common.collect.Iterables;

public class DataDomainQueryUI extends GLElementContainer {

	public DataDomainQueryUI(Iterable<ADataDomainQuery> queries) {
		super(new GLFlowLayout(false, 5, new GLPadding(5)));

		float total = 10;
		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
			if (mode.getNumCategories() > 1) {
				GLElementContainer c =	new GLElementContainer(GLLayouts.flowHorizontal(2));
				GLElementContainer[] cs = new GLElementContainer[mode.getNumCategories()];
				for(int i = 0; i < cs.length; ++i)
					cs[i] = new GLElementContainer(GLLayouts.flowVertical(2));
				for (ADataDomainQuery q : queries) {
					if (mode.isCompatible(q.getDataDomain()))
						cs[mode.getCategory(q.getDataDomain())].add(createFor(q));
				}

				for (int i = 0; i < cs.length; ++i) {
					cs[i].setSize(-1, cs[i].size() * 20);
					c.add(cs[i]);
				}
				c.pack(false, true);
				this.add(c);
				total += c.getSize().y();
			} else {
				GLElementContainer c = new GLElementContainer(GLLayouts.flowVertical(2));
				for (ADataDomainQuery q : queries) {
					if (mode.isCompatible(q.getDataDomain()))
						c.add(createFor(q));
				}
				c.setSize(-1, c.size() * 20);
				this.add(c);
				total += c.getSize().y();
			}
		}
		setSize(-1, total);
	}

	private ADataDomainElement createFor(ADataDomainQuery q) {
		if (q instanceof CategoricalDataDomainQuery)
			return new CategoricalDataDomainElement((CategoricalDataDomainQuery) q);
		if (q instanceof PathwayDataDomainQuery)
			return new PathwayDataDomainElement((PathwayDataDomainQuery) q);
		return new TableDataDomainElement((TableDataDomainQuery) q);
	}

	public void updateSelections() {
		this.accept(new IGLElementVisitor<Void, Void>() {

			@Override
			public Void visit(GLElement elem, Void para) {
				if (elem instanceof ADataDomainElement) {
					((ADataDomainElement) elem).updateSelection();
				}
				return null;
			}

			@Override
			public Void visit(GLElementContainer elem, Void para) {
				for (GLElement child : elem)
					child.accept(this, para);
				return null;
			}

			@Override
			public Void visit(AnimatedGLElementContainer elem, Void para) {
				return null;
			}

			@Override
			public Void visit(AGLElementDecorator elem, Void para) {
				return elem.getContent().accept(this, para);
			}

			@Override
			public Void visit(GLElementSelector elem, Void para) {
				for (GLElement child : elem)
					child.accept(this, para);
				return null;
			}

		}, null);
	}

	/**
	 * @param query
	 */
	public void add(ADataDomainQuery query) {
		// FIXME
		GLElementContainer c = (GLElementContainer) get(query.getMode().ordinal());
		c.add(createFor(query));
		c.setSize(-1, c.size() * 20);
		pack(false, true);
		relayoutParent();
	}

	/**
	 * @param query
	 */
	public void remove(ADataDomainQuery query) {
		// FIXME
		GLElementContainer c = (GLElementContainer) get(query.getMode().ordinal());
		for (ADataDomainElement d : Iterables.filter(c, ADataDomainElement.class)) {
			if (d.getModel() == query) {
				c.remove(d);
				break;
			}
		}
		c.setSize(-1, c.size() * 20);
		pack(false, true);
		relayoutParent();
	}
}