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
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.GeneralManager;

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
	TablePerspective referenceTablePerspective;

	HashMap<TablePerspective, Float> tablePerspectiveToScore = new HashMap<TablePerspective, Float>();

	public AdjustedRandIndex(TablePerspective referenceTablePerspective) {
		this.referenceTablePerspective = referenceTablePerspective;
	}

	/**
	 * 
	 * @param tablePerspective The data container to compare
	 * @return the calculation result
	 */
	public float getScore(TablePerspective tablePerspective, boolean storeResult) {

		// check if calculation result is already available
		if (tablePerspectiveToScore.containsKey(tablePerspective))
			return tablePerspectiveToScore.get(tablePerspective);

		float score = 1;
		// score = new Random().nextFloat();#
		
		long startTime = System.currentTimeMillis();
		
		score = GeneralManager.get().getRStatisticsPerformer()
				.adjustedRandIndex(tablePerspective, referenceTablePerspective);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Calculation took " + (endTime - startTime) + "ms");
		
		if (storeResult) {
			tablePerspectiveToScore.put(tablePerspective, score);
			referenceTablePerspective.getContainerStatistics().adjustedRandIndex()
					.setScore(tablePerspective, score);
		}

		return score;
	}

	public void setScore(TablePerspective tablePerspective, float score) {
		tablePerspectiveToScore.put(tablePerspective, score);
	}
}
