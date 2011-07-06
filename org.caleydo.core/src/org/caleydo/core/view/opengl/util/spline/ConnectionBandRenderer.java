package org.caleydo.core.view.opengl.util.spline;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;

public class ConnectionBandRenderer {

	public final static int NUMBER_OF_SPLINE_POINTS = 30;

	private GLU glu;
	// private int displayListID;

	private GLUtessellator tobj;

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

	public void renderTestBand(GL2 gl) {

		// gl.glCallList(displayListID);
		// gl.glFlush();

		TesselationCallback tessCallback = new TesselationCallback(gl, glu);

		double star[][] = new double[][] {// [5][6]; 6x5 in java
			{ 2.50, 5.00, 0.0, 1.0, 0.0, 1.0 }, { 3.250, 2.000, 0.0, 1.0, 1.0, 0.0 },
					{ 4.000, 5.00, 0.0, 0.0, 1.0, 1.0 }, { 2.500, 1.500, 0.0, 1.0, 0.0, 0.0 },
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

	public void renderStraightBand(GL2 gl, float[] leftTopPos, float[] leftBottomPos, float[] rightTopPos,
		float[] rightBottomPos, boolean highlight, float xOffset, int bandID, boolean bandDetailAdaption,
		float[] color, float opacity) {

		if (leftTopPos == null || leftBottomPos == null || rightTopPos == null || rightBottomPos == null)
			return;

		// gl.glPushName(pickingManager.getPickingID(viewID,
		// EPickingType.COMPARE_RIBBON_SELECTION, bandID));

		// Band border
		gl.glLineWidth(1);

		if (highlight)
			gl.glColor4f(color[0], color[1], color[2], 0.8f);
		else
			gl.glColor4f(color[0], color[1], color[2], opacity * 2);

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(leftTopPos[0], leftTopPos[1], 0);
		gl.glVertex3f(rightTopPos[0], rightTopPos[1], 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(rightBottomPos[0], rightBottomPos[1], 0);
		gl.glVertex3f(leftBottomPos[0], leftBottomPos[1], 0);
		gl.glEnd();

		if (highlight)
			gl.glColor4f(color[0], color[1], color[2], 0.5f);
		else
			gl.glColor4f(color[0], color[1], color[2], opacity);

		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(leftTopPos[0], leftTopPos[1], 0);
		gl.glVertex3f(rightTopPos[0], rightTopPos[1], 0);
		gl.glVertex3f(rightBottomPos[0], rightBottomPos[1], 0);
		gl.glVertex3f(leftBottomPos[0], leftBottomPos[1], 0);
		gl.glEnd();

		// gl.glPopName();
	}

	public void renderSingleBand(GL2 gl, float[] leftTopPos, float[] leftBottomPos, float[] rightTopPos,
		float[] rightBottomPos, boolean highlight, float xOffset, int bandID, boolean bandDetailAdaption,
		float[] color, float opacity) {

		if (leftTopPos == null || leftBottomPos == null || rightTopPos == null || rightBottomPos == null)
			return;

		// gl.glPushName(pickingManager.getPickingID(viewID,
		// EPickingType.COMPARE_RIBBON_SELECTION, bandID));

		float yCorrection = 0;
		float z = 0.1f;

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftTopPos[0], leftTopPos[1], z));
		inputPoints.add(new Vec3f(leftTopPos[0] + xOffset, leftTopPos[1] - yCorrection, z));
		inputPoints.add(new Vec3f(rightTopPos[0] - xOffset, rightTopPos[1] + yCorrection, z));
		inputPoints.add(new Vec3f(rightTopPos[0], rightTopPos[1], z));

		NURBSCurve curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		// Band border
		gl.glLineWidth(1);

		if (highlight)
			gl.glColor4f(color[0], color[1], color[2], opacity);// 0.8f);
		else
			gl.glColor4f(color[0], color[1], color[2], opacity * 2);

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < outputPoints.size(); i++) {
			gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(), outputPoints.get(i).z());
		}
		gl.glEnd();

		inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(leftBottomPos[0], leftBottomPos[1], z));
		inputPoints.add(new Vec3f(leftTopPos[0] + xOffset, leftBottomPos[1] - yCorrection, z));
		inputPoints.add(new Vec3f(rightBottomPos[0] - xOffset, rightBottomPos[1] + yCorrection, z));
		inputPoints.add(new Vec3f(rightBottomPos[0], rightBottomPos[1], z));

		curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> points = curve.getCurvePoints();

		// Reverse point order
		for (int i = points.size() - 1; i >= 0; i--) {
			outputPoints.add(points.get(i));
		}

		// Band border
		// gl.glLineWidth(1);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++) {
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), points.get(i).z());
		}
		gl.glEnd();

		if (highlight)
			gl.glColor4f(color[0], color[1], color[2], opacity);// 0.5f);
		else
			gl.glColor4f(color[0], color[1], color[2], opacity);

		render(gl, outputPoints);
		// gl.glPopName();
	}

	public void renderSingleBand(GL2 gl, float[] side1AnchorPos1, float[] side1AnchorPos2,
		float[] side2AnchorPos1, float[] side2AnchorPos2, boolean highlight, float offsetSide1,
		float offsetSide2, float[] color, float opacity, boolean isOffset1Horizontal,
		boolean isOffset2Horizontal) {

		if (side1AnchorPos1 == null || side1AnchorPos2 == null || side2AnchorPos1 == null
			|| side2AnchorPos2 == null)
			return;

		// gl.glPushName(pickingManager.getPickingID(viewID,
		// EPickingType.COMPARE_RIBBON_SELECTION, bandID));

		float yCorrection = 0;
		float z = 0.1f;

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(side1AnchorPos1[0], side1AnchorPos1[1], z));
		inputPoints.add(new Vec3f(side1AnchorPos1[0] + ((isOffset1Horizontal) ? offsetSide1 : 0),
			side1AnchorPos1[1] + ((!isOffset1Horizontal) ? offsetSide1 : 0), z));
		inputPoints.add(new Vec3f(side2AnchorPos1[0] + ((isOffset2Horizontal) ? offsetSide2 : 0),
			side2AnchorPos1[1] + ((!isOffset2Horizontal) ? offsetSide2 : 0), z));
		inputPoints.add(new Vec3f(side2AnchorPos1[0], side2AnchorPos1[1], z));

		NURBSCurve curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		// Band border
		gl.glLineWidth(1);

		if (highlight)
			gl.glColor4f(color[0], color[1], color[2], opacity);// 0.8f);
		else
			gl.glColor4f(color[0], color[1], color[2], opacity * 2);

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < outputPoints.size(); i++) {
			gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(), outputPoints.get(i).z());
		}
		gl.glEnd();

		inputPoints = new ArrayList<Vec3f>();
		inputPoints.add(new Vec3f(side1AnchorPos2[0], side1AnchorPos2[1], z));
		inputPoints.add(new Vec3f(side1AnchorPos2[0] + ((isOffset1Horizontal) ? offsetSide1 : 0),
			side1AnchorPos2[1] + ((!isOffset1Horizontal) ? offsetSide1 : 0), z));
		inputPoints.add(new Vec3f(side2AnchorPos2[0] + ((isOffset2Horizontal) ? offsetSide2 : 0),
			side2AnchorPos2[1] + ((!isOffset2Horizontal) ? offsetSide2 : 0), z));
		inputPoints.add(new Vec3f(side2AnchorPos2[0], side2AnchorPos2[1], z));

		curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> points = curve.getCurvePoints();

		// Reverse point order
		for (int i = points.size() - 1; i >= 0; i--) {
			outputPoints.add(points.get(i));
		}

		// Band border
		// gl.glLineWidth(1);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++) {
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), points.get(i).z());
		}
		gl.glEnd();

		if (highlight)
			gl.glColor4f(color[0], color[1], color[2], opacity);// 0.5f);
		else
			gl.glColor4f(color[0], color[1], color[2], opacity);

		render(gl, outputPoints);
		// gl.glPopName();
	}

	public void renderComplexBand(GL2 gl, List<Pair<Point2D, Point2D>> anchorPoints, boolean highlight,
		float[] color, float opacity) {

		if (anchorPoints == null || anchorPoints.size() < 2)
			return;

		// gl.glPushName(pickingManager.getPickingID(viewID,
		// EPickingType.COMPARE_RIBBON_SELECTION, bandID));

		float yCorrection = 0;
		float z = 0.1f;

		ArrayList<Vec3f> inputPoints = new ArrayList<Vec3f>();
		for (Pair<Point2D, Point2D> anchorPair : anchorPoints) {
			inputPoints.add(new Vec3f((float) anchorPair.getFirst().getX(), (float) anchorPair.getFirst()
				.getY(), z));
		}
		// inputPoints.add(new Vec3f(side1AnchorPos1[0], side1AnchorPos1[1], z));
		// inputPoints.add(new Vec3f(side1AnchorPos1[0] + ((isOffset1Horizontal) ? offsetSide1 : 0),
		// side1AnchorPos1[1] + ((!isOffset1Horizontal) ? offsetSide1 : 0), z));
		// inputPoints.add(new Vec3f(side2AnchorPos1[0] + ((isOffset2Horizontal) ? offsetSide2 : 0),
		// side2AnchorPos1[1] + ((!isOffset2Horizontal) ? offsetSide2 : 0), z));
		// inputPoints.add(new Vec3f(side2AnchorPos1[0], side2AnchorPos1[1], z));

		NURBSCurve curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> outputPoints = curve.getCurvePoints();

		// Band border
		gl.glLineWidth(1);

		if (highlight)
			gl.glColor4f(color[0], color[1], color[2], opacity);// 0.8f);
		else
			gl.glColor4f(color[0], color[1], color[2], opacity * 2);

		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < outputPoints.size(); i++) {
			gl.glVertex3f(outputPoints.get(i).x(), outputPoints.get(i).y(), outputPoints.get(i).z());
		}
		gl.glEnd();

		inputPoints = new ArrayList<Vec3f>();
		for (Pair<Point2D, Point2D> anchorPair : anchorPoints) {
			inputPoints.add(new Vec3f((float) anchorPair.getSecond().getX(), (float) anchorPair.getSecond()
				.getY(), z));
		}
		// inputPoints.add(new Vec3f(side1AnchorPos2[0], side1AnchorPos2[1], z));
		// inputPoints.add(new Vec3f(side1AnchorPos2[0] + ((isOffset1Horizontal) ? offsetSide1 : 0),
		// side1AnchorPos2[1] + ((!isOffset1Horizontal) ? offsetSide1 : 0), z));
		// inputPoints.add(new Vec3f(side2AnchorPos2[0] + ((isOffset2Horizontal) ? offsetSide2 : 0),
		// side2AnchorPos2[1] + ((!isOffset2Horizontal) ? offsetSide2 : 0), z));
		// inputPoints.add(new Vec3f(side2AnchorPos2[0], side2AnchorPos2[1], z));

		curve = new NURBSCurve(inputPoints, NUMBER_OF_SPLINE_POINTS);
		ArrayList<Vec3f> points = curve.getCurvePoints();

		// Reverse point order
		for (int i = points.size() - 1; i >= 0; i--) {
			outputPoints.add(points.get(i));
		}

		// Band border
		// gl.glLineWidth(1);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (int i = 0; i < points.size(); i++) {
			gl.glVertex3f(points.get(i).x(), points.get(i).y(), points.get(i).z());
		}
		gl.glEnd();

		if (highlight)
			gl.glColor4f(color[0], color[1], color[2], opacity);// 0.5f);
		else
			gl.glColor4f(color[0], color[1], color[2], opacity);

		render(gl, outputPoints);
		// gl.glPopName();
	}
}
