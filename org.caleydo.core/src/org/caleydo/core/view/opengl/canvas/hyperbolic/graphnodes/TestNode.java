package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import javax.media.opengl.GL;

/**
 * Implementation of a drawable TestNode, just to show how our nodes would work.
 * 
 * @author Georg Neubauer
 *
 */
public final class TestNode
	extends ADrawableNode {

	public TestNode(String nodeName, int iComparableValue) {
		super(nodeName, iComparableValue);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public void drawAtPostion(GL gl, float fXCoord, float fYCoord, float fHeight, float fWidth, EDrawAbleNodeDetailLevel eDetailLevel) {
//		
//		this.fXCoord = fXCoord;
//		this.fYCoord = fYCoord;
//		this.fHeight = fHeight;
//		this.fWidth = fWidth;
//		this.gl = gl;
//		
//		switch(eDetailLevel){
//			case VeryHigh: 
//			case High:
//			case Normal:
//			case Low:
//			case VeryLow:
//		}
//		
//		
//		return 0;
//	}

	@Override
	protected void drawDetailLevelHigh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void drawDetailLevelLow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void drawDetailLevelNormal() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void drawDetailLevelVeryHigh() {
		// Just a little example, draws polygon
		gl.glColor4f(1, 0, 0, 0.5f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXCoord + fWidth, fYCoord + fHeight, 0.0f);
		gl.glVertex3f(fXCoord + fWidth, fYCoord - fHeight, 0.0f);
		gl.glVertex3f(fXCoord - fWidth, fYCoord - fHeight, 0.0f);
		gl.glVertex3f(fXCoord - fWidth, fYCoord + fHeight, 0.0f);
		gl.glEnd();
		gl.glFlush();
	}

	@Override
	protected void drawDetailLevelVeryLow() {
		// TODO Auto-generated method stub
		
	}



}
