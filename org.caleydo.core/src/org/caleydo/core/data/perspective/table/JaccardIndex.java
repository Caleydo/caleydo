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
import java.util.HashSet;
import java.util.Set;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;

/**
 * Jaccard index for comparing individual clusters. See:
 * http://en.wikipedia.org/wiki/Jaccard_index
 * 
 * @author Marc Streit
 */
public class JaccardIndex {

	/**
	 * The table perspective to which the score belongs to.
	 */
	TablePerspective referenceTablePerspective;

	HashMap<TablePerspective, HashMap<TablePerspective, Float>> tablePerspectiveToScore = new HashMap<TablePerspective, HashMap<TablePerspective, Float>>();

	public JaccardIndex(TablePerspective referenceTablePerspective) {
		this.referenceTablePerspective = referenceTablePerspective;
	}

	/**
	 * 
	 * @param tablePerspective The table perspective to compare
	 * @return the calculation result
	 */
	public HashMap<TablePerspective, Float> getScore(TablePerspective tablePerspective,
			boolean storeResult) {

		// check if calculation result is already available
		if (tablePerspectiveToScore.containsKey(tablePerspective))
			return tablePerspectiveToScore.get(tablePerspective);

		long startTime = System.currentTimeMillis();

		RecordVirtualArray referenceVA = referenceTablePerspective.getRecordPerspective()
				.getVirtualArray();
		RecordVirtualArray va = tablePerspective.getRecordPerspective().getVirtualArray();

		HashMap<TablePerspective, Float> subTablePerspetiveToScore = new HashMap<TablePerspective, Float>();
		tablePerspectiveToScore.put(tablePerspective, subTablePerspetiveToScore);

		// System.out.println("group list 1: "+ va1.getGroupList());
		// System.out.println("group list 2: "+ va2.getGroupList());

		// System.out.println("Size left table " +va1.size());
		Set<Integer> unionCounter = new HashSet<Integer>();
		Group referenceGroup = referenceTablePerspective.getRecordGroup();

		for (TablePerspective subTablePerspective : tablePerspective
				.getRecordSubTablePerspectives()) {

			int intersectionCount = 0;

			// System.out.println("Group1: " + (group.getEndIndex() -
			// group.getStartIndex()));

			// System.out.println("Group2: "
			// + (group2.getEndIndex() - group2.getStartIndex()));

			Group subGroup = subTablePerspective.getRecordGroup();

//			System.out.println("subtable perspective " + subGroup.getLabel());

			if (subGroup.getSize() == 0)
				continue;

			for (int vaIndex = 0; vaIndex < referenceGroup.getSize(); vaIndex++) {

				int id = referenceVA.get(vaIndex);
				unionCounter.add(id);

				for (int vaIndex2 = subGroup.getStartIndex(); vaIndex2 < subGroup
						.getEndIndex(); vaIndex2++) {

					int id2 = va.get(vaIndex2);
					unionCounter.add(id2);

					if (referenceVA.getIdType() != va.getIdType()) {
						IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
								.getIDMappingManager(referenceVA.getIdType().getIDCategory());
						Set<Integer> ids = idMappingManager.getIDAsSet(va.getIdType(),
								referenceVA.getIdType(), id2);

						if (ids != null) {
							id2 = ids.iterator().next();
							if (ids.size() > 2) {
								System.out.println("Multi-Mapping");
							}
						}
					}

					if (id == id2) {
						intersectionCount++;
						continue;
					}
				}
			}

			int unionCount = unionCounter.size();
			// System.out.println("Union count: " + unionCount);
			// System.out.println("Intersection count: " +
			// intersectionCount);

			float jaccardIndex = 0;
			if (unionCount > 0)
				jaccardIndex = (float) intersectionCount / unionCount;

			// System.out.println("Jaccard index: " + jaccardIndex);

			subTablePerspetiveToScore.put(subTablePerspective, jaccardIndex);

			unionCounter.clear();
		}

		long endTime = System.currentTimeMillis();
		System.out.println("Calculation of Jaccard index of " + tablePerspective.getLabel()
				+ " took " + (endTime - startTime) + "ms");

		if (storeResult) {
			tablePerspectiveToScore.put(tablePerspective, subTablePerspetiveToScore);
		}

		return subTablePerspetiveToScore;
	}
}
