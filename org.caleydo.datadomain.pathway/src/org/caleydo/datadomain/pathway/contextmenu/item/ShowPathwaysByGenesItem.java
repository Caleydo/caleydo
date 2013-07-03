/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.contextmenu.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.listener.LoadPathwaysByGeneEvent;
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

		Collections.sort(pathways, Pair.<Integer> compareFirst());
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
