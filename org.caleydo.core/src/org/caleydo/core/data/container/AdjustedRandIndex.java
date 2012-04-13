/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
