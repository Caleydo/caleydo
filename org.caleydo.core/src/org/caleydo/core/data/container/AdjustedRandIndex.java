package org.caleydo.core.data.container;

import java.util.HashMap;
import java.util.Random;

/**
 * Adjusted Rand Index for comparing clustering results. See:
 * http://en.wikipedia.org/wiki/Rand_index#Adjusted_Rand_index
 * 
 * @author Marc Streit
 */
public class AdjustedRandIndex {

	/**
	 * The data container to which the score belongs to.
	 */
	DataContainer referenceDataContainer;

	HashMap<DataContainer, Float> dataContainerToScore = new HashMap<DataContainer, Float>();

	public AdjustedRandIndex(DataContainer referenceDataContainer) {
		this.referenceDataContainer = referenceDataContainer;
	}

	/**
	 * 
	 * @param dataContainer The data container to compare
	 * @return the calculation result
	 */
	public float getScore(DataContainer dataContainer, boolean storeResult) {

		// check if calculation result is alrady available
		if (dataContainerToScore.containsKey(dataContainer))
			return dataContainerToScore.get(dataContainer);

		float score = 1;
		score = new Random().nextFloat();

		// TODO calculate rand index

		System.out.println("Calculate Adjusted Rand Index");
		
		if (storeResult) {
			dataContainerToScore.put(dataContainer, score);
			referenceDataContainer.getContainerStatistics().adjustedRandIndex()
					.setScore(dataContainer, score);
		}

		return score;
	}

	public void setScore(DataContainer dataContainer, float score) {
		dataContainerToScore.put(dataContainer, score);
	}
}
