package org.caleydo.core.data.virtualarray.similarity;

import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;

/**
 * A map containing similarities from one specific tableID / recordVA pair to all other pairs.
 * 
 * @author Alexander Lex
 */
public class SimilarityMap {

	/**
	 * Get the VASimilarity between this SimilarityMap's VA and the VA associated with the perspectiveID.
	 * 
	 * @param foreignPerspectiveID
	 *            the perspectiveID associated with the VA that should be compared to the VA in this
	 *            SimilarityMap
	 * @return
	 */
	public VASimilarity<RecordVirtualArray, RecordGroupList> getVASimilarity(String foreignPerspectiveID) {
		return similarityMap.get(foreignPerspectiveID);
	}

	// -------------------- END OF PUBLIC INTERFACE ----------------------------------

	HashMap<String, VASimilarity<RecordVirtualArray, RecordGroupList>> similarityMap =
		new HashMap<String, VASimilarity<RecordVirtualArray, RecordGroupList>>(20);

	private String tableID;
	private RecordVirtualArray recordVA;

	/**
	 * Constructor with the key pair tableID and recordVA
	 */
	SimilarityMap(String tableID, RecordVirtualArray recordVA) {
		this.tableID = tableID;
		this.recordVA = recordVA;
	}

	/**
	 * Sets a va that the key va of this object is to be compared to and calculates the similarity.
	 * 
	 * @param comparedSetID
	 * @param comparedRecordVA
	 */
	VASimilarity<RecordVirtualArray, RecordGroupList> calculateVASimilarity(String comparedSetID,
		RecordVirtualArray comparedRecordVA) {
		VASimilarity<RecordVirtualArray, RecordGroupList> vaSimilarity;

		vaSimilarity = new VASimilarity<RecordVirtualArray, RecordGroupList>();
		vaSimilarity.addVA(tableID, recordVA);

		vaSimilarity.addVA(comparedSetID, comparedRecordVA);
		vaSimilarity.calculateSimilarities();
		similarityMap.put(comparedSetID, vaSimilarity);
		return vaSimilarity;
	}

	/**
	 * Set a pre-existing similarity between this objects key va and another.
	 * 
	 * @param vaSimilarity
	 */
	void setVaSimilarity(VASimilarity<RecordVirtualArray, RecordGroupList> vaSimilarity) {
		Set<String> keys = vaSimilarity.getPerspectiveIDs();
		boolean ownKeyContained = false;
		String comparedPerspectiveID = null;
		for (String key : keys) {
			if (key.equals(tableID))
				ownKeyContained = true;
			else
				comparedPerspectiveID = key;
		}
		if (!ownKeyContained)
			throw new IllegalStateException("Can not set this similarity (" + vaSimilarity
				+ ") since it does not contain this SimilarityMap's VA (tableID: " + tableID + ")");
		if (comparedPerspectiveID == null)
			throw new IllegalStateException("No other va set in " + vaSimilarity);

		similarityMap.put(comparedPerspectiveID, vaSimilarity);

	}

	@Override
	public String toString() {
		return "SimilarityMap for " + tableID + " with relations to " + similarityMap.keySet();
	}

}
