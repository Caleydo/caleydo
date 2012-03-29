package org.caleydo.view.visbricks.dimensiongroup;

import java.util.Collection;
import java.util.HashMap;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * FIXME documentation
 * 
 * @author alexsb
 * 
 */
public class GroupMatch {

	private GLBrick glBrick;

	private HashMap<Integer, SubGroupMatch> hashSubGroupID2SubGroupMatch = new HashMap<Integer, SubGroupMatch>();

	public GroupMatch(GLBrick glBrick) {
		this.glBrick = glBrick;
	}

	public void addSubGroupMatch(Integer subGroupID, SubGroupMatch subGroupMatch) {

		hashSubGroupID2SubGroupMatch.put(subGroupID, subGroupMatch);
	}

	public Collection<SubGroupMatch> getSubGroupMatches() {
		return hashSubGroupID2SubGroupMatch.values();
	}

	public SubGroupMatch getSubGroupMatch(Integer groupID) {
		return hashSubGroupID2SubGroupMatch.get(groupID);
	}

	public GLBrick getBrick() {
		return glBrick;
	}
}
