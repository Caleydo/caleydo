package org.caleydo.core.data.virtualarray.similarity;

import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;

/**
 * A map containing similarities from one specific setID / contentVA pair to all other pairs.
 * 
 * @author Alexander Lex
 */
public class SimilarityMap {

	/**
	 * Get the VASimilarity between this SimilarityMap's VA and the VA associated with the setID.
	 * 
	 * @param foreignSetID
	 *            the setID associated with the VA that should be compared to the VA in this SimilarityMap
	 * @return
	 */
	public VASimilarity<ContentVirtualArray, ContentGroupList> getVASimilarity(Integer foreignSetID) {
		return similarityMap.get(foreignSetID);
	}

	// -------------------- END OF PUBLIC INTERFACE ----------------------------------

	HashMap<Integer, VASimilarity<ContentVirtualArray, ContentGroupList>> similarityMap =
		new HashMap<Integer, VASimilarity<ContentVirtualArray, ContentGroupList>>(20);

	private Integer setID;
	private ContentVirtualArray contentVA;

	/**
	 * Constructor with the key pair setID and contentVA
	 */
	SimilarityMap(Integer setID, ContentVirtualArray contentVA) {
		this.setID = setID;
		this.contentVA = contentVA;
	}

	/**
	 * Sets a va that the key va of this object is to be compared to and calculates the similarity.
	 * 
	 * @param comparedSetID
	 * @param comparedContentVA
	 */
	VASimilarity<ContentVirtualArray, ContentGroupList> calculateVASimilarity(Integer comparedSetID,
		ContentVirtualArray comparedContentVA) {
		VASimilarity<ContentVirtualArray, ContentGroupList> vaSimilarity;

			vaSimilarity = new VASimilarity<ContentVirtualArray, ContentGroupList>();
			vaSimilarity.addVA(setID, contentVA);

		vaSimilarity.addVA(comparedSetID, comparedContentVA);
		vaSimilarity.calculateSimilarities();
		similarityMap.put(comparedSetID, vaSimilarity);
		return vaSimilarity;
	}

	/**
	 * Set a pre-existing similarity between this objects key va and another.
	 * 
	 * @param vaSimilarity
	 */
	void setVaSimilarity(VASimilarity<ContentVirtualArray, ContentGroupList> vaSimilarity) {
		Set<Integer> keys = vaSimilarity.getSetIDs();
		boolean ownKeyContained = false;
		Integer comparedSetID = null;
		for (Integer key : keys) {
			if (key.equals(setID))
				ownKeyContained = true;
			else
				comparedSetID = key;
		}
		if (!ownKeyContained)
			throw new IllegalStateException("Can not set this similarity (" + vaSimilarity
				+ ") since it does not contain this SimilarityMap's VA (setID: " + setID + ")");
		if (comparedSetID == null)
			throw new IllegalStateException("No other va set in " + vaSimilarity);

		similarityMap.put(comparedSetID, vaSimilarity);

	}

	@Override
	public String toString() {
		return "SimilarityMap for " + setID + " with relations to " + similarityMap.keySet();
	}

}
