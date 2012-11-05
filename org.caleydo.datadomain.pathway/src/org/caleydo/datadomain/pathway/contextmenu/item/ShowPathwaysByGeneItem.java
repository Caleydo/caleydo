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
package org.caleydo.datadomain.pathway.contextmenu.item;

import java.util.Arrays;
import java.util.Set;
import org.caleydo.core.event.view.pathway.LoadPathwaysByGeneEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * <p>
 * Item for showing all pathways that contain a specific gene in a sub menu,
 * where these pathways can be loaded individually. The sub-pathways can either
 * be specified manually or the convenience method
 * {@link ShowPathwaysByGeneItem#setRefSeqInt(int)} can be used, which creates
 * the sub-menus automatically.
 * </p>
 * <p>
 * Text and icon have default values but can be overriden.
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ShowPathwaysByGeneItem extends AContextMenuItem {

	private PathwayDataDomain dataDomain;

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ShowPathwaysByGeneItem(PathwayDataDomain dataDomain) {
		super();
		this.dataDomain = dataDomain;
		// setIconTexture(EIconTextures.CM_DEPENDING_PATHWAYS);
		setLabel("Load Pathways");
	}

	/**
	 * Convenience method that automatically creates a
	 * {@link LoadPathwaysByGeneEvent} based on a david ID
	 * 
	 */
	public void setDavidID(IDType idType, int david, String dataDomainID) {

		Set<PathwayGraph> pathwayGraphs = PathwayManager.get().getPathwayGraphsByGeneID(
				idType, david);

		int pathwayCount = 0;

		if (pathwayGraphs != null) {

			pathwayCount = pathwayGraphs.size();

			PathwayGraph[] pathways = new PathwayGraph[pathwayGraphs.size()];
			pathwayGraphs.toArray(pathways);
			Arrays.sort(pathways);

			for (PathwayGraph pathwayGraph : pathways) {
				addSubItem(new LoadPathwaysByPathwayItem(pathwayGraph, dataDomainID));
			}
		}

		setLabel("Load Pathways (" + pathwayCount + ")");
	}
}
