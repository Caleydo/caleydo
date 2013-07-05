/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.specific;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.model.AScoreRow;
import org.caleydo.view.tourguide.internal.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.internal.model.PathwayPerspectiveRow;
import org.caleydo.view.tourguide.internal.view.col.SizeRankColumnModel;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.GroupRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class PathwaySpecifics implements IDataDomainQueryModeSpecfics {

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQueries() {
		PathwayDataDomain dd = DataDomainManager.get().getDataDomainsByType(PathwayDataDomain.class).get(0);
		Collection<ADataDomainQuery> r = new ArrayList<ADataDomainQuery>();
		for (EPathwayDatabaseType type : EPathwayDatabaseType.values()) {
			if (PathwayManager.get().hasPathways(type))
				r.add(new PathwayDataDomainQuery(dd, type));
		}
		return r;
	}

	@Override
	public void addDefaultColumns(RankTableModel table) {
		final StringRankColumnModel base = new StringRankColumnModel(GLRenderers.drawText("Pathway"),
				StringRankColumnModel.DEFAULT);
		table.add(base);
		base.setWidth(150);
		base.orderByMe();

		Map<EPathwayDatabaseType, String> metaData = new EnumMap<>(EPathwayDatabaseType.class);
		for(EPathwayDatabaseType type : EPathwayDatabaseType.values()) {
			metaData.put(type, type.getName());
		}

		GroupRankColumnModel group = new GroupRankColumnModel("Metrics", Color.GRAY, new Color(0.95f, .95f, .95f));
		table.add(group);
		group.add(new CategoricalRankColumnModel<>(GLRenderers.drawText("Database"),
				new Function<IRow, EPathwayDatabaseType>() {
			@Override
			public EPathwayDatabaseType apply(IRow in) {
				PathwayPerspectiveRow r = (PathwayPerspectiveRow)in;
				return r.getType();
			}
				}, metaData));

		// table.add(new StringRankColumnModel(GLRenderers.drawText("Description"), new Function<IRow, String>() {
		// @Override
		// public String apply(IRow in) {
		// PathwayPerspectiveRow r = (PathwayPerspectiveRow) in;
		// return r.getPathway().getTitle();
		// }
		// }).setCollapsed(true));

		group.add(new SizeRankColumnModel("#Genes", new Function<IRow, Integer>() {
			@Override
			public Integer apply(IRow in) {
				int s = ((AScoreRow) in).size();

				return s;
			}
		}).setWidth(75));


	}

	@Override
	public Iterable<? extends ADataDomainQuery> createDataDomainQuery(IDataDomain dd) {
		if (dd instanceof PathwayDataDomain)
			return createDataDomainQueries();
		else
			return Collections.emptyList();
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
