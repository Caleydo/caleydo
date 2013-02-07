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
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.connectionline.ClosedArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineCrossingRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndStaticLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineLabelRenderer;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayReactionEdgeType;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayRelationEdgeSubType;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
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
public class EnRoutePathRenderer extends APathwayPathRenderer {

	/**
	 * Default size for vertical node space, i.e., the space reserved for a single node.
	 */
	protected static final int DEFAULT_VERTICAL_NODE_SPACING_PIXELS = 50;
	/**
	 * Spacing before the first node.
	 */
	protected final static int TOP_SPACING_PIXELS = 60;
	/**
	 * Spacing after the last node.
	 */
	protected final static int BOTTOM_SPACING_PIXELS = 60;
	public final static int DEFAULT_DATA_ROW_HEIGHT_PIXELS = 60;
	protected final static int BRANCH_SUMMARY_NODE_TO_LINEARIZED_NODE_VERTICAL_DISTANCE_PIXELS = 20;
	protected final static int BRANCH_AREA_SIDE_SPACING_PIXELS = 8;
	public final static int BRANCH_COLUMN_WIDTH_PIXELS = 100;
	public final static int PATH_COLUMN_WIDTH_PIXELS = 150;

	protected static final int MAX_BRANCH_SWITCHING_PATH_LENGTH = 5;

	/**
	 * Branch summary node that is currently expanded
	 */
	protected BranchSummaryNode expandedBranchSummaryNode;

	/**
	 * List of nodes that can currently be displayed in branches.
	 */
	protected List<ANode> branchNodes = new ArrayList<ANode>();

	/**
	 * Map that associates the linearized nodes with their incoming branch summary nodes.
	 */
	protected Map<ANode, ANode> linearizedNodesToIncomingBranchSummaryNodesMap = new HashMap<ANode, ANode>();

	/**
	 * Map that associates the linearized nodes with their outgoing branch summary nodes.
	 */
	protected Map<ANode, ANode> linearizedNodesToOutgoingBranchSummaryNodesMap = new HashMap<ANode, ANode>();

	/**
	 * Map that associates every node in a branch with a linearized node.
	 */
	protected Map<ANode, ALinearizableNode> branchNodesToLinearizedNodesMap = new HashMap<ANode, ALinearizableNode>();

	public EnRoutePathRenderer(AGLView view, List<TablePerspective> tablePerspectives) {
		super(view, tablePerspectives);
		minWidthPixels = BRANCH_COLUMN_WIDTH_PIXELS + PATH_COLUMN_WIDTH_PIXELS;
	}

	@Override
	protected void createNodes(List<PathwayVertexRep> path) {
		super.createNodes(path);
		expandedBranchSummaryNode = null;

		for (ANode node : branchNodes) {
			node.destroy();
		}

		branchNodes.clear();
		branchNodesToLinearizedNodesMap.clear();
		linearizedNodesToIncomingBranchSummaryNodesMap.clear();
		linearizedNodesToOutgoingBranchSummaryNodesMap.clear();

		// Create branch nodes
		for (int i = 0; i < pathNodes.size(); i++) {
			ALinearizableNode currentNode = pathNodes.get(i);
			PathwayVertexRep currentVertexRep = currentNode.getPathwayVertexRep();
			PathwayVertexRep prevVertexRep = null;
			PathwayVertexRep nextVertexRep = null;

			if (i > 0) {
				ALinearizableNode prevNode = pathNodes.get(i - 1);
				prevVertexRep = prevNode.getPathwayVertexRep();
			}
			if (i != pathNodes.size() - 1) {
				ALinearizableNode nextNode = pathNodes.get(i + 1);
				nextVertexRep = nextNode.getPathwayVertexRep();
			}

			BranchSummaryNode incomingNode = new BranchSummaryNode(view, lastNodeID++, currentNode, this);
			incomingNode.init();
			BranchSummaryNode outgoingNode = new BranchSummaryNode(view, lastNodeID++, currentNode, this);
			outgoingNode.init();
			List<PathwayVertexRep> sourceVertexReps = Graphs.predecessorListOf(pathway, currentVertexRep);
			sourceVertexReps.remove(prevVertexRep);
			List<PathwayVertexRep> targetVertexReps = Graphs.successorListOf(pathway, currentVertexRep);
			targetVertexReps.remove(nextVertexRep);

			if (sourceVertexReps.size() > 0) {
				List<ALinearizableNode> sourceNodes = new ArrayList<ALinearizableNode>();

				createNodesForList(sourceNodes, sourceVertexReps);
				incomingNode.setBranchNodes(sourceNodes);
				linearizedNodesToIncomingBranchSummaryNodesMap.put(currentNode, incomingNode);
				branchNodes.add(incomingNode);
				branchNodes.addAll(sourceNodes);
				for (ANode node : sourceNodes) {
					setPreviewMode((ALinearizableNode) node);
					branchNodesToLinearizedNodesMap.put(node, currentNode);
				}
			}

			if (targetVertexReps.size() > 0) {
				List<ALinearizableNode> targetNodes = new ArrayList<ALinearizableNode>();
				createNodesForList(targetNodes, targetVertexReps);

				outgoingNode.setBranchNodes(targetNodes);
				linearizedNodesToOutgoingBranchSummaryNodesMap.put(currentNode, outgoingNode);
				branchNodes.add(outgoingNode);
				branchNodes.addAll(targetNodes);
				for (ANode node : targetNodes) {
					setPreviewMode((ALinearizableNode) node);
					branchNodesToLinearizedNodesMap.put(node, currentNode);
				}
			}

		}

	}

	private void setPreviewMode(ALinearizableNode node) {
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

		float minNodeSpacing = pixelGLConverter.getGLHeightForPixelHeight(DEFAULT_VERTICAL_NODE_SPACING_PIXELS);

		int numSpacingAnchorNodeRows = 0;

		if (startAnchorNode != null) {
			numSpacingAnchorNodeRows += startAnchorNode.getMappedDavidIDs().size();
		}
		if (endAnchorNode != null) {
			numSpacingAnchorNodeRows += endAnchorNode.getMappedDavidIDs().size();
		}

		float additionalSpacing = 0;
		if (isFirstSpacing)
			additionalSpacing += pixelGLConverter.getGLHeightForPixelHeight(TOP_SPACING_PIXELS);
		if (isLastSpacing)
			additionalSpacing += pixelGLConverter.getGLHeightForPixelHeight(BOTTOM_SPACING_PIXELS);

		float dataRowHeight = pixelGLConverter.getGLHeightForPixelHeight(DEFAULT_DATA_ROW_HEIGHT_PIXELS);

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
		BranchSummaryNode summaryNode = (BranchSummaryNode) linearizedNodesToIncomingBranchSummaryNodesMap
				.get(linearizedNode);

		boolean isIncomingBranch = false;
		if (summaryNode != null && summaryNode.getBranchNodes().contains(node)) {
			isIncomingBranch = true;
		}

		PathwayVertexRep linearizedVertexRep = linearizedNode.getPathwayVertexRep();
		PathwayVertexRep branchVertexRep = node.getPathwayVertexRep();

		DefaultEdge edge = pathway.getEdge(linearizedVertexRep, branchVertexRep);
		if (edge == null) {
			edge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		}

		int linearizedNodeIndex = pathNodes.indexOf(linearizedNode);
		List<PathwayVertexRep> newPath = null;
		List<PathwayVertexRep> branchPath = determineDefiniteUniDirectionalBranchPath(branchVertexRep,
				linearizedVertexRep, isIncomingBranch);

		if (isIncomingBranch) {
			// insert above linearized node
			Collections.reverse(branchPath);
			newPath = path.subList(linearizedNodeIndex, path.size());

			newPath.addAll(0, branchPath);

		} else {
			// insert below linearized node
			newPath = path.subList(0, linearizedNodeIndex + 1);
			newPath.addAll(branchPath);
		}

		setPath(pathway, newPath);

		broadcastPath();
	}

	/**
	 * Calculates a branch path consisting of {@link PathwayVertexRep} objects for a specified branch node. This path
	 * ends if there is no unambiguous way to continue, the direction of edges changes, the pathway ends, or the
	 * {@link #maxBranchSwitchingPathLength} is reached. The specified <code>PathwayVertexRep</code> that represents the
	 * start of the path is added at the beginning of the path.
	 *
	 * @param branchVertexRep
	 *            The <code>PathwayVertexRep</code> that represents the start of the branch path.
	 * @param linearizedVertexRep
	 *            The <code>PathwayVertexRep</code> of the linearized path this branch belongs to.
	 * @param isIncomingBranchPath
	 *            Determines whether the branch path is incoming or outgoing. This is especially important for
	 *            bidirectional edges.
	 * @return
	 */
	private List<PathwayVertexRep> determineDefiniteUniDirectionalBranchPath(PathwayVertexRep branchVertexRep,
			PathwayVertexRep linearizedVertexRep, boolean isIncomingBranchPath) {

		List<PathwayVertexRep> vertexReps = new ArrayList<PathwayVertexRep>();
		vertexReps.add(branchVertexRep);
		DefaultEdge existingEdge = pathway.getEdge(branchVertexRep, linearizedVertexRep);
		if (existingEdge == null)
			existingEdge = pathway.getEdge(linearizedVertexRep, branchVertexRep);

		PathwayVertexRep currentVertexRep = branchVertexRep;

		for (int i = 0; i < MAX_BRANCH_SWITCHING_PATH_LENGTH; i++) {
			List<PathwayVertexRep> nextVertices = null;
			if (isIncomingBranchPath) {
				nextVertices = Graphs.predecessorListOf(pathway, currentVertexRep);
			} else {
				nextVertices = Graphs.successorListOf(pathway, currentVertexRep);
			}

			if (nextVertices.size() == 0 || nextVertices.size() > 1) {
				return vertexReps;
			} else {
				currentVertexRep = nextVertices.get(0);
				vertexReps.add(currentVertexRep);
			}

		}

		return vertexReps;
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

		float branchColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS);
		float pathColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(PATH_COLUMN_WIDTH_PIXELS);

		float pathwayHeight = 0;
		int minViewHeightRequiredByBranchNodes = 0;

		List<AnchorNodeSpacing> anchorNodeSpacings = calcAnchorNodeSpacings(pathNodes);

		Vec3f currentPosition = new Vec3f(branchColumnWidth + pathColumnWidth / 2.0f, y, 0.2f);

		float minNodeSpacing = pixelGLConverter.getGLHeightForPixelHeight(DEFAULT_VERTICAL_NODE_SPACING_PIXELS);

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

		float sideSpacing = pixelGLConverter.getGLHeightForPixelHeight(BRANCH_AREA_SIDE_SPACING_PIXELS);
		float branchSummaryNodeToLinearizedNodeDistance = pixelGLConverter
				.getGLHeightForPixelHeight(BRANCH_SUMMARY_NODE_TO_LINEARIZED_NODE_VERTICAL_DISTANCE_PIXELS);
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
		// if (viewFrustum.getBottom() > bottomPositionY) {
		minViewHeightPixels = pixelGLConverter.getPixelHeightForGLHeight(y - bottomPositionY);
		// setMinSize(minViewHeightPixels + 3);
		// }

		return minViewHeightPixels;

	}

	@Override
	protected void renderContent(GL2 gl) {

		GLU glu = new GLU();
		List<ALinearizableNode> pathNodes = getPathNodes();

		// List<Float> nodeSpaces = calcNodeSpace(pathNodes, view.getPixelGLConverter());
		//
		// // gl.glPushMatrix();
		// // gl.glLoadIdentity();
		// // gl.glTranslatef(pathwayPathRenderer.getY(), 0, 0);
		// // gl.glRotatef(90f, 0, 0, 1);
		// // gl.glColor3f(1, 0, 0);
		// // gl.glBegin(GL2.GL_QUADS);
		// // gl.glVertex3f(0, 0, 0);
		// // gl.glVertex3f(pathwayPathRenderer.getX(), 0, 0);
		// // gl.glVertex3f(pathwayPathRenderer.getX(), pathwayPathRenderer.getY(), 0);
		// // gl.glVertex3f(0, pathwayPathRenderer.getY(), 0);
		// // gl.glEnd();
		//
		// float currentPositionY = pathwayPathRenderer.getY();
		for (int i = 0; i < pathNodes.size(); i++) {
			ALinearizableNode node = pathNodes.get(i);
			node.render(gl, glu);
			renderBranchNodes(gl, glu, node);
		}

		if (expandedBranchSummaryNode != null) {
			renderBranchSummaryNode(gl, glu, expandedBranchSummaryNode);
			// float coverWidth = pixelGLConverter.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS
			// + BRANCH_COLUMN_WIDTH_PIXELS);
			gl.glColor4f(1, 1, 1, 0.9f);

			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0, 0, 0.1f);
			gl.glVertex3f(x, 0, 0.1f);
			gl.glVertex3f(x, y, 0.1f);
			gl.glVertex3f(0, y, 0.1f);
			gl.glEnd();
		}
		// currentPositionY -= nodeSpaces.get(i);
		// }

		renderEdges(gl, pathNodes);
		// gl.glPopMatrix();
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

			LineEndArrowRenderer lineEndArrowRenderer = createDefaultLineEndArrowRenderer();
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

			LineEndArrowRenderer lineEndArrowRenderer = createDefaultLineEndArrowRenderer();
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
				renderEdge(gl, node, linearizedNode, node.getRightConnectionPoint(),
						linearizedNode.getLeftConnectionPoint(), 0.2f, false);
			}
		}

	}

	private void renderEdges(GL2 gl, List<ALinearizableNode> pathNodes) {
		for (int i = 0; i < pathNodes.size() - 1; i++) {
			ALinearizableNode node1 = pathNodes.get(i);
			ALinearizableNode node2 = pathNodes.get(i + 1);
			renderEdge(gl, node1, node2, node1.getBottomConnectionPoint(), node2.getTopConnectionPoint(), 0.2f, true);
		}
	}

	private void renderEdge(GL2 gl, ALinearizableNode node1, ALinearizableNode node2, Vec3f node1ConnectionPoint,
			Vec3f node2ConnectionPoint, float zCoordinate, boolean isVerticalConnection) {

		PathwayVertexRep vertexRep1 = node1.getPathwayVertexRep();
		PathwayVertexRep vertexRep2 = node2.getPathwayVertexRep();

		DefaultEdge edge = pathway.getEdge(vertexRep1, vertexRep2);
		if (edge == null) {
			edge = pathway.getEdge(vertexRep2, vertexRep1);
			if (edge == null)
				return;
		}

		ConnectionLineRenderer connectionRenderer = new ConnectionLineRenderer();
		List<Vec3f> linePoints = new ArrayList<Vec3f>();

		boolean isNode1Target = pathway.getEdgeTarget(edge) == vertexRep1;

		Vec3f sourceConnectionPoint = (isNode1Target) ? node2ConnectionPoint : node1ConnectionPoint;
		Vec3f targetConnectionPoint = (isNode1Target) ? node1ConnectionPoint : node2ConnectionPoint;

		sourceConnectionPoint.setZ(zCoordinate);
		targetConnectionPoint.setZ(zCoordinate);

		linePoints.add(sourceConnectionPoint);
		linePoints.add(targetConnectionPoint);

		if (edge instanceof PathwayReactionEdgeRep) {
			// TODO: This is just a default edge. Is this right?
			PathwayReactionEdgeRep reactionEdge = (PathwayReactionEdgeRep) edge;

			ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
			LineEndArrowRenderer lineEndArrowRenderer = new LineEndArrowRenderer(false, arrowRenderer);

			connectionRenderer.addAttributeRenderer(lineEndArrowRenderer);

			if (reactionEdge.getType() == EPathwayReactionEdgeType.reversible) {
				arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
				lineEndArrowRenderer = new LineEndArrowRenderer(true, arrowRenderer);
				connectionRenderer.addAttributeRenderer(lineEndArrowRenderer);
			}

		} else {
			if (edge instanceof PathwayRelationEdgeRep) {
				PathwayRelationEdgeRep relationEdgeRep = (PathwayRelationEdgeRep) edge;

				ArrayList<EPathwayRelationEdgeSubType> subtypes = relationEdgeRep.getRelationSubTypes();
				float spacing = pixelGLConverter.getGLHeightForPixelHeight(3);

				for (EPathwayRelationEdgeSubType subtype : subtypes) {
					switch (subtype) {
					case compound:
						// TODO:
						break;
					case hidden_compound:
						// TODO:
						break;
					case activation:
						connectionRenderer.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						break;
					case inhibition:
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndStaticLineRenderer(isVerticalConnection));
						if (isVerticalConnection) {
							targetConnectionPoint.setY(targetConnectionPoint.y()
									+ ((isNode1Target) ? -spacing : spacing));
						} else {
							targetConnectionPoint.setX(targetConnectionPoint.x()
									+ ((isNode1Target) ? spacing : -spacing));
						}
						break;
					case expression:
						connectionRenderer.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						if (vertexRep1.getType() == EPathwayVertexType.gene
								&& vertexRep1.getType() == EPathwayVertexType.gene) {
							connectionRenderer.addAttributeRenderer(createDefaultLabelOnLineRenderer("e"));
						}
						break;
					case repression:
						connectionRenderer.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						connectionRenderer
								.addAttributeRenderer(createDefaultLineEndStaticLineRenderer(isVerticalConnection));
						targetConnectionPoint.setY(targetConnectionPoint.y() + ((isNode1Target) ? -spacing : spacing));
						break;
					case indirect_effect:
						connectionRenderer.addAttributeRenderer(createDefaultLineEndArrowRenderer());
						connectionRenderer.setLineStippled(true);
						break;
					case state_change:
						connectionRenderer.setLineStippled(true);
						break;
					case binding_association:
						// Nothing to do
						break;
					case dissociation:
						connectionRenderer.addAttributeRenderer(createDefaultOrthogonalLineCrossingRenderer());
						break;
					case missing_interaction:
						connectionRenderer.addAttributeRenderer(createDefaultLineCrossingRenderer());
						break;
					case phosphorylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.phosphorylation
										.getSymbol()));
						break;
					case dephosphorylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.dephosphorylation
										.getSymbol()));
						break;
					case glycosylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.glycosylation
										.getSymbol()));
						break;
					case ubiquitination:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.ubiquitination
										.getSymbol()));
						break;
					case methylation:
						connectionRenderer
								.addAttributeRenderer(createDefaultLabelAboveLineRenderer(EPathwayRelationEdgeSubType.methylation
										.getSymbol()));
						break;
					}
				}
			}
		}

		connectionRenderer.renderLine(gl, linePoints);
	}

	private LineEndArrowRenderer createDefaultLineEndArrowRenderer() {
		ClosedArrowRenderer arrowRenderer = new ClosedArrowRenderer(pixelGLConverter);
		return new LineEndArrowRenderer(false, arrowRenderer);
	}

	private LineEndStaticLineRenderer createDefaultLineEndStaticLineRenderer(boolean isHorizontalLine) {
		LineEndStaticLineRenderer lineEndRenderer = new LineEndStaticLineRenderer(false, pixelGLConverter);
		lineEndRenderer.setHorizontalLine(isHorizontalLine);
		return lineEndRenderer;
	}

	private LineLabelRenderer createDefaultLabelOnLineRenderer(String text) {
		LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(0.5f, pixelGLConverter, text, textRenderer);
		lineLabelRenderer.setXCentered(true);
		lineLabelRenderer.setYCentered(true);
		lineLabelRenderer.setLineOffsetPixels(0);
		return lineLabelRenderer;
	}

	private LineLabelRenderer createDefaultLabelAboveLineRenderer(String text) {
		LineLabelRenderer lineLabelRenderer = new LineLabelRenderer(0.66f, pixelGLConverter, text, textRenderer);
		lineLabelRenderer.setLineOffsetPixels(5);
		return lineLabelRenderer;
	}

	private LineCrossingRenderer createDefaultOrthogonalLineCrossingRenderer() {
		LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(0.5f, pixelGLConverter);
		lineCrossingRenderer.setCrossingAngle(90);
		return lineCrossingRenderer;
	}

	private LineCrossingRenderer createDefaultLineCrossingRenderer() {
		LineCrossingRenderer lineCrossingRenderer = new LineCrossingRenderer(0.5f, pixelGLConverter);
		lineCrossingRenderer.setCrossingAngle(45);
		return lineCrossingRenderer;
	}

	@Override
	public int getMinHeightPixels() {
		return 0;
	}

	@Override
	public int getMinWidthPixels() {
		return 0;
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
