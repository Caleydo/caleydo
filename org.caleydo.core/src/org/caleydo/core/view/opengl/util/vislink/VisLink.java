package org.caleydo.core.view.opengl.util.vislink;

import java.util.ArrayList;

import javax.media.opengl.GL;

import gleem.linalg.Vec3f;

import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;


/**
 * 
 * @author Oliver Pimas
 * @version 0.2 (2009-08-11)
 * 
 * VisLink offers static methods to render a connection line.
 * 
 *
 */

public class VisLink {
	
	
	/**
	 * 		Creates a visual link. Static method.
	 * When the number of control points is 2, this method renders a straight line.
	 * If the number of control points is greater then 2, a curved line (using NURBS) is rendered.
	 *
	 * @param gl the GL object
	 * 
	 * @param controlPoints the control points for the NURBS spline
	 * 
	 * @param numberOfSegments the number of sub-intervals the spline is evaluated with
	 * (affects u in the Cox-de Boor recursive formula when evaluating the spline)
	 * Note: For straight lines (only 2 control points), this value doesn't effect the resulting line.
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * 
	 * @throws IllegalArgumentException if there are < 2 control points
	 */	
	public static void renderLine(final GL gl, final ArrayList<Vec3f> controlPoints, final int numberOfSegments, boolean shadow)
		throws IllegalArgumentException
	{
		if(controlPoints.size() == 2)
			line(gl, controlPoints.get(0), controlPoints.get(1), shadow);
		else if(controlPoints.size() > 2)
		{
			spline(gl, controlPoints, numberOfSegments, shadow);
			//polygonLine(gl, controlPoints, numberOfSegments, shadow);
		}
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" ); 			
	}
	
	
	/**
	 * 		Creates a visual link. Static method.
	 * When the number of control points is 2, this method renders a straight line.
	 * If the number of control points is greater then 2, a curved line (using NURBS) is rendered.
	 *
	 * @param gl the GL object
	 * 
	 * @param controlPoints the control points for the NURBS spline
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 * 
	 * @throws IllegalArgumentException if there are < 2 control points
	 */	
	public static void renderLine(final GL gl, final ArrayList<Vec3f> controlPoints, boolean shadow)
		throws IllegalArgumentException
	{
		if(controlPoints.size() == 2)
			line(gl, controlPoints.get(0), controlPoints.get(1), shadow);
		else if(controlPoints.size() > 2)
			spline(gl, controlPoints, 10, shadow); // NOTE: generates a NURBS spline with 10 subsegments.	
		else
			throw new IllegalArgumentException( "Need at least two points to render a line!" ); 
	}
	
	
	/**
	 * 		Creates a visual link. Static method.
	 *
	 * @param gl the GL object
	 * 
	 * @param srcPoint the lines point of origin
	 * 
	 * @param destPoint the lines end point
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */	
	public static void renderLine(final GL gl, Vec3f srcPoint, Vec3f destPoint, boolean shadow) {
		line(gl, srcPoint, destPoint, shadow);		
	}
	
		
	/**
	 * 		Called for creating a curved visual link.
	 * 		This method is not recommended when drawing visual links with higher
	 * 		width, which would case v-shaped gaps to appear at the intersections.
	 *
	 * @param gl the GL object
	 * 
	 * @param controlPoints the control points for the NURBS spline
	 * 
	 * @param numberOfSegments the number of sub-intervals the spline is evaluated with
	 * (affects u in the Cox-de Boor recursive formula when evaluating the spline)
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	protected static void spline(final GL gl, final ArrayList<Vec3f> controlPoints, final int numberOfSegments, boolean shadow) {
		NURBSCurve curve = new NURBSCurve(controlPoints, 10);
		
		if(shadow == true) {
			// line shadow
			gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
			gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 2.0f);
			gl.glBegin(GL.GL_LINE_STRIP);
			for(int i = 0; i < curve.getNumberOfCurvePoints(); i++)
				gl.glVertex3f(curve.getCurvePoint(i).x(), curve.getCurvePoint(i).y(), curve.getCurvePoint(i).z());
			gl.glEnd();
		}
		
		// The spline attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
		//gl.glLineWidth(8f); // FIXME: for testing purpose only
		
		// the spline
		gl.glBegin(GL.GL_LINE_STRIP);
		for(int i = 0; i < curve.getNumberOfCurvePoints(); i++)
			gl.glVertex3f(curve.getCurvePoint(i).x(), curve.getCurvePoint(i).y(), curve.getCurvePoint(i).z());
		gl.glEnd();
	}
	
	
	/**
	 * 		Called for creating a straight visual link.
	 * 		
	 * @param gl the GL object
	 * 
	 * @param vecSrcPoint the lines point of origin
	 * 
	 * @param vecDestPoint the lines end point
	 * 
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	protected static void line(final GL gl, final Vec3f vecSrcPoint, final Vec3f vecDestPoint, boolean shadow) {
		
		if(shadow == true) {
			// Line shadow
			gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_SHADOW_COLOR, 0);
			gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH + 2.0f);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z() - 0.001f);
			gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z() - 0.001f);
			gl.glEnd();
		}
		
		// line
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
		//gl.glLineWidth(8f); // FIXME: for testing purpose only
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecSrcPoint.x(), vecSrcPoint.y(), vecSrcPoint.z());
		gl.glVertex3f(vecDestPoint.x(), vecDestPoint.y(), vecDestPoint.z());
		gl.glEnd();
	}
	
	
	/**
	 * 		Renders a polygon line. Recommended for lines with higher width.
	 * 
	 * @param gl the GL object
	 * @param srcPoint the lines point of origin
	 * @param bundlingPoint the lines bundling point
	 * @param destPoint the lines end point
	 * @param numberOfSegments numberOfSegments the number of sub-intervals the spline is evaluated with
	 * (affects u in the Cox-de Boor recursive formula when evaluating the spline)
	 * @param shadow turns shadow on/off (boolean: true = shadow on, false = shadow off)
	 */
	public static void polygonLine(final GL gl, final Vec3f srcPoint, final Vec3f bundlingPoint, final Vec3f destPoint, final int numberOfSegments, boolean shadow) {
		NURBSCurve curve = new NURBSCurve(srcPoint, bundlingPoint, destPoint, 10);
		ArrayList<Vec3f> polygonPoints = new ArrayList<Vec3f>(generatePolygonVertices(gl, curve.getCurvePoints(), srcPoint, bundlingPoint, destPoint));
		
		// The spline attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		//gl.glColor4f(0.9f, 0.5f, 0.3f, 1);
		
//		gl.glEnable( GL.GL_BLEND );
//		gl.glEnable( GL.GL_POLYGON_SMOOTH );
//		gl.glEnable( GL.GL_ALPHA_TEST );
	
//		gl.glDisable( GL.GL_DEPTH_TEST );
//		gl.glHint( GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST );		
//		gl.glPolygonOffset( 0.0f, .15f );
		
		// the spline
		gl.glPointSize(4f);
		gl.glBegin(GL.GL_QUAD_STRIP);
		for(int i = 0; i < polygonPoints.size(); i++)
			gl.glVertex3f(polygonPoints.get(i).x(), polygonPoints.get(i).y(), polygonPoints.get(i).z());
		gl.glEnd();
	}
	
	
	/**
	 * 		Normalizes a vector to length 0.1
	 * 	
	 * @param vec vector to be normalized
	 * @return normalized vector
	 */
	protected static Vec3f normalizeVector(Vec3f vec) {
		Vec3f result = new Vec3f();
		
		result.setX((vec.x() / java.lang.Math.abs(vec.x())) * 0.01f);
		result.setY((vec.y() / java.lang.Math.abs(vec.y())) * 0.01f);
		result.setZ((vec.z() / java.lang.Math.abs(vec.z())) * 0.01f);
		
		return result; 
	}
	
	
	/**
	 * 		generates a normal vector from 3 given points in space
	 * 
	 * @param v1 point 1
	 * @param v2 point 2
	 * @param v3 point 3
	 * 
	 * @return a normal vector
	 */
	protected static Vec3f calculateNormal(Vec3f v1, Vec3f v2, Vec3f v3) {
		Vec3f v1v3 = new Vec3f(v3.minus(v1));
		Vec3f v1v2 = new Vec3f(v2.minus(v1));		
		return new Vec3f(v1v3.cross(v1v2));
	}
	
	
	/**
	 * 		generates a normal vector from 3 given points in space.
	 * 		This is the inverse normal to the one created by calculateNormal
	 * 
	 * @param v1 point 1
	 * @param v2 point 2
	 * @param v3 point 3
	 * 
	 * @return a normal vector (inverse to the normal created by calculateNormal)
	 */
	protected static Vec3f calculateInvNormal(Vec3f v1, Vec3f v2, Vec3f v3) {
		Vec3f v1v3 = new Vec3f(v3.minus(v1));
		Vec3f v1v2 = new Vec3f(v2.minus(v1));		
		return new Vec3f(v1v2.cross(v1v3));
	}
	
	
	protected static Vec3f directionVec(Vec3f prevPoint, Vec3f point, Vec3f nextPoint) {
		Vec3f straight = nextPoint.minus(prevPoint);
		Vec3f eyepoint = new Vec3f(0.0f, 0.0f, 6.0f);
		Vec3f epVec = eyepoint.minus(point);
		return new Vec3f(straight.cross(epVec));
	}
	
	protected static Vec3f invDirectionVec(Vec3f prevPoint, Vec3f point, Vec3f nextPoint) {
		Vec3f straight = nextPoint.minus(prevPoint);
		Vec3f eyepoint = new Vec3f(0.0f, 0.0f, 6.0f);
		Vec3f epVec = eyepoint.minus(point);
		return new Vec3f(epVec.cross(straight));
	}
	
	/** only for testing. do not use! */
	protected static ArrayList<Vec3f> generatePolygonVertices(final GL gl, ArrayList<Vec3f> curvePoints, Vec3f srcPoint, Vec3f bundlingPoint, Vec3f destPoint)
	throws IllegalArgumentException
	{
		if(curvePoints.size() <= 0)
			throw new IllegalArgumentException( "given set of curve points is empty ()" );
			
		
		ArrayList<Vec3f> polygonPoints = new ArrayList<Vec3f>();
		float width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH / 2f;
		
		Vec3f dirVec = new Vec3f();
		Vec3f invDirVec = new Vec3f();
		
		for ( int i = 0; i < curvePoints.size(); i++) {	
			if(i == 0) {
				dirVec = directionVec(curvePoints.get(i), curvePoints.get(i), curvePoints.get(i+1));
				invDirVec = invDirectionVec(curvePoints.get(i), curvePoints.get(i), curvePoints.get(i+1));
			}
			else if (i == (curvePoints.size() - 1) ) {
				dirVec = directionVec(curvePoints.get(i-1), curvePoints.get(i), curvePoints.get(i));
				invDirVec = invDirectionVec(curvePoints.get(i-1), curvePoints.get(i), curvePoints.get(i));
			}
			else {
				dirVec = directionVec(curvePoints.get(i-1), curvePoints.get(i), curvePoints.get(i+1));
				invDirVec = invDirectionVec(curvePoints.get(i-1), curvePoints.get(i), curvePoints.get(i+1));
			}
			
			dirVec = normalizeVector(dirVec);
			invDirVec = normalizeVector(invDirVec);
			
			polygonPoints.add(curvePoints.get(i).addScaled(width, dirVec)); // FIXME: only testing purpose
			polygonPoints.add(curvePoints.get(i).addScaled(width, invDirVec));
		}		
		return polygonPoints;
	}
	
}
