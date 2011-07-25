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
	 * Get the VASimilarity between this SimilarityMap's VA and the VA associated with the tableID.
	 * 
	 * @param foreignSetID
	 *            the tableID associated with the VA that should be compared to the VA in this SimilarityMap
	 * @return
	 */
	public VASimilarity<RecordVirtualArray, RecordGroupList> getVASimilarity(Integer foreignSetID) {
		return similarityMap.get(foreignSetID);
	}

	// -------------------- END OF PUBLIC INTERFACE ----------------------------------

	HashMap<Integer, VASimilarity<RecordVirtualArray, RecordGroupList>> similarityMap =
		new HashMap<Integer, VASimilarity<RecordVirtualArray, RecordGroupList>>(20);

	private Integer tableID;
	private RecordVirtualArray recordVA;

	/**
	 * Constructor with the key pair tableID and recordVA
	 */
	SimilarityMap(Integer tableID, RecordVirtualArray recordVA) {
		this.tableID = tableID;
		this.recordVA = recordVA;
	}

	/**
	 * Sets a va that the key va of this object is to be compared to and calculates the similarity.
	 * 
	 * @param comparedSetID
	 * @param comparedRecordVA
	 */
	VASimilarity<RecordVirtualArray, RecordGroupList> calculateVASimilarity(Integer comparedSetID,
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
		Set<Integer> keys = vaSimilarity.getTableIDs();
		boolean ownKeyContained = false;
		Integer comparedSetID = null;
		for (Integer key : keys) {
			if (key.equals(tableID))
				ownKeyContained = true;
			else
				comparedSetID = key;
		}
		if (!ownKeyContained)
			throw new IllegalStateException("Can not set this similarity (" + vaSimilarity
				+ ") since it does not contain this SimilarityMap's VA (tableID: " + tableID + ")");
		if (comparedSetID == null)
			throw new IllegalStateException("No other va set in " + vaSimilarity);

		similarityMap.put(comparedSetID, vaSimilarity);

	}

	@Override
	public String toString() {
		return "SimilarityMap for " + tableID + " with relations to " + similarityMap.keySet();
	}

}
