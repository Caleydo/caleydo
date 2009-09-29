package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import gleem.linalg.Vec3f;

import java.util.List;

public abstract class ATreeProjection
	implements ITreeProjection {

	private final int iID;
	protected float fHeight = 0;
	protected float fWidth = 0;
	protected float fDepth = 0;
	protected float[] fViewSpaceX = {};
	protected float fViewSpaceXAbs = 0;
	protected float[] fViewSpaceY = {};
	protected float fViewSpaceYAbs = 0;
	
	protected Vec3f fCenterPoint;
	protected float radius;
	
//	public ATreeProjection(int iID, float fHeight, float fWidth, float fDepth, float[] fViewSpaceX,
//		float fViewSpaceXAbs, float[] fViewSpaceY, float fViewSpaceYAbs) {
//		this.iID = iID;
//		this.fHeight = fHeight;
//		this.fWidth = fWidth;
//		this.fDepth = fDepth;
//		this.fViewSpaceX = fViewSpaceX;
//		this.fViewSpaceXAbs = fViewSpaceXAbs;
//		this.fViewSpaceY = fViewSpaceY;
//		this.fViewSpaceYAbs = fViewSpaceYAbs;
//	}

	public ATreeProjection(int iID) {
		this.iID = iID;
	}

	@Override
	public final int compareTo(ITreeProjection o) {
		return this.iID - o.getID();
	}

	@Override
	public final int getID() {
		return iID;
	}

	@Override
	public void updateFrustumInfos(float fHeight, float fWidth, float fDepth, float[] fViewSpaceX,
		float fViewSpaceXAbs, float[] fViewSpaceY, float fViewSpaceYAbs) {
		this.fHeight = fHeight;
		this.fWidth = fWidth;
		this.fDepth = fDepth;
		this.fViewSpaceX = fViewSpaceX;
		this.fViewSpaceXAbs = fViewSpaceXAbs;
		this.fViewSpaceY = fViewSpaceY;
		this.fViewSpaceYAbs = fViewSpaceYAbs;
		fCenterPoint = new Vec3f(fWidth / 2.0f, fHeight / 2.0f, fDepth);
		radius = Math.min(fViewSpaceXAbs, fViewSpaceYAbs) / 2.0f;
	}
	//@Override
	//public float getProjectedLineFromCenterToBorder(){
	//	return Math.min(fViewSpaceXAbs, fViewSpaceYAbs);
	//}

}
