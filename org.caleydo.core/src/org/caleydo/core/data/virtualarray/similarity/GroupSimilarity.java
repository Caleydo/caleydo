package org.caleydo.core.data.virtualarray.similarity;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.mapping.IDMappingManagerRegistry;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.manager.GeneralManager;

/**
 * The similarity of one group in source VA1 to all groups in target VA2. You can get two types of information
 * from this class:
 * <ol>
 * <li>The <b>score</b> (accessible via {@link #getScore(int)}), which tells you how many elements this group
 * shares with the group of the other VA in this GroupSimilarity.</li>
 * <li>The <b>similarity</b> (accessible via {@link #getSimilarity(int)} and in bulk via
 * {@link #getSimilarities()}) which tells you how similar this group is to the other group or groups. The
 * contract here is that if all elements of this group are contained in the foreign group as well, the
 * similarity will be 1, if none are contained 0.
 * </ol>
 * <p>
 * Notice that the score of two groups for each other in two VAs will be identical, the similarities however
 * not, since the similarities also consider the size of the group.
 * </p>
 * 
 * @author Alexander Lex
 */
public class GroupSimilarity<VAType extends VirtualArray<VAType, ?, GroupListType>, GroupListType extends GroupList<GroupListType, VAType, ?>> {

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
		return similarities[groupID];
	}

	/**
	 * Returns the similarity of this group to all other groups, normalized between 0 and 1
	 * 
	 * @param groupID
	 * @return
	 */
	public float[] getSimilarities() {
		return similarities;
	}

	public int[] getScores() {
		return scores;
	}

	/**
	 * Returns a new virtual array containing all elements which occur in the primary group of this group
	 * similarity and an external group specified through the foreign group ID.
	 * 
	 * @param foreignGroupID
	 *            Returns null if createSimilarity flag is false.
	 * @return
	 */
	public VAType getSimilarityVAs(int foreignGroupID) {

		if (!createSimilarityVAs)
			return null;

		return similarityVAs.get(foreignGroupID);
	}

	@Override
	public String toString() {
		return "Gr. Sim.: src.: " + group + " to: " + scores.length + " groups";
	}

	// -------------------- END OF PUBLIC INTERFACE ----------------------------------

	private int[] scores;
	private Group group;
	private VAType va1;
	private VAType va2;
	private float[] similarities;
	private ArrayList<VAType> similarityVAs;
	private boolean createSimilarityVAs = true;

	GroupSimilarity(Group group, VAType va1, VAType va2) {
		this.va1 = va1;
		this.va2 = va2;
		this.group = group;
		scores = new int[va2.getGroupList().size()];

		if (createSimilarityVAs) {
			similarityVAs = new ArrayList<VAType>(va2.getGroupList().size());

			for (int vaCount = 0; vaCount < va2.getGroupList().size(); vaCount++) {
				// it does not matter which va we use as we only need the object
				similarityVAs.add(va2.getNewInstance());
			}
		}
	}

	void calculateSimilarity() {

		for (int vaIndex = group.getStartIndex(); vaIndex < group.getStartIndex() + group.getSize(); vaIndex++) {
			Integer id = va1.get(vaIndex);
			if (va1.getIdType() != va2.getIdType()) {
				IDMappingManager idMappingManager =
					IDMappingManagerRegistry.get().getIDMappingManager(va1.getIdType().getIDCategory());
				id = idMappingManager.getID(va1.getIdType(), va2.getIdType(), id);
			}
			List<Group> groups2 = va2.getGroupOf(id);

			if (groups2.size() > 1) {
				System.out.println("Similarity size sum: " + groups2.size());
				// throw new IllegalArgumentException("wa");
			}

			for (Group group2 : groups2) {
				scores[group2.getGroupID()] += 1;

				if (createSimilarityVAs) {
					similarityVAs.get(group2.getGroupID()).append(id);
				}
			}
		}
		int sum = 0;
		similarities = new float[scores.length];
		for (int count = 0; count < scores.length; count++) {
			sum += scores[count];
			similarities[count] = ((float) scores[count]) / group.getSize();
		}

		if (sum != group.getSize()) {
			System.out.println("Similarity size sum " + sum + "!= group sum " + group.getSize());
		}
	}

	void setScore(int groupID, int score) {
		scores[groupID] = score;
	}
}
