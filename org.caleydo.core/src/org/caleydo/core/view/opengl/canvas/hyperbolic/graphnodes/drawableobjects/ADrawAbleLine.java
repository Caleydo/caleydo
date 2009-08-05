package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

public abstract class ADrawAbleLine
	implements IDrawAbleLine {
	
	protected float fRed = 0;
	protected float fGreen = 0;
	protected float fBlue = 0;
	protected float fAlpha = 1;

	@Override
	public abstract void drawLineFromStartToEnd(GL gl, Vec3f pStartPoint, Vec3f pEndPoint, float fThickness);

	@Override
	public final void setLineAlpha(float fAlpha) {
		this.fAlpha = fAlpha;

	}

	@Override
	public final void setLineColor3f(float fRed, float fGreen, float fBlue) {
		this.fRed = fRed;
		this.fGreen = fGreen;
		this.fBlue = fBlue;
	}

}
