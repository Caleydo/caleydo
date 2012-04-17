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
package org.caleydo.view.linearizedpathway.mappeddataview;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.node.ANode;

/**
 * @author Alexander Lex
 * 
 */
public class MappedDataRenderer {

	GLLinearizedPathway parentView;

	private List<ANode> linearizedNodes;

	private ArrayList<RelationshipRenderer> relationShipRenderers;

	/**
	 * the distance from the left edge of this renderer to the left edge of the
	 * window
	 */
	private float xOffset = 0;

	/**
	 * The distance from the upper edge of this renderer to the upper edge of
	 * the window
	 */
	private float yOffset = 0;

	private float rowHeight;

	private LayoutManager layoutManger;
	private ViewFrustum viewFrustum;

	private ConnectionBandRenderer connectionBandRenderer;

	private float[] oddColor = { 43f / 255f, 140f / 255, 190f / 255, 1f };
	private float[] evenColor = { 166f / 255f, 189f / 255, 219f / 255, 1f };

	/**
	 * 
	 */
	public MappedDataRenderer(GLLinearizedPathway parentView) {
		this.parentView = parentView;
		viewFrustum = new ViewFrustum();
		layoutManger = new LayoutManager(viewFrustum, parentView.getPixelGLConverter());
	}

	public void init(GL2 gl) {
		connectionBandRenderer = new ConnectionBandRenderer();
		connectionBandRenderer.init(gl);
	}

	public void render(GL2 gl) {
		layoutManger.updateLayout();
		layoutManger.render(gl);

		for (RelationshipRenderer relationshipRenderer : relationShipRenderers) {
			relationshipRenderer.render(gl, connectionBandRenderer);
		}

	}

	public void setFrustum(float width, float height, float rowHeight, float xOffset,
			float yOffset) {
		viewFrustum.setRight(width);
		viewFrustum.setTop(height);
		this.rowHeight = rowHeight;
		layoutManger.updateLayout();
		this.xOffset = xOffset;
		this.yOffset = yOffset;

	}

	/**
	 * @param linearizedNodes
	 *            setter, see {@link #linearizedNodes}
	 */
	public void setLinearizedNodes(List<ANode> linearizedNodes) {

		float[] color;
		relationShipRenderers = new ArrayList<RelationshipRenderer>(
				linearizedNodes.size());

		Row baseRow = new Row("baseRow");
		layoutManger.setBaseElementLayout(baseRow);
		// baseRow.setDebug(true);

		Column dataSetColumn = new Column("dataSetColumn");
		dataSetColumn.setBottomUp(false);
		baseRow.append(dataSetColumn);
		Column captionColumn = new Column("captionColumn");
		captionColumn.setBottomUp(false);

		captionColumn.setPixelSizeX(100);
		baseRow.append(captionColumn);

		// dataSetColumn.setDebug(true);

		this.linearizedNodes = linearizedNodes;

		int nodeCount = 0;
		float previousNodePosition = -1;
		int previousNrDavids = 1;
		for (ANode node : linearizedNodes) {

			if (node.getNumAssociatedRows() == 0)
				continue;

			if (nodeCount % 2 == 0)
				color = evenColor;
			else
				color = oddColor;

			RelationshipRenderer relationShipRenderer = new RelationshipRenderer(color);
			relationShipRenderers.add(relationShipRenderer);
			float x = node.getPosition().x()
					+ parentView.getPixelGLConverter().getGLWidthForPixelWidth(
							node.getWidthPixels()) / 2;
			float height = parentView.getPixelGLConverter().getGLHeightForPixelHeight(
					node.getHeightPixels());
			// float y =
			relationShipRenderer.topLeft[0] = x - xOffset;
			relationShipRenderer.topLeft[1] = node.getPosition().y() + height / 2
					- yOffset;

			relationShipRenderer.bottomLeft[0] = x - xOffset;
			relationShipRenderer.bottomLeft[1] = node.getPosition().y() - height / 2
					- yOffset;

			nodeCount++;

			ArrayList<Integer> davidIDs = node.getPathwayVertexRep().getDavidIDs();

			float currentNodePositionY = node.getPosition().y();
			float deviation;

			if (previousNodePosition < 0) {
				deviation = 0;
			} else {
				deviation = previousNodePosition - rowHeight * previousNrDavids / 2
						- currentNodePositionY - rowHeight * davidIDs.size() / 2;
				// - (currentNodePositionY - rowHeight * davidIDs.size());
			}

			if (previousNodePosition > 0 && deviation > 0) {
				Row spacing = new Row();
				spacing.setAbsoluteSizeY(deviation);
				dataSetColumn.append(spacing);
				captionColumn.append(spacing);
			}
			previousNodePosition = currentNodePositionY;
			previousNrDavids = davidIDs.size();

			int idCount = 0;
			for (Integer davidID : davidIDs) {

				Vec3f nodePosition = node.getPosition();

				Row row = new Row();
				RowBackgroundRenderer rowBackgroundRenderer = new RowBackgroundRenderer();
				rowBackgroundRenderer.setColor(color);
				row.addBackgroundRenderer(rowBackgroundRenderer);
				// row.setPixelSizeY(10);
				row.setAbsoluteSizeY(rowHeight);
				dataSetColumn.append(row);

				Row captionRow = new Row();
				captionRow.setAbsoluteSizeY(rowHeight);
				CaptionRenderer captionRenderer = new CaptionRenderer(
						parentView.getTextRenderer(), parentView.getPixelGLConverter(),
						davidID);
				captionRow.setRenderer(captionRenderer);
				captionColumn.append(captionRow);

				// row.setDebug(true);
				// float rowPositionY = nodePosition.y()
				// + (node.getNumAssociatedRows() * dataRowHeight / 2.0f) -
				// idCount
				// * dataRowHeight;

				if (idCount == 0)
					relationShipRenderer.topRightLayout = row;
				if (idCount == davidIDs.size() - 1)
					relationShipRenderer.bottomRightLayout = row;

				idCount++;
			}
		}

	}
}
