package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import gleem.linalg.Vec3f;

import java.util.List;

public abstract class ADrawAbleConnection
	implements IDrawAbleConnection {

	private int iConnID;
	protected List<Vec3f> lPoints;

	public ADrawAbleConnection(int iConnID) {
		this.iConnID = iConnID;
	}

	@Override
	public final int getConnNr() {
		return iConnID;
	}

	@Override
	public final int compareTo(IDrawAbleConnection conn) {
		return iConnID - conn.getConnNr();
	}

	@Override
	public final void place(List<Vec3f> lPoints) {
		this.lPoints = lPoints;
	}

	// protected float fRed = 0;
	// protected float fGreen = 0;
	// protected float fBlue = 0;
	// protected float fAlpha = 1;
	// protected float fThickness = 0.1f;

	// @Override
	// public final void setHighlight(boolean b) {
	// this.bHighlight = b;
	// }

	// @Override
	// public final boolean isHighlighted(){
	// return this.bHighlight;
	// }
	// protected abstract void switchColorMapping(boolean b);

	// @Override
	// public final void setConnectionAlpha(float fAlpha) {
	// this.fAlpha = fAlpha;
	// }
	//
	// @Override
	// public final void setConnectionColor3f(float fRed, float fGreen, float fBlue) {
	// this.fRed = fRed;
	// this.fGreen = fGreen;
	// this.fBlue = fBlue;
	// }

	// private boolean bHighlight;

	// @Override
	// public abstract void drawConnectionFromStartToEnd(GL gl, List<Vec3f> lPoints, float fThickness);

}
