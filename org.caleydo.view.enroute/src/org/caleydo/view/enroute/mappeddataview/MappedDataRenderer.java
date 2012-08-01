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
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.IMultiDataContainerBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.node.ALinearizableNode;
import org.caleydo.view.enroute.node.ComplexNode;

/**
 * Renderer for mapped genomic data for linearized pathway view. Based on a list
 * of input nodes genomic data is rendered in a row-column layout. For every
 * node 0-n rows are created, depending on the mapping of the node to the data.
 * 
 * @author Alexander Lex
 * 
 */
public class MappedDataRenderer {

	public static float[] FRAME_COLOR = { 1, 1, 1, 1 };

	public static float[] ODD_BACKGROUND_COLOR = { 220f / 255f, 220f / 255, 220f / 255,
			1f };
	public static float[] EVEN_BACKGROUND_COLOR = { 180f / 255f, 180f / 255, 180f / 255,
			1f };

	public static float[] CAPTION_BACKGROUND_COLOR = { 220f / 255f, 220f / 255,
			220f / 255, 1f };

	public static float[] BAR_COLOR = { 43f / 255f, 140f / 255, 190f / 255, 1f };

	public static float[] SUMMARY_BAR_COLOR = { 49f / 255f, 163f / 255, 84f / 255, 1f };

	public static final SelectionType abstractGroupType = new SelectionType(
			"AbstactGroup", new int[] { 0, 0, 0 }, 1, false, false, 0);

	public static int ABSTRACT_GROUP_PIXEL_SIZE = 100;

	private GLEnRoutePathway parentView;

	// private List<ALinearizableNode> linearizedNodes;

	private ArrayList<RelationshipRenderer> relationShipRenderers;

	/**
	 * The top-level data containers as set externally through the
	 * {@link IMultiDataContainerBasedView} interface of {@link #parentView}
	 */
	private ArrayList<DataContainer> dataContainers = new ArrayList<DataContainer>(5);

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

	EventBasedSelectionManager geneSelectionManager;
	EventBasedSelectionManager sampleSelectionManager;
	EventBasedSelectionManager sampleGroupSelectionManager;

	/** The mapping Type all samples understand */
	IDType sampleIDType;

	int selectedBucketID = 0;

	/**
	 * Constructor with parent view as parameter.
	 */
	public MappedDataRenderer(GLEnRoutePathway parentView) {
		this.parentView = parentView;
		viewFrustum = new ViewFrustum();
		layoutManger = new LayoutManager(viewFrustum, parentView.getPixelGLConverter());
		usedDataContainers = resolvedDataContainers;

		geneSelectionManager = new EventBasedSelectionManager(parentView,
				IDType.getIDType("DAVID"));
		geneSelectionManager.registerEventListeners();

		ArrayList<GeneticDataDomain> dataDomains = DataDomainManager.get()
				.getDataDomainsByType(GeneticDataDomain.class);
		if (dataDomains.size() != 0) {
			sampleIDType = dataDomains.get(0).getSampleIDType().getIDCategory()
					.getPrimaryMappingType();
			sampleSelectionManager = new EventBasedSelectionManager(parentView,
					sampleIDType);

			sampleGroupSelectionManager = new EventBasedSelectionManager(parentView,
					dataDomains.get(0).getSampleGroupIDType());

			SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(
					abstractGroupType);
			GeneralManager.get().getEventPublisher().triggerEvent(selectionTypeEvent);

		} else {
			throw new IllegalStateException("No Valid Datadomain");
		}

		registerPickingListeners();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		geneSelectionManager.unregisterEventListeners();
		geneSelectionManager = null;
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

		// layoutManger.updateLayout();

	}

	/**
	 * Sets the list of nodes that are used as the basis for rendering the
	 * mapped data. Triggers a complete re-build of the layout. Creates the
	 * layout used for the rendering.
	 * 
	 * @param linearizedNodes
	 *            setter, see {@link #linearizedNodes}
	 */
	public void setLinearizedNodes(List<ALinearizableNode> linearizedNodes) {

		float[] color;
		relationShipRenderers = new ArrayList<RelationshipRenderer>(
				linearizedNodes.size());

		Row baseRow = new Row("baseRow");
		layoutManger.setBaseElementLayout(baseRow);

		Column dataSetColumn = new Column("dataSetColumn");
		dataSetColumn.setBottomUp(false);
		// dataSetColumn.setDebug(true);
		baseRow.append(dataSetColumn);
		Column captionColumn = new Column("captionColumn");
		captionColumn.setBottomUp(false);

		captionColumn.setPixelSizeX(100);
		ElementLayout columnCaptionSpacing = new ElementLayout();
		columnCaptionSpacing.setPixelSizeY(50);
		captionColumn.append(columnCaptionSpacing);
		baseRow.append(captionColumn);

		int nodeCount = 0;
		float previousNodePosition = viewFrustum.getHeight() + yOffset
				- parentView.getPixelGLConverter().getGLHeightForPixelHeight(50);
		int previousNrDavids = 0;

		/**
		 * A list of lists of element layouts where the outer list contains one
		 * nested list for every data container and the inner list one element
		 * layout for every gene in the linearized pathway
		 */
		ArrayList<ArrayList<ElementLayout>> rowListForDataContainers = new ArrayList<ArrayList<ElementLayout>>(
				(int) (usedDataContainers.size() * 1.6));

		for (int count = 0; count < usedDataContainers.size(); count++) {
			rowListForDataContainers.add(new ArrayList<ElementLayout>(linearizedNodes
					.size() * 2));
		}

		ArrayList<Integer> davidIDs = new ArrayList<Integer>(linearizedNodes.size() * 2);

		ElementLayout xSpacing = new ElementLayout();
		xSpacing.setPixelSizeX(1);

		ArrayList<ALinearizableNode> resolvedNodes = new ArrayList<ALinearizableNode>();

		// resolve complex nodes
		for (ALinearizableNode node : linearizedNodes) {
			if (node instanceof ComplexNode) {
				List<ALinearizableNode> embeddedNodes = ((ComplexNode) node).getNodes();
				resolvedNodes.addAll(embeddedNodes);
			} else
				resolvedNodes.add(node);
		}

		for (ALinearizableNode node : resolvedNodes) {

			if (node.getNumAssociatedRows() == 0)
				continue;

			List<Integer> subDavidIDs = node.getMappedDavidIDs();
			int currentNrDavids = subDavidIDs.size();
			davidIDs.addAll(subDavidIDs);

			float currentNodePositionY = node.getPosition().y();
			float deviation;

			if (node.getParentNode() != null) {
				currentNodePositionY = node.getParentNode().getPosition().y();
				currentNrDavids = node.getParentNode().getNumAssociatedRows();
			}
			float previousLowerHeight = previousNodePosition - rowHeight
					* ((float) previousNrDavids) / 2;
			float currentUpperHeight = (currentNodePositionY + rowHeight
					* ((float) currentNrDavids) / 2);
			deviation = previousLowerHeight - currentUpperHeight;

			if (previousNodePosition > 0 && deviation > 0) {
				ElementLayout spacing = new ElementLayout();
				spacing.setAbsoluteSizeY(deviation);
				dataSetColumn.append(spacing);
				captionColumn.append(spacing);
			}

			previousNodePosition = currentNodePositionY;
			previousNrDavids = currentNrDavids;

			if (nodeCount % 2 == 0)
				color = EVEN_BACKGROUND_COLOR;
			else
				color = ODD_BACKGROUND_COLOR;

			RelationshipRenderer relationShipRenderer = new RelationshipRenderer(color,
					parentView);
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

			int idCount = 0;
			// for (int davidCounter = subDavidIDs.size()-1; davidCounter>=0;
			// davidCounter--) {

			for (Integer davidID : subDavidIDs) {

				Row row = new Row("Row " + davidID);
				// RowBackgroundRenderer rowBackgroundRenderer = new
				// RowBackgroundRenderer(
				// color);
				// row.addBackgroundRenderer(rowBackgroundRenderer);
				row.setAbsoluteSizeY(rowHeight);
				// row.setDebug(true);
				dataSetColumn.append(row);

				for (int dataContainerCount = 0; dataContainerCount < usedDataContainers
						.size(); dataContainerCount++) {

					ElementLayout dataContainerLayout = new ElementLayout(
							"DataContainer " + dataContainerCount + " / " + idCount);
					// dataContainerRow.setPixelSizeX(5);
					dataContainerLayout.addBackgroundRenderer(new RowBackgroundRenderer(
							color));

					row.append(dataContainerLayout);
					rowListForDataContainers.get(dataContainerCount).add(
							dataContainerLayout);
					if (dataContainerCount != usedDataContainers.size() - 1) {
						row.append(xSpacing);
					}
				}
				ElementLayout rowCaption = new ElementLayout();
				rowCaption.setAbsoluteSizeY(rowHeight);

				RowCaptionRenderer captionRenderer = new RowCaptionRenderer(davidID,
						parentView, this, color);
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

		Row topCaptionRow = new Row("topCaptionRow");
		// captionRow.setDebug(true);
		topCaptionRow.setPixelSizeY(50);
		// topCaptionRow.setDebug(true);
		// dataSetColumn.add(0, captionRow);
		dataSetColumn.add(0, topCaptionRow);

		Row bottomCaptionRow = new Row("captionRow");
		// captionRow.setDebug(true);
		bottomCaptionRow.setPixelSizeY(50);
		// dataSetColumn.add(0, captionRow);
		dataSetColumn.append(bottomCaptionRow);

		for (int dataContainerCount = 0; dataContainerCount < usedDataContainers.size(); dataContainerCount++) {

			ColumnCaptionLayout topCaptionLayout = new ColumnCaptionLayout(parentView,
					this);
			topCaptionRow.append(topCaptionLayout);

			ColumnCaptionLayout bottomCaptionLayout = new ColumnCaptionLayout(parentView,
					this);
			bottomCaptionRow.append(bottomCaptionLayout);
			if (dataContainerCount != usedDataContainers.size() - 1) {
				bottomCaptionRow.append(xSpacing);
				topCaptionRow.append(xSpacing);
			}
			prepareData(usedDataContainers.get(dataContainerCount),
					rowListForDataContainers.get(dataContainerCount), topCaptionLayout,
					bottomCaptionLayout, davidIDs);
		}

	}

	/** Fills the layout with data specific for the data containers */
	private void prepareData(DataContainer dataContainer,
			ArrayList<ElementLayout> rowLayouts, ColumnCaptionLayout topCaptionLayout,
			ColumnCaptionLayout bottomCaptionLayout, ArrayList<Integer> davidIDs) {
		GeneticDataDomain dataDomain = (GeneticDataDomain) dataContainer.getDataDomain();

		ADataPerspective<?, ?, ?, ?> experimentPerspective;
		if (dataDomain.isGeneRecord()) {
			experimentPerspective = dataContainer.getDimensionPerspective();
		} else {
			experimentPerspective = dataContainer.getRecordPerspective();
		}

		Group group = null;
		if (dataDomain.isGeneRecord()) {
			group = dataContainer.getDimensionGroup();
			if (group == null) {
				group = dataContainer.getDimensionPerspective().getVirtualArray()
						.getGroupList().get(0);
				group.setLabel(dataContainer.getLabel(), dataContainer.isDefaultLabel());
			}
		} else {
			group = dataContainer.getRecordGroup();
			if (group == null) {
				group = dataContainer.getRecordPerspective().getVirtualArray()
						.getGroupList().get(0);
				group.setLabel(dataContainer.getLabel(), dataContainer.isDefaultLabel());
			}
		}
		topCaptionLayout.init(group, experimentPerspective, dataDomain);
		bottomCaptionLayout.init(group, experimentPerspective, dataDomain);

		IDType geneIDTYpe = dataDomain.getGeneIDType();
		// ArrayList<Integer> geneIDs = new ArrayList<Integer>(davidIDs.size());
		for (int rowCount = 0; rowCount < davidIDs.size(); rowCount++) {
			Integer davidID = davidIDs.get(rowCount);
			Set<Integer> geneIDs = dataDomain.getGeneIDMappingManager().getIDAsSet(
					IDType.getIDType("DAVID"), geneIDTYpe, davidID);
			Integer geneID;
			if (geneIDs == null) {
				// System.out.println("No mapping for david: " + davidID);
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
			ElementLayout dataContainerLayout = rowLayouts.get(rowCount);

			if (sampleGroupSelectionManager.checkStatus(abstractGroupType, group.getID())) {
				dataContainerLayout.setPixelSizeX(ABSTRACT_GROUP_PIXEL_SIZE);
				bottomCaptionLayout.setPixelSizeX(ABSTRACT_GROUP_PIXEL_SIZE);
				topCaptionLayout.setPixelSizeX(ABSTRACT_GROUP_PIXEL_SIZE);
			} else {
				// float width = 1.0f / usedDataContainers.size();
				// dataContainerLayout.setRatioSizeX(0.2);
				dataContainerLayout.setDynamicSizeUnitsX(experimentPerspective
						.getVirtualArray().size());
				bottomCaptionLayout.setDynamicSizeUnitsX(experimentPerspective
						.getVirtualArray().size());
				topCaptionLayout.setDynamicSizeUnitsX(experimentPerspective
						.getVirtualArray().size());
			}

			// LayoutRenderer columnCaptionRenderer = new
			// ColumnCaptionRenderer(parentView,
			// this, group);
			// captionLayout.setRenderer(columnCaptionRenderer);

			// FIXME: BAAAAD Hack to distinguish categorical data
			if (!dataDomain.getLabel().contains("Copy")) {
				dataContainerLayout.setRenderer(new ContinuousContentRenderer(geneID,
						davidID, dataDomain, dataContainer, experimentPerspective,
						parentView, this, group));
			} else {
				dataContainerLayout.setRenderer(new CategoricalRowContentRenderer(geneID,
						davidID, dataDomain, dataContainer, experimentPerspective,
						parentView, this, group));
			}

		}

	}

	/**
	 * Adds a data container to {@link #dataContainers} and resolves sub data
	 * containers by calling {@link #resolveSubDataContainers(List)}
	 * 
	 * @param newDataContainer
	 */
	public void addDataContainer(DataContainer newDataContainer) {
		dataContainers.add(newDataContainer);
		ArrayList<DataContainer> newDataContainers = new ArrayList<DataContainer>(1);
		newDataContainers.add(newDataContainer);
		resolveSubDataContainers(newDataContainers);
	}

	/**
	 * Same as {@link #addDataContainer(DataContainer)} but for multiple data
	 * containers
	 */
	public void addDataContainers(List<DataContainer> newDataContainers) {
		dataContainers.addAll(newDataContainers);
		resolveSubDataContainers(newDataContainers);
	}

	/**
	 * @return the dataContainers, see {@link #dataContainers}
	 */
	public ArrayList<DataContainer> getDataContainers() {
		return dataContainers;
	}

	public void removeDataContainer(int dataContainerID) {
		Iterator<DataContainer> dataContainerIterator = dataContainers.iterator();

		while (dataContainerIterator.hasNext()) {
			DataContainer container = dataContainerIterator.next();
			if (container.getID() == dataContainerID) {
				dataContainerIterator.remove();
			}
		}
		resolvedDataContainers.clear();
		// TODO - this is maybe not the most elegant way to remove the resolved
		// sub-data containers
		resolveSubDataContainers(dataContainers);
	}

	/**
	 * Creates new data containers for every group in a gene-group-list of every
	 * data container. If no group lists are present, the original data
	 * container is added.
	 */
	private void resolveSubDataContainers(List<DataContainer> newDataContainers) {
		for (DataContainer dataContainer : newDataContainers) {
			GeneticDataDomain dataDomain = (GeneticDataDomain) dataContainer
					.getDataDomain();

			List<DataContainer> newlyResovedDataContainers;
			if (dataDomain.isGeneRecord()) {
				newlyResovedDataContainers = dataContainer
						.getDimensionSubDataContainers();
			} else {
				newlyResovedDataContainers = dataContainer.getRecordSubDataContainers();
			}

			if (newlyResovedDataContainers != null) {
				resolvedDataContainers.addAll(newlyResovedDataContainers);

			} else {
				resolvedDataContainers.add(dataContainer);
			}

		}

	}

	protected void registerPickingListeners() {
		parentView.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				geneSelectionManager.clearSelection(SelectionType.SELECTION);
				geneSelectionManager.addToType(SelectionType.SELECTION,
						pick.getObjectID());
				geneSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {

				geneSelectionManager.addToType(SelectionType.MOUSE_OVER,
						pick.getObjectID());
				geneSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOut(Pick pick) {
				geneSelectionManager.removeFromType(SelectionType.MOUSE_OVER,
						pick.getObjectID());
				geneSelectionManager.triggerSelectionUpdateEvent();

				parentView.setDisplayListDirty();

			}
		}, PickingType.GENE.name());

		parentView.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				sampleSelectionManager.clearSelection(SelectionType.SELECTION);
				sampleSelectionManager.addToType(SelectionType.SELECTION,
						pick.getObjectID());
				sampleSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {

				sampleSelectionManager.addToType(SelectionType.MOUSE_OVER,
						pick.getObjectID());
				sampleSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOut(Pick pick) {
				sampleSelectionManager.removeFromType(SelectionType.MOUSE_OVER,
						pick.getObjectID());
				sampleSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}
		}, PickingType.SAMPLE.name());

		parentView.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				if (sampleGroupSelectionManager.checkStatus(abstractGroupType,
						pick.getObjectID()))
					sampleGroupSelectionManager.removeFromType(abstractGroupType,
							pick.getObjectID());
				else
					sampleGroupSelectionManager.addToType(abstractGroupType,
							pick.getObjectID());
				sampleGroupSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

		}, PickingType.SAMPLE_GROUP_VIEW_MODE.name());
	}

	public void unregisterPickingListeners() {
	}

	/**
	 * @return the resolvedDataContainers, see {@link #resolvedDataContainers}
	 */
	public ArrayList<DataContainer> getResolvedDataContainers() {
		return resolvedDataContainers;
	}

}
