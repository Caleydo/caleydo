package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.DrawAbleLinearConnection;


public final class LinearLineFactory
	extends ALineFactory {
	
	float fMinDist = 200000;



	
	public LinearLineFactory(ArrayList<Vec3f> pointListA, ArrayList<Vec3f> pointListB) {
		super(pointListA, pointListB);
		findClosestPointsFromPointlists(pointListA, pointListB);
	}
	
	public void findClosestPointsFromPointlists(ArrayList<Vec3f> pointListA, ArrayList<Vec3f> pointListB) {

		float fTmpMinEuclidianDistance = 0;
		// ArrayList<Float> alDistances;

		for (Vec3f tmpPointA : pointListA) {
			for (Vec3f tmpPointB : pointListB) {
				fTmpMinEuclidianDistance =
					(float) Math.sqrt((Math.pow((tmpPointA.x() - tmpPointB.x()), 2) + (Math.pow((tmpPointA
						.y() - tmpPointB.y()), 2))));
				if (fTmpMinEuclidianDistance < fMinDist) {
					
//					if (isPointInUse(tmpPointB))
//						continue;
//					else
					{
					fMinDist = fTmpMinEuclidianDistance;
					pointA = tmpPointA;
					pointB = tmpPointB;
					}
				}
			}
		}
//		alPointsInUse.add(pointA);
//		alPointsInUse.add(pointB);

	}
	
	public Vec3f getStartPoint(){
		return pointA;
	}
	public Vec3f getEndPoint(){
		return pointB;
	}
	
//	public void buildLinearLine(ArrayList<Vec3f> alPointsA, ArrayList<Vec3f> alPointsB)
//	{
//		findClosestPointsFromPointlists(alPointsA, alPointsB);
//		DrawAbleLinearLine line = new DrawAbleLinearLine();
//		line.drawLineFromStartToEnd(dl, pointA, pointB, fThickness);
//		
//	}

}
