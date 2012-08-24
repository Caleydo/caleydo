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
import org.caleydo.core.manager.GeneralManager;

/**
 * Jaccard index for comparing individual clusters. See:
 * http://en.wikipedia.org/wiki/Jaccard_index
 * 
 * @author Marc Streit
 */
public class JaccardIndex {

	/**
	 * The data container to which the score belongs to.
	 */
	TablePerspective referenceTablePerspective;

	HashMap<TablePerspective, HashMap<Group, HashMap<Group, Float>>> tablePerspectiveToScore = new HashMap<TablePerspective, HashMap<Group, HashMap<Group, Float>>>();

	public JaccardIndex(TablePerspective referenceTablePerspective) {
		this.referenceTablePerspective = referenceTablePerspective;
	}

	/**
	 * 
	 * @param tablePerspective The data container to compare
	 * @return the calculation result
	 */
	public HashMap<Group, HashMap<Group, Float>> getScore(TablePerspective tablePerspective,
			boolean storeResult) {

		// check if calculation result is already available
		if (tablePerspectiveToScore.containsKey(tablePerspective))
			return tablePerspectiveToScore.get(tablePerspective);

		long startTime = System.currentTimeMillis();

		RecordVirtualArray va1 = tablePerspective.getRecordPerspective().getVirtualArray();
		RecordVirtualArray va2 = referenceTablePerspective.getRecordPerspective()
				.getVirtualArray();

		HashMap<Group, HashMap<Group, Float>> groupToSubGroup = new HashMap<Group, HashMap<Group, Float>>();
		tablePerspectiveToScore.put(tablePerspective, groupToSubGroup);

		// System.out.println("group list 1: "+ va1.getGroupList());
		// System.out.println("group list 2: "+ va2.getGroupList());

		// System.out.println("Size left table " +va1.size());
		Set<Integer> unionCounter = new HashSet<Integer>();
		for (Group group : va1.getGroupList()) {

			HashMap<Group, Float> subGroupToScore = new HashMap<Group, Float>();
			groupToSubGroup.put(group, subGroupToScore);
			
			for (Group group2 : va2.getGroupList()) {

				int intersectionCount = 0;

				//System.out.println("Group1: " + (group.getEndIndex() - group.getStartIndex()));

//				System.out.println("Group2: "
//						+ (group2.getEndIndex() - group2.getStartIndex()));

				for (int vaIndex = group.getStartIndex(); vaIndex < group.getEndIndex(); vaIndex++) {

					int id = va1.get(vaIndex);
					unionCounter.add(id);

					for (int vaIndex2 = group2.getStartIndex(); vaIndex2 < group2
							.getEndIndex(); vaIndex2++) {

						int id2 = va2.get(vaIndex2);
						unionCounter.add(id2);

						if (va1.getIdType() != va2.getIdType()) {
							IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
									.getIDMappingManager(va1.getIdType().getIDCategory());
							Set<Integer> ids = idMappingManager.getIDAsSet(va2.getIdType(),
									va1.getIdType(), id2);

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
//				System.out.println("Union count: " + unionCount);
//				System.out.println("Intersection count: " + intersectionCount);

				float jaccardIndex = 0;
				if (unionCount > 0)
					jaccardIndex = (float) intersectionCount
							/ unionCount;
				

//				System.out.println("Jaccard index: " + jaccardIndex);
				
				subGroupToScore.put(group2, jaccardIndex);

				unionCounter.clear();
			}
		}

		long endTime = System.currentTimeMillis();
		System.out.println("Calculation took " + (endTime - startTime) + "ms");

		if (storeResult) {
			tablePerspectiveToScore.put(tablePerspective, groupToSubGroup);
		}

		return groupToSubGroup;
	}
}
