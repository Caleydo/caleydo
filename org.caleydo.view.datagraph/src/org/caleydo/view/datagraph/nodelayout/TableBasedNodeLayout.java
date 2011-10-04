package org.caleydo.view.datagraph.nodelayout;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.datagraph.ADataContainerRenderer;
import org.caleydo.view.datagraph.DetailDataContainerRenderer;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.OverviewDataContainerRenderer;
import org.caleydo.view.datagraph.node.ADataNode;

public class TableBasedNodeLayout extends ADataNodeLayout {

	private final static String TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE = "org.caleydo.view.datagraph.toggledatacontainerbutton";
	private final static int TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID = 0;

	// private final static int SPACING_PIXELS = 4;
	// private final static int CAPTION_HEIGHT_PIXELS = 16;
	// private final static int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	// private final static int MIN_DATA_CONTAINER_HEIGHT_PIXELS = 32;
	// private final static int MIN_DATA_CONTAINER_WIDTH_PIXELS = 200;

	// private DataNode node;
	private ATableBasedDataDomain dataDomain;
	private ButtonRenderer toggleDataContainerButtonRenderer;
	private Button toggleDataContainerButton;
	private ALayoutState currentState;
	private ElementLayout dataContainerLayout;

	private abstract class ALayoutState {
		protected ADataContainerRenderer dataContainerRenderer;
		protected int textureRotation;

		public abstract ALayoutState getNextState();
	}

	private class OverviewState extends ALayoutState {

		public OverviewState() {
			dataContainerRenderer = new OverviewDataContainerRenderer(node,
					view, dragAndDropController, node.getDimensionGroups());
			textureRotation = ButtonRenderer.TEXTURE_ROTATION_270;
		}

		@Override
		public ALayoutState getNextState() {
			return new DetailState();
		}
	}

	private class DetailState extends ALayoutState {

		public DetailState() {
			dataContainerRenderer = new DetailDataContainerRenderer(dataDomain,
					view, node, dragAndDropController);
			textureRotation = ButtonRenderer.TEXTURE_ROTATION_90;
		}

		@Override
		public ALayoutState getNextState() {
			return new OverviewState();
		}
	}

	public TableBasedNodeLayout(ADataNode node, GLDataGraph view,
			DragAndDropController dragAndDropController) {
		super(node, view, dragAndDropController);
		this.dataDomain = (ATableBasedDataDomain) node.getDataDomain();
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
				dataContainerRenderer = currentState.dataContainerRenderer;
				dataContainerLayout.setRenderer(dataContainerRenderer);
				toggleDataContainerButtonRenderer
						.setTextureRotation(currentState.textureRotation);
				view.setDisplayListDirty();
			}

		}, TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE + node.getID(),
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID);
	}

	@Override
	public ElementLayout setupLayout() {

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		Row baseRow = createDefaultBaseRow(dataDomain.getColor().getRGBA(),
				node.getID());
		ElementLayout spacingLayoutX = createDefaultSpacingX();

		Column baseColumn = new Column();
		baseColumn.setPixelGLConverter(pixelGLConverter);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		Row titleRow = new Row("titleRow");

		ElementLayout captionLayout = createDefaultCaptionLayout(
				dataDomain.getDataDomainID(), node.getID());

		titleRow.append(captionLayout);
		titleRow.setYDynamic(true);

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		ElementLayout toggleDataContainerDetailLayout = new ElementLayout(
				"toggleDataContainerLayout");
		toggleDataContainerDetailLayout.setPixelGLConverter(pixelGLConverter);
		toggleDataContainerDetailLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		toggleDataContainerDetailLayout.setPixelSizeX(CAPTION_HEIGHT_PIXELS);
		toggleDataContainerButton = new Button(
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE + node.getID(),
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID,
				EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK);
		// FIXME: set button invisible if there are not more than 1 perspectives
		// if (dataDomain.getRecordPerspectiveIDs().size() <= 1
		// && dataDomain.getDimensionPerspectiveIDs().size() <= 1) {
		// toggleDataContainerButton.setVisible(false);
		// }
		toggleDataContainerButtonRenderer = new ButtonRenderer(
				toggleDataContainerButton, view, view.getTextureManager(),
				currentState.textureRotation);
		toggleDataContainerButtonRenderer.setZCoordinate(1);
		toggleDataContainerDetailLayout
				.setRenderer(toggleDataContainerButtonRenderer);
		titleRow.append(toggleDataContainerDetailLayout);

		Row bodyRow = new Row("bodyRow");

		if (node.getDimensionGroups().size() > 0) {
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1,
					1, 1 }));
		}

		Column bodyColumn = new Column("bodyColumn");

		dataContainerLayout = new ElementLayout("dataContainerLayout");
		dataContainerLayout.setRatioSizeY(1);
		dataContainerLayout.setRenderer(dataContainerRenderer);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(dataContainerLayout);
		bodyColumn.append(spacingLayoutY);

		bodyRow.append(bodyColumn);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(bodyRow);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(titleRow);
		baseColumn.append(spacingLayoutY);

		return baseRow;
	}

	@Override
	public void destroy() {
		view.removeSingleIDPickingListeners(
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE + node.getID(),
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID);
		dataContainerRenderer.destroy();
	}

	@Override
	public void update() {
		// FIXME: set button visible if there are more than 1 perspectives
		// if (dataDomain.getRecordPerspectiveIDs().size() > 1
		// || dataDomain.getDimensionPerspectiveIDs().size() > 1) {
		// toggleDataContainerButton.setVisible(true);
		// }
		dataContainerRenderer.setDimensionGroups(node.getDimensionGroups());
	}

	@Override
	public Class<? extends IDataDomain> getDataDomainType() {
		return ATableBasedDataDomain.class;
	}

}
