/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.adapter;

import java.util.Collections;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.api.adapter.ATourGuideDataMode;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.api.model.InhomogenousDataDomainQuery;
import org.caleydo.view.tourguide.api.model.StratificationDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.col.GroupDistributionRankColumnModel;
import org.caleydo.view.tourguide.internal.view.col.SizeRankColumnModel;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.vis.lineup.model.GroupRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StringRankColumnModel;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class StratificationDataMode extends ATourGuideDataMode implements ITourGuideDataMode {

	@Override
	public boolean apply(IDataDomain dataDomain) {
		if (!(dataDomain instanceof ATableBasedDataDomain))
			return false;
		if (!((ATableBasedDataDomain) dataDomain).getTable().isDataHomogeneous())
			return InhomogenousDataDomainQuery.hasOne(dataDomain, Sets.immutableEnumSet(EDataClass.CATEGORICAL));
		return true;
	}

	@Override
	public int getNumCategories() {
		return 2;
	}

	@Override
	public int getCategory(IDataDomain dataDomain) {
		if (DataDomainOracle.isCategoricalDataDomain(dataDomain)
				|| !DataSupportDefinitions.homogenousTables.apply(dataDomain))
			return 1;
		return 0;
	}

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQuery(IDataDomain dd) {
		return Collections.singleton(createFor(dd));
	}
	protected ADataDomainQuery createFor(IDataDomain dd) {
		if (!DataSupportDefinitions.homogenousTables.apply(dd))
			return new InhomogenousDataDomainQuery((ATableBasedDataDomain) dd,
					Sets.immutableEnumSet(EDataClass.CATEGORICAL));
		if (DataSupportDefinitions.categoricalTables.apply(dd))
			return new CategoricalDataDomainQuery((ATableBasedDataDomain) dd, EDimension.DIMENSION);
		return new StratificationDataDomainQuery((ATableBasedDataDomain) dd, EDimension.RECORD);
	}

	/**
	 * @param table
	 */
	@Override
	public void addDefaultColumns(RankTableModel table) {
		addDataDomainRankColumn(table);
		final StringRankColumnModel base = new StringRankColumnModel(GLRenderers.drawText("Stratification"),
				StringRankColumnModel.DEFAULT);
		table.add(base);
		base.setWidth(150);
		base.orderByMe();

		GroupRankColumnModel group = new GroupRankColumnModel("Metrics", Color.GRAY, new Color(0.95f, .95f, .95f));
		table.add(group);
		group.add(new SizeRankColumnModel("#Elements", new Function<IRow, Integer>() {
			@Override
			public Integer apply(IRow in) {
				return ((AScoreRow) in).size();
			}
		}).setWidth(75));

		group.add(new SizeRankColumnModel("#Groups", new Function<IRow, Integer>() {
			@Override
			public Integer apply(IRow in) {
				return ((AScoreRow) in).getGroupSize();
			}
		}).setWidth(75).setCollapsed(false));

		group.add(new GroupDistributionRankColumnModel());
	}
}

