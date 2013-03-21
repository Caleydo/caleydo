/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.subgraph;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.spline.TesselationCallback;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;

class ColorTessellationCallBack extends TesselationCallback {//GLUtessellatorCallbackAdapter{

	public ColorTessellationCallBack(GL2 gl, GLU glu) {
		super(gl,glu);
	}
	
	@Override
	public void vertex(Object vertexData) {
		//gl.glVertex3dv((double[]) vertexData, 0);
		double [] data= (double[]) vertexData;
		if(data.length>6)
			this.gl.glColor4d( data[3],data[4],data[5],data[6]);
		this.gl.glVertex3d(data[0],data[1],data[2]);
	}
	
}


public class ColoredConnectionBandRenderer extends ConnectionBandRenderer {

	@Override
	public void init(GL2 gl) {
		ColorTessellationCallBack tessCallback = new ColorTessellationCallBack(gl, new GLU());

		tobj = GLU.gluNewTess();

		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);

		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
	}
	

	public void render(GL2 gl, List<Vec3f> points, boolean fade) {
		double inputPoints[][] = new double[points.size()][7];
		float t=0f;
		for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
			if(fade)
				t=((float)pointIndex/ ((float)points.size()/2f));
			if(t>1f)t=2f-t;
			inputPoints[pointIndex][0] = points.get(pointIndex).x();
			inputPoints[pointIndex][1] = points.get(pointIndex).y();
			inputPoints[pointIndex][2] = points.get(pointIndex).z();			
			inputPoints[pointIndex][3] = 1f-t;
			inputPoints[pointIndex][4] = 0;
			inputPoints[pointIndex][5] = 0;
			inputPoints[pointIndex][6] = 1f-t;
		}
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		GLU.gluTessProperty(tobj, //
				GLU.GLU_TESS_WINDING_RULE, //
				GLU.GLU_TESS_WINDING_POSITIVE);
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		GLU.gluTessBeginPolygon(tobj, null);
		GLU.gluTessBeginContour(tobj);
		//

		//gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
			GLU.gluTessVertex(tobj, inputPoints[pointIndex], 0, inputPoints[pointIndex]);
		}
		//
		GLU.gluTessEndContour(tobj);
		GLU.gluTessEndPolygon(tobj);
		GLU.gluDeleteTess(tobj);
		//gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	}
	

	public void renderComplexBand(GL2 gl, List<Pair<Point2D, Point2D>> anchorPoints,
			boolean highlight, float[] color, float opacity, boolean fade) {

		if (anchorPoints == null || anchorPoints.size() < 2)
			return;

		// gl.glPushName(pickingManager.getPickingID(viewID,
		// EPickingType.COMPARE_RIBBON_SELECTION, bandID));

		// float yCorrection = 0;
		float z = 0f;
		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		for (Pair<Point2D, Point2D> anchorPair : anchorPoints) {
			Vec3f vec = new Vec3f((float) anchorPair.getFirst().getX(),
					(float) anchorPair.getFirst().getY(), z);
			inputPoints.add(vec);
			// gl.glPointSize(4);
			// gl.glColor4f(1, 0, 0, 1);
			// gl.glBegin(GL2.GL_POINTS);
			// gl.glVertex3f(vec.x(), vec.y(), -1);
			// gl.glEnd();
			// GLHelperFunctions.drawPointAt(gl, vec);
		}
		// inputPoints.add(new Vec3f(side1AnchorPos1[0], side1AnchorPos1[1],
		// z));
		// inputPoints.add(new Vec3f(side1AnchorPos1[0] + ((isOffset1Horizontal)
		// ? offsetSide1 : 0),
		// side1AnchorPos1[1] + ((!isOffset1Horizontal) ? offsetSide1 : 0), z));
		// inputPoints.add(new Vec3f(side2AnchorPos1[0] + ((isOffset2Horizontal)
		// ? offsetSide2 : 0),
		// side2AnchorPos1[1] + ((!isOffset2Horizontal) ? offsetSide2 : 0), z));
		// inputPoints.add(new Vec3f(side2AnchorPos1[0], side2AnchorPos1[1],
		// z));

		NURBSCurve curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		// Band border
		gl.glLineWidth(1);

		if (highlight)
			gl.glColor4f(color[0], color[1], color[2], opacity);// 0.8f);
		else
			gl.glColor4f(color[0], color[1], color[2], opacity * 2);

//		gl.glBegin(GL.GL_LINE_STRIP);
//		for (int i = 0; i < outputPoints.size(); i++) {
//			gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(), outputPoints
//					.get(i).z());
//		}
//		gl.glEnd();

		inputPoints = new ArrayList<Vec3f>();
		for (Pair<Point2D, Point2D> anchorPair : anchorPoints) {
			Vec3f vec = new Vec3f((float) anchorPair.getSecond().getX(),
					(float) anchorPair.getSecond().getY(), z);
			inputPoints.add(vec);
			// gl.glPointSize(4);
			// gl.glColor4f(1, 0, 0, 1);
			// gl.glBegin(GL2.GL_POINTS);
			// gl.glVertex3f(vec.x(), vec.y(), vec.z());
			// gl.glEnd();
			// GLHelperFunctions.drawPointAt(gl, vec);
		}
		// inputPoints.add(new Vec3f(side1AnchorPos2[0], side1AnchorPos2[1],
		// z));
		// inputPoints.add(new Vec3f(side1AnchorPos2[0] + ((isOffset1Horizontal)
		// ? offsetSide1 : 0),
		// side1AnchorPos2[1] + ((!isOffset1Horizontal) ? offsetSide1 : 0), z));
		// inputPoints.add(new Vec3f(side2AnchorPos2[0] + ((isOffset2Horizontal)
		// ? offsetSide2 : 0),
		// side2AnchorPos2[1] + ((!isOffset2Horizontal) ? offsetSide2 : 0), z));
		// inputPoints.add(new Vec3f(side2AnchorPos2[0], side2AnchorPos2[1],
		// z));

		curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> points = curve.getCurvePoints();
		// Reverse point order
		for (int i = points.size() - 1; i >= 0; i--) {
			outputPoints.add(points.get(i));
		}

		// Band border
//		// gl.glLineWidth(1);
//		gl.glBegin(GL.GL_LINE_STRIP);
//		for (int i = 0; i < points.size(); i++) {
//			gl.glVertex3f(points.get(i).x(), points.get(i).y(), points.get(i).z());
//		}
//		gl.glEnd();

//		if (highlight)
//			gl.glColor4f(color[0], color[1], color[2], opacity);// 0.5f);
//		else
//			gl.glColor4f(color[0], color[1], color[2], opacity);

			this.render(gl, outputPoints, fade);

		// gl.glPopName();
	}
	
}
