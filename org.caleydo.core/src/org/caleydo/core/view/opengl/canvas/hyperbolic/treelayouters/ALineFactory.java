package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

public abstract class ALineFactory
	implements ILineFactory {

	protected Vec3f pointA = null;
	protected Vec3f pointB = null;

	ArrayList<Vec3f> alPointsInUse = null;

	public ALineFactory(ArrayList<Vec3f> pointListA, ArrayList<Vec3f> pointListB) {

		
	}

	@Override
	public abstract void findClosestPointsFromPointlists(ArrayList<Vec3f> pointListA, ArrayList<Vec3f> pointListB);

	@Override
	public boolean isPointInUse(Vec3f point) {

		for (Vec3f tmpPoint : alPointsInUse) {
			if (tmpPoint == point)
				return true;
		}
		return false;
	}

}
