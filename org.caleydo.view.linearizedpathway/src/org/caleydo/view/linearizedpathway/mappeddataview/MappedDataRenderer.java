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

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.node.ANode;

/**
 * @author Alexander Lex
 * 
 */
public class MappedDataRenderer {

	GLLinearizedPathway parentView;

	private List<ANode> linearizedNodes;

	private float rowHeight;

	private LayoutManager layoutManger;
	private ViewFrustum viewFrustum;

	/**
	 * 
	 */
	public MappedDataRenderer(GLLinearizedPathway parentView) {
		this.parentView = parentView;
		viewFrustum = new ViewFrustum();
		layoutManger = new LayoutManager(viewFrustum, parentView.getPixelGLConverter());
	}

	public void render(GL2 gl) {
		layoutManger.updateLayout();
		layoutManger.render(gl);

	}

	public void setFrustum(float width, float height, float rowHeight) {
		viewFrustum.setRight(width);
		viewFrustum.setTop(height);
		this.rowHeight = rowHeight;
		layoutManger.updateLayout();
	}

	/**
	 * @param linearizedNodes
	 *            setter, see {@link #linearizedNodes}
	 */
	public void setLinearizedNodes(List<ANode> linearizedNodes) {

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

			boolean isEven;
			if (nodeCount % 2 == 0)
				isEven = true;
			else
				isEven = false;

			nodeCount++;

			int idCount = 0;

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

			for (Integer davidID : davidIDs) {

				Vec3f nodePosition = node.getPosition();

				Row row = new Row();
				RowBackgroundRenderer rowBackgroundRenderer = new RowBackgroundRenderer();
				rowBackgroundRenderer.setEven(isEven);
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

				idCount++;
			}
		}

	}

}
