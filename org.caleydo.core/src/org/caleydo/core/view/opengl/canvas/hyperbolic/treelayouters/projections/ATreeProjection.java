package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

public abstract class ATreeProjection
	implements ITreeProjection {

	private final int iID;
	protected final float fHeight;
	protected final float fWidth;
	protected final float fDepth;
	protected final float[] fViewSpaceX;
	protected final float fViewSpaceXAbs;
	protected final float[] fViewSpaceY;
	protected final float fViewSpaceYAbs;
	
	
	public ATreeProjection(int iID,float fHeight, float fWidth, float fDepth, float[] fViewSpaceX,
		float fViewSpaceXAbs, float[] fViewSpaceY,float fViewSpaceYAbs){
		this.iID = iID;
		this.fHeight = fHeight;
		this.fWidth = fWidth;
		this.fDepth = fDepth;
		this.fViewSpaceX = fViewSpaceX;
		this.fViewSpaceXAbs = fViewSpaceXAbs;
		this.fViewSpaceY = fViewSpaceY;
		this.fViewSpaceYAbs = fViewSpaceYAbs;
		
	}
	
	@Override
	public final int compareTo(ITreeProjection o) {
		return this.iID - o.getID();
	}
	
	@Override
	public final int getID(){
		return iID;
	}
	

}
