/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.similarity;

import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.virtualarray.VirtualArray;

/**
 * A map containing similarities from one specific tableID / recordVA pair to all other pairs.
 *
 * @author Alexander Lex
 */
public class SimilarityMap {
	private final HashMap<String, VASimilarity> similarityMap = new HashMap<String, VASimilarity>(20);

	private String tableID;
	private VirtualArray recordVA;

	/**
	 * Constructor with the key pair tableID and recordVA
	 */
	SimilarityMap(String tableID, VirtualArray recordVA) {
		this.tableID = tableID;
		this.recordVA = recordVA;
	}

	/**
	 * Get the VASimilarity between this SimilarityMap's VA and the VA associated with the perspectiveID.
	 *
	 * @param foreignPerspectiveID
	 *            the perspectiveID associated with the VA that should be compared to the VA in this
	 *            SimilarityMap
	 * @return
	 */
	public VASimilarity getVASimilarity(String foreignPerspectiveID) {
		return similarityMap.get(foreignPerspectiveID);
	}

	public VASimilarity removeVASimilarity(String foreignPerspectiveID) {
		return similarityMap.remove(foreignPerspectiveID);
	}


	/**
	 * Sets a va that the key va of this object is to be compared to and calculates the similarity.
	 *
	 * @param comparedSetID
	 * @param comparedRecordVA
	 */
	VASimilarity calculateVASimilarity(String comparedSetID,
		VirtualArray comparedRecordVA) {
		VASimilarity vaSimilarity;

		vaSimilarity = new VASimilarity();
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
	void setVaSimilarity(VASimilarity vaSimilarity) {
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
		{
			// comparison to one self
			comparedPerspectiveID = tableID;
//			throw new IllegalStateException("No other va set in " + vaSimilarity);
		}
		similarityMap.put(comparedPerspectiveID, vaSimilarity);

	}

	@Override
	public String toString() {
		return "SimilarityMap for " + tableID + " with relations to " + similarityMap.keySet();
	}

}
