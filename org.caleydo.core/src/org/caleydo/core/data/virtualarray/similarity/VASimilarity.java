/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.virtualarray.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;

import com.google.common.collect.ImmutableList;

/**
 * The similarities of two specific virtual arrays on a {@link Group} basis. Each VASimilarity object is stored twice -
 * once for each VA's SimilarityMap.
 *
 * @author Alexander Lex
 * @param <VirtualArray>
 */
public class VASimilarity {

	/** The two virtual arrays stored by their key */
	private final Map<String, VirtualArray> vaMap = new LinkedHashMap<>(4);
	/**
	 * Contains one ArrayList for every VA, which in turn contains the GroupSimilarity for every group
	 */
	private final Map<String, List<GroupSimilarity>> groupListSimilarities = new HashMap<>(4);

	/**
	 * Returns the VA associated with the provided perspectiveID
	 *
	 * @param perspectiveID
	 * @return
	 */
	public VirtualArray getVA(String perspectiveID) {
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
	 * Get the GroupSimilarity of a specific group of a specific VA associated with the perspectiveID
	 *
	 * @param perspectiveID
	 * @param groupID
	 * @return
	 */
	public GroupSimilarity getGroupSimilarity(String perspectiveID, Integer groupID) {
		return groupListSimilarities.get(perspectiveID).get(groupID);
	}

	/**
	 * Add a new VA
	 *
	 * @param perspectiveID
	 * @param va
	 */
	public void addVA(String perspectiveID, VirtualArray va) {

		if (vaMap.size() <= 2 && !vaMap.containsKey(perspectiveID)) {
			vaMap.put(perspectiveID, va);
		} else {
			if (!vaMap.containsKey(perspectiveID))
				throw new IllegalStateException("VASimilarity has already two VAs.");

			vaMap.put(perspectiveID, va);
		}
	}

	// -------------------- END OF PUBLIC INTERFACE
	// ----------------------------------

	/**
	 * Calculates the similarities of the previously specified VAs
	 */
	void calculateSimilarities() {
		// System.out.println("Calculating similarities");
		groupListSimilarities.clear();
		VirtualArray va1;
		VirtualArray va2;
		String key1;
		String key2;

		final List<String> keys = ImmutableList.copyOf(vaMap.keySet());
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
			throw new IllegalStateException("Key or VAMap incorrect. Should contain excatly one or two values. Keys: "
					+ keys + ". VAMap: " + vaMap);
		}

		GroupList groupList1 = va1.getGroupList();
		GroupList groupList2 = va2.getGroupList();

		// ------ first we calculate the similarities from 1 to 2 ------

		// the list of all similarities from group 1 to group 2
		List<GroupSimilarity> groupSimilarities1 = new ArrayList<>(
				groupList1.size());

		for (Group group : groupList1) {
			// the similarities of one individual group of groupList 1
			GroupSimilarity groupSimilarity = new GroupSimilarity(
					group, va1, va2);
			groupSimilarity.calculateSimilarity();
			groupSimilarities1.add(groupSimilarity);
		}
		groupListSimilarities.put(key1, groupSimilarities1);

		if (key1.equals(key2))
			return;

		// ----- then we create the containers and copy the values from 2 to 1
		// -----

		// the list of all similarities from group 2 to group 1
		List<GroupSimilarity> groupSimilarities2 = new ArrayList<>(groupList2.size());

		// for (Group group : groupList2) {
		// GroupSimilarity<VirtualArray, GroupList> groupSimilarity =
		// new GroupSimilarity<VirtualArray, GroupList>(group, va2, va1);
		// groupSimilarity.calculateSimilarity();
		// for (GroupSimilarity<VirtualArray, GroupList> similarity1 :
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
			GroupSimilarity groupSimilarity = new GroupSimilarity(
					group, va2, va1);
			groupSimilarity.calculateSimilarity();
			groupSimilarities2.add(groupSimilarity);
		}

		groupListSimilarities.put(keys.get(1), groupSimilarities2);

	}

	@Override
	public String toString() {
		return "VASimilarity between " + vaMap.keySet();
	}
}
