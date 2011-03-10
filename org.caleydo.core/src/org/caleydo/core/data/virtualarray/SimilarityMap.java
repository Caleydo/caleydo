package org.caleydo.core.data.virtualarray;

import java.util.HashMap;
import java.util.Set;

/**
 * A map containing similarities from one specific setID / contentVA pair to (many) other pairs.
 * 
 * @author Alexander Lex
 */
public class SimilarityMap {

	HashMap<Integer, VASimilarity<ContentVirtualArray>> similarityMap =
		new HashMap<Integer, VASimilarity<ContentVirtualArray>>(20);

	private Integer setID;
	private ContentVirtualArray contentVA;

	/**
	 * Constructor with the key pair setID and contentVA
	 */
	public SimilarityMap(Integer setID, ContentVirtualArray contentVA) {
		this.setID = setID;
		this.contentVA = contentVA;
	}

	/**
	 * Sets a va that the key va of this object is to be compared to and calculates the similarity.
	 * 
	 * @param comparedSetID
	 * @param comparedContentVA
	 */
	public VASimilarity<ContentVirtualArray> calculateVASimilarity(Integer comparedSetID,
		ContentVirtualArray comparedContentVA) {
		VASimilarity<ContentVirtualArray> similarity;
		if (similarityMap.containsKey(comparedSetID)) {
			similarity = similarityMap.get(comparedSetID);

		}
		else {
			similarity = new VASimilarity<ContentVirtualArray>();
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
	public void setVaSimilarity(VASimilarity<ContentVirtualArray> vaSimilarity) {
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

	public VASimilarity<ContentVirtualArray> getSmilarity(Integer setID) {
		return similarityMap.get(setID);
	}

}
