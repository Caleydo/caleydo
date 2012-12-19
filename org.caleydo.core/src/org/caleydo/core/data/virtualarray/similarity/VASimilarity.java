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
package org.caleydo.core.data.virtualarray.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;

/**
 * The similarities of two specific virtual arrays on a {@link Group} basis.
 * Each VASimilarity object is stored twice - once for each VA's SimilarityMap.
 * 
 * @author Alexander Lex
 * @param <VAType>
 */
public class VASimilarity<VAType extends VirtualArray<VAType, ?, GroupListType>, GroupListType extends GroupList<GroupListType, VAType, ?>> {

	/**
	 * Returns the VA associated with the provided perspectiveID
	 * 
	 * @param perspectiveID
	 * @return
	 */
	public VAType getVA(String perspectiveID) {
		return vaMap.get(perspectiveID);
	}

	/**
	 * Returns the two keys of this VASimilarity
	 * 
	 * @return
	 */
	public Set<String> getPerspectiveIDs() {
		return vaMap.keySet();
	}

	/**
	 * Get the GroupSimilarity of a specific group of a specific VA associated
	 * with the perspectiveID
	 * 
	 * @param perspectiveID
	 * @param groupID
	 * @return
	 */
	public GroupSimilarity<VAType, GroupListType> getGroupSimilarity(
			String perspectiveID, Integer groupID) {
		return groupListSimilarities.get(perspectiveID).get(groupID);
	}

	/**
	 * Add a new VA
	 * 
	 * @param perspectiveID
	 * @param va
	 */
	public void addVA(String perspectiveID, VAType va) {

		if (vaMap.size() <= 2 && !vaMap.containsKey(perspectiveID)) {
			vaMap.put(perspectiveID, va);
			keys.add(perspectiveID);
		} else {
			if (!vaMap.containsKey(perspectiveID))
				throw new IllegalStateException("VASimilarity has already two VAs.");

			vaMap.put(perspectiveID, va);
		}
	}

	// -------------------- END OF PUBLIC INTERFACE
	// ----------------------------------

	/** The two virtual arrays stored by their key */
	HashMap<String, VAType> vaMap = new HashMap<String, VAType>(4);
	/** The keys */
	ArrayList<String> keys = new ArrayList<String>(4);
	/**
	 * Contains one ArrayList for every VA, which in turn contains the
	 * GroupSimilarity for every group
	 */
	HashMap<String, ArrayList<GroupSimilarity<VAType, GroupListType>>> groupListSimilarities = new HashMap<String, ArrayList<GroupSimilarity<VAType, GroupListType>>>(
			4);

	/**
	 * Calculates the similarities of the previously specified VAs
	 */
	void calculateSimilarities() {
		System.out.println("Calculating similarities");
		groupListSimilarities.clear();
		VAType va1;
		VAType va2;
		String key1;
		String key2;
		if (keys.size() == 1 || vaMap.size() == 1) {
			key1 = key2 = keys.get(0);
			va1 = vaMap.get(keys.get(0));
			va2 = vaMap.get(keys.get(0));
		} else if (keys.size() == 2 || vaMap.size() == 2) {
			key1 = keys.get(0);
			key2 = keys.get(1);
			va1 = vaMap.get(keys.get(0));
			va2 = vaMap.get(keys.get(1));
		} else {
			throw new IllegalStateException(
					"Key or VAMap incorrect. Should contain excatly one or two values. Keys: "
							+ keys + ". VAMap: " + vaMap);
		}

		GroupListType groupList1 = va1.getGroupList();
		GroupListType groupList2 = va2.getGroupList();

		// ------ first we calculate the similarities from 1 to 2 ------

		// the list of all similarities from group 1 to group 2
		ArrayList<GroupSimilarity<VAType, GroupListType>> groupSimilarities1 = new ArrayList<GroupSimilarity<VAType, GroupListType>>(
				groupList1.size());

		for (Group group : groupList1) {
			// the similarities of one individual group of groupList 1
			GroupSimilarity<VAType, GroupListType> groupSimilarity = new GroupSimilarity<VAType, GroupListType>(
					group, va1, va2);
			groupSimilarity.calculateSimilarity();
			groupSimilarities1.add(groupSimilarity);
		}
		groupListSimilarities.put(key1, groupSimilarities1);

		if(key1.equals(key2))
			return;
		
		// ----- then we create the containers and copy the values from 2 to 1
		// -----

		// the list of all similarities from group 2 to group 1
		ArrayList<GroupSimilarity<VAType, GroupListType>> groupSimilarities2 = new ArrayList<GroupSimilarity<VAType, GroupListType>>(
				groupList2.size());

		// for (Group group : groupList2) {
		// GroupSimilarity<VAType, GroupListType> groupSimilarity =
		// new GroupSimilarity<VAType, GroupListType>(group, va2, va1);
		// groupSimilarity.calculateSimilarity();
		// for (GroupSimilarity<VAType, GroupListType> similarity1 :
		// groupSimilarities1) {
		// groupSimilarity.setScore(similarity1.getGroupID(),
		// similarity1.getScore(group.getGroupID()));
		// }
		//
		// // groupSimilarity.setScore(0,
		// groupSimilarities1.get(group.getGroupID()).getScore(groupID));
		// groupSimilarities2.add(groupSimilarity);
		// }

		for (Group group : groupList2) {
			// the similarities of one individual group of groupList 1
			GroupSimilarity<VAType, GroupListType> groupSimilarity = new GroupSimilarity<VAType, GroupListType>(
					group, va2, va1);
			groupSimilarity.calculateSimilarity();
			groupSimilarities2.add(groupSimilarity);
		}

		groupListSimilarities.put(keys.get(1), groupSimilarities2);

	}

	@Override
	public String toString() {
		return "VASimilarity between " + keys;
	}
}
