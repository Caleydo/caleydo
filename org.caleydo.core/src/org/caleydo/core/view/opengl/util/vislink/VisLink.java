package org.caleydo.core.view.opengl.util.vislink;

import java.util.ArrayList;

import javax.media.opengl.GL;

import gleem.linalg.Vec3f;

import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;



public class VisLink {
	
	
	
	/**
	 * 		Static method to create a visual link (render a connection line).
	 *
	 * @param gl
	 * 			the GL object
	 * 
	 * @param controlPoints
	 * 			the control points for the NURBS spline
	 * 
	 * @param numberOfSegments
	 * 			the number of sub-intervals the spline is evaluated with
	 * 			(affects u in the Cox-de Boor recursive formula when evaluating the spline)
	 */
	public static void spline(final GL gl, final Vec3f[] controlPoints, final int numberOfSegments) {
		NURBSCurve curve = new NURBSCurve(controlPoints, 10);
		
		// The spline attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
		
		// the spline (FIXME: at the moment this is also line strip)
		gl.glBegin(GL.GL_LINE_STRIP);
		for(int i = 0; i < curve.getNumberOfCurvePoints(); i++) {
			gl.glVertex3f(curve.getCurvePoint(i).x(), curve.getCurvePoint(i).y(), curve.getCurvePoint(i).z());
			//System.out.println("renderLine: created vertex " + x + " " + y + " " + z);
		}
		gl.glEnd();
	}
	
	
	
	/**
	 * 		Static method to create a visual link drawn with GL_LINE_STRIP.
	 * 		This method is not recommended when drawing visual links with higher
	 * 		width, which would case v-shaped gaps to appear at the intersections.
	 *
	 * @param gl
	 * 			the GL object
	 * 
	 * @param controlPoints
	 * 			the control points for the NURBS spline
	 * 
	 * @param numberOfSegments
	 * 			the number of sub-intervals the spline is evaluated with
	 * 			(affects u in the Cox-de Boor recursive formula when evaluating the spline)
	 */
	public static void lineStripSpline(final GL gl, final Vec3f[] controlPoints, final int numberOfSegments) {
		NURBSCurve curve = new NURBSCurve(controlPoints, 10);
		
		// The spline attributes
		gl.glColor4fv(ConnectionLineRenderStyle.CONNECTION_LINE_COLOR, 0);
		gl.glLineWidth(ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH);
		
		// the spline
		gl.glBegin(GL.GL_LINE_STRIP);
		for(int i = 0; i < curve.getNumberOfCurvePoints(); i++) {
			gl.glVertex3f(curve.getCurvePoint(i).x(), curve.getCurvePoint(i).y(), curve.getCurvePoint(i).z());
			//System.out.println("renderLine: created vertex " + curve.getCurvePoint(i).x() + " " + curve.getCurvePoint(i).y() + " " + curve.getCurvePoint(i).z());
		}
		gl.glEnd();
		//System.out.println("----------------------------");
	}
	
	/**
	 * Generates 2 vertices from each given vertex. So the line can be built with polygons.
	 * 
	 * @param curvePoints
	 * 					Given set of curve points (n vertices).
	 * @return
	 * 					Calculated set of curve points (n*2 vertices)
	 */
	protected static ArrayList<Vec3f> generatePolygonVertices(Vec3f[] curvePoints) {
		
		ArrayList<Vec3f> polygonPoints = new ArrayList<Vec3f>();
		float width = ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH / 20; // FIXME: calculate correct width
		
		for(int i = 0; i < curvePoints.length; i++) {
			Vec3f normal = new Vec3f(curvePoints[i].y(), ( -1 * curvePoints[i].x() ), curvePoints[i].z());
			normal = normalizeVector(normal);
			polygonPoints.add(new Vec3f( curvePoints[i].addScaled(width, normal) ));
			polygonPoints.add(new Vec3f( curvePoints[i].addScaled( (-width), normal) ));
		}
		return polygonPoints;
	}
	
	/**
	 * Normalizes the given vector.
	 * 
	 * @param vec
	 * 			Vector to be normalized.
	 * @return
	 * 			Normalized vector.
	 */
	protected static Vec3f normalizeVector(Vec3f vec) {
		return new Vec3f(	vec.x() / java.lang.Math.abs(vec.x()),
							vec.y() / java.lang.Math.abs(vec.y()),
							vec.z() / java.lang.Math.abs(vec.z()));
	}


}
