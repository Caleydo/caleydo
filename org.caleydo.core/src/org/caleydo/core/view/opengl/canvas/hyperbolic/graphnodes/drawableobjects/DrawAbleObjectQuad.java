package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

/**
 * Draw able object: SQUARE
 * 
 * @author Georg Neubauer
 */
public class DrawAbleObjectQuad
	extends ADrawAbleObject {

	@Override
	public ArrayList<Vec3f> draw(GL gl, boolean bHighlight) {
		float fSideL = Math.min(fWidth, fHeight) / 2f;
		if(bIsAbleToPick)
			if (bHighlight)
				gl.glColor4fv(HyperbolicRenderStyle.DA_OBJ_QUAD_COLORSCHEME_HL, 0);
			else
				gl.glColor4fv(HyperbolicRenderStyle.DA_OBJ_QUAD_COLORSCHEME, 0);
		else
			gl.glColor4fv(HyperbolicRenderStyle.DA_OBJ_QUAD_COLORSCHEME_NO_PICK, 0);
			
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(fXCoord + fSideL, fYCoord + fSideL, fZCoord);
		gl.glVertex3f(fXCoord + fSideL, fYCoord - fSideL, fZCoord);
		gl.glVertex3f(fXCoord - fSideL, fYCoord - fSideL, fZCoord);
		gl.glVertex3f(fXCoord - fSideL, fYCoord + fSideL, fZCoord);
		gl.glEnd();
		return getConnectionPoints();
	}

	@Override
	public ArrayList<Vec3f> getConnectionPoints() {
		ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
		int iSegPerLine = HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS / 4;
		float fSideL = Math.min(fWidth, fHeight) / 2f;
		// first add corners
		alPoints.add(new Vec3f(fXCoord + fSideL, fYCoord + fSideL, fZCoord));
		alPoints.add(new Vec3f(fXCoord + fSideL, fYCoord - fSideL, fZCoord));
		alPoints.add(new Vec3f(fXCoord - fSideL, fYCoord + fSideL, fZCoord));
		alPoints.add(new Vec3f(fXCoord - fSideL, fYCoord - fSideL, fZCoord));
		// up, down
		for (int i = 1; i < iSegPerLine; i++) {
			alPoints.add(new Vec3f(fXCoord + fSideL - fWidth / iSegPerLine * i, fYCoord + fSideL, fZCoord));
			alPoints.add(new Vec3f(fXCoord + fSideL - fWidth / iSegPerLine * i, fYCoord - fSideL, fZCoord));
		}
		// left, right
		for (int i = 1; i < iSegPerLine; i++) {
			alPoints.add(new Vec3f(fXCoord + fSideL, fYCoord + fSideL - fHeight / iSegPerLine * i, fZCoord));
			alPoints.add(new Vec3f(fXCoord - fSideL, fYCoord + fSideL - fHeight / iSegPerLine * i, fZCoord));
		}
		return alPoints;
	}

	// @Override
	// protected void switchColorMapping(boolean b) {
	// if (b) {
	// this.fRed = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME_HL[0];
	// this.fGreen = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME_HL[1];
	// this.fBlue = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME_HL[2];
	// this.fAlpha = HyperbolicRenderStyle.DA_OBJ_SQUARE_ALPHA_HL;
	// }
	// else {
	// this.fRed = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME[0];
	// this.fGreen = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME[1];
	// this.fBlue = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME[2];
	// this.fAlpha = HyperbolicRenderStyle.DA_OBJ_SQUARE_ALPHA;
	// }
	// }

	// public DrawAbleObjectSquare() {
	// this.fRed = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME[0];
	// this.fGreen = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME[1];
	// this.fBlue = HyperbolicRenderStyle.DA_OBJ_SQUARE_COLORSCHEME[2];
	// this.fAlpha = HyperbolicRenderStyle.DA_OBJ_SQUARE_ALPHA;
	// }
	//	
	// @Override
	// public ArrayList<Vec3f> drawObjectAtPosition(GL gl, float fXCoord, float fYCoord, float fZCoord,
	// float fHeight, float fWidth) {
	// float fSideL = Math.min(fWidth, fHeight) / 2f;
	// gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
	// gl.glBegin(GL.GL_POLYGON);
	// gl.glVertex3f(fXCoord + fSideL, fYCoord + fSideL, fZCoord);
	// gl.glVertex3f(fXCoord + fSideL, fYCoord - fSideL, fZCoord);
	// gl.glVertex3f(fXCoord - fSideL, fYCoord - fSideL, fZCoord);
	// gl.glVertex3f(fXCoord - fSideL, fYCoord + fSideL, fZCoord);
	// gl.glEnd();
	//
	// ArrayList<Vec3f> alPoints = new ArrayList<Vec3f>();
	//
	// int iSegPerLine = HyperbolicRenderStyle.DA_OBJ_NUM_CONTACT_POINTS / 4;
	//
	// // first add corners
	// alPoints.add(new Vec3f(fXCoord + fSideL, fYCoord + fSideL, fZCoord));
	// alPoints.add(new Vec3f(fXCoord + fSideL, fYCoord - fSideL, fZCoord));
	// alPoints.add(new Vec3f(fXCoord - fSideL, fYCoord + fSideL, fZCoord));
	// alPoints.add(new Vec3f(fXCoord - fSideL, fYCoord - fSideL, fZCoord));
	//
	// // up, down
	// for (int i = 1; i < iSegPerLine; i++) {
	// alPoints.add(new Vec3f(fXCoord + fSideL - fWidth / iSegPerLine * i, fYCoord + fSideL,
	// fZCoord));
	// alPoints.add(new Vec3f(fXCoord + fSideL - fWidth / iSegPerLine * i, fYCoord - fSideL,
	// fZCoord));
	// }
	//
	// // left, right
	// for (int i = 1; i < iSegPerLine; i++) {
	// alPoints.add(new Vec3f(fXCoord + fSideL, fYCoord + fSideL - fHeight / iSegPerLine * i,
	// fZCoord));
	// alPoints.add(new Vec3f(fXCoord - fSideL, fYCoord + fSideL - fHeight / iSegPerLine * i,
	// fZCoord));
	// }
	// return alPoints;
	// }
}
