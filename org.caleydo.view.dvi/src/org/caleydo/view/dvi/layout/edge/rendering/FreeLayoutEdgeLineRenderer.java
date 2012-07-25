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
package org.caleydo.view.dvi.layout.edge.rendering;

import gleem.linalg.Vec3f;
import java.awt.geom.Point2D;
import java.util.List;
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

		gl.glBegin(GL2.GL_LINE_STRIP);
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
