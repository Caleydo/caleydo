package org.caleydo.core.data.virtualarray.similarity;

import java.util.List;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;

/**
 * The similarity of one group in source VA1 to all groups in target VA2
 * 
 * @author Alexander Lex
 */
public class GroupSimilarity<VAType extends VirtualArray<?, ?, GroupListType>, GroupListType extends GroupList<GroupListType, ?, ?>> {

	/** Get the id of the group for which the similarities are contained */

	public int getGroupID() {
		return group.getGroupID();
	}

	/**
	 * Get the number of shared elements between this group and the group of va2 specified through the groupID
	 * 
	 * @param groupID
	 * @return
	 */
	public int getScore(int groupID) {
		return scores[groupID];
	}

	// -------------------- END OF PUBLIC INTERFACE ----------------------------------

	private int[] scores;
	private Group group;
	private VAType va1;
	private VAType va2;

	GroupSimilarity(Group group, VAType va1, VAType va2) {
		this.va1 = va1;
		this.va2 = va2;
		this.group = group;
		scores = new int[va2.getGroupList().size()];
	}

	void calculateSimilarity() {

		for (int vaIndex = group.getStartIndex(); vaIndex < group.getStartIndex() + group.getSize(); vaIndex++) {
			Integer id = va1.get(vaIndex);
			List<Group> groups2 = va2.getGroupOf(id);
			for (Group group2 : groups2) {
				scores[group2.getGroupID()] += 1;
			}
		}
	}

	void setScore(int groupID, int score) {
		scores[groupID] = score;
	}

}
