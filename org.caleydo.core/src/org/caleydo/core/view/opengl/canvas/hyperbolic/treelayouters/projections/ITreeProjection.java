package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import javax.media.opengl.GL;

import gleem.linalg.Vec3f;

public interface ITreeProjection 
extends Comparable<ITreeProjection>{
	
	public Vec3f projectCoordinates(Vec3f fvCoords);

	public int getID();
	
	public void drawCanvas(GL gl);

}
