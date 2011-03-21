package org.caleydo.core.data.virtualarray.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;

/**
 * The similarities of two specific virtual arrays on a {@link Group} basis. Each VASimilarity object is
 * stored twice - once for each VA's SimilarityMap.
 * 
 * @author Alexander Lex
 * @param <VAType>
 */
public class VASimilarity<VAType extends VirtualArray<VAType, ?, GroupListType>, GroupListType extends GroupList<GroupListType, VAType, ?>> {

	/**
	 * Returns the VA associated with the provides setID
	 * 
	 * @param setID
	 * @return
	 */
	public VAType getVA(Integer setID) {
		return vaMap.get(setID);
	}

	/**
	 * Returns the two keys of this VASimilarity
	 * 
	 * @return
	 */
	public Set<Integer> getSetIDs() {
		return vaMap.keySet();
	}

	/**
	 * Get the GroupSimilarity of a specific group of a specific VA associated with the setID
	 * 
	 * @param setID
	 * @param groupID
	 * @return
	 */
	public GroupSimilarity<VAType, GroupListType> getGroupSimilarity(Integer setID, Integer groupID) {
		return groupListSimilarities.get(setID).get(groupID);
	}

	/**
	 * Add a new VA
	 * 
	 * @param setID
	 * @param va
	 */
	public void addVA(Integer setID, VAType va) {

		if (vaMap.size() <= 2 && !vaMap.containsKey(setID)) {
			vaMap.put(setID, va);
			keys.add(setID);
		}
		else {
			if (!vaMap.containsKey(setID))
				throw new IllegalStateException("VASimilarity has already two VAs set.");

			vaMap.put(setID, va);
		}
	}

	// -------------------- END OF PUBLIC INTERFACE ----------------------------------

	/** The two virtual arrays stored by their key */
	HashMap<Integer, VAType> vaMap = new HashMap<Integer, VAType>(4);
	/** The keys */
	ArrayList<Integer> keys = new ArrayList<Integer>(4);
	/** Contains one ArrayList for every VA, which in turn contains the GroupSimilarity for every group */
	HashMap<Integer, ArrayList<GroupSimilarity<VAType, GroupListType>>> groupListSimilarities =
		new HashMap<Integer, ArrayList<GroupSimilarity<VAType, GroupListType>>>(4);

	/**
	 * Calculates the similarities of the previously specified VAs
	 */
	void calculateSimilarities() {
		System.out.println("Calculating similarities");
		groupListSimilarities.clear();

		if (keys.size() != 2 || vaMap.size() != 2)
			throw new IllegalStateException(
				"Key or VAMap incorrect. Should contain excatly two values. Keys: " + keys + ". VAMap: "
					+ vaMap);

		VAType va1 = vaMap.get(keys.get(0));
		VAType va2 = vaMap.get(keys.get(1));

		GroupListType groupList1 = va1.getGroupList();
		GroupListType groupList2 = va2.getGroupList();

		// ------ first we calculate the similariteis from 1 to 2 ------

		// the list of all similarities from group 1 to group 2
		ArrayList<GroupSimilarity<VAType, GroupListType>> groupSimilarities1 =
			new ArrayList<GroupSimilarity<VAType, GroupListType>>(groupList1.size());

		for (Group group : groupList1) {
			// the similarities of one individual group of groupList 1
			GroupSimilarity<VAType, GroupListType> groupSimilarity =
				new GroupSimilarity<VAType, GroupListType>(group, va1, va2);
			groupSimilarity.calculateSimilarity();
			groupSimilarities1.add(groupSimilarity);
		}
		groupListSimilarities.put(keys.get(0), groupSimilarities1);

		// ----- then we create the containers and copy the values from 2 to 1 -----

		// the list of all similarities from group 2 to group 1
		ArrayList<GroupSimilarity<VAType, GroupListType>> groupSimilarities2 =
			new ArrayList<GroupSimilarity<VAType, GroupListType>>(groupList2.size());

		for (Group group : groupList2) {
			GroupSimilarity<VAType, GroupListType> groupSimilarity =
				new GroupSimilarity<VAType, GroupListType>(group, va2, va1);
			groupSimilarity.calculateSimilarity();
			for (GroupSimilarity<VAType, GroupListType> similarity1 : groupSimilarities1) {
				groupSimilarity.setScore(similarity1.getGroupID(), similarity1.getScore(group.getGroupID()));
			}

			// groupSimilarity.setScore(0, groupSimilarities1.get(group.getGroupID()).getScore(groupID));
			groupSimilarities2.add(groupSimilarity);
		}

		// for (int groupID = 0; groupID < groupList2.size(); groupID++) {
		// // the similarities of one individual group of groupList 2
		// GroupSimilarity<VAType, GroupListType> groupSimilarity2 =
		// new GroupSimilarity<VAType, GroupListType>(groupList2.get(groupID), va2, va1);
		//
		// // for (GroupSimilarity<VAType, GroupListType> groupSimilarity1 : groupSimilarities1) {
		// // groupSimilarity2.setScore(groupSimilarity1.getGroupID(), groupSimilarity1.getScore(groupID));
		// // }
		// groupSimilarity2.calculateSimilarity();
		// groupSimilarities2.add(groupSimilarity2);
		// }
		groupListSimilarities.put(keys.get(1), groupSimilarities2);

	}

	@Override
	public String toString() {
		return "VASimilarity between " + keys;
	}
}
