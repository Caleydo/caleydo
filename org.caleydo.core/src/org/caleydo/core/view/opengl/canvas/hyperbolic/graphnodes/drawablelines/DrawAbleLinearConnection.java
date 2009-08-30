package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

public final class DrawAbleLinearConnection
	extends ADrawAbleConnection {

	// TODO: define line color scheme!!!
	public DrawAbleLinearConnection(int iConnID) {
		super(iConnID);
		this.fRed = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[0];
		this.fGreen = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[1];
		this.fBlue = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[2];
		this.fAlpha = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_ALPHA;
	}

	// TODO: maybe define thickness in renderstyle
	// @Override
	// public void drawConnectionFromStartToEnd(GL gl, List<Vec3f> lPoints, float fThickness) {
	// gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
	// gl.glLineWidth(fThickness);
	//		
	// for (int i = 0; i < lPoints.size()-1; i++){
	// gl.glBegin(GL.GL_LINE);
	// gl.glVertex3f(lPoints.get(i).x(), lPoints.get(i).y(), lPoints.get(i).z());
	// gl.glVertex3f(lPoints.get(i+1).x(), lPoints.get(i+1).y(), lPoints.get(i+1).z());
	// gl.glEnd();
	// }
	// }

	@Override
	public void setHighlight(boolean b) {
		this.bHighlight = b;
		if (b) {
			this.fRed = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME_HL[0];
			this.fGreen = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME_HL[1];
			this.fBlue = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME_HL[2];
			this.fAlpha = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_ALPHA_HL;
			this.fThickness = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_THICKNESS_HL;
		}
		else {
			this.fRed = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[0];
			this.fGreen = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[1];
			this.fBlue = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_COLORSHEME[2];
			this.fAlpha = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_ALPHA;
			this.fThickness = HyperbolicRenderStyle.DA_LINEAR_CONNECTION_THICKNESS;
		}
	}

	@Override
	public void draw(GL gl) {
		gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
		gl.glLineWidth(fThickness);

		for (int i = 0; i < lPoints.size() - 1; i++) {
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(lPoints.get(i).x(), lPoints.get(i).y(), lPoints.get(i).z());
			gl.glVertex3f(lPoints.get(i + 1).x(), lPoints.get(i + 1).y(), lPoints.get(i + 1).z());
			gl.glEnd();
		}
	}

}
