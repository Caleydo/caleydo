package org.caleydo.datadomain.pathway.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItem;
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

	private IDMappingManager idMappingManager;

	/**
	 * Constructor
	 */
	public GeneticIDMappingHelper(IDMappingManager idMappingManager) {
		this.idMappingManager = idMappingManager;
	}

	/**
	 * TODO
	 * 
	 * @param idType
	 * @param id
	 * @return a Set of PathwayGraphs or null if no such mapping exists
	 */
	public Set<PathwayGraph> getPathwayGraphsByGeneID(IDType idType, int id) {

		// set to avoid duplicate pathways
		Set<PathwayGraph> newPathways = new HashSet<PathwayGraph>();

		PathwayVertexGraphItem pathwayVertexGraphItem;
		if (idType == IDType.getIDType("DAVID"))
			pathwayVertexGraphItem = PathwayItemManager.get().getPathwayVertexGraphItemByDavidId(id);
		else 
			throw new IllegalStateException("Only David IDs can be resolved to pathways lists");
		
		if (pathwayVertexGraphItem == null)
			return null;

		List<IGraphItem> pathwayItems = pathwayVertexGraphItem
				.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD);

		for (IGraphItem pathwayItem : pathwayItems) {
			PathwayGraph pathwayGraph = (PathwayGraph) pathwayItem.getAllGraphByType(
					EGraphItemHierarchy.GRAPH_PARENT).get(0);
			newPathways.add(pathwayGraph);
		}

		return newPathways;
	}
}
