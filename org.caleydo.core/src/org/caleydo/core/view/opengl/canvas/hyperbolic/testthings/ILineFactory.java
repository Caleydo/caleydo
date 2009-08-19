package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

import gleem.linalg.Vec3f;

import java.util.ArrayList;


public interface ILineFactory {
	
	public void findClosestPointsFromPointlists(ArrayList<Vec3f> pPointA, ArrayList<Vec3f> pPointB);
	
	public boolean isPointInUse(Vec3f pPoint);

}
