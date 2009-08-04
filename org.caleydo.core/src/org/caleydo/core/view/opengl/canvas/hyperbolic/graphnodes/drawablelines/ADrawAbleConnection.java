package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

public abstract class ADrawAbleConnection
	implements IDrawAbleConnection {
	
	protected float fRed = 0;
	protected float fGreen = 0;
	protected float fBlue = 0;
	protected float fAlpha = 1;

	@Override
	public abstract void drawLineFromStartToEnd(GL gl, List<Vec3f> lPoints, float fThickness);

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
