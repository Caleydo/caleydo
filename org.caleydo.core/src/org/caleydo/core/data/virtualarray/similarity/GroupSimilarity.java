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
		if (groupID >= scores.length)
			throw new IllegalArgumentException();
		return scores[groupID];
	}

	/**
	 * Returns the similarity of this group to the group specified via the groupID in a rate normalized
	 * between 0 and 1
	 * 
	 * @param groupID
	 * @return
	 */
	public float getSimilarity(int groupID) {
		if (groupID >= scores.length)
			throw new IllegalArgumentException();
		float similarity = ((float) scores[groupID]) / group.getSize();
		return similarity;
	}

	public float[] getSimilarities() {
		float[] similarities = new float[scores.length];
		for (int count = 0; count < scores.length; count++) {
			similarities[count] = ((float) scores[count]) / group.getSize();
		}
		return similarities;
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

	@Override
	public String toString() {
		return "Gr. Sim.: src.: " + group + " to: " + scores.length + " groups";
	}

}
