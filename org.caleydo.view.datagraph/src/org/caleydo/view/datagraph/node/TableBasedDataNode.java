package org.caleydo.view.datagraph.node;

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
import org.caleydo.view.datagraph.ForceDirectedGraphLayout;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.OverviewDataContainerRenderer;

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

	private abstract class ALayoutState {
		protected ADataContainerRenderer dataContainerRenderer;
		protected int textureRotation;

		public void apply() {
			TableBasedDataNode.this.dataContainerRenderer = dataContainerRenderer;
			dataContainerLayout.setRenderer(dataContainerRenderer);
			toggleDataContainerButtonRenderer
					.setTextureRotation(currentState.textureRotation);
		}

		public abstract ALayoutState getNextState();
	}

	private class OverviewState extends ALayoutState {

		public OverviewState() {
			dataContainerRenderer = new OverviewDataContainerRenderer(
					TableBasedDataNode.this, view, dragAndDropController,
					getDimensionGroups());
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
			if (getDimensionGroups().size() > 0) {
				bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] {
						1, 1, 1, 1 }));
			}
		}
	}

	private class DetailState extends ALayoutState {

		public DetailState() {
			dataContainerRenderer = new DetailDataContainerRenderer(dataDomain,
					view, TableBasedDataNode.this, dragAndDropController);
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
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1,
					1, 1 }));
		}
	}

	public TableBasedDataNode(ForceDirectedGraphLayout graphLayout,
			GLDataGraph view, DragAndDropController dragAndDropController,
			Integer id, IDataDomain dataDomain) {
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
	}

	@Override
	protected ElementLayout setupLayout() {

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		Row baseRow = createDefaultBaseRow(dataDomain.getColor().getRGBA(),
				getID());
		ElementLayout spacingLayoutX = createDefaultSpacingX();

		Column baseColumn = new Column();
		baseColumn.setPixelGLConverter(pixelGLConverter);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		Row titleRow = new Row("titleRow");

		ElementLayout captionLayout = createDefaultCaptionLayout(
				dataDomain.getDataDomainID(), getID());

		titleRow.append(captionLayout);
		titleRow.setYDynamic(true);

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		ElementLayout toggleDataContainerButtonLayout = new ElementLayout(
				"toggleDataContainerLayout");
		toggleDataContainerButtonLayout.setPixelGLConverter(pixelGLConverter);
		toggleDataContainerButtonLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		toggleDataContainerButtonLayout.setPixelSizeX(CAPTION_HEIGHT_PIXELS);
		toggleDataContainerButton = new Button(
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE + getID(),
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID,
				EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK);
		// FIXME: set button invisible if there are not more than 1 perspectives
		// if (dataDomain.getRecordPerspectiveIDs().size() <= 1
		// && dataDomain.getDimensionPerspectiveIDs().size() <= 1) {
		// toggleDataContainerButton.setVisible(false);
		// }
		toggleDataContainerButtonRenderer = new ButtonRenderer(
				toggleDataContainerButton, view, view.getTextureManager());
		toggleDataContainerButtonRenderer.setZCoordinate(1);
		toggleDataContainerButtonLayout
				.setRenderer(toggleDataContainerButtonRenderer);
		titleRow.append(spacingLayoutX);
		titleRow.append(toggleDataContainerButtonLayout);

		bodyRow = new Row("bodyRow");

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
		baseColumn.append(spacingLayoutY);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(titleRow);
		baseColumn.append(spacingLayoutY);

		currentState.apply();

		return baseRow;
	}

	@Override
	public void destroy() {
		view.removeSingleIDPickingListeners(
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE + getID(),
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
		dataContainerRenderer.setDimensionGroups(getDimensionGroups());
	}

	@Override
	protected ADataContainerRenderer getDataContainerRenderer() {
		return dataContainerRenderer;
	}

}
