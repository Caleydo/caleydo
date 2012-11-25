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
package org.caleydo.view.stratomex.column;

import java.util.HashMap;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * FIXME documentation
 * 
 * @author alexsb
 * 
 */
public class SubGroupMatch {

	private int connectionBandID = -1;

	private GLBrick glBrick;

	private Group subGroup;

	private float leftAnchorYStart;

	private float leftAnchorYEnd;

	private float rightAnchorYStart;

	private float rightAnchorYEnd;

	/**
	 * The ratio that is returned for the left side by the group similarity
	 * calculation.
	 */
	private float leftSimilarityRatio;

	/**
	 * The ratio that is returned for the right side by the group similarity
	 * calculation.
	 */
	private float rightSimilarityRatio;

	/**
	 * Hash from the selection type to the ratio of the selection band with
	 * respect to the brick (which is 1).
	 */
	private HashMap<SelectionType, Float> hashSelectionTypeToRatio = new HashMap<SelectionType, Float>();

	public SubGroupMatch(int connectionBandID, GLBrick glBrick, Group group) {
		this.glBrick = glBrick;
		this.subGroup = group;

		this.connectionBandID = connectionBandID;
	}

	public void setLeftAnchorYStart(float leftAnchorYStart) {
		this.leftAnchorYStart = leftAnchorYStart;
	}

	public void setLeftAnchorYEnd(float leftAnchorYEnd) {
		this.leftAnchorYEnd = leftAnchorYEnd;
	}

	public void setRightAnchorYStart(float rightAnchorYStart) {
		this.rightAnchorYStart = rightAnchorYStart;
	}

	public void setRightAnchorYEnd(float rightAnchorYEnd) {
		this.rightAnchorYEnd = rightAnchorYEnd;
	}

	public float getLeftAnchorYTop() {
		return leftAnchorYStart;
	}

	public float getLeftAnchorYBottom() {
		return leftAnchorYEnd;
	}

	public float getRightAnchorYTop() {
		return rightAnchorYStart;
	}

	public float getRightAnchorYBottom() {
		return rightAnchorYEnd;
	}

	public GLBrick getBrick() {

		return glBrick;
	}

	/**
	 * @return the group, see {@link #subGroup}
	 */
	public Group getSubGroup() {
		return subGroup;
	}

	public void addSelectionTypeRatio(float ratio, SelectionType selectionType) {
		hashSelectionTypeToRatio.put(selectionType, ratio);
	}

	public HashMap<SelectionType, Float> getHashRatioToSelectionType() {
		return hashSelectionTypeToRatio;
	}

	public void setSimilarityRatioLeft(float leftSimilarityRatio) {
		this.leftSimilarityRatio = leftSimilarityRatio;
	}

	public void setSimilarityRatioRight(float rightSimilarityRatio) {
		this.rightSimilarityRatio = rightSimilarityRatio;
	}

	public float getLeftSimilarityRatio() {
		return leftSimilarityRatio;
	}

	public float getRightSimilarityRatio() {
		return rightSimilarityRatio;
	}

	public int getConnectionBandID() {
		return connectionBandID;
	}
}
