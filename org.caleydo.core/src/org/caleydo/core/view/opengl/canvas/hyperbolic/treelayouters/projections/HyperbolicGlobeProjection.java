package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections;

import java.util.List;

import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

public class HyperbolicGlobeProjection
	extends ATreeProjection {

	public HyperbolicGlobeProjection(int iID) {
		super(iID);
	}

	// public HyperbolicGlobeProjection(int iID, float height, float width, float depth, float[] viewSpaceX,
	// float viewSpaceXAbs, float[] viewSpaceY, float viewSpaceYAbs) {
	// super(iID, height, width, depth, viewSpaceX, viewSpaceXAbs, viewSpaceY, viewSpaceYAbs);
	//
	// fCenterPoint = new Vec3f(width / 2.0f, height / 2.0f, depth);
	// radius = Math.min(viewSpaceXAbs, viewSpaceYAbs) / 2.0f;
	// // radius = viewSpaceYAbs / 2.0f;
	// }

	public void updateFrustumInfos(float fHeight, float fWidth, float fDepth, float[] fViewSpaceX,
		float fViewSpaceXAbs, float[] fViewSpaceY, float fViewSpaceYAbs) {
		super.updateFrustumInfos(fHeight, fWidth, fDepth, fViewSpaceX, fViewSpaceXAbs, fViewSpaceY,
			fViewSpaceYAbs);
	}

	@Override
	public Vec3f projectCoordinates(Vec3f fvCoords) {

		
		
		float fNewXCoordinate = 0;
		float fNewYCoordinate = 0;
		float fXCoordOfPoint = fvCoords.x();
		float fYCoordOfPoint = fvCoords.y();

		float fDistanceInXDirection = fCenterPoint.x() - fXCoordOfPoint;
		float fDistanceInYDirection = fCenterPoint.y() - fYCoordOfPoint;

		// float fXAngle = Math.abs(fDistanceInXDirection/radius);
		// float fYAngle = Math.abs(fDistanceInYDirection/radius);

		float fXAngle = fDistanceInXDirection / radius;
		float fYAngle = fDistanceInYDirection / radius;

		float fZCoordOfProjectedPoint =
			fCenterPoint.z() + (float) Math.cos(fYAngle) * (float) Math.cos(fXAngle) * radius;
//		if(fZCoordOfProjectedPoint > (float)Math.PI/2){
//			fZCoordOfProjectedPoint = (float)Math.PI/2;
//		}
//		else if(fZCoordOfProjectedPoint < -(float)Math.PI/2)
//			fZCoordOfProjectedPoint = -(float)Math.PI/2;
//		if(fZCoordOfProjectedPoint > (float)Math.PI/4){
//			fZCoordOfProjectedPoint = (float)Math.PI/4;
//		}
//		else if(fZCoordOfProjectedPoint < 0)
//			fZCoordOfProjectedPoint = 0;
//	if(fZCoordOfProjectedPoint < 0)
//		fZCoordOfProjectedPoint = 0;
		float fProjectedXDistance = (float) Math.cos(fYAngle) * (float) Math.sin(fXAngle) * radius;
		float fProjectedYDistance = ((float) Math.sin(fYAngle) * radius);

		//		
		fNewXCoordinate = fCenterPoint.x() + fProjectedXDistance;
		fNewYCoordinate = fCenterPoint.y() + fProjectedYDistance;

		// if(fDistanceInXDirection < 0)
		// fNewXCoordinate = fProjectedXDistance;
		// else
		// fNewXCoordinate = -fProjectedXDistance;
		//
		//			
		// if(fDistanceInYDirection < 0)
		// fNewYCoordinate = fProjectedYDistance;
		// else
		// fNewYCoordinate = -fProjectedYDistance;

		// float fZCoordOfProjectedPoint = -0.2f;

		// TODO: disable picking!

//		if(fZCoordOfProjectedPoint > (float)Math.PI/2)
//			fZCoordOfProjectedPoint = (float)Math.PI/2;
//		else if(fZCoordOfProjectedPoint < -(float)Math.PI/2)
//			fZCoordOfProjectedPoint = -(float)Math.PI/2;
//		if(fZCoordOfProjectedPoint < 0)
//			fZCoordOfProjectedPoint = 0;
		// float fZCoordOfProjectedPoint = 0;
		// if(fXAngle >= -(float)Math.PI/2 || fXAngle <= (float)Math.PI/2)
		// fZCoordOfProjectedPoint = 2.0f;
		// else
		// fZCoordOfProjectedPoint = -2.0f;
		return new Vec3f(fNewXCoordinate, fNewYCoordinate, fZCoordOfProjectedPoint);
	}

	// @Override
	// public Vec3f projectCoordinates(Vec3f fvCoords) {
	//		
	// float fNewXCoordinate = 0;
	// float fNewYCoordinate = 0;
	// float fXCoordOfPoint = fvCoords.x();
	// float fYCoordOfPoint = fvCoords.y();
	//		
	// float fDistanceInXDirection = fCenterPoint.x() - fXCoordOfPoint;
	// float fDistanceInYDirection = fCenterPoint.y() - fYCoordOfPoint;
	//		
	// float fy = (float) Math.sqrt(Math.pow(fDistanceInXDirection, 2)+Math.pow(fDistanceInYDirection, 2));
	//		
	// float fAngle = fy/radius;
	// float fProjectedDistance = (float)Math.sin(fAngle);
	//		
	//		
	// if(fDistanceInXDirection < 0)
	// fNewXCoordinate = fCenterPoint.x() + calculateLineLenth(fCenterPoint.x(), fXCoordOfPoint);
	// else
	// fNewXCoordinate = fCenterPoint.x() - calculateLineLenth(fCenterPoint.x(), fXCoordOfPoint);
	//
	//			
	// if(fDistanceInYDirection < 0)
	// fNewYCoordinate = fCenterPoint.y() + calculateLineLenth(fCenterPoint.y(), fYCoordOfPoint);
	// else
	// fNewYCoordinate = fCenterPoint.y() - calculateLineLenth(fCenterPoint.y(), fYCoordOfPoint);
	//		
	// Vec3f newPoint = new Vec3f();
	// newPoint.set(fNewXCoordinate, fNewYCoordinate, 0.0f);
	//		
	// return newPoint;
	// }

	private float calculateLineLength(float fCenterPoint, float fLinePoint) {

		float fDistanceFromCenterToPointOnGlobe = Math.abs(fCenterPoint - fLinePoint);

		float fAngle = fDistanceFromCenterToPointOnGlobe / radius;
		float fProjectedDistance = (float) Math.sin(fAngle);

		return fProjectedDistance;
	}

	// public float getGlobeSurfaceLengthFromEquatorToPole(){
	// return radius * (float)Math.toRadians(90);
	// }
//	@Override
//	public float getLineFromCenterToBorderOfViewSpace() {
//		return radius * ((float) Math.PI / 2);
//	}
	

	@Override
	public void drawCanvas(GL gl) {

		if (HyperbolicRenderStyle.PROJECTION_DRAW_CANVAS) {
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glColor4fv(HyperbolicRenderStyle.DA_TREE_PROJECTION_COLORSHEME, 0);
			gl.glBegin(GL.GL_POLYGON);
		}
		else {
			gl.glColor4fv(HyperbolicRenderStyle.DA_TREE_PROJECTION_CANVAS_COLORSCHEME, 0);
			gl.glLineWidth(HyperbolicRenderStyle.DA_TREE_PROJECTION_CANVAS_THICKNESS);
			gl.glBegin(GL.GL_LINE_LOOP);
		}

		for (int i = 0; i < 360; i++) {
			float angle = (float) (i * 2 * Math.PI / 180f);
			gl.glVertex3f((float) (fCenterPoint.x() + Math.cos(angle) * radius),
				(float) (fCenterPoint.y() + Math.sin(angle) * radius), -0.1f);
		}
		gl.glEnd();

		// gl.glAlphaFunc(1, 0);

		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glEnable(GL.GL_BLEND);
		// gl.glAlphaFunc(GL.GL_GREATER,0.1f);
		// gl.glEnable(GL.GL_ALPHA_TEST);

		// //Debug Grid
		// gl.glColor3f(1.0f, 0.0f, 0.0f);
		// gl.glLineWidth(2);
		//		
		// //Horizontal Lines
		// gl.glBegin(GL.GL_LINE);
		//	
		// Vec3f point1 = new Vec3f();
		// point1.set(fCenterPoint.x()-1.0f, fCenterPoint.y()+1.0f, 0.1f);
		// point1 = projectCoordinates(point1);
		// gl.glVertex3f(point1.x(),point1.y(),point1.z());
		// Vec3f point2 = new Vec3f();
		// point2.set(fCenterPoint.x()+1.0f, fCenterPoint.y()+1.0f, 0.1f);
		// point2 = projectCoordinates(point2);
		// gl.glVertex3f(point2.x(),point2.y(),point2.z());
		// gl.glEnd();
		//		
		// gl.glBegin(GL.GL_LINE);
		// Vec3f point3 = new Vec3f();
		// point3.set(fCenterPoint.x()-1.0f, fCenterPoint.y()-1.0f, 0.1f);
		// point3 = projectCoordinates(point3);
		// gl.glVertex3f(point3.x(),point3.y(),point3.z());
		// Vec3f point4 = new Vec3f();
		// point4.set(fCenterPoint.x()+1.0f, fCenterPoint.y()-1.0f, 0.1f);
		// point4 = projectCoordinates(point4);
		// gl.glVertex3f(point4.x(),point4.y(),point4.z());
		//		
		// gl.glEnd();
		//		
		// gl.glBegin(GL.GL_LINE);
		//		
		// Vec3f point5 = new Vec3f();
		// point5.set(fCenterPoint.x()-1.0f, fCenterPoint.y()+0.5f, 0.1f);
		// point5 = projectCoordinates(point5);
		// gl.glVertex3f(point5.x(),point5.y(),point5.z());
		// Vec3f point6 = new Vec3f();
		// point6.set(fCenterPoint.x()+1.0f, fCenterPoint.y()+0.5f, 0.1f);
		// point6 = projectCoordinates(point6);
		// gl.glVertex3f(point6.x(),point6.y(),point6.z());
		// gl.glEnd();
		//		
		// gl.glBegin(GL.GL_LINE);
		// Vec3f point7 = new Vec3f();
		// point7.set(fCenterPoint.x()-1.0f, fCenterPoint.y()-0.5f, 0.1f);
		// point7 = projectCoordinates(point7);
		// gl.glVertex3f(point7.x(),point7.y(),point7.z());
		// Vec3f point8 = new Vec3f();
		// point8.set(fCenterPoint.x()+1.0f, fCenterPoint.y()-0.5f, 0.1f);
		// gl.glVertex3f(point8.x(),point8.y(),point8.z());
		// point8 = projectCoordinates(point8);
		// gl.glVertex3f(fCenterPoint.x()+1.0f, fCenterPoint.y()-0.5f, 0.1f);
		//		
		// gl.glEnd();
		//		
		// //Vertical Lines
		// gl.glBegin(GL.GL_LINE);
		//		
		// Vec3f point9 = new Vec3f();
		// point9.set(fCenterPoint.x()-0.5f, fCenterPoint.y()+1.5f, 0.1f);
		// point9 = projectCoordinates(point9);
		// gl.glVertex3f(point9.x(),point9.y(),point9.z());
		// Vec3f point10 = new Vec3f();
		// point10.set(fCenterPoint.x()-0.5f, fCenterPoint.y()-1.5f, 0.1f);
		// point10 = projectCoordinates(point10);
		// gl.glVertex3f(point10.x(),point10.y(),point10.z());
		// gl.glEnd();
		//		
		// gl.glBegin(GL.GL_LINE);
		// Vec3f point11 = new Vec3f();
		// point11.set(fCenterPoint.x()+0.5f, fCenterPoint.y()+1.5f, 0.1f);
		// point11 = projectCoordinates(point11);
		// gl.glVertex3f(point11.x(),point11.y(),point11.z());
		// Vec3f point12 = new Vec3f();
		// point12.set(fCenterPoint.x()+0.5f, fCenterPoint.y()-1.5f, 0.1f);
		// point12 = projectCoordinates(point12);
		// gl.glVertex3f(point12.x(),point12.y(),point12.z());
		//		
		// gl.glEnd();

		// gl.glBegin(GL.GL_LINE);
		//		
		// Vec3f point5 = new Vec3f();
		// point5.set(fCenterPoint.x()-1.0f, fCenterPoint.y()+0.5f, 0.1f);
		// gl.glVertex3f(point5.x(),point5.y(),point5.z());
		// Vec3f point6 = new Vec3f();
		// point6.set(fCenterPoint.x()+1.0f, fCenterPoint.y()+0.5f, 0.1f);
		// gl.glVertex3f(point6.x(),point6.y(),point6.z());
		// gl.glEnd();
		//		
		// gl.glBegin(GL.GL_LINE);
		// Vec3f point7 = new Vec3f();
		// point7.set(fCenterPoint.x()-1.0f, fCenterPoint.y()-0.5f, 0.1f);
		// gl.glVertex3f(point7.x(),point7.y(),point7.z());
		// Vec3f point8 = new Vec3f();
		// point8.set(fCenterPoint.x()+1.0f, fCenterPoint.y()-0.5f, 0.1f);
		// gl.glVertex3f(point8.x(),point8.y(),point8.z());
		// gl.glVertex3f(fCenterPoint.x()+1.0f, fCenterPoint.y()-0.5f, 0.1f);
		//		
		// gl.glEnd();

		// gl.glFlush();
		//		

	}

	@Override
	public float[][] getEuclidianCanvas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vec3f getNearestPointOnEuclidianBorder(Vec3f point) {

	//	return point;
		Vec3f vec = point.minus(fCenterPoint);
		vec.normalize();
		vec.scale(getProjectedLineFromCenterToBorder() );
		vec.add(fCenterPoint);
		return vec;
	}

	@Override
	public float getProjectedLineFromCenterToBorder() {
		return radius * ((float) Math.PI / 2);
	}

}
