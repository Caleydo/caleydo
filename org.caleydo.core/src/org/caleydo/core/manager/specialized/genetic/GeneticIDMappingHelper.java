package org.caleydo.core.manager.specialized.genetic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.graph.pathway.item.vertex.PathwayVertexGraphItem;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;

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

	public int getDavidIDFromStorageIndex(int index) {
		Integer iRefSeqID = idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, index);

		if (iRefSeqID == null)
			return -1;

		Integer iDavidId = idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID, iRefSeqID);

		if (iDavidId == null)
			return -1;

		return iDavidId;
	}

	public int getRefSeqFromStorageIndex(int index) {
		Integer iRefSeqID = idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, index);

		if (iRefSeqID == null)
			return -1;

		return iRefSeqID;
	}

	public String getShortNameFromDavid(int index) {
		// Convert expression storage ID to RefSeq
		Integer iDavidID = getDavidIDFromStorageIndex(index);

		if (iDavidID == null)
			return "Unknown Gene";

		String sGeneSymbol = idMappingManager.getID(EMappingType.DAVID_2_GENE_SYMBOL, iDavidID);
		if (sGeneSymbol == "")
			return "Unkonwn Gene";
		else
			return sGeneSymbol;
	}

	public String getRefSeqStringFromStorageIndex(int iIndex) {
		int iRefSeqID = getRefSeqFromStorageIndex(iIndex);
		return idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA, iRefSeqID);
	}
	
	public Set<PathwayGraph> getPathwayGraphsByGeneID(EIDType idType, int geneID) {
		
		// set to avoid duplicate pathways
		Set<PathwayGraph> newPathways = new HashSet<PathwayGraph>();

		PathwayVertexGraphItem pathwayVertexGraphItem = convertGeneIDToPathwayVertex(idType, geneID);
		
		List<IGraphItem> pathwayItems =
			pathwayVertexGraphItem.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD);
		
		for (IGraphItem pathwayItem : pathwayItems) {
			PathwayGraph pathwayGraph =
				(PathwayGraph) pathwayItem.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).get(0);
			newPathways.add(pathwayGraph);
		}
		
		return newPathways;
	}
	
	public PathwayVertexGraphItem convertGeneIDToPathwayVertex(EIDType idType, int geneID) {

		int iGraphItemID = 0;
		Integer iDavidID = -1;

		if (idType == EIDType.REFSEQ_MRNA_INT) {
			iDavidID =
				GeneralManager.get().getIDMappingManager()
					.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID, geneID);
		}
		else if (idType == EIDType.DAVID) {
			iDavidID = geneID;
		}
		else
			return null;

		if (iDavidID == null || iDavidID == -1)
			throw new IllegalStateException("Cannot resolve RefSeq ID to David ID.");

		iGraphItemID =
			GeneralManager.get().getPathwayItemManager().getPathwayVertexGraphItemIdByDavidId(iDavidID);

		if (iGraphItemID == -1) {
			return null;
		}

		return (PathwayVertexGraphItem) GeneralManager.get().getPathwayItemManager().getItem(iGraphItemID);
	}
}
