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
import java.util.List;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IIDTypeMapper;

/**
 * The similarity of one group in source VA1 to all groups in target VA2. You
 * can get two types of information from this class:
 * <ol>
 * <li>The <b>score</b> (accessible via {@link #getScore(int)}), which tells you
 * how many elements this group shares with the group of the other VA in this
 * GroupSimilarity.</li>
 * <li>The <b>similarity</b> (accessible via {@link #getSimilarity(int)} and in
 * bulk via {@link #getSimilarities()}) which tells you how similar this group
 * is to the other group or groups. The contract here is that if all elements of
 * this group are contained in the foreign group as well, the similarity will be
 * 1, if none are contained 0.
 * </ol>
 * <p>
 * Notice that the score of two groups for each other in two VAs will be
 * identical, the similarities however not, since the similarities also consider
 * the size of the group.
 * </p>
 *
 * @author Alexander Lex
 */
public class GroupSimilarity {

	/** Get the id of the group for which the similarities are contained */

	public int getGroupID() {
		return group.getGroupIndex();
	}

	/**
	 * Get the number of shared elements between this group and the group of va2
	 * specified through the groupID
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
	 * Returns the similarity of this group to the group specified via the
	 * groupID in a rate normalized between 0 and 1
	 *
	 * @param groupID
	 * @return
	 */
	public float getSimilarity(int groupID) {
		return similarities[groupID];
	}

	/**
	 * Returns the similarity of this group to all other groups, normalized
	 * between 0 and 1
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
	 * Returns a new virtual array containing all elements which occur in the
	 * primary group of this group similarity and an external group specified
	 * through the foreign group ID.
	 *
	 * @param foreignGroupID
	 *            Returns null if createSimilarity flag is false.
	 * @return
	 */
	public VirtualArray getSimilarityVAs(int foreignGroupID) {

		if (!createSimilarityVAs)
			return null;

		return similarityVAs.get(foreignGroupID);
	}

	@Override
	public String toString() {
		return "Gr. Sim.: src.: " + group + " to: " + scores.length + " groups";
	}

	// -------------------- END OF PUBLIC INTERFACE
	// ----------------------------------

	private int[] scores;
	private Group group;
	private VirtualArray va1;
	private VirtualArray va2;
	private float[] similarities;
	private ArrayList<VirtualArray> similarityVAs;
	private boolean createSimilarityVAs = true;

	GroupSimilarity(Group group, VirtualArray va1, VirtualArray va2) {
		this.va1 = va1;
		this.va2 = va2;
		this.group = group;
		scores = new int[va2.getGroupList().size()];

		if (createSimilarityVAs) {
			similarityVAs = new ArrayList<VirtualArray>(va2.getGroupList().size());

			for (int vaCount = 0; vaCount < va2.getGroupList().size(); vaCount++) {
				// it does not matter which va we use as we only need the object
				similarityVAs.add(new VirtualArray(va2.getIdType()));
			}
		}
	}

	void calculateSimilarity() {
		IIDTypeMapper<Integer, Integer> mapper = IDMappingManagerRegistry.get().getIDMappingManager(va1.getIdType())
				.getIDTypeMapper(va1.getIdType(), va2.getIdType());
		// map all at once
		for (Integer id : mapper.apply(va1.getIDsOfGroup(group.getGroupIndex()))) {
			List<Group> groups2 = va2.getGroupOf(id);

			if (groups2.size() > 1) {
				System.out.println("Similarity size sum: " + groups2.size());
				// throw new IllegalArgumentException("wa");
			}

			for (Group group2 : groups2) {
				scores[group2.getGroupIndex()] += 1;

				if (createSimilarityVAs) {
					similarityVAs.get(group2.getGroupIndex()).append(id);
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
			System.out.println("Similarity size sum " + sum + "!= group sum "
					+ group.getSize());
		}
	}

	void setScore(int groupID, int score) {
		scores[groupID] = score;
	}
}
