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
package org.caleydo.view.tourguide.internal.stratomex.state;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.stratomex.brick.configurer.PathwayDataConfigurer;
import org.caleydo.view.stratomex.tourguide.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.state.ABrowseState;
import org.caleydo.view.tourguide.api.state.ISelectReaction;

/**
 * @author Samuel Gratzl
 *
 */
public class BrowsePathwayState extends ABrowseState {
	protected Perspective underlying;

	public BrowsePathwayState(String label) {
		super(EDataDomainQueryMode.PATHWAYS, label);
	}

	/**
	 * @param underlying
	 *            setter, see {@link underlying}
	 */
	public final void setUnderlying(Perspective underlying) {
		this.underlying = underlying;
	}

	@Override
	public void onUpdate(UpdatePathwayPreviewEvent event, ISelectReaction adapter) {
		if (underlying == null)
			return;
		TablePerspective tablePerspective = asPerspective(underlying, event.getPathway());
		adapter.replaceTemplate(tablePerspective, new PathwayDataConfigurer());
	}

	protected static TablePerspective asPerspective(Perspective record, PathwayGraph pathway) {
		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
				PathwayDataDomain.DATA_DOMAIN_TYPE);

		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) record.getDataDomain();
		Perspective dimension = dataDomain.getTable().getDefaultDimensionPerspective();
		for (PathwayTablePerspective p : pathwayDataDomain.getTablePerspectives()) {
			if (p.getPathway().equals(pathway) && p.getRecordPerspective().equals(record)
					&& p.getDimensionPerspective().equals(dimension))
				return p;
		}
		// not found create new one
		PathwayTablePerspective pathwayDimensionGroup = new PathwayTablePerspective(dataDomain, pathwayDataDomain,
				record, dimension, pathway);

		pathwayDimensionGroup.setPrivate(true);
		pathwayDataDomain.addTablePerspective(pathwayDimensionGroup);

		return pathwayDimensionGroup;
	}
}
