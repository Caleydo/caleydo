package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;
import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

public final class DrawAbleLinearLine
	extends ADrawAbleLine {

	// TODO: define line color scheme!!!
	public DrawAbleLinearLine() {
		this.fRed = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[0];
		this.fGreen = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[1];
		this.fBlue = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[2];
		this.fAlpha = HyperbolicRenderStyle.DA_OBJ_FALLBACK_ALPHA;
	}

	// TODO: maybe define thickness in renderstyle
	@Override
	public void drawLineFromStartToEnd(GL gl, Vec3f pStartPoint, Vec3f pEndPoint, float fThickness) {
		gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
		gl.glLineWidth(fThickness);
		gl.glBegin(GL.GL_LINE);
		gl.glVertex3f(pStartPoint.x(), pStartPoint.y(), pStartPoint.z());
		gl.glVertex3f(pEndPoint.x(), pEndPoint.y(), pEndPoint.z());
		gl.glEnd();
		gl.glFlush();
	}

}
