package org.caleydo.view.compare.renderer;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import org.caleydo.view.compare.renderer.util.TesselationCallback;

public class CompareConnectionBandRenderer
		implements
			ICompareConnectionRenderer {

	private GLU glu;
	private int displayListID;

	public void init(GL gl) {
		glu = new GLU();

//		TesselationCallback tessCallback = new TesselationCallback(gl, glu);
//
//		double star[][] = new double[][]{// [5][6]; 6x5 in java
//		{2.50, 5.00, 0.0, 1.0, 0.0, 1.0}, {3.250, 2.000, 0.0, 1.0, 1.0, 0.0},
//				{4.000, 5.00, 0.0, 0.0, 1.0, 1.0},
//				{2.500, 1.500, 0.0, 1.0, 0.0, 0.0},
//				{4.000, 1.500, 0.0, 0.0, 1.0, 0.0}};
//
//		displayListID = gl.glGenLists(2);
//
//		GLUtessellator tobj = glu.gluNewTess();
//
//		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
//		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
//		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
//		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
//		glu.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);
//
//		/* smooth shaded, self-intersecting star */
//		gl.glNewList(displayListID, GL.GL_COMPILE);
//		gl.glShadeModel(GL.GL_SMOOTH);
//		glu.gluTessProperty(tobj, //
//				GLU.GLU_TESS_WINDING_RULE, //
//				GLU.GLU_TESS_WINDING_POSITIVE);
//		glu.gluTessBeginPolygon(tobj, null);
//		glu.gluTessBeginContour(tobj);
//		glu.gluTessVertex(tobj, star[0], 0, star[0]);
//		glu.gluTessVertex(tobj, star[1], 0, star[1]);
//		glu.gluTessVertex(tobj, star[2], 0, star[2]);
//		glu.gluTessVertex(tobj, star[3], 0, star[3]);
//		glu.gluTessVertex(tobj, star[4], 0, star[4]);
//		glu.gluTessEndContour(tobj);
//		glu.gluTessEndPolygon(tobj);
//		gl.glEndList();
//		glu.gluDeleteTess(tobj);
	}

	public void display(GL gl) {
		
//		gl.glCallList(displayListID);
//		gl.glFlush();
		
		TesselationCallback tessCallback = new TesselationCallback(gl, glu);

		double star[][] = new double[][]{// [5][6]; 6x5 in java
		{2.50, 5.00, 0.0, 1.0, 0.0, 1.0}, {3.250, 2.000, 0.0, 1.0, 1.0, 0.0},
				{4.000, 5.00, 0.0, 0.0, 1.0, 1.0},
				{2.500, 1.500, 0.0, 1.0, 0.0, 0.0},
				{4.000, 1.500, 0.0, 0.0, 1.0, 0.0}};

		GLUtessellator tobj = glu.gluNewTess();

		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);

		/* smooth shaded, self-intersecting star */
		gl.glShadeModel(GL.GL_SMOOTH);
		glu.gluTessProperty(tobj, //
				GLU.GLU_TESS_WINDING_RULE, //
				GLU.GLU_TESS_WINDING_POSITIVE);
		glu.gluTessBeginPolygon(tobj, null);
		glu.gluTessBeginContour(tobj);
		glu.gluTessVertex(tobj, star[0], 0, star[0]);
		glu.gluTessVertex(tobj, star[1], 0, star[1]);
		glu.gluTessVertex(tobj, star[2], 0, star[2]);
		glu.gluTessVertex(tobj, star[3], 0, star[3]);
		glu.gluTessVertex(tobj, star[4], 0, star[4]);
		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);
		glu.gluDeleteTess(tobj);
	}
	
	public void render(GL gl, ArrayList<Vec3f> points) {
		
		TesselationCallback tessCallback = new TesselationCallback(gl, glu);

		double inputPoints[][] = new double[points.size()][3];

		for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
			inputPoints[pointIndex][0] = points.get(pointIndex).x();
			inputPoints[pointIndex][1] = points.get(pointIndex).y();
			inputPoints[pointIndex][2] = points.get(pointIndex).z();
		}
		
		GLUtessellator tobj = glu.gluNewTess();

		glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
		glu.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);

		gl.glShadeModel(GL.GL_SMOOTH);
//		glu.gluTessProperty(tobj, //
//				GLU.GLU_TESS_WINDING_RULE, //
//				GLU.GLU_TESS_WINDING_POSITIVE);
		glu.gluTessBeginPolygon(tobj, null);
		glu.gluTessBeginContour(tobj);
		
		for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
			glu.gluTessVertex(tobj, inputPoints[pointIndex], 0, inputPoints[pointIndex]);
		}
		
		glu.gluTessEndContour(tobj);
		glu.gluTessEndPolygon(tobj);
		glu.gluDeleteTess(tobj);
	}
}
