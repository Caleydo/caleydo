package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import gleem.linalg.Vec3f;

public interface ITreeProjection 
extends Comparable<ITreeProjection>{
	
	public Vec3f projectCoordinates();

	public int getID();

}
