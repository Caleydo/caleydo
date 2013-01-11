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
package org.caleydo.view.dvi.layout.edge.routing;

import java.awt.geom.Point2D;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.view.dvi.layout.TwoLayeredGraphLayout;
import org.caleydo.view.dvi.node.IDVINode;

public class ArcRoutingStrategy implements IEdgeRoutingStrategy {

	protected static final int BEND_POINT_STEP_PIXELS_PER_SLOT = 20;

	private TwoLayeredGraphLayout graphLayout;
	private PixelGLConverter pixelGLConverter;

	public ArcRoutingStrategy(TwoLayeredGraphLayout graphLayout,
			PixelGLConverter pixelGLConverter) {
		this.graphLayout = graphLayout;
		this.pixelGLConverter = pixelGLConverter;
	}

	@Override
	public void createEdge(IDVINode node1, IDVINode node2, List<Point2D> edgePoints) {
		if (edgePoints == null || edgePoints.size() < 2)
			return;

		Point2D point1 = edgePoints.get(0);
		Point2D point2 = edgePoints.get(1);

		Point2D leftPoint;
		Point2D rightPoint;

		if (point1.getX() > point2.getX()) {
			leftPoint = point2;
			rightPoint = point1;
		} else {
			leftPoint = point1;
			rightPoint = point2;
		}

		float anchorPositionY = (float) point1.getY()
				- pixelGLConverter.getGLHeightForPixelHeight((int) (node1
						.getHeightPixels() / 2.0f)
						+ calcEdgeBendPointYOffsetPixels(node1, node2));

		Point2D anchorPoint1 = new Point2D.Float((float) leftPoint.getX()
				+ node1.getWidth() / 2.0f, anchorPositionY);
		Point2D anchorPoint2 = new Point2D.Float((float) rightPoint.getX()
				- node2.getWidth() / 2.0f, anchorPositionY);

		edgePoints.clear();

		edgePoints.add(leftPoint);
		edgePoints.add(anchorPoint1);
		edgePoints.add(anchorPoint2);
		edgePoints.add(rightPoint);

	}

	public int calcEdgeBendPointYOffsetPixels(IDVINode node1, IDVINode node2) {
		int slotDistance = graphLayout.getSlotDistance(node1, node2);

		return (BEND_POINT_STEP_PIXELS_PER_SLOT * slotDistance);
	}
}
