package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.swing.text.Segment;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

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
		float fDrawWidth = fWidth/2f;
		float fDrawHeight = fHeight / 2f;
		gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXCoord + fDrawWidth, fYCoord + fDrawHeight, fZCoord);
		gl.glVertex3f(fXCoord + fDrawWidth, fYCoord - fDrawHeight, fZCoord);
		gl.glVertex3f(fXCoord - fDrawWidth, fYCoord - fDrawHeight, fZCoord);
		gl.glVertex3f(fXCoord - fDrawWidth, fYCoord + fDrawHeight, fZCoord);
		gl.glEnd();
		gl.glFlush();

		ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();

		int iSegPerLine = HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS / 4;

		// first add corners
		alPoints.add(new Vec3f(fXCoord + fDrawWidth, fYCoord + fDrawHeight, fZCoord));
		alPoints.add(new Vec3f(fXCoord + fDrawWidth, fYCoord - fDrawHeight, fZCoord));
		alPoints.add(new Vec3f(fXCoord - fDrawWidth, fYCoord + fDrawHeight, fZCoord));
		alPoints.add(new Vec3f(fXCoord - fDrawWidth, fYCoord - fDrawHeight, fZCoord));

		// up, down
		for (int i = 1; i < iSegPerLine; i++) {
			alPoints.add(new Vec3f(fXCoord + fDrawWidth - fWidth / iSegPerLine * i , fYCoord + fDrawHeight, fZCoord));
			alPoints.add(new Vec3f(fXCoord + fDrawWidth - fWidth / iSegPerLine * i, fYCoord - fDrawHeight, fZCoord));
		}
		
		// left, right
		for (int i = 1; i < iSegPerLine; i++) {
			alPoints.add(new Vec3f(fXCoord + fDrawWidth, fYCoord + fDrawHeight - fHeight / iSegPerLine * i, fZCoord));
			alPoints.add(new Vec3f(fXCoord - fDrawWidth, fYCoord + fDrawHeight - fHeight / iSegPerLine * i, fZCoord));
		}
		return alPoints;
	}
}
