package org.caleydo.view.visbricks.dimensiongroup;

import java.util.HashMap;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.visbricks.brick.GLBrick;


public class SubGroupMatch {
	
	private GLBrick glBrick;
	
	private float leftAnchorYStart;

	private float leftAnchorYEnd;
	
	private float rightAnchorYStart;
	
	private float rightAnchorYEnd;
	
	/**
	 * The ratio that is returned for the left side by the group similarity calculation.
	 */
	private float leftSimilarityRatio;
	
	/**
	 * The ratio that is returned for the right side by the group similarity calculation.
	 */
	private float rightSimilarityRatio;

	/**
	 * Hash from the selection type to the ratio of the selection band with respect to the brick (which is 1).
	 */
	private HashMap<SelectionType, Float> hashSelectionTypeToRatio = new HashMap<SelectionType, Float>();
	
	public SubGroupMatch(GLBrick glBrick) {
		this.glBrick = glBrick;
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
}
