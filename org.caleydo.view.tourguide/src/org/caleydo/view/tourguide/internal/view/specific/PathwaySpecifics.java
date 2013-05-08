/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.view.specific;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.model.AScoreRow;
import org.caleydo.view.tourguide.internal.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.internal.model.PathwayPerspectiveRow;
import org.caleydo.view.tourguide.internal.view.col.IAddToStratomex;
import org.caleydo.view.tourguide.internal.view.col.SizeRankColumnModel;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
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
			r.add(new PathwayDataDomainQuery(dd, type));
		}
		return r;
	}

	@Override
	public void addDefaultColumns(RankTableModel table, IAddToStratomex add2Stratomex) {
		final StringRankColumnModel base = new StringRankColumnModel(GLRenderers.drawText("Pathway"),
				StringRankColumnModel.DEFAULT);
		table.add(base);
		base.setWidth(150);
		base.orderByMe();

		Map<EPathwayDatabaseType, String> metaData = new EnumMap<>(EPathwayDatabaseType.class);
		for(EPathwayDatabaseType type : EPathwayDatabaseType.values()) {
			metaData.put(type, type.getName());
		}

		table.add(new CategoricalRankColumnModel<>(GLRenderers.drawText("Database"), new Function<IRow,EPathwayDatabaseType>() {
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

		table.add(new SizeRankColumnModel("#Genes", new Function<IRow, Integer>() {
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
}
