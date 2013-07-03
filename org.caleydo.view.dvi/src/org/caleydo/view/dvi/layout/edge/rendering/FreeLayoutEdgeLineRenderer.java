/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;

public class FreeLayoutEdgeLineRenderer extends AEdgeLineRenderer {

	public FreeLayoutEdgeLineRenderer(Edge edge, GLDataViewIntegrator view, String label) {
		super(edge, view, label);
	}

	@Override
	protected void render(GL2 gl, List<Point2D> routedEdgePoints,
			ConnectionBandRenderer connectionBandRenderer, Point2D position1,
			Point2D position2, boolean highlight) {
		routedEdgePoints.add(0, position1);
		routedEdgePoints.add(position2);

		if (highlight) {
			renderLabeledCurve(gl, routedEdgePoints, connectionBandRenderer);
		} else {
			gl.glPushMatrix();
			gl.glTranslatef(0, 0, -0.1f);
			gl.glLineWidth(2);
			connectionBandRenderer.renderInterpolatedCurve(gl, routedEdgePoints);
			// connectionBandRenderer.renderComplexCurve(gl, routedEdgePoints);
			gl.glPopMatrix();
		}
	}

	private void renderLabeledCurve(GL2 gl, List<Point2D> edgePoints,
			ConnectionBandRenderer connectionBandRenderer) {
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, -0.1f);
		gl.glLineWidth(2);
		List<Vec3f> curvePoints = connectionBandRenderer.calcInterpolatedCurve(gl,
				edgePoints);

		Vec3f startPoint = curvePoints.get(0);
		Vec3f endPoint = curvePoints.get(curvePoints.size() - 1);
		Vec3f centerPoint = startPoint;
		float distanceDelta = centerPoint.minus(endPoint).lengthSquared();

		gl.glBegin(GL.GL_LINE_STRIP);
		for (Vec3f point : curvePoints) {
			gl.glVertex3f(point.x(), point.y(), point.z());
			float distanceStart = point.minus(startPoint).lengthSquared();
			float dinstanceEnd = point.minus(endPoint).lengthSquared();
			float currentDistanceDelta = Math.abs(distanceStart - dinstanceEnd);
			if (currentDistanceDelta < distanceDelta) {
				distanceDelta = currentDistanceDelta;
				centerPoint = point;
			}
		}
		gl.glEnd();

		gl.glPopMatrix();

		renderLabel(gl, centerPoint);
	}

}
