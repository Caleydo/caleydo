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
