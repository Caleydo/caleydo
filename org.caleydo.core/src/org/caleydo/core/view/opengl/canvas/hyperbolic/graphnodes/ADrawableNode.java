package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;



import javax.media.opengl.GL;

public abstract class ADrawableNode
//	extends DefaultNode
	implements IDrawableNode, Comparable<ADrawableNode> {
	String nodeName;
	int iComparableValue;

	protected float fRed = 0;
	protected float fGreen = 0;
	protected float fBlue = 0;
	protected float fAlpha = 1;

	protected float fXCoord = 0;
	protected float fYCoord = 0;

	protected float fHeight = 0;
	protected float fWidth = 0;
	
	protected GL gl;

	public ADrawableNode(String nodeName, int iComparableValue) {
	//	super(nodeName, iComparableValue);
		this.nodeName = nodeName;
		this.iComparableValue = iComparableValue;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	@Override
	public int compareTo(ADrawableNode node) {
		return this.iComparableValue - node.iComparableValue;
	}

	@Override
	public String toString() {
		return this.nodeName + " " + this.iComparableValue;
	}

	// TODO: needs implementation of GLList
	
	@Override
	public final void drawAtPostion(GL gl, float fXCoord, float fYCoord, float fHeight, float fWidth,
		EDrawAbleNodeDetailLevel eDetailLevel) {
		this.fXCoord = fXCoord;
		this.fYCoord = fYCoord;
		this.fHeight = fHeight;
		this.fWidth = fWidth;
		this.gl = gl;
		
		switch(eDetailLevel){
			case VeryHigh: drawDetailLevelVeryHigh(); break; 
			case High: drawDetailLevelHigh(); break;
			case Normal: drawDetailLevelNormal(); break;
			case Low: drawDetailLevelNormal(); break;
			case VeryLow: drawDetailLevelVeryLow(); break;
		}
	}

	@Override
	public final void setAlpha(float fAlpha) {
		this.fAlpha = fAlpha;
	}

	@Override
	public final void setBgColor3f(float fRed, float fGreen, float fBlue) {
		this.fRed = fRed;
		this.fGreen = fGreen;
		this.fBlue = fBlue;
	}
	
	/**
	 * Draw node in very high detail level.
	 */
	protected abstract void drawDetailLevelVeryHigh();
	
	protected abstract void drawDetailLevelHigh();
	protected abstract void drawDetailLevelNormal();
	protected abstract void drawDetailLevelLow();
	protected abstract void drawDetailLevelVeryLow();
	
}
