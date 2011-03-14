package org.caleydo.view.visbricks.dimensiongroup;

import java.util.Collection;
import java.util.HashMap;

public class GroupMatch {

	private int groupID;
	
	private HashMap<Integer, SubGroupMatch> hashSubGroupID2SubGroupMatch = new HashMap<Integer, SubGroupMatch>();
	
	public GroupMatch(int groupID) {
		this.groupID = groupID;
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
}
 