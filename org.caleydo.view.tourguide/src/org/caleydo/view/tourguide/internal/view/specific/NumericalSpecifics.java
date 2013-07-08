/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.specific;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.model.InhomogenousDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.col.DataDomainRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class NumericalSpecifics implements IDataDomainQueryModeSpecfics {

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQueries() {
		List<ADataDomainQuery> r = new ArrayList<>();
		for (IDataDomain dd : EDataDomainQueryMode.OTHER.getAllDataDomains()) {
			r.add(createFor(dd));
		}
		return r;
	}

	@Override
	public void addDefaultColumns(RankTableModel table) {
		table.add(new DataDomainRankColumnModel().setWidth(80).setCollapsed(true));
		final StringRankColumnModel base = new StringRankColumnModel(GLRenderers.drawText("Name"),
				StringRankColumnModel.DEFAULT);
		table.add(base);
		base.setWidth(150);
		base.orderByMe();
	}

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQuery(IDataDomain dd) {
		return Collections.singleton(createFor(dd));
	}

	private static ADataDomainQuery createFor(IDataDomain dd) {
		return new InhomogenousDataDomainQuery((ATableBasedDataDomain) dd, EDataClass.NATURAL_NUMBER);
	}

	/**
	 * datadomains can be categorized in multiple categories
	 *
	 * @return
	 */
	@Override
	public int getNumCategories() {
		return 1;
	}

	@Override
	public int getCategory(IDataDomain dataDomain) {
		return 0;
	}
}
