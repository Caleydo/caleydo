/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.api.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.base.Labels;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.view.col.DataDomainRankColumnModel;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.vis.lineup.model.RankTableModel;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ATourGuideDataMode implements ITourGuideDataMode {
	/**
	 * @return
	 */
	public Collection<? extends IDataDomain> getAllDataDomains() {
		List<? extends IDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class);
		dataDomains = Lists.newArrayList(Iterables.filter(dataDomains, this));
		Collections.sort(dataDomains, Labels.BY_LABEL);
		return dataDomains;
	}

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQueries() {
		List<ADataDomainQuery> r = new ArrayList<>();
		for (IDataDomain dd : getAllDataDomains()) {
			r.add(createFor(dd));
		}
		return r;
	}

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQuery(IDataDomain dd) {
		return Collections.singleton(createFor(dd));
	}

	protected abstract ADataDomainQuery createFor(IDataDomain dd);

	@Override
	public int getNumCategories() {
		return 1;
	}

	@Override
	public int getCategory(IDataDomain dataDomain) {
		return 0;
	}

	protected static void addDataDomainRankColumn(RankTableModel table) {
		table.add(new DataDomainRankColumnModel().setWidth(80).setCollapsed(true));
	}
}

