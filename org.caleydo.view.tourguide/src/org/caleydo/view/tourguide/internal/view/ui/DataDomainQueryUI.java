package org.caleydo.view.tourguide.internal.view.ui;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.layout.GLFlowLayout;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.DataDomainQueries;
import org.caleydo.view.tourguide.internal.view.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.model.TableDataDomainQuery;

public class DataDomainQueryUI extends GLElementContainer {

	public DataDomainQueryUI(DataDomainQueries model) {
		super(new GLFlowLayout(true, 5, 5));
		for (EDataDomainQueryMode mode : EDataDomainQueryMode.values()) {
			GLElementContainer c = new GLElementContainer(new GLFlowLayout(false, 2, 0));
			for (ADataDomainQuery q : model.getQueries()) {
				if (mode.isCompatible(q.getDataDomain()))
					c.add(createFor(q));
			}
			c.setSize(-1, c.size() * 20);
			this.add(c);
		}
		pack(false, true);
	}

	private GLButton createFor(ADataDomainQuery q) {
		if (q instanceof CategoricalDataDomainQuery)
			return new CategoricalDataDomainElement((CategoricalDataDomainQuery) q);
		if (q instanceof PathwayDataDomainQuery)
			return new PathwayDataDomainElement((PathwayDataDomainQuery) q);
		return new TableDataDomainElement((TableDataDomainQuery) q);
	}
}