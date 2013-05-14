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

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.model.ADataDomainQuery;
import org.caleydo.view.tourguide.internal.model.AScoreRow;
import org.caleydo.view.tourguide.internal.model.CategoricalDataDomainQuery;
import org.caleydo.view.tourguide.internal.model.PathwayDataDomainQuery;
import org.caleydo.view.tourguide.internal.model.StratificationDataDomainQuery;
import org.caleydo.view.tourguide.internal.view.col.DataDomainRankColumnModel;
import org.caleydo.view.tourguide.internal.view.col.IAddToStratomex;
import org.caleydo.view.tourguide.internal.view.col.SizeRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainModeSpecifics {

	static Iterable<? extends ADataDomainQuery> createFor(EDataDomainQueryMode mode, IDataDomain dd) {
		if (DataSupportDefinitions.categoricalTables.apply(dd))
			return Collections.singleton(new CategoricalDataDomainQuery((ATableBasedDataDomain) dd));
		if (dd instanceof PathwayDataDomain) {
			Collection<ADataDomainQuery> r = new ArrayList<ADataDomainQuery>();
			for (EPathwayDatabaseType type : EPathwayDatabaseType.values()) {
				r.add(new PathwayDataDomainQuery((PathwayDataDomain) dd, type));
			}
			return r;
		}
		return Collections.singleton(new StratificationDataDomainQuery((ATableBasedDataDomain) dd));
	}

	/**
	 * @param table
	 */
	public static void addDefaultColumns(EDataDomainQueryMode mode, RankTableModel table, IAddToStratomex add2Stratomex) {

		table.add(new DataDomainRankColumnModel(add2Stratomex).setWidth(80).setCollapsed(true));
		final StringRankColumnModel base = new StringRankColumnModel(GLRenderers.drawText("Stratification"),
				StringRankColumnModel.DEFAULT);
		table.add(base);
		base.setWidth(150);
		base.orderByMe();
		table.add(new SizeRankColumnModel("#Elements", new Function<IRow, Integer>() {
			@Override
			public Integer apply(IRow in) {
				return ((AScoreRow) in).size();
			}
		}).setWidth(75));

		table.add(new SizeRankColumnModel("#Clusters", new Function<IRow, Integer>() {
			@Override
			public Integer apply(IRow in) {
				return ((AScoreRow) in).getGroupSize();
			}
		}).setWidth(75).setCollapsed(true));

	}

	/**
	 * @param mode
	 * @return
	 */
	public static IDataDomainQueryModeSpecfics of(EDataDomainQueryMode mode) {
		switch (mode) {
		case NUMERICAL:
			return new NumericalSpecifics();
		case PATHWAYS:
			return new PathwaySpecifics();
		case STRATIFICATIONS:
			return new StratificationSpecifics();
		}
		throw new IllegalStateException();
	}
}
