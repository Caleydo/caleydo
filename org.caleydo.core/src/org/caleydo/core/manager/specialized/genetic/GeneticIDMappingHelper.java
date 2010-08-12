package org.caleydo.core.manager.specialized.genetic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;

/**
 * TODO: Document
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GeneticIDMappingHelper {
	private static GeneticIDMappingHelper idMappingHelper;

	private IIDMappingManager idMappingManager;

	/**
	 * Constructor
	 */
	private GeneticIDMappingHelper() {
		idMappingManager = GeneralManager.get().getIDMappingManager();
	}

	public static GeneticIDMappingHelper get() {
		if (idMappingHelper == null) {
			idMappingHelper = new GeneticIDMappingHelper();
		}

		return idMappingHelper;
	}

	/**
	 * TODO
	 * 
	 * @param idType
	 * @param geneID
	 * @return a Set of PathwayGraphs or null if no such mapping exists
	 */
	public Set<PathwayGraph> getPathwayGraphsByGeneID(IDType idType, int geneID) {

		// set to avoid duplicate pathways
		Set<PathwayGraph> newPathways = new HashSet<PathwayGraph>();

		PathwayVertexGraphItem pathwayVertexGraphItem = convertGeneIDToPathwayVertex(idType, geneID);
		if (pathwayVertexGraphItem == null)
			return null;

		List<IGraphItem> pathwayItems =
			pathwayVertexGraphItem.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD);

		for (IGraphItem pathwayItem : pathwayItems) {
			PathwayGraph pathwayGraph =
				(PathwayGraph) pathwayItem.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).get(0);
			newPathways.add(pathwayGraph);
		}

		return newPathways;
	}

	/**
	 * TODO: Marc document
	 * 
	 * @param idType
	 * @param geneID
	 * @return the PathwayVertexGraphItem corresponding to the mapping or null if no such mapping exists
	 */
	public PathwayVertexGraphItem convertGeneIDToPathwayVertex(IDType idType, int geneID) {

		// int iGraphItemID = 0;
		Integer iDavidID = -1;

		if (idType == IDType.getIDType("REF_SEQ_MRNA_INT")) {
			iDavidID = idMappingManager.getID(IDType.getIDType("REF_SEQ_MRNA_INT"), IDType.getIDType("DAVID"), geneID);
		}
		else if (idType == IDType.getIDType("DAVID")) {
			iDavidID = geneID;
		}
		else
			return null;

		if (iDavidID == null || iDavidID == -1)
			return null;
		// throw new IllegalStateException("Cannot resolve RefSeq ID to David ID.");

		return GeneralManager.get().getPathwayItemManager().getPathwayVertexGraphItemByDavidId(iDavidID);
	}
}
