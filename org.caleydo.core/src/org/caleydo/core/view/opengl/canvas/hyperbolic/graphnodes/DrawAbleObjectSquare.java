package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

/**
 * Draw able object: SQUARE
 * 
 * @author Georg Neubauer
 */
public class DrawAbleObjectSquare
	extends ADrawAbleObject {

	@Override
	public ArrayList<Vec3f> drawObjectAtPosition(GL gl, float fXCoord, float fYCoord, float fZCoord,
		float fHeight, float fWidth) {
		// Just a little example, draws polygon
		gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXCoord + fWidth, fYCoord + fHeight, 0.0f);
		gl.glVertex3f(fXCoord + fWidth, fYCoord - fHeight, 0.0f);
		gl.glVertex3f(fXCoord - fWidth, fYCoord - fHeight, 0.0f);
		gl.glVertex3f(fXCoord - fWidth, fYCoord + fHeight, 0.0f);
		gl.glEnd();
		gl.glFlush();
		// TODO: calculate points
		return null;
	}
}
