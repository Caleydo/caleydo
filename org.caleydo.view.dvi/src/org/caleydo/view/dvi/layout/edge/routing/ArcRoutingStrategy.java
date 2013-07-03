/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
