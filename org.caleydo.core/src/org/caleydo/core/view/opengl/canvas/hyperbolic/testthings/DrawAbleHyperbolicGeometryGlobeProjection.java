package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

// package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;
//
// import gleem.linalg.Vec2f;
//
// import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
//
// public class DrawAbleHyperbolicGeometryGlobeProjection
// extends DrawAbleHyperbolicGeometryConnection{
//
// private float fFirstProjectedPoint;
// private float fSecondProjectedPoint;
//	
//	
//	
// public DrawAbleHyperbolicGeometryGlobeProjection(IDrawAbleNode nodeA, IDrawAbleNode nodeB,
// Vec2f fvHTCenterpoint, float fHTRadius) {
// super(nodeA, nodeB, fvHTCenterpoint, fHTRadius);
// // TODO Auto-generated constructor stub
// }
//	
// public float projectLineOnGlobe(Vec2f fvFirstPoint, Vec2f fvSecondPoint){
//		
// float fCenterX = fvHTCenterpoint.x();
// float fCenterY = fvHTCenterpoint.y();
//		
// float fFirstX = fvFirstPoint.x();
// float fFirstY = fvFirstPoint.y();
//		
// float fSecondX = fvSecondPoint.x();
// float fSecondY = fvSecondPoint.y();
//		
// float fLineInXDirection = calculateNewLineLenthByProjectingOnTheGlobe(fCenterX, fFirstX, fSecondX);
// float fLineInYDirection = calculateNewLineLenthByProjectingOnTheGlobe(fCenterY, fFirstY, fSecondY);
//		
// float fNewDistance = (float) Math.sqrt(Math.pow(fLineInXDirection, 2)+Math.pow(fLineInYDirection, 2));
//		
// return fNewDistance;
// }
//	
//	
// private float calculateNewLineLenthByProjectingOnTheGlobe(float fCenterPoint, float fFirstLinePoint, float
// fSecondLinePoint){
//
//		
// float fDistanceBetweenPointsOnGlobe = fSecondLinePoint - fFirstLinePoint;
// float fDistanceFromCenterToFirstPointOnGlobe = Math.abs(fCenterPoint - fFirstLinePoint);
//
// float fFirstAngle = fDistanceFromCenterToFirstPointOnGlobe/fHTRadius;
// float fFirstProjectedDistance = (float)Math.cos(fFirstAngle);
//		
// fFirstProjectedPoint = fCenterPoint + fFirstProjectedDistance;
//		
// float fDistanceFromCenterToSecondPointOnGlobe = Math.abs(fCenterPoint - fSecondLinePoint);
//		
// float fSecondAngle = fDistanceFromCenterToSecondPointOnGlobe/fHTRadius;
// float fSecondProjectedDistance = (float)Math.cos(fSecondAngle);
//		
// fSecondProjectedPoint = fCenterPoint + fSecondProjectedDistance;
//		
// return Math.abs(fSecondProjectedPoint - fFirstProjectedPoint);
//
// }
//
// public float getFirstProjectedPoint() {
// return fFirstProjectedPoint;
// }
//
// public void setFirstProjectedPoint(float fFirstProjectedPoint) {
// this.fFirstProjectedPoint = fFirstProjectedPoint;
// }
//
// public float getSecondProjectedPoint() {
// return fSecondProjectedPoint;
// }
//
// public void setSecondProjectedPoint(float fSecondProjectedPoint) {
// this.fSecondProjectedPoint = fSecondProjectedPoint;
// }
//
// }
