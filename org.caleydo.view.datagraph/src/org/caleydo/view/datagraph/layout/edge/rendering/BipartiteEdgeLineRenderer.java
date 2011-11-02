package org.caleydo.view.datagraph.layout.edge.rendering;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.Edge;
import org.caleydo.view.datagraph.GLDataGraph;

public class BipartiteEdgeLineRenderer extends AEdgeLineRenderer {

	public BipartiteEdgeLineRenderer(Edge edge, GLDataGraph view, String label) {
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
