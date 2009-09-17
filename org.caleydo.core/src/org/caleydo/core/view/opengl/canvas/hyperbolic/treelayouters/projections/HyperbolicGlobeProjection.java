package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import javax.media.opengl.GL;

import gleem.linalg.Vec3f;

public class HyperbolicGlobeProjection
	extends ATreeProjection {

	Vec3f fCenterPoint;
	float radius;

	public HyperbolicGlobeProjection(int iID, float height, float width, float depth, float[] viewSpaceX,
		float viewSpaceXAbs, float[] viewSpaceY, float viewSpaceYAbs) {
		super(iID, height, width, depth, viewSpaceX, viewSpaceXAbs, viewSpaceY, viewSpaceYAbs);
		
		fCenterPoint = new Vec3f(width / 2.0f, height / 2.0f, depth);
		radius = Math.min(viewSpaceXAbs, viewSpaceYAbs) / 2.0f;
		
	}

	@Override
	public Vec3f projectCoordinates(Vec3f fvCoords) {
		
		float fNewXCoordinate = 0;
		float fNewYCoordinate = 0;
		float fXCoordOfPoint = fvCoords.x();
		float fYCoordOfPoint = fvCoords.y();
		
		float fDistanceInXDirection = fCenterPoint.x() - fXCoordOfPoint;
		float fDistanceInYDirection = fCenterPoint.y() - fYCoordOfPoint;
		
		float fXAngle = Math.abs(fDistanceInXDirection/radius);
		float fYAngle = Math.abs(fDistanceInYDirection/radius);
		
//		float fProjectedXDistance = ((float)Math.sin(fXAngle)*radius);
//		float fProjectedYDistance = ((float)Math.sin(fYAngle)*radius);
		
//		float fProjectedXDistance = ((float)Math.cos(fXAngle)*(float)Math.cos(fYAngle)*radius);
//		float fProjectedYDistance = ((float)Math.cos(fXAngle)*(float)Math.sin(fYAngle)*radius);
		
		float fProjectedXDistance = ((float)Math.cos(fXAngle)*(float)Math.cos(fYAngle)*radius);
		float fProjectedYDistance = ((float)Math.cos(fXAngle)*(float)Math.sin(fYAngle)*radius);
		
		if(fDistanceInXDirection < 0)
			fNewXCoordinate = fCenterPoint.x() + fProjectedXDistance;
		else
			fNewXCoordinate = fCenterPoint.x() - fProjectedXDistance;

			
		if(fDistanceInYDirection < 0)
			fNewYCoordinate = fCenterPoint.y() + fProjectedYDistance;
		else
			fNewYCoordinate = fCenterPoint.y() - fProjectedYDistance;
		
		float fZCoordOfProjectedPoint = 0;
		if(fXAngle >= -(float)Math.PI/2 || fXAngle <= (float)Math.PI/2)
			fZCoordOfProjectedPoint = 2.0f;
		else
			fZCoordOfProjectedPoint = -2.0f;
		Vec3f newPoint = new Vec3f();
		

//		fNewXCoordinate =  (fXCoordOfPoint - fCenterPoint.x()) / 4;
//		fNewYCoordinate = -((fYCoordOfPoint - fCenterPoint.y()) / 4);
		newPoint.set(fNewXCoordinate, fNewYCoordinate, fZCoordOfProjectedPoint);
		
		return newPoint;
	}
	
//	@Override
//	public Vec3f projectCoordinates(Vec3f fvCoords) {
//		
//		float fNewXCoordinate = 0;
//		float fNewYCoordinate = 0;
//		float fXCoordOfPoint = fvCoords.x();
//		float fYCoordOfPoint = fvCoords.y();
//		
//		float fDistanceInXDirection = fCenterPoint.x() - fXCoordOfPoint;
//		float fDistanceInYDirection = fCenterPoint.y() - fYCoordOfPoint;
//		
//		float fy = (float) Math.sqrt(Math.pow(fDistanceInXDirection, 2)+Math.pow(fDistanceInYDirection, 2));
//		
//		float fAngle = fy/radius;
//		float fProjectedDistance = (float)Math.sin(fAngle);
//		
//		
//		if(fDistanceInXDirection < 0)
//			fNewXCoordinate = fCenterPoint.x() + calculateLineLenth(fCenterPoint.x(), fXCoordOfPoint);
//		else
//			fNewXCoordinate = fCenterPoint.x() - calculateLineLenth(fCenterPoint.x(), fXCoordOfPoint);
//
//			
//		if(fDistanceInYDirection < 0)
//			fNewYCoordinate = fCenterPoint.y() + calculateLineLenth(fCenterPoint.y(), fYCoordOfPoint);
//		else
//			fNewYCoordinate = fCenterPoint.y() - calculateLineLenth(fCenterPoint.y(), fYCoordOfPoint);
//		
//		Vec3f newPoint = new Vec3f();
//		newPoint.set(fNewXCoordinate, fNewYCoordinate, 0.0f);
//		
//		return newPoint;
//	}
	
	private float calculateLineLenth(float fCenterPoint, float fLinePoint){


		float fDistanceFromCenterToPointOnGlobe = Math.abs(fCenterPoint - fLinePoint);

		float fAngle = fDistanceFromCenterToPointOnGlobe/radius;
		float fProjectedDistance = (float)Math.sin(fAngle);
		
		return fProjectedDistance;
	}

	@Override
	public void drawCanvas(GL gl) {
		
		
//		gl.glAlphaFunc(1, 0);
		
//		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);		
//		gl.glEnable(GL.GL_BLEND);						
//		gl.glAlphaFunc(GL.GL_GREATER,0.1f);
//		gl.glEnable(GL.GL_ALPHA_TEST);
		
		gl.glEnable(GL.GL_BLEND);			
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);		
		gl.glColor4f(0, 1, 1, 0.3f);
		gl.glLineWidth(2);
		
		gl.glBegin(GL.GL_POLYGON);
		//gl.glBegin(GL.GL_LINE_LOOP);
		for (int i = 0; i < 180; i++) {
			float angle = (float) (i * 2 * Math.PI / 180f);
			gl.glVertex3f((float) (fCenterPoint.x() + Math.cos(angle) * radius), (float) (fCenterPoint.y() + Math.sin(angle)
				* radius), -0.1f);
		}
		gl.glEnd();
		
	}

}
