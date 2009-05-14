package org.caleydo.core.manager.specialized.genetic;

import java.util.ArrayList;
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
	 * Converts a storage index to a refSeq int. Returns -1 if no mapping can be found.
	 * 
	 * @param index
	 *            The index in the storage which should be converted to a refseq
	 * @return the int representation of a refseq, or -1 if no mapping was found
	 */
	public int getRefSeqFromStorageIndex(int index) {
		Integer iRefSeqID = idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, index);

		if (iRefSeqID == null)
			return -1;

		return iRefSeqID;
	}

	/**
	 * Converts a storage index to a david ID. Returns the david ID or -1 if no mapping was found.
	 * 
	 * @param index
	 *            The index in the storage which should be converted to a refSeq
	 * @return the int representation of a david ID, or -1 if no mapping was found
	 */
	public int getDavidIDFromStorageIndex(int index) {
		int refSeq = getRefSeqFromStorageIndex(index);
		Integer david = idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_DAVID, refSeq);
		if (david == null)
			return -1;
		return david;
	}

	public String getShortNameFromExpressionIndex(int index) {
		// Convert expression storage ID to RefSeq
		Integer iDavidID = getDavidIDFromStorageIndex(index);

		if (iDavidID == null)
			return "Unknown Gene";

		String sGeneSymbol = idMappingManager.getID(EMappingType.DAVID_2_GENE_SYMBOL, iDavidID);
		if (sGeneSymbol == "" || sGeneSymbol == null)
			return "Unkonwn Gene";
		else
			return sGeneSymbol;
	}

	public ArrayList<Integer> getExpressionIndicesFromDavid(int davidID) {

		Set<Integer> setRefSeqMRNA = idMappingManager.getMultiID(EMappingType.DAVID_2_REFSEQ_MRNA_INT,
			davidID);
		
		if (setRefSeqMRNA == null)
			return null;
		
		ArrayList<Integer> alExpIndex = new ArrayList<Integer>();
		
		for (Integer refSeqMRNA : setRefSeqMRNA) {
			
			Set<Integer> setExpIndex = idMappingManager.getMultiID(EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX,
				refSeqMRNA);
			
			if (setExpIndex == null) {
				// No expression index available in the current dataset
				continue;
			}
			
			alExpIndex.addAll(setExpIndex);
		}

		if (alExpIndex == null)
			return null;

		return alExpIndex;
	}

	/**
	 * Returns the refSeq String mapped to a storage index, or null if no string was found
	 * 
	 * @param index
	 *            the storage index for which the mapping is requested
	 * @return the String containing the refSeq, or null if no such mapping existed.
	 */
	public String getRefSeqStringFromStorageIndex(int index) {
		int iRefSeqID = getRefSeqFromStorageIndex(index);
		return idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA, iRefSeqID);
	}

	/**
	 * TODO
	 * 
	 * @param idType
	 * @param geneID
	 * @return a Set of PathwayGraphs or null if no such mapping exists
	 */
	public Set<PathwayGraph> getPathwayGraphsByGeneID(EIDType idType, int geneID) {

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
			return null;
		// throw new IllegalStateException("Cannot resolve RefSeq ID to David ID.");

		return GeneralManager.get().getPathwayItemManager().getPathwayVertexGraphItemByDavidId(iDavidID);
	}
}
