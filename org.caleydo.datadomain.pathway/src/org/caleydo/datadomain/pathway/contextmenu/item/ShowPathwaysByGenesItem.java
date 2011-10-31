package org.caleydo.datadomain.pathway.contextmenu.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

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

	private PathwayDataDomain pathwayDataDomain;

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ShowPathwaysByGenesItem(PathwayDataDomain pathwayDataDomain) {
		super();
		this.pathwayDataDomain = pathwayDataDomain;
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
			ArrayList<Integer> genes) {

		HashMap<PathwayGraph, Integer> hashPathwaysToOccurences = new HashMap<PathwayGraph, Integer>();
		for (Integer gene : genes) {
			Set<Integer> davids = ((GeneticDataDomain) dataDomain)
					.getGeneIDMappingManager().getIDAsSet(idType,
							dataDomain.getPrimaryRecordMappingType(), gene);
			if (davids == null || davids.size() == 0)
				continue;
			for (Integer david : davids) {
				Set<PathwayGraph> pathwayGraphs = pathwayDataDomain.getMappingHelper()
						.getPathwayGraphsByGeneID(
								dataDomain.getPrimaryRecordMappingType(), david);

				// int iPathwayCount = 0;
				if (pathwayGraphs != null) {
					// iPathwayCount = pathwayGraphs.size();

					for (PathwayGraph pathwayGraph : pathwayGraphs) {

						if (!hashPathwaysToOccurences.containsKey(pathwayGraph))
							hashPathwaysToOccurences.put(pathwayGraph, 1);
						else {
							int occurences = hashPathwaysToOccurences.get(pathwayGraph);
							occurences++;
							hashPathwaysToOccurences.put(pathwayGraph, occurences);
						}

					}
				}
			}
		}

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
