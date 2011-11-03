package org.caleydo.view.datagraph.layout.edge.routing;

import java.awt.geom.Point2D;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.view.datagraph.layout.BipartiteGraphLayout;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class BipartiteInsideLayerRoutingStrategy implements
		IEdgeRoutingStrategy {

	protected static final int BEND_POINT_STEP_PIXELS_PER_SLOT = 20;

	private BipartiteGraphLayout graphLayout;
	private IDataGraphNode node1;
	private IDataGraphNode node2;
	private PixelGLConverter pixelGLConverter;

	public BipartiteInsideLayerRoutingStrategy(
			BipartiteGraphLayout graphLayout, PixelGLConverter pixelGLConverter) {
		this.graphLayout = graphLayout;
		this.pixelGLConverter = pixelGLConverter;
	}

	@Override
	public void createEdge(List<Point2D> edgePoints) {
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

	@Override
	public void setNodes(IDataGraphNode node1, IDataGraphNode node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	public int calcEdgeBendPointYOffsetPixels(IDataGraphNode node1,
			IDataGraphNode node2) {
		int slotDistance = graphLayout.getSlotDistance(node1, node2);

		return (BEND_POINT_STEP_PIXELS_PER_SLOT * slotDistance);
	}

}
