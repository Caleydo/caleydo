package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;

//
// import gleem.linalg.Vec3f;
//
// import java.nio.FloatBuffer;
// import java.util.ArrayList;
// import java.util.List;
//
// import javax.media.opengl.GL;
//
// import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
// import org.caleydo.core.view.opengl.util.spline.Spline3D;
//
public class DrawAbleHyperbolicGeometryConnection
	extends ADrawAbleConnection {

	protected Vec2f fvHTCenterpoint;
	protected float fHTRadius;

	public DrawAbleHyperbolicGeometryConnection(IDrawAbleNode iNodeA, IDrawAbleNode iNodeB,
		Vec2f fvHTCenterpoint, float fHTRadius) {
		super(iNodeA, iNodeB);
		this.fvHTCenterpoint = fvHTCenterpoint;
		this.fHTRadius = fHTRadius;
	}

	@Override
	public void draw(GL gl, boolean bHighlight) {
		if (bHighlight) {
			gl.glColor4fv(HyperbolicRenderStyle.DA_HB_GEOM_CONNECTION_COLORSHEME_HL, 0);
			gl.glLineWidth(HyperbolicRenderStyle.DA_HB_GEOM_CONNECTION_THICKNESS_HL);
		}
		else {
			gl.glColor4fv(HyperbolicRenderStyle.DA_HB_GEOM_CONNECTION_COLORSHEME, 0);
			gl.glLineWidth(HyperbolicRenderStyle.DA_HB_GEOM_CONNECTION_THICKNESS);
		}
		// TODO: implement!!!

		Vec3f[] endPoints = findClosestCorrespondendingPoints();
		// first point
		Vec3f pStartP = endPoints[0];
		// last point
		Vec3f pEndP = endPoints[1];
		Vec3f vSE = new Vec3f(pEndP.x() - pStartP.x(), pEndP.y() - pStartP.y(), pEndP.z() - pStartP.z());
		// base point ... middle of vec start -> end
		Vec3f pSpP1 =
			new Vec3f(pStartP.x() + vSE.x() / 2.0f, pStartP.y() + vSE.y() / 2.0f, pStartP.z() + vSE.z()
				/ 2.0f);

		Vec2f v2Start = new Vec2f(pStartP.x(), pStartP.y());
		Vec2f v2End = new Vec2f(pEndP.x(), pEndP.y());
	//	float alpha =
	//		(float) Math.acos((v2Start.x() * v2End.x() + v2Start.y() * v2End.y())
	//			/ (v2Start.length() * v2End.length()));
//		if (v2Start.x() > fvHTCenterpoint.x())
//			if (v2Start.y() > v2End.y())
//				alpha = alpha * -1.0f;
//			else
//				;
//		else if (v2Start.y() < v2End.y())
//			alpha = alpha * -1.0f;
//		else
//			;

		
		
	//	Vec3f pSpP2 =
	//		new Vec3f((float) (pSpP1.x() + Math.cos(alpha)), (float) (pSpP1.y() + Math.sin(alpha)), pSpP1.z());

		// Vec3f vnpSP1 = new Vec3f(-pSpP1.y(), pSpP1.x(), pSpP1.z());
		// Vec3f vME = new Vec3f(pEndP.x() - fvHTCenterpoint.x(), pEndP.y() - fvHTCenterpoint.y(), 0);

		// float s = (pSpP1.y() - vME.y() * (pSpP1.x() + fvHTCenterpoint.x()) - fvHTCenterpoint.y() * vME.x())
		// / (vnpSP1.x() * vME.y() - vnpSP1.y());
		// float s = (vME.x()*(fvHTCenterpoint.y()-pSpP1.y())+vME.y()*(pSpP1.x() - fvHTCenterpoint.x())) /
		// (vME.x()*vnpSP1.y() - vME.y()*vnpSP1.x());
		// Vec3f pSpP2 = new Vec3f(pSpP1.x() + s * vnpSP1.x(), pSpP1.y() + s * vnpSP1.y(), pSpP1.z());

		// Vec3f vnSP1 = new Vec3f(-pSpP1.y()+pSpP1.x(), pSpP1.x()+pSpP1.y(), pSpP1.z());
		// vnSP1.times((float) 1.0f / (float) Math.sqrt(vnSP1.x() * vnSP1.x() + vnSP1.y() * vnSP1.y()));
		// Vec3f pSpP2 =
		// new Vec3f((vME.y() - vnSP1.y()) / (vnSP1.x() - vME.x()), (vnSP1.x() * vME.y() - vME.x()
		// * vnSP1.y())
		// / (vnSP1.x() - vME.x()), vnSP1.z());
		// FloatBuffer fBuff = FloatBuffer.allocate(4 * 3);
		// float[] fA =
		// { pStartP.x(), pStartP.y(), pStartP.z(), pSpP1.x(), pSpP1.y(), pSpP1.z(), pSpP2.x(), pSpP2.y(),
		// pSpP2.z(), pEndP.x(), pEndP.y(), pEndP.z() };
		FloatBuffer fBuff;
		fBuff = FloatBuffer.allocate(3 * 3);
		//if (Math.abs(alpha) < 2 * Math.PI / 180.0f) {
			float[] fA =
				{ pStartP.x(), pStartP.y(), pStartP.z(), pSpP1.x(), pSpP1.y(), pSpP1.z(), pEndP.x(),
						pEndP.y(), pEndP.z() };
			fBuff.put(fA);
//		}
//		else {
//			float[] fA =
//				{ pStartP.x(), pStartP.y(), pStartP.z(), pSpP2.x(), pSpP2.y(), pSpP2.z(), pEndP.x(),
//						pEndP.y(), pEndP.z() };
//			fBuff.put(fA);
//		}
		fBuff.rewind();
		gl.glEnable(GL.GL_MAP1_VERTEX_3);
		gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 3, fBuff);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (int i = 0; i <= HyperbolicRenderStyle.DA_SPLINE_CONNECTION_NR_CTRLPOINTS; i++)
			gl.glEvalCoord1f((float) i / (float) HyperbolicRenderStyle.DA_SPLINE_CONNECTION_NR_CTRLPOINTS);
		gl.glEnd();
	}
	// FloatBuffer fBuff = FloatBuffer.allocate(lPoints.size() * 3);
	// for (Vec3f point : lPoints) {
	// fBuff.put(point.x());
	// fBuff.put(point.y());
	// fBuff.put(point.z());
	// }
	// fBuff.rewind();

	// float[] fA={0.0f,0.0f,0.0f,1.0f,1.0f,0.0f,3.0f,3.0f,0.0f,5.0f,2.0f,0.0f};
	// fBuff = FloatBuffer.allocate(12);
	// fBuff.put(fA);
	// fBuff.rewind();
	// gl.glEnable(GL.GL_MAP1_VERTEX_3);
	// gl.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, lPoints.size(), fBuff);
	// gl.glBegin(GL.GL_LINE_STRIP);
	// // for (int i = 0; i < lPoints.size(); i++) gl.glVertex3f(lPoints.get(i).x(), lPoints.get(i).y(),
	// lPoints.get(i).z());
	// for (int i = 0; i <= HyperbolicRenderStyle.DA_SPLINE_CONNECTION_NR_CTRLPOINTS; i++)
	// gl.glEvalCoord1f((float) i / (float) HyperbolicRenderStyle.DA_SPLINE_CONNECTION_NR_CTRLPOINTS);
	// gl.glEnd();
	//
	// gl.glPointSize(10);
	// float[] fA = { 0.0f, 0.0f, 1.0f, 1.0f };
	// gl.glColor4fv(fA, 0);
	// gl.glBegin(GL.GL_POINTS);
	// for (int i = 0; i < lPoints.size(); i++) {
	// gl.glVertex3f(lPoints.get(i).x(), lPoints.get(i).y(), lPoints.get(i).z());
	// }
	// gl.glEnd();

	// }
}
// implements IDrawAbleConnection {
//	
// private Spline3D spline;
// private Vec3f[] vecArray;
//	
//	
// public DrawAbleSplineConnection() {
// this.fRed = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[0];
// this.fGreen = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[1];
// this.fBlue = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[2];
// this.fAlpha = HyperbolicRenderStyle.DA_OBJ_FALLBACK_ALPHA;
// }
//
// @Override
// public void drawConnectionFromStartToEnd(GL gl, List<Vec3f> points, float thickness) {
//
// vecArray = new Vec3f[points.size()];
// for(int i = 0 ; i < points.size(); i++)
// {
// vecArray[i] = points.get(i);
// }
// float accuracy = 0.001f;
// float margin = 0.01f;
// spline = new Spline3D(vecArray, accuracy, margin);
//    
// gl.glClearColor(1, 1, 1, 1);
//	
// gl.glLineWidth(4);
// gl.glColor3f(1, 0, 0);
// gl.glCallList(GL.GL_COLOR_BUFFER_BIT);
// gl.glBegin(GL.GL_LINE_STRIP);
//	
// for (int i=0; i<30; i++)
// {
// Vec3f vec = spline.getPositionAt((float)i/5);
// // Vec3f vec = spline.getPositionAt((float)i / 100*2);
// gl.glVertex3f(vec.x(), vec.y(), vec.z());
// }
// gl.glEnd();
//		
// // gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
// // gl.glLineWidth(thickness);
// // int i = 0;
// //
// // // Clear the window with current clearing color
// // // glClear(GL_COLOR_BUFFER_BIT);
// // // Sets up the bezier
// // // This actually only needs to be called once and could go in
// // // the setup function
// //// FloatBuffer.allocate(points.size());
// //// for(int j = 0; j<points.size(); j++)
// //// {
// //// for (Vec3f tmpVec : points )
// //// {
// //// x.put(tmpVec.x());
// //// }
// //// }
// //
// //// gl.glMap1f(GL.GL_MAP1_VERTEX_3, // Type of data generated
// //// 0.0f, // Lower u range
// //// 100.0f, // Upper u range
// //// 3, // Distance between points in the data
// //// points.size(), // number of control points
// //// (FloatBuffer) points); // array of control points
// ////
// //// // Enable the evaluator
// // gl.glEnable(GL.GL_MAP1_VERTEX_3);
// // // Use a line strip to “connect the dots”
// // gl.glBegin(GL.GL_LINE_STRIP);
// // for (i = 0; i <= 100; i++) {
// //
// // // Evaluate the curve at this point
// // gl.glEvalCoord1f((float) i);
// // }
// // gl.glEnd();
// // // Draw the Control Points
// // DrawPoints(gl, points);
// // // Flush drawing commands
// // //glutSwapBuffers();
// // gl.glFlush();
//
// }
//
// private void DrawPoints(GL gl, List<Vec3f> points) {
// int i = 0; // Counting variable
// // Set point size larger to make more visible
// // gl.glPointSize(5.0f);
// // Loop through all control points for this example
// gl.glPointSize(10.0f);
// gl.glBegin(GL.GL_POINTS);
// for (i = 0; i < points.size(); i++)
// gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0);
// gl.glEnd();
// }
//
//
//
// }
