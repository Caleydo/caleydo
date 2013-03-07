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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.connectionline.ClosedArrowRenderer;
import org.caleydo.core.view.opengl.util.connectionline.ConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.connectionline.LineEndArrowRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANode;
import org.caleydo.view.enroute.path.node.BranchSummaryNode;

/**
 * Renders a vertical path of nodes with constant spacings. No branches are rendered.
 *
 * @author Christian Partl
 *
 */
public class VerticalPathRenderer extends APathwayPathRenderer {

	// protected static final int TOP_SPACING_PIXELS = 60;
	// protected static final int BOTTOM_SPACING_PIXELS = 60;
	// protected static final int NODE_SPACING_PIXELS = 60;
	// public final static int PATHWAY_TITLE_COLUMN_WIDTH_PIXELS = 20;
	// protected final static int PATHWAY_TITLE_TEXT_HEIGHT_PIXELS = 16;

	/**
	 * @param view
	 * @param tablePerspectives
	 */
	public VerticalPathRenderer(AGLView view, List<TablePerspective> tablePerspectives) {
		super(view, tablePerspectives);
	}

	/**
	 * @param node
	 *            The node for which the positions of associated branch nodes shall be calculated
	 * @return the minimum view height in pixels that would be required by the nodes to be displayed.
	 */
	protected int calculatePositionsOfBranchNodes(ANode node) {
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
	 * Calculates the position for a single branch node.
	 *
	 * @param summaryNode
	 * @return
	 */
	protected int calculateBranchNodePosition(BranchSummaryNode summaryNode) {
		boolean isIncomingNode = linearizedNodesToIncomingBranchSummaryNodesMap.get(summaryNode
				.getAssociatedLinearizedNode()) == summaryNode;
		ALinearizableNode linearizedNode = summaryNode.getAssociatedLinearizedNode();
		Vec3f linearizedNodePosition = linearizedNode.getPosition();

		float sideSpacing = pixelGLConverter
				.getGLHeightForPixelHeight((pathway == null ? sizeConfig.pathwayTitleAreaWidth : 0)
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
	protected void updateLayout() {

		float branchColumnWidth = pixelGLConverter
				.getGLWidthForPixelWidth(expandedBranchSummaryNode == null ? sizeConfig.collapsedBranchAreaWidth
						: sizeConfig.expandedBranchAreaWidth);
		float pathColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.pathAreaWidth);
		float pathwayTitleColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.pathwayTitleAreaWidth);

		float nodePositionX = (pathway == null ? pathwayTitleColumnWidth : 0) + branchColumnWidth + pathColumnWidth
				/ 2.0f;

		float currentPositionY = y - pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathStartSpacing);
		float nodeSpacing = pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.minNodeSpacing);

		int minViewHeightRequiredByBranchNodes = 0;
		int minPathHeight = 0;

		if (pathNodes.size() == 0) {
			minPathHeight = 0;
		} else {
			minPathHeight = sizeConfig.pathStartSpacing + sizeConfig.pathEndSpacing + (pathNodes.size() - 1)
					* sizeConfig.minNodeSpacing;
		}
		if (pixelGLConverter.getGLHeightForPixelHeight(minPathHeight) > y) {
			nodeSpacing = Math.max(
					(y - pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathStartSpacing
							+ sizeConfig.pathEndSpacing))
							/ (pathNodes.size() - 1),
					pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.rectangleNodeHeight));
		}

		for (ALinearizableNode node : pathNodes) {
			node.setPosition(new Vec3f(nodePositionX, currentPositionY, 0));
			int minViewHeight = calculatePositionsOfBranchNodes(node);
			if (minViewHeight > minViewHeightRequiredByBranchNodes) {
				minViewHeightRequiredByBranchNodes = minViewHeight;
			}
			currentPositionY -= nodeSpacing;
		}

		if (expandedBranchSummaryNode != null) {
			int minViewHeight = calculateBranchNodePosition(expandedBranchSummaryNode);
			if (minViewHeight > minViewHeightRequiredByBranchNodes) {
				minViewHeightRequiredByBranchNodes = minViewHeight;
			}
		}

		setMinHeightPixels(Math.max(minViewHeightRequiredByBranchNodes,
				pixelGLConverter.getPixelHeightForGLHeight(minPathHeight)));
		setMinWidthPixels(pixelGLConverter.getPixelWidthForGLWidth(branchColumnWidth
				+ (pathway == null ? pathwayTitleColumnWidth : 0)
				+ pathColumnWidth));

	}

	@Override
	protected void renderContent(GL2 gl) {
		GLU glu = new GLU();
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_LINE_SMOOTH);

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
	protected void renderBranchNodes(GL2 gl, GLU glu, ANode node) {

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

	protected void renderBranchSummaryNode(GL2 gl, GLU glu, BranchSummaryNode summaryNode) {

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

	protected void renderPathwayBorders(GL2 gl) {

		float topPathwayTitleLimit = y - pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathStartSpacing);
		float halfBorderWidth = pixelGLConverter
				.getGLHeightForPixelHeight((int) (sizeConfig.pathwayBorderWidth / 2.0f));
		int segmentIndex = 0;
		for (int i = 0; i < pathNodes.size(); i++) {
			ALinearizableNode node = pathNodes.get(i);

			if (node.getVertexReps().size() > 1) {
				float nodePositionY = node.getPosition().y();

				gl.glColor3f(0.95f, 0.95f, 0.95f);
				gl.glBegin(GL2.GL_QUADS);

				gl.glVertex3f(0, nodePositionY - halfBorderWidth, 0);
				gl.glVertex3f(x, nodePositionY - halfBorderWidth, 0);
				// gl.glColor3f(1f, 1f, 1f);
				gl.glVertex3f(x, nodePositionY + halfBorderWidth, 0);
				gl.glVertex3f(0, nodePositionY + halfBorderWidth, 0);
				gl.glEnd();

				gl.glColor3f(0.5f, 0.5f, 0.5f);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(x, nodePositionY + halfBorderWidth, 0);
				gl.glVertex3f(0, nodePositionY + halfBorderWidth, 0);
				gl.glVertex3f(x, nodePositionY - halfBorderWidth, 0);
				gl.glVertex3f(0, nodePositionY - halfBorderWidth, 0);
				gl.glEnd();

				renderPathwayTitle(gl, segmentIndex, topPathwayTitleLimit, nodePositionY);
				// gl.glPopMatrix();

				topPathwayTitleLimit = nodePositionY;
				segmentIndex++;
			}
			if (segmentIndex == pathSegments.size() - 1) {
				renderPathwayTitle(gl, segmentIndex, topPathwayTitleLimit,
						y - pixelGLConverter.getGLHeightForPixelHeight(getMinHeightPixels()));
				segmentIndex++;
			}
		}
	}

	private void renderPathwayTitle(GL2 gl, int pathSegmentIndex, float topPathwayTitleLimit,
			float bottomPathwayTitleLimit) {
		if (pathway != null)
			return;
		float pathwayTitlePositionX = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.pathwayTitleAreaWidth
				- (int) ((sizeConfig.pathwayTitleAreaWidth - sizeConfig.pathwayTextHeight) / 2.0f));
		float pathwayTitleTextHeight = pixelGLConverter.getGLHeightForPixelHeight(sizeConfig.pathwayTextHeight);
		String text = pathSegments.get(pathSegmentIndex).get(0).getPathway().getTitle();
		float maxTextWidth = topPathwayTitleLimit - bottomPathwayTitleLimit;
		float requiredTextWidth = textRenderer.getRequiredTextWidthWithMax(text, pathwayTitleTextHeight, maxTextWidth);
		gl.glPushMatrix();
		gl.glTranslatef(pathwayTitlePositionX, bottomPathwayTitleLimit + (maxTextWidth - requiredTextWidth) / 2.0f, 0);
		gl.glRotatef(90, 0, 0, 1);
		// gl.glColor3f(0, 0, 0);
		// textRenderer.setColor(0, 0, 0, 1);
		textRenderer.renderTextInBounds(gl, text, 0, 0, 0, maxTextWidth, pathwayTitleTextHeight);
		gl.glPopMatrix();
	}

	protected void renderEdges(GL2 gl, List<ALinearizableNode> pathNodes) {
		for (int i = 0; i < pathNodes.size() - 1; i++) {
			ALinearizableNode node1 = pathNodes.get(i);
			ALinearizableNode node2 = pathNodes.get(i + 1);
			EdgeRenderUtil.renderEdge(gl, node1, node2, node1.getBottomConnectionPoint(),
					node2.getTopConnectionPoint(), 0.2f, true, pixelGLConverter, textRenderer, sizeConfig);
		}
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

	@Override
	public void setSizeConfig(PathSizeConfiguration sizeConfig) {
		super.setSizeConfig(sizeConfig);
		setMinWidthPixels((expandedBranchSummaryNode == null ? this.sizeConfig.collapsedBranchAreaWidth
				: this.sizeConfig.expandedBranchAreaWidth)
				+ this.sizeConfig.pathAreaWidth
				+ (pathway == null ? this.sizeConfig.pathwayTitleAreaWidth : 0));
	}

}
