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
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.connectionline.ClosedArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineCrossingRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndStaticLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineLabelRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayReactionEdgeType;
import org.caleydo.datadomain.pathway.graph.item.edge.EPathwayRelationEdgeSubType;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayReactionEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.edge.PathwayRelationEdgeRep;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANode;
import org.caleydo.view.enroute.path.node.BranchSummaryNode;
import org.jgrapht.graph.DefaultEdge;

/**
 * Renders a simple vertical path of pathway nodes.
 *
 * @author Christian Partl
 *
 */
public class VerticalPathStrategy implements IPathwayPathRenderingStrategy {

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
	protected final static int DEFAULT_DATA_ROW_HEIGHT_PIXELS = 60;
	protected final static int BRANCH_SUMMARY_NODE_TO_LINEARIZED_NODE_VERTICAL_DISTANCE_PIXELS = 20;
	protected final static int BRANCH_AREA_SIDE_SPACING_PIXELS = 8;
	protected final static int BRANCH_COLUMN_WIDTH_PIXELS = 100;
	protected final static int PATHWAY_COLUMN_WIDTH_PIXELS = 150;

	/**
	 * PathwayPathRenderer that uses this strategy.
	 */
	protected final PathwayPathRenderer pathwayPathRenderer;
	protected PixelGLConverter pixelGLConverter;
	protected CaleydoTextRenderer textRenderer;

	protected boolean isEnRoutePath = true;

	public VerticalPathStrategy(PathwayPathRenderer pathwayPathRenderer) {
		this.pathwayPathRenderer = pathwayPathRenderer;
		this.pixelGLConverter = pathwayPathRenderer.getView().getPixelGLConverter();
		this.textRenderer = pathwayPathRenderer.getView().getTextRenderer();
	}

	/**
	 * @param node
	 *            The node for which the positions of associated branch nodes shall be calculated
	 * @return the minimum view height in pixels that would be required by the nodes to be displayed.
	 */
	private int calculatePositionsOfBranchNodes(ANode node) {
		int minViewHeightPixelsIncoming = 0;
		int minViewHeightPixelsOutgoing = 0;
		ANode incomingNode = pathwayPathRenderer.linearizedNodesToIncomingBranchSummaryNodesMap.get(node);
		if ((incomingNode != null) && (incomingNode != pathwayPathRenderer.expandedBranchSummaryNode)) {
			minViewHeightPixelsIncoming = calculateBranchNodePosition((BranchSummaryNode) incomingNode);
		}

		ANode outgoingNode = pathwayPathRenderer.linearizedNodesToOutgoingBranchSummaryNodesMap.get(node);
		if ((outgoingNode != null) && (outgoingNode != pathwayPathRenderer.expandedBranchSummaryNode)) {
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
		if (isEnRoutePath) {
			if (startAnchorNode != null) {
				numSpacingAnchorNodeRows += startAnchorNode.getMappedDavidIDs().size();
			}
			if (endAnchorNode != null) {
				numSpacingAnchorNodeRows += endAnchorNode.getMappedDavidIDs().size();
			}
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

	public void updateLayout() {

		float branchColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(BRANCH_COLUMN_WIDTH_PIXELS);
		float pathwayColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS);

		float pathwayHeight = 0;
		int minViewHeightRequiredByBranchNodes = 0;
		List<ALinearizableNode> pathNodes = pathwayPathRenderer.getPathNodes();

		List<AnchorNodeSpacing> anchorNodeSpacings = calcAnchorNodeSpacings(pathNodes);

		Vec3f currentPosition = new Vec3f((isEnRoutePath) ? (branchColumnWidth + pathwayColumnWidth / 2.0f)
				: (pathwayPathRenderer.getX() / 2.0f), pathwayPathRenderer.getY(), 0.2f);

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
	}

	/**
	 * Calculates the position for a single branch node.
	 *
	 * @param summaryNode
	 * @return
	 */
	private int calculateBranchNodePosition(BranchSummaryNode summaryNode) {
		boolean isIncomingNode = pathwayPathRenderer.linearizedNodesToIncomingBranchSummaryNodesMap.get(summaryNode
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
		minViewHeightPixels = pixelGLConverter.getPixelHeightForGLHeight(pathwayPathRenderer.getY() - bottomPositionY);
		// setMinSize(minViewHeightPixels + 3);
		// }

		return minViewHeightPixels;

	}

	@Override
	public void render(GL2 gl, GLU glu) {

		List<ALinearizableNode> pathNodes = pathwayPathRenderer.getPathNodes();

		updateLayout();

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

		if (pathwayPathRenderer.expandedBranchSummaryNode != null) {
			renderBranchSummaryNode(gl, glu, pathwayPathRenderer.expandedBranchSummaryNode);
			// float coverWidth = pixelGLConverter.getGLWidthForPixelWidth(PATHWAY_COLUMN_WIDTH_PIXELS
			// + BRANCH_COLUMN_WIDTH_PIXELS);
			gl.glColor4f(1, 1, 1, 0.9f);

			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0, 0, 0.1f);
			gl.glVertex3f(pathwayPathRenderer.getX(), 0, 0.1f);
			gl.glVertex3f(pathwayPathRenderer.getX(), pathwayPathRenderer.getY(), 0.1f);
			gl.glVertex3f(0, pathwayPathRenderer.getY(), 0.1f);
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

		ANode incomingNode = pathwayPathRenderer.linearizedNodesToIncomingBranchSummaryNodesMap.get(node);
		if ((incomingNode != null) && (incomingNode != pathwayPathRenderer.expandedBranchSummaryNode)) {

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

		ANode outgoingNode = pathwayPathRenderer.linearizedNodesToOutgoingBranchSummaryNodesMap.get(node);
		if ((outgoingNode != null) && (outgoingNode != pathwayPathRenderer.expandedBranchSummaryNode)) {

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

		PathwayGraph pathway = pathwayPathRenderer.getPathway();

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

	/**
	 * @return the isEnRoutePath, see {@link #isEnRoutePath}
	 */
	public boolean isEnRoutePath() {
		return isEnRoutePath;
	}

	/**
	 * @param isEnRoutePath
	 *            setter, see {@link isEnRoutePath}
	 */
	public void setEnRoutePath(boolean isEnRoutePath) {
		this.isEnRoutePath = isEnRoutePath;
	}

}
