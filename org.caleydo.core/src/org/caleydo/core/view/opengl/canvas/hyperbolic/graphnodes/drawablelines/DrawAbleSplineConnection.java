package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

import gleem.linalg.Vec3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.util.spline.Spline3D;

public class DrawAbleSplineConnection
	extends ADrawAbleConnection
	implements IDrawAbleConnection {
	
	private Spline3D spline;
	private Vec3f[] vecArray;
	
	
	public DrawAbleSplineConnection() {
		this.fRed = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[0];
		this.fGreen = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[1];
		this.fBlue = HyperbolicRenderStyle.DA_OBJ_FALLBACK_COLORSCHEME[2];
		this.fAlpha = HyperbolicRenderStyle.DA_OBJ_FALLBACK_ALPHA;
	}

	@Override
	public void drawConnectionFromStartToEnd(GL gl, List<Vec3f> points, float thickness) {

		vecArray = new Vec3f[points.size()];
	for(int i = 0 ; i < points.size(); i++)
	{
		vecArray[i] = points.get(i);
	}
    float accuracy = 0.001f;
    float margin = 0.01f;
    spline = new Spline3D(vecArray, accuracy, margin);
    
	gl.glClearColor(1, 1, 1, 1);
	
	gl.glLineWidth(4);
	gl.glColor3f(1, 0, 0);
	gl.glCallList(GL.GL_COLOR_BUFFER_BIT);
	gl.glBegin(GL.GL_LINE_STRIP);
	
	for (int i=0; i<30; i++)
	{
		Vec3f vec = spline.getPositionAt((float)i/5);
//		Vec3f vec = spline.getPositionAt((float)i / 100*2);
		gl.glVertex3f(vec.x(), vec.y(), vec.z());			
	}
	gl.glEnd();
		
		//		gl.glColor4f(this.fRed, this.fGreen, this.fBlue, this.fAlpha);
//		gl.glLineWidth(thickness);
//		int i = 0;
//		
//		// Clear the window with current clearing color
//		// glClear(GL_COLOR_BUFFER_BIT);
//		// Sets up the bezier
//		// This actually only needs to be called once and could go in
//		// the setup function
////		FloatBuffer.allocate(points.size());
////		for(int j = 0; j<points.size(); j++)
////		{
////			for (Vec3f tmpVec : points )
////			{
////				x.put(tmpVec.x());
////			}
////		}
//		
////		gl.glMap1f(GL.GL_MAP1_VERTEX_3, // Type of data generated
////			0.0f, // Lower u range
////			100.0f, // Upper u range
////			3, // Distance between points in the data
////			points.size(), // number of control points
////			(FloatBuffer) points); // array of control points
////
////		// Enable the evaluator
//		gl.glEnable(GL.GL_MAP1_VERTEX_3);
//		// Use a line strip to “connect the dots”
//		gl.glBegin(GL.GL_LINE_STRIP);
//		for (i = 0; i <= 100; i++) {
//
//			// Evaluate the curve at this point
//			gl.glEvalCoord1f((float) i);
//		}
//		gl.glEnd();
//		// Draw the Control Points
//		DrawPoints(gl, points);
//		// Flush drawing commands
//		//glutSwapBuffers();
//		gl.glFlush();

	}

	private void DrawPoints(GL gl, List<Vec3f> points) {
		int i = 0; // Counting variable
		// Set point size larger to make more visible
		// gl.glPointSize(5.0f);
		// Loop through all control points for this example
		gl.glPointSize(10.0f);
		gl.glBegin(GL.GL_POINTS);
		for (i = 0; i < points.size(); i++)
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), 0);
		gl.glEnd();
	}



}
