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
package org.caleydo.view.dvi.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.Graph;
import org.caleydo.view.dvi.layout.edge.rendering.AEdgeRenderer;
import org.caleydo.view.dvi.layout.edge.rendering.FreeLayoutEdgeBandRenderer;
import org.caleydo.view.dvi.layout.edge.rendering.FreeLayoutEdgeLineRenderer;
import org.caleydo.view.dvi.layout.edge.rendering.TwoLayeredEdgeBandRenderer;
import org.caleydo.view.dvi.layout.edge.rendering.TwoLayeredEdgeLineRenderer;
import org.caleydo.view.dvi.layout.edge.routing.ArcRoutingStrategy;
import org.caleydo.view.dvi.layout.edge.routing.CollisionAvoidanceRoutingStrategy;
import org.caleydo.view.dvi.layout.edge.routing.IEdgeRoutingStrategy;
import org.caleydo.view.dvi.node.ADataNode;
import org.caleydo.view.dvi.node.IDVINode;
import org.caleydo.view.dvi.node.ViewNode;

public class TwoLayeredGraphLayout
	extends AGraphLayout {

	protected static final int MIN_NODE_SPACING_PIXELS = 20;
	protected static final int MAX_NODE_SPACING_PIXELS = 300;

	protected static final int BARYCENTER_ITERATIONS_PHASE1 = 1;
	protected static final int BARYCENTER_ITERATIONS_PHASE2 = 2;

	private Rectangle2D layoutArea;
	private IEdgeRoutingStrategy customEdgeRoutingStrategy;
	private ArcRoutingStrategy insideLayerEdgeRoutingStrategy;
	private int maxDataNodeHeightPixels;
	private List<IDVINode> sortedDataNodes;
	private List<IDVINode> sortedViewNodes;

	public int getMaxDataNodeHeightPixels() {
		return maxDataNodeHeightPixels;
	}

	public TwoLayeredGraphLayout(GLDataViewIntegrator view, Graph graph) {
		super(view, graph);
		nodePositions = new HashMap<Object, Point2D>();
		sortedDataNodes = new ArrayList<IDVINode>();
		sortedViewNodes = new ArrayList<IDVINode>();
		customEdgeRoutingStrategy = new CollisionAvoidanceRoutingStrategy(graph);
		insideLayerEdgeRoutingStrategy = new ArcRoutingStrategy(this,
				view.getPixelGLConverter());
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

		Set<IDVINode> dataNodes = new HashSet<IDVINode>();
		Set<IDVINode> viewNodes = new HashSet<IDVINode>();

		Collection<IDVINode> nodes = graph.getNodes();

		int summedDataNodesWidthPixels = 0;
		int summedViewNodesWidthPixels = 0;
		maxDataNodeHeightPixels = Integer.MIN_VALUE;
		int maxViewNodeHeightPixels = Integer.MIN_VALUE;

		for (IDVINode node : nodes) {
			if (node instanceof ADataNode) {
				dataNodes.add(node);
				summedDataNodesWidthPixels += node.getWidthPixels();
				if (node.getHeightPixels() > maxDataNodeHeightPixels)
					maxDataNodeHeightPixels = node.getHeightPixels();
			}
			else {
				viewNodes.add(node);
				summedViewNodesWidthPixels += node.getWidthPixels();
				if (node.getHeightPixels() > maxViewNodeHeightPixels)
					maxViewNodeHeightPixels = node.getHeightPixels();
			}
		}

		sortedDataNodes.clear();
		sortedViewNodes.clear();
		sortedViewNodes.addAll(viewNodes);
		
		List<Pair<String, IDVINode>> dataNodeSortingList = new ArrayList<Pair<String,IDVINode>>();
		for(IDVINode dataNode : dataNodes) {
			dataNodeSortingList.add(new Pair<String, IDVINode>(dataNode.getCaption().toUpperCase(), dataNode));
		}
		Collections.sort(dataNodeSortingList);
		
		for(Pair<String, IDVINode> pair : dataNodeSortingList) {
			sortedDataNodes.add(pair.getSecond());
		}

		applyBaryCenterReordering();

		float dataNodeSpacingPixels = (float) (layoutArea.getWidth() - summedDataNodesWidthPixels)
				/ (float) (dataNodes.size() - 1);
		dataNodeSpacingPixels = Math.max(dataNodeSpacingPixels, MIN_NODE_SPACING_PIXELS);
		dataNodeSpacingPixels = Math.min(dataNodeSpacingPixels, MAX_NODE_SPACING_PIXELS);

		float currentDataNodePositionX = (float) Math.max(
				(float) (layoutArea.getMinX() + (layoutArea.getWidth()
						- summedDataNodesWidthPixels - (dataNodes.size() - 1)
						* dataNodeSpacingPixels) / 2.0f), layoutArea.getMinX());

		int maxBendPointOffsetYPixels = Integer.MIN_VALUE;
		for (Edge edge : graph.getAllEdges()) {
			if (edge.getNode1() instanceof ADataNode && edge.getNode2() instanceof ADataNode) {
				int bendPointOffsetYPixels = insideLayerEdgeRoutingStrategy
						.calcEdgeBendPointYOffsetPixels(edge.getNode1(), edge.getNode2());
				if (bendPointOffsetYPixels > maxBendPointOffsetYPixels) {
					maxBendPointOffsetYPixels = bendPointOffsetYPixels;
				}

			}
		}

		boolean isUpsideDown = false;
		float dataNodesBottomY = 0;
		// expand direction factor determines if node is expanded to the top or to the bottom
		float expandDirectorFactor = 1;

		if (view.isRenderedRemote()) {
			// VisBricks 2.0 case where data nodes will be rendered on top and expand to the bottom
			isUpsideDown = false;
			dataNodesBottomY = (float) layoutArea.getMaxY();
			expandDirectorFactor = -1;
		}
		else {
			isUpsideDown = true;
			dataNodesBottomY = (float) layoutArea.getMinY() + maxBendPointOffsetYPixels;
		}

		for (IDVINode node : sortedDataNodes) {

			if (!node.isCustomPosition()) {
				
				setNodePosition(node,
						new Point2D.Float(currentDataNodePositionX + node.getWidthPixels()
								/ 2.0f, dataNodesBottomY + node.getHeightPixels() / 2.0f * expandDirectorFactor));
			}

			currentDataNodePositionX += node.getWidthPixels() + dataNodeSpacingPixels;

			node.setUpsideDown(isUpsideDown);
		}

		float viewNodeSpacingPixels = (float) (layoutArea.getWidth() - summedViewNodesWidthPixels)
				/ (float) (viewNodes.size() - 1);
		viewNodeSpacingPixels = Math.max(viewNodeSpacingPixels, MIN_NODE_SPACING_PIXELS);
		viewNodeSpacingPixels = Math.min(viewNodeSpacingPixels, MAX_NODE_SPACING_PIXELS);

		float currentViewNodePositionX = (float) Math.max(
				(float) (layoutArea.getMinX() + (layoutArea.getWidth()
						- summedViewNodesWidthPixels - (viewNodes.size() - 1)
						* viewNodeSpacingPixels) / 2.0f), layoutArea.getMinX());

		float viewNodesTopY = (float) layoutArea.getHeight() + (float) layoutArea.getMinY();

		for (IDVINode node : sortedViewNodes) {
			if (!node.isCustomPosition()) {
				setNodePosition(node,
						new Point2D.Float(currentViewNodePositionX + node.getWidthPixels()
								/ 2.0f, viewNodesTopY - node.getHeightPixels() / 2.0f));
			}

			currentViewNodePositionX += node.getWidthPixels() + viewNodeSpacingPixels;
		}

	}

	// @Override
	// public void layout(Rectangle2D area) {
	//
	// layoutArea = area;
	// if (layoutArea == null)
	// return;
	//
	// Set<IDataGraphNode> dataNodes = new HashSet<IDataGraphNode>();
	// Set<IDataGraphNode> viewNodes = new HashSet<IDataGraphNode>();
	//
	// Collection<IDataGraphNode> nodes = graph.getNodes();
	//
	// int summedDataNodesWidthPixels = 0;
	// int summedViewNodesWidthPixels = 0;
	// maxDataNodeHeightPixels = Integer.MIN_VALUE;
	// int maxViewNodeHeightPixels = Integer.MIN_VALUE;
	//
	// for (IDataGraphNode node : nodes) {
	// if (node instanceof ADataNode) {
	// dataNodes.add(node);
	// summedDataNodesWidthPixels += node.getWidthPixels();
	// if (node.getHeightPixels() > maxDataNodeHeightPixels)
	// maxDataNodeHeightPixels = node.getHeightPixels();
	// } else {
	// viewNodes.add(node);
	// summedViewNodesWidthPixels += node.getWidthPixels();
	// if (node.getHeightPixels() > maxViewNodeHeightPixels)
	// maxViewNodeHeightPixels = node.getHeightPixels();
	// }
	// }
	//
	// sortedDataNodes.clear();
	// sortedViewNodes.clear();
	// // TODO: do a proper sort
	// sortedDataNodes.addAll(dataNodes);
	//
	// List<Pair<Float, IDataGraphNode>> viewSortingList = new
	// ArrayList<Pair<Float, IDataGraphNode>>(
	// viewNodes.size());
	//
	// for (IDataGraphNode viewNode : viewNodes) {
	// Set<Edge> edges = graph.getEdgesOfNode(viewNode);
	// if (edges != null) {
	// int dataNodeIndexSum = 0;
	// for (Edge edge : edges) {
	// IDataGraphNode dataNode = (edge.getNode1() == viewNode) ? edge
	// .getNode2() : edge.getNode1();
	// dataNodeIndexSum += sortedDataNodes.indexOf(dataNode);
	// }
	// viewSortingList.add(new Pair<Float, IDataGraphNode>(
	// (float) dataNodeIndexSum / (float) edges.size(), viewNode));
	// } else {
	// viewSortingList.add(new Pair<Float, IDataGraphNode>(0.0f, viewNode));
	// }
	// }
	//
	// Collections.sort(viewSortingList);
	//
	// for (Pair<Float, IDataGraphNode> viewPair : viewSortingList) {
	// sortedViewNodes.add(viewPair.getSecond());
	// }
	//
	// float dataNodeSpacingPixels = (float) (layoutArea.getWidth() -
	// summedDataNodesWidthPixels)
	// / (float) (dataNodes.size() - 1);
	// dataNodeSpacingPixels = Math.max(dataNodeSpacingPixels,
	// MIN_NODE_SPACING_PIXELS);
	// dataNodeSpacingPixels = Math.min(dataNodeSpacingPixels,
	// MAX_NODE_SPACING_PIXELS);
	//
	// float currentDataNodePositionX = (float) Math.max(
	// (float) (layoutArea.getMinX() + (layoutArea.getWidth()
	// - summedDataNodesWidthPixels - (dataNodes.size() - 1)
	// * dataNodeSpacingPixels) / 2.0f), layoutArea.getMinX());
	//
	// int maxBendPointOffsetYPixels = Integer.MIN_VALUE;
	// for (Edge edge : graph.getAllEdges()) {
	// if (edge.getNode1() instanceof ADataNode
	// && edge.getNode2() instanceof ADataNode) {
	// int bendPointOffsetYPixels = insideLayerEdgeRoutingStrategy
	// .calcEdgeBendPointYOffsetPixels(edge.getNode1(), edge.getNode2());
	// if (bendPointOffsetYPixels > maxBendPointOffsetYPixels) {
	// maxBendPointOffsetYPixels = bendPointOffsetYPixels;
	// }
	//
	// }
	// }
	//
	// float dataNodesBottomY = (float) layoutArea.getMinY() +
	// maxBendPointOffsetYPixels;
	//
	// for (IDataGraphNode node : sortedDataNodes) {
	// if (!node.isCustomPosition()) {
	// setNodePosition(
	// node,
	// new Point2D.Float(currentDataNodePositionX
	// + node.getWidthPixels() / 2.0f, dataNodesBottomY
	// + node.getHeightPixels() / 2.0f));
	// }
	//
	// currentDataNodePositionX += node.getWidthPixels() +
	// dataNodeSpacingPixels;
	// node.setUpsideDown(true);
	// }
	//
	// float viewNodeSpacingPixels = (float) (layoutArea.getWidth() -
	// summedViewNodesWidthPixels)
	// / (float) (viewNodes.size() - 1);
	// viewNodeSpacingPixels = Math.max(viewNodeSpacingPixels,
	// MIN_NODE_SPACING_PIXELS);
	// viewNodeSpacingPixels = Math.min(viewNodeSpacingPixels,
	// MAX_NODE_SPACING_PIXELS);
	//
	// float currentViewNodePositionX = (float) Math.max(
	// (float) (layoutArea.getMinX() + (layoutArea.getWidth()
	// - summedViewNodesWidthPixels - (viewNodes.size() - 1)
	// * viewNodeSpacingPixels) / 2.0f), layoutArea.getMinX());
	//
	// float viewNodesTopY = (float) layoutArea.getHeight()
	// + (float) layoutArea.getMinY();
	//
	// for (IDataGraphNode node : sortedViewNodes) {
	// if (!node.isCustomPosition()) {
	// setNodePosition(
	// node,
	// new Point2D.Float(currentViewNodePositionX
	// + node.getWidthPixels() / 2.0f, viewNodesTopY
	// - node.getHeightPixels() / 2.0f));
	// }
	//
	// currentViewNodePositionX += node.getWidthPixels() +
	// viewNodeSpacingPixels;
	// }
	//
	// }

	private void applyBaryCenterReordering() {
		
		List<IDVINode> viewNodesCopy = new ArrayList<IDVINode>(
				sortedViewNodes);
		
		int numCrossings = calcNumCrossings(sortedViewNodes, sortedDataNodes);
		sortAccordingToBaryCenter(viewNodesCopy, sortedDataNodes);

		int currentNumCrossings = calcNumCrossings(viewNodesCopy, sortedDataNodes);
		if (currentNumCrossings <= numCrossings) {
			sortedViewNodes = viewNodesCopy;
			numCrossings = currentNumCrossings;
		}
		viewNodesCopy = new ArrayList<IDVINode>(
				sortedViewNodes);
		
		boolean wasReversed = reverseOrderOfNodesWithEqualBaryCenters(viewNodesCopy,
				sortedDataNodes);
		
		if(wasReversed) {
			currentNumCrossings = calcNumCrossings(viewNodesCopy, sortedDataNodes);
			if (currentNumCrossings <= numCrossings) {
				sortedViewNodes = viewNodesCopy;
				numCrossings = currentNumCrossings;
			}
		}
		
		
		
		//This version of the sugiyama 1981 barycenter edge crossing minimization algorithm is untested and unfinished.
//		int numCrossings = calcNumCrossings(sortedViewNodes, sortedDataNodes);
//
//		List<IDataGraphNode> viewNodesCopy = new ArrayList<IDataGraphNode>(
//				sortedViewNodes);
//		List<IDataGraphNode> dataNodesCopy = new ArrayList<IDataGraphNode>(
//				sortedDataNodes);
//		
//		reorderAccordingToBaryCenters(numCrossings);
//
//		for (int i = 0; i < BARYCENTER_ITERATIONS_PHASE2; i++) {
//
//			viewNodesCopy = new ArrayList<IDataGraphNode>(sortedViewNodes);
//			dataNodesCopy = new ArrayList<IDataGraphNode>(sortedDataNodes);
//			boolean wasReversed = reverseOrderOfNodesWithEqualBaryCenters(viewNodesCopy,
//					dataNodesCopy);
//
//			if (wasReversed) {
//				if(!areBaryCentersAscending(dataNodesCopy, viewNodesCopy)) {
//					reorderAccordingToBaryCenters(numCrossings);
//				}
//			}
//
//		}

	}

//	private void reorderAccordingToBaryCenters(int numCrossings) {
//
//		for (int j = 0; j < BARYCENTER_ITERATIONS_PHASE1; j++) {
//
//			List<IDataGraphNode> viewNodesCopy = new ArrayList<IDataGraphNode>(
//					sortedViewNodes);
//			List<IDataGraphNode> dataNodesCopy = new ArrayList<IDataGraphNode>(
//					sortedDataNodes);
//			sortAccordingToBaryCenter(viewNodesCopy, dataNodesCopy);
//
//			int currentNumCrossings = calcNumCrossings(viewNodesCopy, dataNodesCopy);
//			if (currentNumCrossings <= numCrossings) {
//				sortedViewNodes = viewNodesCopy;
//				sortedDataNodes = dataNodesCopy;
//				numCrossings = currentNumCrossings;
//			}
//
//			viewNodesCopy = new ArrayList<IDataGraphNode>(sortedViewNodes);
//			dataNodesCopy = new ArrayList<IDataGraphNode>(sortedDataNodes);
//			sortAccordingToBaryCenter(sortedDataNodes, sortedViewNodes);
//
//			currentNumCrossings = calcNumCrossings(viewNodesCopy, dataNodesCopy);
//			if (currentNumCrossings <= numCrossings) {
//				sortedViewNodes = viewNodesCopy;
//				sortedDataNodes = dataNodesCopy;
//				numCrossings = currentNumCrossings;
//			}
//		}
//	}
//
//	private boolean areBaryCentersAscending(List<IDataGraphNode> nodesToTest,
//			List<IDataGraphNode> otherLevelNodes) {
//
//		float prevBaryCenter = -1;
//		for (IDataGraphNode node : nodesToTest) {
//			float baryCenter = calcBaryCenter(node, otherLevelNodes);
//			if (baryCenter < prevBaryCenter)
//				return false;
//			prevBaryCenter = baryCenter;
//		}
//
//		return true;
//	}

	/**
	 * @param nodesToSort
	 *            It is assumed, that these nodes are sorted according to their
	 *            barycenters
	 * @param nodesOtherLevel
	 * @return
	 */
	private boolean reverseOrderOfNodesWithEqualBaryCenters(
			List<IDVINode> nodesToSort, List<IDVINode> otherLevelNodes) {

		List<IDVINode> sortedList = new ArrayList<IDVINode>();
		List<IDVINode> currentSubList = new ArrayList<IDVINode>();
		boolean nodesReversed = false;
		float prevBaryCenter = -1;

		for (int i = 0; i < nodesToSort.size(); i++) {
			IDVINode node = nodesToSort.get(i);
			// Maybe float problem
			float baryCenter = calcBaryCenter(node, otherLevelNodes);

			if (baryCenter == prevBaryCenter) {
				if (currentSubList.isEmpty()) {
					currentSubList.add(nodesToSort.get(i - 1));
				}
				currentSubList.add(node);
				nodesReversed = true;
			} else {
				if (!currentSubList.isEmpty()) {
					Collections.reverse(currentSubList);
					sortedList.addAll(currentSubList);
					currentSubList.clear();
				} else {
					if (i != 0)
						sortedList.add(nodesToSort.get(i - 1));
				}
			}
			prevBaryCenter = baryCenter;
		}
		if (!currentSubList.isEmpty()) {
			Collections.reverse(currentSubList);
			sortedList.addAll(currentSubList);
		} else {
			if (!nodesToSort.isEmpty()) {
				sortedList.add(nodesToSort.get(nodesToSort.size() - 1));
			}
		}

		nodesToSort.clear();
		nodesToSort.addAll(sortedList);

		return nodesReversed;
	}

	private int calcNumCrossings(List<IDVINode> level1Nodes,
			List<IDVINode> level2Nodes) {

		int numCrossings = 0;

		for (int j = 0; j < level1Nodes.size() - 1; j++) {
			IDVINode level1Node1 = level1Nodes.get(j);
			for (int k = j + 1; k < level1Nodes.size(); k++) {
				IDVINode level1Node2 = level1Nodes.get(k);
				for (int a = 0; a < level2Nodes.size() - 1; a++) {
					IDVINode level2Node1 = level2Nodes.get(a);
					for (int b = a + 1; b < level2Nodes.size(); b++) {
						IDVINode level2Node2 = level2Nodes.get(b);
						if (graph.incident(level1Node1, level2Node2)
								&& graph.incident(level1Node2, level2Node1)) {
							numCrossings++;
						}
					}

				}

			}

		}

		return numCrossings;

	}

	private void sortAccordingToBaryCenter(List<IDVINode> nodesToSort,
			List<IDVINode> otherLevelNodes) {
		List<Pair<Float, IDVINode>> sortingList = new ArrayList<Pair<Float, IDVINode>>(
				nodesToSort.size());

		for (IDVINode viewNode : nodesToSort) {
			float baryCenter = calcBaryCenter(viewNode, otherLevelNodes);
			sortingList.add(new Pair<Float, IDVINode>(baryCenter, viewNode));
		}

		Collections.sort(sortingList);

		nodesToSort.clear();

		for (Pair<Float, IDVINode> viewPair : sortingList) {
			nodesToSort.add(viewPair.getSecond());
		}
	}

	private float calcBaryCenter(IDVINode node, List<IDVINode> otherLevelNodes) {

		int dataNodeIndexSum = 0;
		int numEdges = 0;
		for (IDVINode otherLevelNode : otherLevelNodes) {
			if (graph.incident(node, otherLevelNode)) {
				dataNodeIndexSum += otherLevelNodes.indexOf(otherLevelNode) + 1;
				numEdges++;
			}
		}
		if (numEdges != 0)
			return (float) dataNodeIndexSum / (float) numEdges;
		return 0;
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

		IDVINode node1 = edge.getNode1();
		IDVINode node2 = edge.getNode2();

		AEdgeRenderer edgeRenderer = null;

		if (node1 instanceof ViewNode || node2 instanceof ViewNode) {
			edgeRenderer = new TwoLayeredEdgeBandRenderer(edge, view);
			edgeRenderer.setEdgeRoutingStrategy(customEdgeRoutingStrategy);

		}
		else {
			edgeRenderer = new TwoLayeredEdgeLineRenderer(edge, view, view.getEdgeLabel(
					(ADataNode) node1, (ADataNode) node2));
			edgeRenderer.setEdgeRoutingStrategy(insideLayerEdgeRoutingStrategy);
		}

		return edgeRenderer;
	}

	@Override
	public AEdgeRenderer getCustomLayoutEdgeRenderer(Edge edge) {
		IDVINode node1 = edge.getNode1();
		IDVINode node2 = edge.getNode2();

		AEdgeRenderer edgeRenderer = null;

		if (node1 instanceof ViewNode || node2 instanceof ViewNode) {
			edgeRenderer = new FreeLayoutEdgeBandRenderer(edge, view);

		}
		else {
			edgeRenderer = new FreeLayoutEdgeLineRenderer(edge, view, view.getEdgeLabel(
					(ADataNode) node1, (ADataNode) node2));
		}

		edgeRenderer.setEdgeRoutingStrategy(customEdgeRoutingStrategy);
		return edgeRenderer;
	}

	public int getSlotDistance(IDVINode node1, IDVINode node2) {
		int index1 = sortedDataNodes.indexOf(node1);
		int index2 = sortedDataNodes.indexOf(node2);
		if (index1 == -1 || index2 == -1)
			return 0;

		return Math.abs(index1 - index2);
	}

}
