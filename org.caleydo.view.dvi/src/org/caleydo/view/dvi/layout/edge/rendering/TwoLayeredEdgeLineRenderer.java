/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.rendering;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;

public class TwoLayeredEdgeLineRenderer extends AEdgeLineRenderer {

	public TwoLayeredEdgeLineRenderer(Edge edge, GLDataViewIntegrator view, String label) {
		super(edge, view, label);
	}

	@Override
	protected void render(GL2 gl, List<Point2D> routedEdgePoints,
			ConnectionBandRenderer connectionBandRenderer, Point2D position1,
			Point2D position2, boolean highlight) {

		gl.glPushMatrix();
		gl.glTranslatef(0, 0, -0.1f);
		gl.glLineWidth(2);
		connectionBandRenderer.renderComplexCurve(gl, routedEdgePoints);
		gl.glPopMatrix();

		if (highlight) {
			Point2D bendPoint1 = routedEdgePoints.get(1);
			Vec3f centerPoint = new Vec3f((float) position1.getX()
					+ (float) (position2.getX() - position1.getX()) / 2.0f,
					(float) bendPoint1.getY(), 0.1f);
			renderLabel(gl, centerPoint);
		}

	}

}
