package org.caleydo.view.datagraph.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.view.datagraph.Edge;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.Graph;
import org.caleydo.view.datagraph.layout.edge.rendering.AEdgeRenderer;
import org.caleydo.view.datagraph.layout.edge.rendering.BipartiteEdgeBandRenderer;
import org.caleydo.view.datagraph.layout.edge.rendering.BipartiteEdgeLineRenderer;
import org.caleydo.view.datagraph.layout.edge.rendering.CustomLayoutEdgeBandRenderer;
import org.caleydo.view.datagraph.layout.edge.rendering.CustomLayoutEdgeLineRenderer;
import org.caleydo.view.datagraph.layout.edge.routing.BipartiteInsideLayerRoutingStrategy;
import org.caleydo.view.datagraph.layout.edge.routing.IEdgeRoutingStrategy;
import org.caleydo.view.datagraph.layout.edge.routing.SimpleEdgeRoutingStrategy;
import org.caleydo.view.datagraph.node.ADataNode;
import org.caleydo.view.datagraph.node.IDataGraphNode;
import org.caleydo.view.datagraph.node.ViewNode;

public class BipartiteGraphLayout extends AGraphLayout {

	protected static final int MIN_NODE_SPACING_PIXELS = 20;
	protected static final int MAX_NODE_SPACING_PIXELS = 300;

	private Rectangle2D layoutArea;
	private IEdgeRoutingStrategy customEdgeRoutingStrategy;
	private BipartiteInsideLayerRoutingStrategy insideLayerEdgeRoutingStrategy;
	private int maxDataNodeHeightPixels;
	private List<IDataGraphNode> sortedDataNodes;
	private List<IDataGraphNode> sortedViewNodes;

	public int getMaxDataNodeHeightPixels() {
		return maxDataNodeHeightPixels;
	}

	public BipartiteGraphLayout(GLDataGraph view, Graph graph) {
		super(view, graph);
		nodePositions = new HashMap<Object, Point2D>();
		sortedDataNodes = new ArrayList<IDataGraphNode>();
		sortedViewNodes = new ArrayList<IDataGraphNode>();
		customEdgeRoutingStrategy = new SimpleEdgeRoutingStrategy(graph);
		insideLayerEdgeRoutingStrategy = new BipartiteInsideLayerRoutingStrategy(
				this, view.getPixelGLConverter());
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
		maxDataNodeHeightPixels = Integer.MIN_VALUE;
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

		sortedDataNodes.clear();
		sortedViewNodes.clear();
		// TODO: do a proper sort
		sortedDataNodes.addAll(dataNodes);
		sortedViewNodes.addAll(viewNodes);

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

		int maxBendPointOffsetYPixels = Integer.MIN_VALUE;
		for (Edge edge : graph.getAllEdges()) {
			if (edge.getNode1() instanceof ADataNode
					&& edge.getNode2() instanceof ADataNode) {
				int bendPointOffsetYPixels = insideLayerEdgeRoutingStrategy
						.calcEdgeBendPointYOffsetPixels(edge.getNode1(),
								edge.getNode2());
				if (bendPointOffsetYPixels > maxBendPointOffsetYPixels) {
					maxBendPointOffsetYPixels = bendPointOffsetYPixels;
				}

			}
		}

		float dataNodesBottomY = (float) layoutArea.getMinY()
				+ maxBendPointOffsetYPixels;

		for (IDataGraphNode node : dataNodes) {
			if (!node.isCustomPosition()) {
				setNodePosition(
						node,
						new Point2D.Float(currentDataNodePositionX
								+ node.getWidthPixels() / 2.0f,
								dataNodesBottomY + node.getHeightPixels()
										/ 2.0f));
			}

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

		float viewNodesTopY = (float) layoutArea.getHeight()
				+ (float) layoutArea.getMinY();

		for (IDataGraphNode node : viewNodes) {
			if (!node.isCustomPosition()) {
				setNodePosition(
						node,
						new Point2D.Float(currentViewNodePositionX
								+ node.getWidthPixels() / 2.0f, viewNodesTopY
								- node.getHeightPixels() / 2.0f));
			}

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

	@Override
	public AEdgeRenderer getLayoutSpecificEdgeRenderer(Edge edge) {

		IDataGraphNode node1 = edge.getNode1();
		IDataGraphNode node2 = edge.getNode2();

		AEdgeRenderer edgeRenderer = null;

		if (node1 instanceof ViewNode || node2 instanceof ViewNode) {
			edgeRenderer = new BipartiteEdgeBandRenderer(edge, view);
			edgeRenderer.setEdgeRoutingStrategy(customEdgeRoutingStrategy);

		} else {
			edgeRenderer = new BipartiteEdgeLineRenderer(edge, view,
					view.getEdgeLabel((ADataNode) node1, (ADataNode) node2));
			edgeRenderer.setEdgeRoutingStrategy(insideLayerEdgeRoutingStrategy);
		}

		return edgeRenderer;
	}

	@Override
	public AEdgeRenderer getCustomLayoutEdgeRenderer(Edge edge) {
		IDataGraphNode node1 = edge.getNode1();
		IDataGraphNode node2 = edge.getNode2();

		AEdgeRenderer edgeRenderer = null;

		if (node1 instanceof ViewNode || node2 instanceof ViewNode) {
			edgeRenderer = new CustomLayoutEdgeBandRenderer(edge, view);

		} else {
			edgeRenderer = new CustomLayoutEdgeLineRenderer(edge, view,
					view.getEdgeLabel((ADataNode) node1, (ADataNode) node2));
		}

		edgeRenderer.setEdgeRoutingStrategy(customEdgeRoutingStrategy);
		return edgeRenderer;
	}

	public int getSlotDistance(IDataGraphNode node1, IDataGraphNode node2) {
		int index1 = sortedDataNodes.indexOf(node1);
		int index2 = sortedDataNodes.indexOf(node2);
		if (index1 == -1 || index2 == -1)
			return 0;

		return Math.abs(index1 - index2);
	}

}
