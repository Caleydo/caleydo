package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.view.datagraph.node.ADataNode;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class BipartiteGraphLayout extends AGraphLayout {

	protected static final int MIN_NODE_SPACING_PIXELS = 20;
	protected static final int MAX_NODE_SPACING_PIXELS = 300;

	private Rectangle2D layoutArea;

	public BipartiteGraphLayout(GLDataGraph view, Graph<IDataGraphNode> graph) {
		super(view, graph);
		nodePositions = new HashMap<Object, Point2D>();
	}

	@Override
	public void setNodePosition(Object node, Point2D position) {
		nodePositions.put(node, position);
	}

	@Override
	public Point2D getNodePosition(Object node) {
		return nodePositions.get(node);
	}

	@Override
	public void layout(Rectangle2D area) {

		layoutArea = area;
		if (layoutArea == null)
			return;

		Set<IDataGraphNode> dataNodes = new HashSet<IDataGraphNode>();
		Set<IDataGraphNode> viewNodes = new HashSet<IDataGraphNode>();

		Collection<IDataGraphNode> nodes = graph.getNodes();

		int summedDataNodesWidthPixels = 0;
		int summedViewNodesWidthPixels = 0;
		int maxDataNodeHeightPixels = Integer.MIN_VALUE;
		int maxViewNodeHeightPixels = Integer.MIN_VALUE;

		for (IDataGraphNode node : nodes) {
			if (node instanceof ADataNode) {
				dataNodes.add(node);
				summedDataNodesWidthPixels += node.getWidthPixels();
				if (node.getHeightPixels() > maxDataNodeHeightPixels)
					maxDataNodeHeightPixels = node.getHeightPixels();
			} else {
				viewNodes.add(node);
				summedViewNodesWidthPixels += node.getWidthPixels();
				if (node.getHeightPixels() > maxViewNodeHeightPixels)
					maxViewNodeHeightPixels = node.getHeightPixels();
			}
		}

		float dataNodeSpacingPixels = (float) (layoutArea.getWidth() - summedDataNodesWidthPixels)
				/ (float) (dataNodes.size() - 1);
		dataNodeSpacingPixels = Math.max(dataNodeSpacingPixels,
				MIN_NODE_SPACING_PIXELS);
		dataNodeSpacingPixels = Math.min(dataNodeSpacingPixels,
				MAX_NODE_SPACING_PIXELS);

		float currentDataNodePositionX = (float) Math.max(
				(float) (layoutArea.getMinX() + (layoutArea.getWidth()
						- summedDataNodesWidthPixels - (dataNodes.size() - 1)
						* dataNodeSpacingPixels) / 2.0f), layoutArea.getMinX());

		float dataNodesCenterY = (float) layoutArea.getMinY()
				+ maxDataNodeHeightPixels / 2.0f;

		for (IDataGraphNode node : dataNodes) {
			setNodePosition(node, new Point2D.Float(currentDataNodePositionX
					+ node.getWidthPixels() / 2.0f, dataNodesCenterY));

			currentDataNodePositionX += node.getWidthPixels()
					+ dataNodeSpacingPixels;
			node.setUpsideDown(true);
		}

		float viewNodeSpacingPixels = (float) (layoutArea.getWidth() - summedViewNodesWidthPixels)
				/ (float) (viewNodes.size() - 1);
		viewNodeSpacingPixels = Math.max(viewNodeSpacingPixels,
				MIN_NODE_SPACING_PIXELS);
		viewNodeSpacingPixels = Math.min(viewNodeSpacingPixels,
				MAX_NODE_SPACING_PIXELS);

		float currentViewNodePositionX = (float) Math.max(
				(float) (layoutArea.getMinX() + (layoutArea.getWidth()
						- summedViewNodesWidthPixels - (viewNodes.size() - 1)
						* viewNodeSpacingPixels) / 2.0f), layoutArea.getMinX());

		float viewNodesCenterY = (float) layoutArea.getHeight()
				+ (float) layoutArea.getMinY() - maxViewNodeHeightPixels / 2.0f;

		for (IDataGraphNode node : viewNodes) {
			setNodePosition(node, new Point2D.Float(currentViewNodePositionX
					+ node.getWidthPixels() / 2.0f, viewNodesCenterY));

			currentViewNodePositionX += node.getWidthPixels()
					+ viewNodeSpacingPixels;
		}

	}

	@Override
	public void updateNodePositions() {
		layout(layoutArea);
		view.setNodePositionsUpdated(true);
	}

	@Override
	public void clearNodePositions() {
		nodePositions.clear();
	}

}
