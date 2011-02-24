package org.caleydo.core.view.opengl.util.spline;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

public class ConnectionBandRenderer implements IConnectionRenderer {

	public final static int NUMBER_OF_SPLINE_POINTS = 30;
	
	private GLU glu;
	// private int displayListID;

	private GLUtessellator tobj;

	@Override
	public void init(GL2 gl) {
		TesselationCallback tessCallback = new TesselationCallback(gl, new GLU());

		tobj = GLU.gluNewTess();

		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);

		gl.glShadeModel(GL2.GL_SMOOTH);
	}

	@Override
	public void display(GL2 gl) {

		// gl.glCallList(displayListID);
		// gl.glFlush();

		TesselationCallback tessCallback = new TesselationCallback(gl, glu);

		double star[][] = new double[][] {// [5][6]; 6x5 in java
		{ 2.50, 5.00, 0.0, 1.0, 0.0, 1.0 }, { 3.250, 2.000, 0.0, 1.0, 1.0, 0.0 },
				{ 4.000, 5.00, 0.0, 0.0, 1.0, 1.0 },
				{ 2.500, 1.500, 0.0, 1.0, 0.0, 0.0 },
				{ 4.000, 1.500, 0.0, 0.0, 1.0, 0.0 } };

		GLUtessellator tobj = GLU.gluNewTess();

		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);

		/* smooth shaded, self-intersecting star */
		gl.glShadeModel(GL2.GL_SMOOTH);
		GLU.gluTessProperty(tobj, //
				GLU.GLU_TESS_WINDING_RULE, //
				GLU.GLU_TESS_WINDING_POSITIVE);
		GLU.gluTessBeginPolygon(tobj, null);
		GLU.gluTessBeginContour(tobj);
		GLU.gluTessVertex(tobj, star[0], 0, star[0]);
		GLU.gluTessVertex(tobj, star[1], 0, star[1]);
		GLU.gluTessVertex(tobj, star[2], 0, star[2]);
		GLU.gluTessVertex(tobj, star[3], 0, star[3]);
		GLU.gluTessVertex(tobj, star[4], 0, star[4]);
		GLU.gluTessEndContour(tobj);
		GLU.gluTessEndPolygon(tobj);
		GLU.gluDeleteTess(tobj);
	}

	@Override
	public void render(GL2 gl, ArrayList<Vec3f> points) {

		double inputPoints[][] = new double[points.size()][3];

		for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
			inputPoints[pointIndex][0] = points.get(pointIndex).x();
			inputPoints[pointIndex][1] = points.get(pointIndex).y();
			inputPoints[pointIndex][2] = points.get(pointIndex).z();
		}
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		// glu.gluTessProperty(tobj, //
		// GLU.GLU_TESS_WINDING_RULE, //
		// GLU.GLU_TESS_WINDING_POSITIVE);
		GLU.gluTessBeginPolygon(tobj, null);
		GLU.gluTessBeginContour(tobj);

		for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
			GLU.gluTessVertex(tobj, inputPoints[pointIndex], 0, inputPoints[pointIndex]);
		}

		GLU.gluTessEndContour(tobj);
		GLU.gluTessEndPolygon(tobj);
		GLU.gluDeleteTess(tobj);
	}
}
