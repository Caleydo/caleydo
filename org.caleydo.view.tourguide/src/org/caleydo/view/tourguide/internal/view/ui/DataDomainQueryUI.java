package org.caleydo.view.tourguide.internal.view.ui;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
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
		super(new GLFlowLayout(true, 5, new GLPadding(5)));
		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
			GLElementContainer c = new GLElementContainer(GLLayouts.flowVertical(2));
			for (ADataDomainQuery q : queries) {
				if (mode.isCompatible(q.getDataDomain()))
					c.add(createFor(q));
			}
			c.setSize(-1, c.size() * 20);
			this.add(c);
		}
		pack(false, true);
	}

	private ADataDomainElement createFor(ADataDomainQuery q) {
		if (q instanceof CategoricalDataDomainQuery)
			return new CategoricalDataDomainElement((CategoricalDataDomainQuery) q);
		if (q instanceof PathwayDataDomainQuery)
			return new PathwayDataDomainElement((PathwayDataDomainQuery) q);
		return new TableDataDomainElement((TableDataDomainQuery) q);
	}

	public void updateSelections() {
		for (GLElementContainer elem : Iterables.filter(this, GLElementContainer.class)) {
			for (ADataDomainElement c : Iterables.filter(elem, ADataDomainElement.class)) {
				c.updateSelection();
			}
		}
	}

	/**
	 * @param query
	 */
	public void add(ADataDomainQuery query) {
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