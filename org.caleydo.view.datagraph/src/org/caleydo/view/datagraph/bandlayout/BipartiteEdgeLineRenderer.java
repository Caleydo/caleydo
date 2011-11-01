package org.caleydo.view.datagraph.bandlayout;

import gleem.linalg.Vec3f;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.DataDomainGraph;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.datagraph.Edge;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.node.ADataNode;

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
			Point2D centerPosition = routedEdgePoints.get(1);
			Vec3f centerPoint = new Vec3f((float) centerPosition.getX(),
					(float) centerPosition.getY(), 0.1f);
			renderLabel(gl, centerPoint);
		}

	}

	private void renderLabeledCurve(GL2 gl, List<Point2D> edgePoints,
			ConnectionBandRenderer connectionBandRenderer) {
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, -0.1f);
		List<Vec3f> curvePoints = connectionBandRenderer.calcInterpolatedCurve(
				gl, edgePoints);

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

		ADataNode node1 = (ADataNode) edge.getNode1();
		ADataNode node2 = (ADataNode) edge.getNode2();

		DataDomainGraph dataDomainGraph = DataDomainManager.get()
				.getDataDomainGraph();

		Set<org.caleydo.core.data.datadomain.Edge> edges = dataDomainGraph
				.getEdges(node1.getDataDomain(), node2.getDataDomain());

		StringBuffer stringBuffer = new StringBuffer();

		Iterator<org.caleydo.core.data.datadomain.Edge> iterator = edges
				.iterator();
		while (iterator.hasNext()) {
			org.caleydo.core.data.datadomain.Edge e = iterator.next();
			IDCategory category = e.getIdCategory();
			if (category != null) {
				stringBuffer.append(e.getIdCategory().getCategoryName());
			} else {
				stringBuffer.append("Unknown Mapping");
			}
			if (iterator.hasNext()) {
				stringBuffer.append(", ");
			}
		}

		String edgeLabel = stringBuffer.toString();

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		float height = pixelGLConverter.getGLHeightForPixelHeight(14);
		float requiredWidth = textRenderer.getRequiredTextWidth(edgeLabel,
				height);

		textRenderer.renderTextInBounds(gl, edgeLabel, centerPoint.x()
				- (requiredWidth / 2.0f), centerPoint.y() - (height / 2.0f),
				centerPoint.z() + 0.1f, requiredWidth, height);
	}

}
