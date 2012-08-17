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
		score = new Random().nextFloat();

		int a = 0;

		RecordVirtualArray va1 = tablePerspective.getRecordPerspective().getVirtualArray();

		RecordVirtualArray va2 = referenceTablePerspective.getRecordPerspective().getVirtualArray();
 
		for (Group group1 : va1.getGroupList()) {

			// System.out.println("Group 1: " + group1.getLabel());

			for (int vaIndex = group1.getStartIndex(); vaIndex < group1.getEndIndex(); vaIndex++) {

				int id1_1 = va1.get(vaIndex);
				int id1_2 = va1.get(vaIndex + 1);

				// System.out.println("Pair: " + id1_1 + " " + id1_2);

				for (Group group2 : va2.getGroupList()) {

					boolean matchId1_1 = false;
					boolean matchId1_2 = false;

					// System.out.println("Group 2: " + group2.getLabel());

					for (int va2Index = group2.getStartIndex(); va2Index < group2
							.getEndIndex(); va2Index++) {

						int id2 = va2.get(va2Index);
						// System.out.println("Match ID: " + id2);

						if (id2 == id1_1) {
							// System.out.println("1 Match found");
							matchId1_1 = true;
						}

						if (id2 == id1_2) {
							// System.out.println("2 Match found");
							matchId1_2 = true;
						}

						if (matchId1_1 && matchId1_2) {
							// System.out.println("Match found for id " +id2);
							a++;
							break;
						}
					}

					// remove this for fuzzy clusters
					if (matchId1_1 && matchId1_2) {
						break;
					}
				}
			}
		}

		System.out.println("a: " + a);

		score = a;
		// for (TablePerspective recordGroupTablePerspective : tablePerspective
		// .getRecordSubTablePerspectives()) {
		//
		// Group recordGroup = recordGroupTablePerspective.getRecordGroup();
		// for (int vaIndex = recordGroup.getStartIndex(); vaIndex < recordGroup
		// .getEndIndex(); vaIndex++) {
		//
		// Integer id1 = va1.get(vaIndex);
		// Integer id2 = va1.get(vaIndex + 1);
		//
		// }
		//
		// System.out.println(recordGroupTablePerspective);
		// }
		//

		// TODO calculate rand index

		System.out.println("Calculate Adjusted Rand Index");

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
