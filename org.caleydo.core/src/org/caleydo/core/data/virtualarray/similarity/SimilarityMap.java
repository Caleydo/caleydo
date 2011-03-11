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
	 * @param setID
	 *            the setID associated with the VA that should be compared to the VA in this SimilarityMap
	 * @return
	 */
	public VASimilarity<ContentVirtualArray, ContentGroupList> getVASimilarity(Integer setID) {
		return similarityMap.get(setID);
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
		VASimilarity<ContentVirtualArray, ContentGroupList> similarity;
		if (similarityMap.containsKey(comparedSetID)) {
			similarity = similarityMap.get(comparedSetID);

		}
		else {
			similarity = new VASimilarity<ContentVirtualArray, ContentGroupList>();
			similarity.addVA(setID, contentVA);
		}

		similarity.addVA(comparedSetID, comparedContentVA);
		similarity.calculateSimilarities();
		return similarity;
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
				comparedSetID = setID;
		}
		if (!ownKeyContained)
			throw new IllegalStateException("Can not set this similarity (" + vaSimilarity
				+ ") since it does not contain this SimilarityMap's VA (setID: " + setID + ")");
		if (comparedSetID == null)
			throw new IllegalStateException("No other va set in " + vaSimilarity);

		similarityMap.put(comparedSetID, vaSimilarity);

	}

}
