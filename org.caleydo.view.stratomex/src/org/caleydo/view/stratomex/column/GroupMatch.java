/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.column;

import java.util.Collection;
import java.util.HashMap;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * FIXME documentation
 * 
 * @author alexsb
 * 
 */
public class GroupMatch {

	private GLBrick glBrick;

	private Group group;

	private HashMap<Integer, SubGroupMatch> hashSubGroupID2SubGroupMatch = new HashMap<Integer, SubGroupMatch>();

	public GroupMatch(GLBrick glBrick, Group group) {
		this.glBrick = glBrick;
		this.group = group;
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
	
	/**
	 * @return the group, see {@link #group}
	 */
	public Group getGroup() {
		return group;
	}
}
