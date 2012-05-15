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
