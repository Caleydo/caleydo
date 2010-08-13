package org.caleydo.datadomain.genetic.contextmenu.item;

import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * <p>
 * Item for showing all pathways that contain a specific gene in a sub menu, where these pathways can be
 * loaded individually. The sub-pathways can either be specified manually or the convenience method
 * {@link ShowPathwaysByGenesItem#setRefSeqInt(int)} can be used, which creates the sub-menus automatically.
 * </p>
 * <p>
 * Text and icon have default values but can be overriden.
 * </p>
 * 
 * @author Alexander Lex
 */
public class ShowPathwaysByGenesItem
	extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ShowPathwaysByGenesItem() {
		super();
		setIconTexture(EIconTextures.CM_DEPENDING_PATHWAYS);
		setText("Pathways");
	}

	/**
	 * Convenience method that automatically creates a {@link LoadPathwaysByGeneEvent} based on a david ID
	 * 
	 * @param david
	 *            the int code associated with a refseq
	 */
//	public void setIDs(IDType idType, ArrayList<Integer> genes) {
//
//		HashMap<PathwayGraph, Integer> hashPathwaysToOccurences = new HashMap<PathwayGraph, Integer>();
//		for (int gene : genes) {
//			int david = GeneralManager.get().getIDMappingManager().getID(idType, GeneticDataDomain.centralIDType, gene);
//
//			Set<PathwayGraph> pathwayGraphs =
//				GeneticIDMappingHelper.get().getPathwayGraphsByGeneID(GeneticDataDomain.centralIDType, david);
//
//			// int iPathwayCount = 0;
//
//			if (pathwayGraphs != null) {
//
//				// iPathwayCount = pathwayGraphs.size();
//
//				for (PathwayGraph pathwayGraph : pathwayGraphs) {
//
//					if (!hashPathwaysToOccurences.containsKey(pathwayGraph))
//						hashPathwaysToOccurences.put(pathwayGraph, 1);
//					else {
//						int occurences = hashPathwaysToOccurences.get(pathwayGraph);
//						occurences++;
//						hashPathwaysToOccurences.put(pathwayGraph, occurences);
//					}
//
//				}
//			}
//		}
//
//		ArrayList<Pair<Integer, PathwayGraph>> pathways = new ArrayList<Pair<Integer, PathwayGraph>>();
//		for (PathwayGraph pathway : hashPathwaysToOccurences.keySet()) {
//			pathways.add(new Pair<Integer, PathwayGraph>(hashPathwaysToOccurences.get(pathway), pathway));
//		}
//		Collections.sort(pathways);
//		for (int count = pathways.size() - 1; count >= 0; count--) {
//			Pair<Integer, PathwayGraph> pair = pathways.get(count);
//			if (pair.getFirst() > 1) {
//				LoadPathwaysByPathwayIDItem item =
//					new LoadPathwaysByPathwayIDItem(pair.getSecond().getID(), pair.getFirst());
//				addSubItem(item);
//			}
//		}
//		// setText("Pathways (" + iPathwayCount + ")");
//	}

}
