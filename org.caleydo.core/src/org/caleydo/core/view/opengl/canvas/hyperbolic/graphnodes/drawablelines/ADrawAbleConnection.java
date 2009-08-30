package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import gleem.linalg.Vec3f;
import java.util.List;



public abstract class ADrawAbleConnection
	implements IDrawAbleConnection {

	protected float fRed = 0;
	protected float fGreen = 0;
	protected float fBlue = 0;
	protected float fAlpha = 1;
	protected float fThickness = 0.1f;

	private int iConnID;
	protected boolean bHighlight;
	protected List<Vec3f> lPoints;

	// @Override
	// public abstract void drawConnectionFromStartToEnd(GL gl, List<Vec3f> lPoints, float fThickness);

	public ADrawAbleConnection(int iConnID) {
		this.iConnID = iConnID;
	}

	@Override
	public final void setConnectionAlpha(float fAlpha) {
		this.fAlpha = fAlpha;
	}

	@Override
	public final void setConnectionColor3f(float fRed, float fGreen, float fBlue) {
		this.fRed = fRed;
		this.fGreen = fGreen;
		this.fBlue = fBlue;
	}

	@Override
	public final int getConnNr() {
		return iConnID;
	}

	public final int compareTo(IDrawAbleConnection conn) {
		return iConnID - conn.getConnNr();
	}

	@Override
	public abstract void setHighlight(boolean b);

	// {
	// this.bHighlight = b;
	// }

	@Override
	public final void place(List<Vec3f> lPoints) {
		this.lPoints = lPoints;
	}

	// protected abstract void switchColorMapping(boolean b);
}
