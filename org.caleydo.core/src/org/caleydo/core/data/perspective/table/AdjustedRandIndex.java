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
package org.caleydo.core.data.perspective.table;

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
	TablePerspective referenceDataContainer;

	HashMap<TablePerspective, Float> dataContainerToScore = new HashMap<TablePerspective, Float>();

	public AdjustedRandIndex(TablePerspective referenceDataContainer) {
		this.referenceDataContainer = referenceDataContainer;
	}

	/**
	 * 
	 * @param tablePerspective The data container to compare
	 * @return the calculation result
	 */
	public float getScore(TablePerspective tablePerspective, boolean storeResult) {

		// check if calculation result is alrady available
		if (dataContainerToScore.containsKey(tablePerspective))
			return dataContainerToScore.get(tablePerspective);

		float score = 1;
		score = new Random().nextFloat();

		// TODO calculate rand index

		System.out.println("Calculate Adjusted Rand Index");
		
		if (storeResult) {
			dataContainerToScore.put(tablePerspective, score);
			referenceDataContainer.getContainerStatistics().adjustedRandIndex()
					.setScore(tablePerspective, score);
		}

		return score;
	}

	public void setScore(TablePerspective tablePerspective, float score) {
		dataContainerToScore.put(tablePerspective, score);
	}
}
