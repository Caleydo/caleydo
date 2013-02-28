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

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ANode;

/**
 * Renders a simple vertical path of pathway nodes.
 *
 * @author Christian Partl
 *
 */
public class EnRoutePathRenderer extends VerticalPathRenderer {

	public EnRoutePathRenderer(AGLView view, List<TablePerspective> tablePerspectives) {
		super(view, tablePerspectives);
		setSizeConfig(PathSizeConfiguration.ENROUTE_DEFAULT);
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

	@Override
	public void updateLayout() {

		float branchColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.branchAreaWidth);
		float pathColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.pathAreaWidth);
		float pathwayTitleColumnWidth = pixelGLConverter.getGLWidthForPixelWidth(sizeConfig.pathwayTitleAreaWidth);

		float pathwayHeight = 0;
		int minViewHeightRequiredByBranchNodes = 0;

		List<AnchorNodeSpacing> anchorNodeSpacings = calcAnchorNodeSpacings(pathNodes);

		Vec3f currentPosition = new Vec3f((pathway == null ? pathwayTitleColumnWidth : 0) + branchColumnWidth
				+ pathColumnWidth / 2.0f, y, 0.2f);

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

	// @Override
	// protected void renderContent(GL2 gl) {
	//
	// GLU glu = new GLU();
	// List<ALinearizableNode> pathNodes = getPathNodes();
	// renderPathwayBorders(gl);
	//
	// for (int i = 0; i < pathNodes.size(); i++) {
	// ALinearizableNode node = pathNodes.get(i);
	//
	// node.render(gl, glu);
	// // renderBranchNodes(gl, glu, node);
	// }
	//
	// renderEdges(gl, pathNodes);
	// }

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}

}
