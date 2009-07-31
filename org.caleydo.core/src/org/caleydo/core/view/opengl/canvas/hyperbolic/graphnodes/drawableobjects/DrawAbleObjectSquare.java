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

	public DrawAbleObjectSquare() {
		this.fRed = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME[0];
		this.fGreen = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME[1];
		this.fBlue = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME[2];
		this.fAlpha = HyperbolicRenderStyle.DA_OBJ_SQUARE_ALPHA;
	}
	
	@Override
	public ArrayList<Vec3f> drawObjectAtPosition(GL gl, float fXCoord, float fYCoord, float fZCoord,
		float fHeight, float fWidth) {
		float fSideL = Math.min(fWidth, fHeight) / 2f;
		gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXCoord + fSideL, fYCoord + fSideL, fZCoord);
		gl.glVertex3f(fXCoord + fSideL, fYCoord - fSideL, fZCoord);
		gl.glVertex3f(fXCoord - fSideL, fYCoord - fSideL, fZCoord);
		gl.glVertex3f(fXCoord - fSideL, fYCoord + fSideL, fZCoord);
		gl.glEnd();
		gl.glFlush();

		ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();

		int iSegPerLine = HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS / 4;

		// first add corners
		alPoints.add(new Vec3f(fXCoord + fSideL, fYCoord + fSideL, fZCoord));
		alPoints.add(new Vec3f(fXCoord + fSideL, fYCoord - fSideL, fZCoord));
		alPoints.add(new Vec3f(fXCoord - fSideL, fYCoord + fSideL, fZCoord));
		alPoints.add(new Vec3f(fXCoord - fSideL, fYCoord - fSideL, fZCoord));

		// up, down
		for (int i = 1; i < iSegPerLine; i++) {
			alPoints.add(new Vec3f(fXCoord + fSideL - fWidth / iSegPerLine * i, fYCoord + fSideL,
				fZCoord));
			alPoints.add(new Vec3f(fXCoord + fSideL - fWidth / iSegPerLine * i, fYCoord - fSideL,
				fZCoord));
		}

		// left, right
		for (int i = 1; i < iSegPerLine; i++) {
			alPoints.add(new Vec3f(fXCoord + fSideL, fYCoord + fSideL - fHeight / iSegPerLine * i,
				fZCoord));
			alPoints.add(new Vec3f(fXCoord - fSideL, fYCoord + fSideL - fHeight / iSegPerLine * i,
				fZCoord));
		}
		return alPoints;
	}
}
