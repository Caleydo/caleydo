package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import javax.media.opengl.GL;

public class TestNode
	extends ADefaultDrawableNode {

	public TestNode(String nodeName, int iComparableValue) {
		super(nodeName, iComparableValue);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int drawAtPostion(GL gl, float fXCoord, float fYCoord, float fHeight, float fWidth, ENodeDetailLevelType eDetailLevel) {
		
		gl.glColor4f(1, 0, 0, 0.5f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXCoord + fWidth, fYCoord + fHeight, 0.0f);
		gl.glVertex3f(fXCoord + fWidth, fYCoord - fHeight, 0.0f);
		gl.glVertex3f(fXCoord - fWidth, fYCoord - fHeight, 0.0f);
		gl.glVertex3f(fXCoord - fWidth, fYCoord + fHeight, 0.0f);
		gl.glEnd();
		gl.glFlush();
		
		
		
		return 0;
	}

	@Override
	public int setAlpha(float fAlpha) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setBgColor3f(float fRed, float fGreen, float fBlue) {
		// TODO Auto-generated method stub
		return 0;
	}

}
