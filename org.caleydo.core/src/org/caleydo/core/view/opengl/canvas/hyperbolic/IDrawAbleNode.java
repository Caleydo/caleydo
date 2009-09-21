package org.caleydo.core.view.opengl.canvas.hyperbolic;

public class IDrawAbleNode
	implements Comparable<IDrawAbleNode> {

	String nodeName;
	int iComparableValue;
	float fXCoord;
	float fYCoord;
	float fLeftXBorder;
	float fRightXBorder;

	public IDrawAbleNode(String nodeName, int iComparableValue) {
		this.iComparableValue = iComparableValue;
		this.nodeName = nodeName;
	}

	public String getNodeName() {
		return nodeName;
	}

	@Override
	public int compareTo(IDrawAbleNode node) {
		return iComparableValue - node.iComparableValue;
	}

	@Override
	public String toString() {
		return nodeName + " " + iComparableValue;
	}
	
	public float getXCoord()
	{
		return fXCoord;
	}
	public float getYCoord()
	{
		return fYCoord;
	}

	public void setXCoord( float fx)
	{
		fXCoord = fx;
	}
	public void setYCoord(float fy)
	{
		fYCoord = fy;
	}
	
	public float getLeftBorderOfXCoord()
	{
		return fLeftXBorder;
	}
	
	public float getRightBorderOfXCoord()
	{
		return fRightXBorder;
	}
	
	
	public void setLeftBorderOfXCoord(float fXBorder)
	{
		fLeftXBorder = fXBorder;
	}
	public void setRightBorderOfXCoord(float fXBorder)
	{
		fRightXBorder = fXBorder;
	}
	


	
	
	

}
