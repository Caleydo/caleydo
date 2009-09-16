package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

public abstract class ATreeProjection
	implements ITreeProjection {

	private final int iID;
	
	public ATreeProjection(int iID){
		this.iID = iID;
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
