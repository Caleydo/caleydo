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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * <p>
 * Item for showing all pathways that contain a specific gene in a sub menu,
 * where these pathways can be loaded individually. The sub-pathways can either
 * be specified manually or the convenience method
 * {@link ShowPathwaysByGenesItem#setRefSeqInt(int)} can be used, which creates
 * the sub-menus automatically.
 * </p>
 * <p>
 * Text and icon have default values but can be overriden.
 * </p>
 * 
 * @author Alexander Lex
 */
public class ShowPathwaysByGenesItem extends AContextMenuItem {
	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ShowPathwaysByGenesItem() {
		super();
		// setIconTexture(EIconTextures.CM_DEPENDING_PATHWAYS);
		setLabel("Pathways");
	}

	/**
	 * Convenience method that automatically creates a
	 * {@link LoadPathwaysByGeneEvent} based on a david ID
	 * 
	 * @param david
	 *            the int code associated with a refseq
	 */
	public void setTableIDs(ATableBasedDataDomain dataDomain, IDType idType,
			List<Integer> genes) {

		HashMap<PathwayGraph, Integer> hashPathwaysToOccurences = PathwayManager.get()
				.getPathwayGraphsWithOccurencesByGeneIDs((GeneticDataDomain) dataDomain,
						idType, genes);

		ArrayList<Pair<Integer, PathwayGraph>> pathways = new ArrayList<Pair<Integer, PathwayGraph>>();
		for (PathwayGraph pathway : hashPathwaysToOccurences.keySet()) {
			pathways.add(new Pair<Integer, PathwayGraph>(hashPathwaysToOccurences
					.get(pathway), pathway));
		}

		Collections.sort(pathways);
		int pathwayCount = 0;

		for (int count = pathways.size() - 1; count >= 0; count--) {
			Pair<Integer, PathwayGraph> pair = pathways.get(count);
			if (pair.getFirst() > 1) {
				LoadPathwaysByPathwayItem item = new LoadPathwaysByPathwayItem(
						pair.getSecond(), dataDomain.getDataDomainID(), pair.getFirst());
				addSubItem(item);

				pathwayCount++;
			}
		}

		setLabel("Load Pathways (" + pathwayCount + ")");
	}
}
