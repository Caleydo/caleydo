package org.caleydo.view.visbricks.dimensiongroup;


public class SubGroupMatch {

	private int subGroupID;
	
	private float leftAnchorYStart;

	private float leftAnchorYEnd;
	
	private float rightAnchorYStart;
	
	private float rightAnchorYEnd;
	
	public SubGroupMatch(int subGroupID) {
		this.subGroupID = subGroupID;
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
}
