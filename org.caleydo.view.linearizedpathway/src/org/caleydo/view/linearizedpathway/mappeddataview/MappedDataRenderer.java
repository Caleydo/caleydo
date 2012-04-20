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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.view.IMultiDataContainerBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.node.ANode;

/**
 * Renderer for mapped genomic data for linearized pathway view. Based on a list
 * of input nodes genomic data is rendered in a row-column layout. For every
 * node 0-n rows are created, depending on the mapping of the node to the data.
 * 
 * @author Alexander Lex
 * 
 */
public class MappedDataRenderer {

	GLLinearizedPathway parentView;

	private List<ANode> linearizedNodes;

	private ArrayList<RelationshipRenderer> relationShipRenderers;

	/**
	 * The top-level data containers as set externally through the
	 * {@link IMultiDataContainerBasedView} interface of {@link #parentView}
	 */
	private ArrayList<DataContainer> dataContainers = new ArrayList<DataContainer>(5);;

	/**
	 * The data containers resolved based on the {@link GroupList}s of the
	 * {@link #dataContainers}. That means that this list contains a
	 * dataContainer for every experiment group in one of the DataContainers in
	 * {@link #dataContainers}.
	 */
	private ArrayList<DataContainer> resolvedDataContainers = new ArrayList<DataContainer>();

	/**
	 * Set to either {@link #dataContainers} or {@link #resolvedDataContainers}
	 */
	private ArrayList<DataContainer> usedDataContainers;

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

	// private ConnectionBandRenderer connectionBandRenderer;

	// private float[] oddColor = { 43f / 255f, 140f / 255, 190f / 255, 1f };
	// private float[] evenColor = { 166f / 255f, 189f / 255, 219f / 255, 1f };
	private float[] oddColor = { 240f / 255f, 240f / 255, 240f / 255, 1f };
	private float[] evenColor = { 220f / 255f, 220f / 255, 220f / 255, 1f };

	/**
	 * Constructor with parent view as parameter.
	 */
	public MappedDataRenderer(GLLinearizedPathway parentView) {
		this.parentView = parentView;
		viewFrustum = new ViewFrustum();
		layoutManger = new LayoutManager(viewFrustum, parentView.getPixelGLConverter());
		usedDataContainers = resolvedDataContainers;
	}

	public void render(GL2 gl) {
		layoutManger.updateLayout();
		layoutManger.render(gl);

		for (RelationshipRenderer relationshipRenderer : relationShipRenderers) {
			relationshipRenderer.render(gl);
		}

	}

	/**
	 * Set the geometry information for this <code>MappedDataRenderer</code>.
	 * 
	 * @param width
	 *            the width of the drawing space of this renderer
	 * @param height
	 *            the height of the drawing space of this renderer
	 * @param xOffset
	 *            how much this renderer is translated in x direction from the
	 *            window origin
	 * @param yOffset
	 *            how much this renderer is translated in y direction from the
	 *            window origin
	 * @param rowHeight
	 *            the height of a single row in the renderer rows
	 */
	public void setGeometry(float width, float height, float xOffset, float yOffset,
			float rowHeight) {
		viewFrustum.setRight(width);
		viewFrustum.setTop(height);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.rowHeight = rowHeight;

		layoutManger.updateLayout();

	}

	/**
	 * Sets the list of nodes that are used as the basis for rendering the
	 * mapped data. Triggers a complete re-build of the layout
	 * 
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

		/** A list of rows for each data container */
		ArrayList<ArrayList<Row>> rowListForDataContainers = new ArrayList<ArrayList<Row>>(
				(int) (usedDataContainers.size() * 1.6));

		for (DataContainer dataContainer : usedDataContainers) {
			rowListForDataContainers.add(new ArrayList<Row>(linearizedNodes.size() * 2));
		}

		ArrayList<Integer> davidIDs = new ArrayList<Integer>(linearizedNodes.size() * 2);

		ElementLayout xSpacing = new ElementLayout();
		xSpacing.setPixelSizeX(5);

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

			relationShipRenderer.topLeft[0] = x - xOffset;
			relationShipRenderer.topLeft[1] = node.getPosition().y() + height / 2
					- yOffset;

			relationShipRenderer.bottomLeft[0] = x - xOffset;
			relationShipRenderer.bottomLeft[1] = node.getPosition().y() - height / 2
					- yOffset;

			nodeCount++;

			ArrayList<Integer> subDavidIDs = node.getPathwayVertexRep().getDavidIDs();
			davidIDs.addAll(subDavidIDs);

			float currentNodePositionY = node.getPosition().y();
			float deviation;

			if (previousNodePosition < 0) {
				deviation = 0;
			} else {
				deviation = previousNodePosition - rowHeight * previousNrDavids / 2
						- currentNodePositionY - rowHeight * subDavidIDs.size() / 2;
			}

			if (previousNodePosition > 0 && deviation > 0) {
				Row spacing = new Row();
				spacing.setAbsoluteSizeY(deviation);
				dataSetColumn.append(spacing);
				captionColumn.append(spacing);
			}
			previousNodePosition = currentNodePositionY;
			previousNrDavids = subDavidIDs.size();

			int idCount = 0;

			for (Integer davidID : subDavidIDs) {

				Row row = new Row();
				RowBackgroundRenderer rowBackgroundRenderer = new RowBackgroundRenderer(
						color);
				row.addBackgroundRenderer(rowBackgroundRenderer);
				row.setAbsoluteSizeY(rowHeight);
				dataSetColumn.append(row);

				for (int dataContainerCount = 0; dataContainerCount < usedDataContainers
						.size(); dataContainerCount++) {
					Row dataContainerRow = new Row("DataContainer " + dataContainerCount
							+ " / " + idCount);
					// dataContainerRow.setPixelSizeX(5);
					row.append(dataContainerRow);
					rowListForDataContainers.get(dataContainerCount)
							.add(dataContainerRow);
					if (dataContainerCount != usedDataContainers.size() - 1) {
						row.append(xSpacing);
					}
				}
				ElementLayout rowCaption = new ElementLayout();
				rowCaption.setAbsoluteSizeY(rowHeight);
				CaptionRenderer captionRenderer = new CaptionRenderer(
						parentView.getTextRenderer(), parentView.getPixelGLConverter(),
						davidID);
				rowCaption.setRenderer(captionRenderer);
				captionColumn.append(rowCaption);

				if (idCount == 0)
					relationShipRenderer.topRightLayout = row;
				if (idCount == subDavidIDs.size() - 1)
					relationShipRenderer.bottomRightLayout = row;

				idCount++;
			}

		}
		ElementLayout ySpacing = new ElementLayout();
		ySpacing.setPixelSizeY(5);
		dataSetColumn.append(ySpacing);
		
		Row captionRow = new Row("captionRow");
		captionRow.setPixelSizeY(40);
		dataSetColumn.append(captionRow);

		for (int dataContainerCount = 0; dataContainerCount < usedDataContainers.size(); dataContainerCount++) {
			ElementLayout captionLayout = new ElementLayout("caption layout");
			captionRow.append(captionLayout);
			if (dataContainerCount != usedDataContainers.size() - 1) {
				captionRow.append(xSpacing);
			}
			prepareData(usedDataContainers.get(dataContainerCount),
					rowListForDataContainers.get(dataContainerCount), captionLayout,
					davidIDs);
		}

	}

	private void prepareData(DataContainer dataContainer, ArrayList<Row> rowLayouts,
			ElementLayout captionLayout, ArrayList<Integer> davidIDs) {
		GeneticDataDomain dataDomain = (GeneticDataDomain) dataContainer.getDataDomain();

		ADataPerspective<?, ?, ?, ?> experimentPerspective;
		if (dataDomain.isGeneRecord()) {
			experimentPerspective = dataContainer.getDimensionPerspective();
		} else {
			experimentPerspective = dataContainer.getRecordPerspective();
		}

		IDType geneIDTYpe = dataDomain.getGeneIDType();
		// ArrayList<Integer> geneIDs = new ArrayList<Integer>(davidIDs.size());
		for (int rowCount = 0; rowCount < davidIDs.size(); rowCount++) {
			Integer davidID = davidIDs.get(rowCount);
			Set<Integer> geneIDs = dataDomain.getGeneIDMappingManager().getIDAsSet(
					IDType.getIDType("DAVID"), geneIDTYpe, davidID);
			Integer geneID;
			if (geneIDs == null) {
				System.out.println("No mapping for david: " + davidID);
				geneID = null;

			} else {
				geneID = geneIDs.iterator().next();
				if (geneIDs.size() > 1) {

					Set<String> names = dataDomain.getGeneIDMappingManager().getIDAsSet(
							IDType.getIDType("DAVID"),
							dataDomain.getHumanReadableGeneIDType(), davidID);
					System.out.println("Here's the problem: " + names + " / " + geneIDs);
				}
			}

			// geneIDs.add(davidID);
			Row row = rowLayouts.get(rowCount);

			float width = 1.0f / usedDataContainers.size();
			row.setRatioSizeX(width);

			captionLayout.setRatioSizeX(width);
			
			
			
			LayoutRenderer columnCaptionRenderer = new ColumnCaptionRenderer(
					parentView.getTextRenderer(), parentView.getPixelGLConverter(),
					dataContainer.getLabel());
			captionLayout.setRenderer(columnCaptionRenderer);

			row.setRenderer(new RowRenderer(geneID, dataDomain, dataContainer,
					experimentPerspective));
		}

	}

	public void addDataContainer(DataContainer newDataContainer) {
		dataContainers.add(newDataContainer);
		ArrayList<DataContainer> newDataContainers = new ArrayList<DataContainer>(1);
		newDataContainers.add(newDataContainer);
		resolveSubDataContainers(newDataContainers);

	}

	public void addDataContainers(List<DataContainer> newDataContainers) {
		dataContainers.addAll(newDataContainers);
		resolveSubDataContainers(newDataContainers);
	}

	public List<DataContainer> getDataContainers() {
		return dataContainers;
	}

	private void resolveSubDataContainers(List<DataContainer> newDataContainers) {
		for (DataContainer dataContainer : newDataContainers) {
			GeneticDataDomain dataDomain = (GeneticDataDomain) dataContainer
					.getDataDomain();
			if (dataDomain.isGeneRecord()) {
				resolvedDataContainers.addAll(dataContainer
						.getDimensionSubDataContainers());
			} else {
				resolvedDataContainers.addAll(dataContainer.getRecordSubDataContainers());
			}
		}

	}

}
