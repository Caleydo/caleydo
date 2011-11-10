package org.caleydo.view.datagraph.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.contextmenu.CreateViewItem;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;
import org.caleydo.view.datagraph.datacontainer.DataContainerListRenderer;
import org.caleydo.view.datagraph.datacontainer.matrix.DataContainerMatrixRenderer;
import org.caleydo.view.datagraph.layout.AGraphLayout;

public class TableBasedDataNode extends ADataNode {

	private final static String TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE = "org.caleydo.view.datagraph.toggledatacontainerbutton";
	private final static int TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID = 0;

	private ATableBasedDataDomain dataDomain;
	private ButtonRenderer toggleDataContainerButtonRenderer;
	private Button toggleDataContainerButton;
	private ALayoutState currentState;
	private ElementLayout dataContainerLayout;
	private ADataContainerRenderer dataContainerRenderer;
	private Row bodyRow;
	private List<DataContainer> dataContainers;

	private abstract class ALayoutState {
		protected ADataContainerRenderer dataContainerRenderer;
		protected int textureRotation;

		public void apply() {
			TableBasedDataNode.this.dataContainerRenderer = dataContainerRenderer;
			dataContainerRenderer.setUpsideDown(isUpsideDown);
			dataContainerLayout.setRenderer(dataContainerRenderer);
			toggleDataContainerButtonRenderer
					.setTextureRotation(currentState.textureRotation);
			graphLayout.updateNodePositions();
		}

		public abstract ALayoutState getNextState();
	}

	private class OverviewState extends ALayoutState {

		public OverviewState() {
			dataContainerRenderer = new DataContainerListRenderer(
					TableBasedDataNode.this, view, dragAndDropController,
					getDataContainers());
			textureRotation = ButtonRenderer.TEXTURE_ROTATION_270;
		}

		@Override
		public ALayoutState getNextState() {
			return new DetailState();
		}

		@Override
		public void apply() {
			super.apply();
			bodyRow.clearBackgroundRenderers();
			if (getDataContainers().size() > 0) {
				bodyRow.addBackgroundRenderer(new ColorRenderer(
						new float[] { 1, 1, 1, 1 }));
			}
		}
	}

	private class DetailState extends ALayoutState {

		public DetailState() {
			dataContainerRenderer = new DataContainerMatrixRenderer(dataDomain, view,
					TableBasedDataNode.this, dragAndDropController);

			textureRotation = ButtonRenderer.TEXTURE_ROTATION_90;
		}

		@Override
		public ALayoutState getNextState() {
			return new OverviewState();
		}

		@Override
		public void apply() {
			super.apply();
			bodyRow.clearBackgroundRenderers();
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
		}
	}

	public TableBasedDataNode(AGraphLayout graphLayout, GLDataGraph view,
			DragAndDropController dragAndDropController, Integer id,
			IDataDomain dataDomain) {
		super(graphLayout, view, dragAndDropController, id, dataDomain);
		this.dataDomain = (ATableBasedDataDomain) dataDomain;

		currentState = new OverviewState();
		dataContainerRenderer = currentState.dataContainerRenderer;

		addPickingListeners();
	}

	private void addPickingListeners() {
		view.addSingleIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dataContainerRenderer.destroy();
				currentState = currentState.getNextState();
				currentState.apply();

				view.setDisplayListDirty();
			}

		}, TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE + getID(),
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID);

		// FIXME: Use in DataContainer and get views properly
		view.addSingleIDPickingListener(new APickingListener() {

			@Override
			public void rightClicked(Pick pick) {
				view.getContextMenuCreator().addContextMenuItem(
						new CreateViewItem("Parallel Coordinates",
								"org.caleydo.view.parcoords", dataDomain, null));
			}

		}, PickingType.DATA_GRAPH_NODE.name(), id);
	}

	@Override
	protected ElementLayout setupLayout() {

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		Row baseRow = createDefaultBaseRow(dataDomain.getColor().getRGBA(), getID());
		ElementLayout spacingLayoutX = createDefaultSpacingX();

		baseColumn = new Column();
		baseColumn.setPixelGLConverter(pixelGLConverter);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		Row titleRow = new Row("titleRow");

		ElementLayout captionLayout = createDefaultCaptionLayout(dataDomain.getLabel(),
				getID());

		titleRow.append(captionLayout);
		titleRow.setYDynamic(true);

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		ElementLayout toggleDataContainerButtonLayout = new ElementLayout(
				"toggleDataContainerLayout");
		toggleDataContainerButtonLayout.setPixelGLConverter(pixelGLConverter);
		toggleDataContainerButtonLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		toggleDataContainerButtonLayout.setPixelSizeX(CAPTION_HEIGHT_PIXELS);
		toggleDataContainerButton = new Button(TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE
				+ getID(), TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID,
				EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK);
		// FIXME: set button invisible if there are not more than 1 perspectives
		if (dataDomain.getRecordPerspectiveIDs().size() <= 1
				&& dataDomain.getDimensionPerspectiveIDs().size() <= 1) {
			toggleDataContainerButton.setVisible(false);
		}
		toggleDataContainerButtonRenderer = new ButtonRenderer(toggleDataContainerButton,
				view, view.getTextureManager());
		toggleDataContainerButtonRenderer.setZCoordinate(1);
		toggleDataContainerButtonLayout.setRenderer(toggleDataContainerButtonRenderer);
		titleRow.append(spacingLayoutX);
		titleRow.append(toggleDataContainerButtonLayout);

		bodyRow = new Row("bodyRow");

		bodyColumn = new Column("bodyColumn");

		dataContainerLayout = new ElementLayout("dataContainerLayout");
		dataContainerLayout.setRatioSizeY(1);
		dataContainerLayout.setRenderer(dataContainerRenderer);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(dataContainerLayout);
		bodyColumn.append(spacingLayoutY);

		bodyRow.append(bodyColumn);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(bodyRow);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(titleRow);
		baseColumn.append(spacingLayoutY);

		setUpsideDown(isUpsideDown);

		currentState.apply();

		return baseRow;
	}

	@Override
	public void destroy() {
		view.removeSingleIDPickingListeners(TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE
				+ getID(), TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID);
		dataContainerRenderer.destroy();
	}

	@Override
	public void update() {

		retrieveDataContainers();
		if (dataDomain.getRecordPerspectiveIDs().size() > 1
				|| dataDomain.getDimensionPerspectiveIDs().size() > 1) {
			toggleDataContainerButton.setVisible(true);
		}
		currentState.apply();
		dataContainerRenderer.setDataContainers(getDataContainers());
	}

	@Override
	protected ADataContainerRenderer getDataContainerRenderer() {
		return dataContainerRenderer;
	}

	protected void retrieveDataContainers() {
		Collection<DataContainer> containerCollection = dataDomain.getAllDataContainers();
		if (containerCollection == null) {
			dataContainers = new ArrayList<DataContainer>();
			return;
		}
		List<Pair<String, DataContainer>> sortedDataContainers = new ArrayList<Pair<String, DataContainer>>();
		for (DataContainer container : containerCollection) {
			sortedDataContainers.add(new Pair<String, DataContainer>(
					container.getLabel(), container));
		}

		Collections.sort(sortedDataContainers);

		dataContainers = new ArrayList<DataContainer>(sortedDataContainers.size());

		for (Pair<String, DataContainer> pair : sortedDataContainers) {
			dataContainers.add(pair.getSecond());
		}
	}

	@Override
	public List<DataContainer> getDataContainers() {

		if (dataContainers == null)
			retrieveDataContainers();

		return dataContainers;

		// List<ADimensionGroupData> groups = new
		// ArrayList<ADimensionGroupData>();
		// FakeDimensionGroupData data = new FakeDimensionGroupData(0);
		// data.setDimensionPerspectiveID("ColumnPerspec2");
		// data.setRecordPerspectiveID("Row1");
		// if (dataDomain instanceof ATableBasedDataDomain)
		// data.setDataDomain((ATableBasedDataDomain) dataDomain);
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(1);
		// data.setDimensionPerspectiveID("ColumnPerspec2");
		// data.setRecordPerspectiveID("AnotherRow");
		// if (dataDomain instanceof ATableBasedDataDomain)
		// data.setDataDomain((ATableBasedDataDomain) dataDomain);
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(2);
		// data.setDimensionPerspectiveID("ColumnPerspec2");
		// data.setRecordPerspectiveID("YetAnotherRow");
		// if (dataDomain instanceof ATableBasedDataDomain)
		// data.setDataDomain((ATableBasedDataDomain) dataDomain);
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(3);
		// data.setDimensionPerspectiveID("ColumnPerspec2");
		// data.setRecordPerspectiveID("RowPerspec2");
		// if (dataDomain instanceof ATableBasedDataDomain)
		// data.setDataDomain((ATableBasedDataDomain) dataDomain);
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(4);
		// data.setDimensionPerspectiveID("AnotherColumn2");
		// data.setRecordPerspectiveID("Row1");
		// if (dataDomain instanceof ATableBasedDataDomain)
		// data.setDataDomain((ATableBasedDataDomain) dataDomain);
		// groups.add(data);
		//
		// data = new FakeDimensionGroupData(5);
		// data.setDimensionPerspectiveID("YetAnotherColumn2");
		// data.setRecordPerspectiveID("YetAnotherRow");
		// if (dataDomain instanceof ATableBasedDataDomain)
		// data.setDataDomain((ATableBasedDataDomain) dataDomain);
		// groups.add(data);
	}

}
