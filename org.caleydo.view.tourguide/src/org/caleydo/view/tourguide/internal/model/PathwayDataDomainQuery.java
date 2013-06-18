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
package org.caleydo.view.tourguide.internal.model;

import java.util.List;

import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.vis.rank.model.RankTableModel;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public class PathwayDataDomainQuery extends ADataDomainQuery {
	private final EPathwayDatabaseType type;

	public PathwayDataDomainQuery(PathwayDataDomain dataDomain, EPathwayDatabaseType type) {
		super(dataDomain);
		this.type = type;
	}

	/**
	 * @return the type, see {@link #type}
	 */
	public EPathwayDatabaseType getType() {
		return type;
	}
	@Override
	public PathwayDataDomain getDataDomain() {
		return (PathwayDataDomain) super.getDataDomain();
	}

	@Override
	public boolean apply(AScoreRow row) {
		return true;
	}

	@Override
	protected List<AScoreRow> getAll() {
		List<AScoreRow> r = Lists.newArrayList();
		for (PathwayGraph per : PathwayManager.get().getAllItems()) {
			if (per.getType() != type)
				continue;
			r.add(new PathwayPerspectiveRow(per));
		}
		return r;
	}

	@Override
	public List<AScoreRow> onDataDomainUpdated() {
		// up to now not way to create new pathways
		return null;
	}

	@Override
	public boolean hasFilter() {
		return false;
	}

	@Override
	public boolean isFilteringPossible() {
		return false;
	}

	@Override
	public void createSpecificColumns(RankTableModel table) {

	}

	@Override
	public void removeSpecificColumns(RankTableModel table) {

	}
}
