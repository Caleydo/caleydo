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
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.node.IDVINode;

public abstract class AEdgeLineRenderer extends AEdgeRenderer {

	private String label;

	public AEdgeLineRenderer(Edge edge, GLDataViewIntegrator view, String label) {
		super(edge, view);
		this.label = label;
	}

	@Override
	public void renderEdge(GL2 gl, ConnectionBandRenderer connectionBandRenderer,
			boolean highlight) {

		if (!view.isShowDataConnections() && !highlight)
			return;
		gl.glPushAttrib(GL2.GL_LINE_BIT | GL2.GL_COLOR_BUFFER_BIT);
		if (highlight) {
			gl.glColor3f(0.5f, 0.5f, 0.5f);
		} else {
			gl.glColor3f(0.8f, 0.8f, 0.8f);
		}
		// gl.glLineWidth(2);
		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glLineStipple(1, (short) 127);

		IDVINode node1 = edge.getNode1();
		IDVINode node2 = edge.getNode2();
		Point2D position1 = node1.getPosition();
		Point2D position2 = node2.getPosition();

		List<Point2D> edgePoints = new ArrayList<Point2D>();
		edgePoints.add(position1);
		edgePoints.add(position2);

		edgeRoutingStrategy.setNodes(node1, node2);
		edgeRoutingStrategy.createEdge(edgePoints);

		render(gl, edgePoints, connectionBandRenderer, position1, position2, highlight);
		gl.glPopAttrib();

	}

	protected abstract void render(GL2 gl, List<Point2D> routedEdgePoints,
			ConnectionBandRenderer connectionBandRenderer, Point2D position1,
			Point2D position2, boolean highlight);

	protected void renderLabel(GL2 gl, Vec3f centerPoint) {

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		float height = pixelGLConverter.getGLHeightForPixelHeight(14);
		float requiredWidth = textRenderer.getRequiredTextWidth(label, height);

		textRenderer.renderTextInBounds(gl, label, centerPoint.x()
				- (requiredWidth / 2.0f), centerPoint.y() - (height / 2.0f),
				centerPoint.z() + 0.1f,
				requiredWidth + pixelGLConverter.getGLWidthForPixelWidth(1), height);
	}

}
