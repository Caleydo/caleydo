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
package org.caleydo.view.enroute.path;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.connectionline.ClosedArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndArrowRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.enroute.event.PathRendererChangedEvent;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANode;
import org.caleydo.view.enroute.path.node.BranchSummaryNode;
import org.caleydo.view.enroute.path.node.ComplexNode;
import org.caleydo.view.enroute.path.node.CompoundNode;
import org.caleydo.view.enroute.path.node.mode.ComplexNodePreviewMode;
import org.caleydo.view.enroute.path.node.mode.CompoundNodePreviewMode;
import org.caleydo.view.enroute.path.node.mode.GeneNodePreviewMode;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

/**
 * Renders a simple vertical path of pathway nodes.
 *
 * @author Christian Partl
 *
 */
public class EnRoutePathRenderer extends VerticalPathRenderer {

	/**
	 * Default size for vertical node space, i.e., the space reserved for a single node.
	 */
	// protected static final int DEFAULT_VERTICAL_NODE_SPACING_PIXELS = 50;
	// public final static int DEFAULT_DATA_ROW_HEIGHT_PIXELS = 60;
	// protected final static int BRANCH_SUMMARY_NODE_TO_LINEARIZED_NODE_VERTICAL_DISTANCE_PIXELS = 20;
	// protected final static int BRANCH_AREA_SIDE_SPACING_PIXELS = 8;
	// public final static int BRANCH_COLUMN_WIDTH_PIXELS = 100;
	// public final static int PATH_COLUMN_WIDTH_PIXELS = 150;

	protected static final int MAX_BRANCH_SWITCHING_PATH_LENGTH = 5;

	/**
	 * Branch summary node that is currently expanded
	 */
	protected BranchSummaryNode expandedBranchSummaryNode;

	/**
	 * Map that associates the linearized nodes with their incoming branch summary nodes.
	 */
	protected Map<ANode, BranchSummaryNode> linearizedNodesToIncomingBranchSummaryNodesMap = new HashMap<>();

	/**
	 * Map that associates the linearized nodes with their outgoing branch summary nodes.
	 */
	protected Map<ANode, BranchSummaryNode> linearizedNodesToOutgoingBranchSummaryNodesMap = new HashMap<>();

	/**
	 * Map that associates every node in a branch with a linearized node.
	 */
	protected Map<ALinearizableNode, ALinearizableNode> branchNodesToLinearizedNodesMap = new HashMap<>();

	public EnRoutePathRenderer(AGLView view, List<TablePerspective> tablePerspectives) {
		super(view, tablePerspectives);
		setSizeConfig(PathSizeConfiguration.ENROUTE_DEFAULT);
		// setSizeConfig(new PathSizeConfiguration.Builder(PathSizeConfiguration.ENROUTE_COMPACT).minNodeSpacing(40)
		// .build());

	}

	@Override
	protected void destroyNodes() {
		for (ALinearizableNode node : branchNodesToLinearizedNodesMap.keySet()) {
			node.destroy();
		}
		for (BranchSummaryNode node : linearizedNodesToIncomingBranchSummaryNodesMap.values()) {
			node.destroy();
		}
		for (BranchSummaryNode node : linearizedNodesToOutgoingBranchSummaryNodesMap.values()) {
			node.destroy();
		}
		super.destroyNodes();
	}

	@Override
	protected void createNodes(List<List<PathwayVertexRep>> pathSegments) {
		super.createNodes(pathSegments);
		expandedBranchSummaryNode = null;

		branchNodesToLinearizedNodesMap.clear();
		linearizedNodesToIncomingBranchSummaryNodesMap.clear();
		linearizedNodesToOutgoingBranchSummaryNodesMap.clear();

		// Create branch nodes
		for (int i = 0; i < pathNodes.size(); i++) {

			ALinearizableNode currentNode = pathNodes.get(i);
			BranchSummaryNode incomingNode = new BranchSummaryNode(view, lastNodeID++, currentNode, this);
			incomingNode.init();
			BranchSummaryNode outgoingNode = new BranchSummaryNode(view, lastNodeID++, currentNode, this);
			outgoingNode.init();
			List<ALinearizableNode> sourceNodes = new ArrayList<ALinearizableNode>();
			List<ALinearizableNode> targetNodes = new ArrayList<ALinearizableNode>();

			for (PathwayVertexRep currentVertexRep : currentNode.getVertexReps()) {
				PathwayVertexRep prevVertexRep = null;
				PathwayVertexRep nextVertexRep = null;
				PathwayGraph pathway = currentVertexRep.getPathway();

				if (i > 0) {
					ALinearizableNode prevNode = pathNodes.get(i - 1);
					for (PathwayVertexRep prevVR : prevNode.getVertexReps()) {
						if (prevVR.getPathway() == pathway) {
							DefaultEdge edge = pathway.getEdge(prevVR, currentVertexRep);
							if (edge != null) {
								prevVertexRep = prevVR;
							}
						}
					}
				}
				if (i != pathNodes.size() - 1) {
					ALinearizableNode nextNode = pathNodes.get(i + 1);
					for (PathwayVertexRep nextVR : nextNode.getVertexReps()) {
						if (nextVR.getPathway() == pathway) {
							DefaultEdge edge = pathway.getEdge(currentVertexRep, nextVR);
							if (edge != null) {
								nextVertexRep = nextVR;
							}
						}
					}
				}

				List<PathwayVertexRep> sourceVertexReps = Graphs.predecessorListOf(pathway, currentVertexRep);
				sourceVertexReps.remove(prevVertexRep);
				List<PathwayVertexRep> targetVertexReps = Graphs.successorListOf(pathway, currentVertexRep);
				targetVertexReps.remove(nextVertexRep);

				if (sourceVertexReps.size() > 0) {
					createNodesForList(sourceNodes, sourceVertexReps);
				}

				if (targetVertexReps.size() > 0) {
					createNodesForList(targetNodes, targetVertexReps);
				}
			}

			if (sourceNodes.size() > 0) {
				incomingNode.setBranchNodes(sourceNodes);
				linearizedNodesToIncomingBranchSummaryNodesMap.put(currentNode, incomingNode);
				for (ALinearizableNode node : sourceNodes) {
					setPreviewMode(node);
					branchNodesToLinearizedNodesMap.put(node, currentNode);
				}
			}

			if (targetNodes.size() > 0) {
				outgoingNode.setBranchNodes(targetNodes);
				linearizedNodesToOutgoingBranchSummaryNodesMap.put(currentNode, outgoingNode);
				for (ALinearizableNode node : targetNodes) {
					setPreviewMode(node);
					branchNodesToLinearizedNodesMap.put(node, currentNode);
				}
			}
		}

	}

	public void setPreviewMode(ALinearizableNode node) {
		if (node instanceof ComplexNode) {
			node.setMode(new ComplexNodePreviewMode(view, this));
		} else if (node instanceof CompoundNode) {
			node.setMode(new CompoundNodePreviewMode(view, this));
		} else {
			node.setMode(new GeneNodePreviewMode(view, this));
		}
	}

	/**
	 * @param node
	 *            The node for which the positions of associated branch nodes shall be calculated
	 * @return the minimum view height in pixels that would be required by the nodes to be displayed.
	 */
	private int calculatePositionsOfBranchNodes(ANode node) {
		int minViewHeightPixelsIncoming = 0;
		int minViewHeightPixelsOutgoing = 0;
		ANode incomingNode = linearizedNodesToIncomingBranchSummaryNodesMap.get(node);
		if ((incomingNode != null) && (incomingNode != expandedBranchSummaryNode)) {
			minViewHeightPixelsIncoming = calculateBranchNodePosition((BranchSummaryNode) incomingNode);
		}

		ANode outgoingNode = linearizedNodesToOutgoingBranchSummaryNodesMap.get(node);
		if ((outgoingNode != null) && (outgoingNode != expandedBranchSummaryNode)) {
			minViewHeightPixelsOutgoing = calculateBranchNodePosition((BranchSummaryNode) outgoingNode);
		}
		return Math.max(minViewHeightPixelsIncoming, minViewHeightPixelsOutgoing);
	}

	/**
	 * Calculates the spacings between all anchor nodes (nodes with mapped data) of the path.
	 *
	 * @return
	 */
	private List<AnchorNodeSpacing> calcAnchorNodeSpacings(List<ALinearizableNode> pathNodes) {

		List<AnchorNodeSpacing> anchorNodeSpacings = new ArrayList<AnchorNodeSpacing>();
		List<ANode> unmappedNodes = new ArrayList<ANode>();
		ALinearizableNode currentAnchorNode = null;

		for (int i = 0; i < pathNodes.size(); i++) {

			ALinearizableNode node = pathNodes.get(i);
			int numAssociatedRows = node.getMappedDavidIDs().size();

			if (numAssociatedRows == 0) {
				unmappedNodes.add(node);

			} else {
				AnchorNodeSpacing anchorNodeSpacing = createAnchorNodeSpacing(currentAnchorNode, node, unmappedNodes,
						currentAnchorNode == null, false);

				anchorNodeSpacings.add(anchorNodeSpacing);

				unmappedNodes = new ArrayList<ANode>();
				currentAnchorNode = node;
			}

			if (i == pathNodes.size() - 1) {
				AnchorNodeSpacing anchorNodeSpacing = createAnchorNodeSpacing(currentAnchorNode, null, unmappedNodes,
						currentAnchorNode == null, true);
				anchorNodeSpacings.add(anchorNodeSpacing);

			}
		}

		return anchorNodeSpacings;
	}

	private AnchorNodeSpacing createAnchorNodeSpacing(ALinearizableNode startAnchorNode,
			ALinearizableNode endAnchorNode, List<ANode> nodesInbetween, boolean isFirstSpacing, boolean isLastSpacing) {

		AnchorNodeSpacing anchorNodeSpacing = new AnchorNodeSpacing();
		anchorNodeSpacing.setStartNode(startAnchorNode);
		anchorNodeSpacing.setEndNode(endAnchorNode);
		anchorNodeSpacing.setNodesInbetween(nodesInbetween);
		anchorNodeSpacing.calcTotalNodeHeight();

		float minNodeSpacing = pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.minNodeSpacing);

		int numSpacingAnchorNodeRows = 0;

		if (startAnchorNode != null) {
			numSpacingAnchorNodeRows += startAnchorNode.getMappedDavidIDs().size();
		}
		if (endAnchorNode != null) {
			numSpacingAnchorNodeRows += endAnchorNode.getMappedDavidIDs().size();
		}

		float additionalSpacing = 0;
		if (isFirstSpacing)
			additionalSpacing += pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathStartSpacing);
		if (isLastSpacing)
			additionalSpacing += pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathEndSpacing);

		float dataRowHeight = pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.rowHeight);

		anchorNodeSpacing.setCurrentAnchorNodeSpacing(Math.max(dataRowHeight * (numSpacingAnchorNodeRows) / 2.0f
				+ additionalSpacing,
				minNodeSpacing * (nodesInbetween.size() + 1) + anchorNodeSpacing.getTotalNodeHeight()));

		return anchorNodeSpacing;
	}

	/**
	 * Selects a branch node to be linearized.
	 *
	 * @param node
	 */
	public void selectBranch(ALinearizableNode node) {

		ALinearizableNode linearizedNode = branchNodesToLinearizedNodesMap.get(node);
		BranchSummaryNode summaryNode = linearizedNodesToIncomingBranchSummaryNodesMap.get(linearizedNode);

		boolean isIncomingBranch = false;
		if (summaryNode != null && summaryNode.getBranchNodes().contains(node)) {
			isIncomingBranch = true;
		}

		// A branch node should only have one vertex rep.
		PathwayVertexRep branchVertexRep = node.getPrimaryPathwayVertexRep();
		PathwayGraph pathway = branchVertexRep.getPathway();
		PathwayVertexRep linearizedVertexRep = null;
		for (PathwayVertexRep vertexRep : linearizedNode.getVertexReps()) {
			DefaultEdge edge = null;
			if (isIncomingBranch) {
				edge = pathway.getEdge(branchVertexRep, vertexRep);
			} else {
				edge = pathway.getEdge(vertexRep, branchVertexRep);
			}
			if (edge != null) {
				linearizedVertexRep = vertexRep;
			}
		}

		Pair<Integer, Integer> indexPair = determinePathSegmentAndIndexOfPathNode(linearizedNode, linearizedVertexRep);
		int segmentIndex = indexPair.getFirst();
		int vertexRepIndex = indexPair.getSecond();

		List<PathwayVertexRep> newSegment = null;
		List<List<PathwayVertexRep>> newPathSegments = new ArrayList<>();
		List<PathwayVertexRep> branchPath = PathwayManager.get().determineDirectionalPath(branchVertexRep,
				!isIncomingBranch, MAX_BRANCH_SWITCHING_PATH_LENGTH);

		if (isIncomingBranch) {
			// insert above linearized node
			Collections.reverse(branchPath);
			newSegment = pathSegments.get(segmentIndex).subList(vertexRepIndex, pathSegments.get(segmentIndex).size());
			newSegment.addAll(0, branchPath);
			newPathSegments.add(newSegment);
			if (segmentIndex + 1 < pathSegments.size())
				newPathSegments.addAll(pathSegments.subList(segmentIndex + 1, pathSegments.size()));

		} else {
			// insert below linearized node

			newSegment = pathSegments.get(segmentIndex).subList(0, vertexRepIndex + 1);
			newSegment.addAll(branchPath);
			if (segmentIndex > 0)
				newPathSegments.addAll(pathSegments.subList(0, segmentIndex));
			newPathSegments.add(newSegment);
		}

		setPath(newPathSegments);

		broadcastPath();
	}

	/**
	 * @param currentExpandedBranchNode
	 *            setter, see {@link #expandedBranchSummaryNode}
	 */
	public void setExpandedBranchSummaryNode(BranchSummaryNode expandedBranchSummaryNode) {
		if (this.expandedBranchSummaryNode != expandedBranchSummaryNode) {
			this.expandedBranchSummaryNode = expandedBranchSummaryNode;
			PathRendererChangedEvent event = new PathRendererChangedEvent(this);
			event.setSender(this);
			GeneralManager.get().getEventPublisher().triggerEvent(event);
			updateLayout();
		}
	}

	/**
	 * @return the expandedBranchSummaryNode, see {@link #expandedBranchSummaryNode}
	 */
	public BranchSummaryNode getExpandedBranchSummaryNode() {
		return expandedBranchSummaryNode;
	}

	@Override
	public void updateLayout() {

		float branchColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.branchAreaWidth);
		float pathColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.pathAreaWidth);
		float pathwayTitleColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.pathwayTitleAreaWidth);

		float pathwayHeight = 0;
		int minViewHeightRequiredByBranchNodes = 0;

		List<AnchorNodeSpacing> anchorNodeSpacings = calcAnchorNodeSpacings(pathNodes);

		Vec3f currentPosition = new Vec3f(pathwayTitleColumnWidth + branchColumnWidth + pathColumnWidth / 2.0f, y, 0.2f);

		float minNodeSpacing = pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.minNodeSpacing);

		for (AnchorNodeSpacing spacing : anchorNodeSpacings) {

			float currentAnchorNodeSpacing = spacing.getCurrentAnchorNodeSpacing();

			float nodeSpacing = (Float.isNaN(currentAnchorNodeSpacing) ? minNodeSpacing
					: (currentAnchorNodeSpacing - spacing.getTotalNodeHeight())
							/ ((float) spacing.getNodesInbetween().size() + 1));
			ANode startAnchorNode = spacing.getStartNode();

			float currentInbetweenNodePositionY = currentPosition.y()
					- ((startAnchorNode != null) ? startAnchorNode.getHeight() / 2.0f : 0);

			int minViewHeight = calculatePositionsOfBranchNodes(startAnchorNode);
			if (minViewHeight > minViewHeightRequiredByBranchNodes) {
				minViewHeightRequiredByBranchNodes = minViewHeight;
			}

			for (int i = 0; i < spacing.getNodesInbetween().size(); i++) {
				ANode node = spacing.getNodesInbetween().get(i);

				node.setPosition(new Vec3f(currentPosition.x(), currentInbetweenNodePositionY - nodeSpacing
						- node.getHeight() / 2.0f, currentPosition.z()));
				currentInbetweenNodePositionY -= (nodeSpacing + node.getHeight());

				minViewHeight = calculatePositionsOfBranchNodes(node);
				if (minViewHeight > minViewHeightRequiredByBranchNodes) {
					minViewHeightRequiredByBranchNodes = minViewHeight;
				}
			}

			currentPosition.setY(currentPosition.y() - spacing.getCurrentAnchorNodeSpacing());

			ANode endAnchorNode = spacing.getEndNode();
			if (endAnchorNode != null) {
				endAnchorNode.setPosition(new Vec3f(currentPosition));
				minViewHeight = calculatePositionsOfBranchNodes(endAnchorNode);
				if (minViewHeight > minViewHeightRequiredByBranchNodes) {
					minViewHeightRequiredByBranchNodes = minViewHeight;
				}
			}

			pathwayHeight += spacing.getCurrentAnchorNodeSpacing();
		}

		if (expandedBranchSummaryNode != null) {
			int minViewHeight = calculateBranchNodePosition(expandedBranchSummaryNode);
			if (minViewHeight > minViewHeightRequiredByBranchNodes) {
				minViewHeightRequiredByBranchNodes = minViewHeight;
			}
		}

		minHeightPixels = Math.max(minViewHeightRequiredByBranchNodes,
				pixelGLConverter.getPixelHeightForGLHeight(pathwayHeight));
	}

	/**
	 * Calculates the position for a single branch node.
	 *
	 * @param summaryNode
	 * @return
	 */
	private int calculateBranchNodePosition(BranchSummaryNode summaryNode) {
		boolean isIncomingNode = linearizedNodesToIncomingBranchSummaryNodesMap.get(summaryNode
				.getAssociatedLinearizedNode()) == summaryNode;
		ALinearizableNode linearizedNode = summaryNode.getAssociatedLinearizedNode();
		Vec3f linearizedNodePosition = linearizedNode.getPosition();

		float sideSpacing = pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathwayTitleAreaWidth
				+ sizeConfig.branchNodeLeftSideSpacing);
		float branchSummaryNodeToLinearizedNodeDistance = pixelGLConverter
				.getGLHeightForPixelHeight(sizeConfig.branchNodeToPathNodeVerticalSpacing);
		float width = summaryNode.getWidth();
		float titleAreaHeight = pixelGLConverter.getGLHeightForPixelHeight(summaryNode.getTitleAreaHeightPixels());

		float nodePositionY = linearizedNodePosition.y()
				+ (isIncomingNode ? branchSummaryNodeToLinearizedNodeDistance
						: -branchSummaryNodeToLinearizedNodeDistance) - (summaryNode.getHeight() / 2.0f)
				+ titleAreaHeight / 2.0f;

		summaryNode.setPosition(new Vec3f(sideSpacing + width / 2.0f, nodePositionY, (summaryNode.isCollapsed() ? 0
				: 0.2f)));

		float bottomPositionY = nodePositionY - (summaryNode.getHeight() / 2.0f);
		int minViewHeightPixels = 0;
		minViewHeightPixels = pixelGLConverter.getPixelHeightForGLHeight(y - bottomPositionY);

		return minViewHeightPixels;

	}

	@Override
	protected void renderContent(GL2 gl) {

		GLU glu = new GLU();
		List<ALinearizableNode> pathNodes = getPathNodes();
		renderPathwayBorders(gl);

		for (int i = 0; i < pathNodes.size(); i++) {
			ALinearizableNode node = pathNodes.get(i);

			node.render(gl, glu);
			renderBranchNodes(gl, glu, node);
		}

		if (expandedBranchSummaryNode != null) {
			renderBranchSummaryNode(gl, glu, expandedBranchSummaryNode);
			gl.glColor4f(1, 1, 1, 0.9f);

			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0, 0, 0.1f);
			gl.glVertex3f(x, 0, 0.1f);
			gl.glVertex3f(x, y, 0.1f);
			gl.glVertex3f(0, y, 0.1f);
			gl.glEnd();
		}
		renderEdges(gl, pathNodes);
	}

	/**
	 * Renders the branch nodes for a specified linearized node. The position of this node has to be set beforehand.
	 *
	 * @param node
	 */
	private void renderBranchNodes(GL2 gl, GLU glu, ANode node) {

		ANode incomingNode = linearizedNodesToIncomingBranchSummaryNodesMap.get(node);
		if ((incomingNode != null) && (incomingNode != expandedBranchSummaryNode)) {

			renderBranchSummaryNode(gl, glu, (BranchSummaryNode) incomingNode);

			ConnectionLineRenderer connectionLineRenderer = new ConnectionLineRenderer();
			List<Vec3f> linePoints = new ArrayList<Vec3f>();
			Vec3f sourcePosition = incomingNode.getRightConnectionPoint();
			Vec3f targetPosition = node.getLeftConnectionPoint();
			sourcePosition.setZ(0);
			targetPosition.setZ(0);
			linePoints.add(sourcePosition);
			linePoints.add(targetPosition);

			ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
			arrowRenderer.setBaseWidthPixels(sizeConfig.edgeArrwoBaseLineSize);
			arrowRenderer.setHeadToBasePixels(sizeConfig.edgeArrowSize);
			LineEndArrowRenderer lineEndArrowRenderer = new LineEndArrowRenderer(false, arrowRenderer);
			connectionLineRenderer.addAttributeRenderer(lineEndArrowRenderer);

			connectionLineRenderer.renderLine(gl, linePoints);
		}

		ANode outgoingNode = linearizedNodesToOutgoingBranchSummaryNodesMap.get(node);
		if ((outgoingNode != null) && (outgoingNode != expandedBranchSummaryNode)) {

			renderBranchSummaryNode(gl, glu, (BranchSummaryNode) outgoingNode);

			ConnectionLineRenderer connectionLineRenderer = new ConnectionLineRenderer();
			List<Vec3f> linePoints = new ArrayList<Vec3f>();

			Vec3f sourcePosition = node.getLeftConnectionPoint();
			Vec3f targetPosition = outgoingNode.getRightConnectionPoint();
			sourcePosition.setZ(0);
			targetPosition.setZ(0);
			linePoints.add(sourcePosition);
			linePoints.add(targetPosition);

			ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
			arrowRenderer.setBaseWidthPixels(sizeConfig.edgeArrwoBaseLineSize);
			arrowRenderer.setHeadToBasePixels(sizeConfig.edgeArrowSize);
			LineEndArrowRenderer lineEndArrowRenderer = new LineEndArrowRenderer(false, arrowRenderer);
			connectionLineRenderer.addAttributeRenderer(lineEndArrowRenderer);

			connectionLineRenderer.renderLine(gl, linePoints);
		}
	}

	private void renderBranchSummaryNode(GL2 gl, GLU glu, BranchSummaryNode summaryNode) {

		ALinearizableNode linearizedNode = summaryNode.getAssociatedLinearizedNode();
		summaryNode.render(gl, glu);

		if (!summaryNode.isCollapsed()) {
			List<ALinearizableNode> branchNodes = summaryNode.getBranchNodes();
			for (ALinearizableNode node : branchNodes) {
				EdgeRenderUtil.renderEdge(gl, node, linearizedNode, node.getRightConnectionPoint(),
						linearizedNode.getLeftConnectionPoint(), 0.2f, false, pixelGLConverter, textRenderer,
						sizeConfig);
			}
		}

	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

	@Override
	public void setSizeConfig(PathSizeConfiguration sizeConfig) {
		super.setSizeConfig(sizeConfig);
		minWidthPixels = sizeConfig.branchAreaWidth + sizeConfig.pathAreaWidth + sizeConfig.pathwayTitleAreaWidth;
	}

}
