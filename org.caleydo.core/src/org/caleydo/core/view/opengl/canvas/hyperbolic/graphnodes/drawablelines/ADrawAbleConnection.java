package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import gleem.linalg.Vec3f;

import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;

public abstract class ADrawAbleConnection
	implements IDrawAbleConnection {

	private int iID;
	protected IDrawAbleNode iNodeA;
	protected IDrawAbleNode iNodeB;

	public ADrawAbleConnection(IDrawAbleNode iNodeA, IDrawAbleNode iNodeB) {
		this.iNodeA = iNodeA;
		this.iNodeB = iNodeB;
		this.iID = generateID(iNodeA.getID(), iNodeB.getID());
	}

	@Override
	public final int getID() {
		return iID;
	}

	@Override
	public final int compareTo(IDrawAbleConnection conn) {
		return iID - conn.getID();
	}

	private int generateID(int iID1, int iID2) {
		int left = (iID1 >= iID2 ? iID1 : iID2);
		int right = (iID1 < iID2 ? iID1 : iID2);
		return ((left << 12) | (left >> (32 - 12))) ^ right;
	}
	
	protected final Vec3f[] findClosestCorrespondendingPoints(){
		float fMin = Float.MAX_VALUE;
		Vec3f foundA = null;
		Vec3f foundB = null;
		float ft;
		for(Vec3f pointA : iNodeA.getConnectionPoints())
			for(Vec3f pointB : iNodeB.getConnectionPoints())
				if((ft = (float) Math.sqrt(Math.pow(pointA.x()-pointB.x(), 2)+Math.pow(pointA.y()-pointB.y(), 2))) < fMin)
				{
					foundA = pointA;
					foundB = pointB;
					fMin = ft;
				}
		Vec3f[] vaPoints = {foundA, foundB}; 
		return vaPoints;
	}
	protected final Vec3f[] findClosestCorrespondingPointsOfOriginalPosition(){
		float fMin = Float.MAX_VALUE;
		Vec3f foundA = null;
		Vec3f foundB = null;
		float ft;
		for(Vec3f pointA : iNodeA.getConnectionPointsOfOriginalPosition())
			for(Vec3f pointB : iNodeB.getConnectionPointsOfOriginalPosition())
				if((ft = (float) Math.sqrt(Math.pow(pointA.x()-pointB.x(), 2)+Math.pow(pointA.y()-pointB.y(), 2))) < fMin)
				{
					foundA = pointA;
					foundB = pointB;
					fMin = ft;
				}
		Vec3f[] vaPoints = {foundA, foundB}; 
		return vaPoints;

	}
	
	
//	@Override
//	public final void place(List<Vec3f> lPoints) {
//		this.lPoints = lPoints;
//	}

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
