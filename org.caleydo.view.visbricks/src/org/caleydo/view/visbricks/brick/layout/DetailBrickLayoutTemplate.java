package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.picking.APickingListener;
import org.caleydo.view.visbricks.brick.ui.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.ui.BorderedAreaRenderer;
import org.caleydo.view.visbricks.brick.ui.BrickViewSwitchingButton;
import org.caleydo.view.visbricks.brick.ui.FuelBarRenderer;
import org.caleydo.view.visbricks.brick.ui.HandleRenderer;
import org.caleydo.view.visbricks.brick.ui.RelationIndicatorRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

public class DetailBrickLayoutTemplate extends ABrickLayoutTemplate {

	protected static final int FUEL_BAR_HEIGHT_PIXELS = 4;
	protected static final int TOOLBAR_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int RELATION_INDICATOR_WIDTH_PIXELS = 3;
	protected static final int HANDLE_SIZE_PIXELS = 8;

	protected static final int CLOSE_BUTTON_ID = 0;
	protected static final int LOCK_RESIZING_BUTTON_ID = 1;
	protected static final int VIEW_SWITCHING_MODE_BUTTON_ID = 5;

	protected ArrayList<BrickViewSwitchingButton> viewSwitchingButtons;

	// protected Button heatMapButton;
	// protected Button parCoordsButton;
	// protected Button histogramButton;
	// protected Button overviewHeatMapButton;
	protected Button viewSwitchingModeButton;
	protected Button lockResizingButton;

	private GLVisBricks visBricks;
	private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	public DetailBrickLayoutTemplate(GLBrick brick,
			DimensionGroup dimensionGroup, GLVisBricks visBricks,
			IBrickConfigurer configurer) {
		super(brick, dimensionGroup);
		this.visBricks = visBricks;
		leftRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
				visBricks, true);
		rightRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
				visBricks, false);
		lockResizingButton = new Button(
				EPickingType.BRICK_LOCK_RESIZING_BUTTON,
				LOCK_RESIZING_BUTTON_ID, EIconTextures.PIN);
		viewSwitchingModeButton = new Button(
				EPickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON,
				VIEW_SWITCHING_MODE_BUTTON_ID, EIconTextures.LOCK);
		viewSwitchingModeButton.setSelected(dimensionGroup
				.isGlobalViewSwitching());

		configurer.configure(this);
		registerPickingListeners();
		viewTypeChanged(getDefaultViewType());

	}

	@Override
	public void setLockResizing(boolean lockResizing) {
		lockResizingButton.setSelected(lockResizing);
	}

	public void setLeftRelationIndicatorRenderer(
			RelationIndicatorRenderer leftRelationIndicatorRenderer) {
		this.leftRelationIndicatorRenderer = leftRelationIndicatorRenderer;
	}

	public void setRightRelationIndicatorRenderer(
			RelationIndicatorRenderer rightRelationIndicatorRenderer) {
		this.rightRelationIndicatorRenderer = rightRelationIndicatorRenderer;
	}

	@Override
	public void setStaticLayouts() {
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		setBaseElementLayout(baseRow);

		leftRelationIndicatorRenderer.updateRelations();
		rightRelationIndicatorRenderer.updateRelations();

		ElementLayout leftRelationIndicatorLayout = new ElementLayout(
				"RightRelationIndicatorLayout");
		// rightRelationIndicatorLayout.setDebug(true);
		leftRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		leftRelationIndicatorLayout
				.setPixelSizeX(RELATION_INDICATOR_WIDTH_PIXELS);
		leftRelationIndicatorLayout.setRenderer(leftRelationIndicatorRenderer);
		baseRow.append(leftRelationIndicatorLayout);

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 0);

		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		fuelBarLayout.setFrameColor(0, 1, 0, 0);

		baseRow.setRenderer(new BorderedAreaRenderer());

		if (showHandles) {
			baseRow.addForeGroundRenderer(new HandleRenderer(brick,
					pixelGLConverter, HANDLE_SIZE_PIXELS, brick
							.getTextureManager(),
					HandleRenderer.ALL_RESIZE_HANDLES));
		}

		fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		fuelBarLayout.setPixelSizeY(SPACING_PIXELS);
		fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer(brick));
		viewLayout.setRenderer(viewRenderer);

		Row toolBar = createBrickToolBar(TOOLBAR_HEIGHT_PIXELS);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		// baseColumn.appendElement(dimensionBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(fuelBarLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(viewLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(toolBar);
		baseColumn.append(spacingLayoutY);

		ElementLayout rightRelationIndicatorLayout = new ElementLayout(
				"RightRelationIndicatorLayout");
		// rightRelationIndicatorLayout.setDebug(true);
		rightRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		rightRelationIndicatorLayout
				.setPixelSizeX(RELATION_INDICATOR_WIDTH_PIXELS);
		rightRelationIndicatorLayout
				.setRenderer(rightRelationIndicatorRenderer);
		baseRow.append(rightRelationIndicatorLayout);

	}

	/**
	 * Creates the toolbar containing buttons for view switching.
	 * 
	 * @param pixelHeight
	 * @return
	 */
	protected Row createBrickToolBar(int pixelHeight) {
		Row toolBar = new Row("ToolBarRow");
		toolBar.setYDynamic(true);

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setPixelSizeY(0);

		for (int i = 0; i < viewSwitchingButtons.size(); i++) {
			BrickViewSwitchingButton button = viewSwitchingButtons.get(i);
			ElementLayout buttonLayout = new ElementLayout();
			buttonLayout.setPixelGLConverter(pixelGLConverter);
			buttonLayout.setPixelSizeX(pixelHeight);
			buttonLayout.setPixelSizeY(pixelHeight);
			buttonLayout.setRenderer(new ButtonRenderer(button, brick, brick
					.getTextureManager()));
			toolBar.append(buttonLayout);
			if (i != viewSwitchingButtons.size() - 1) {
				toolBar.append(spacingLayoutX);
			}
		}

		ElementLayout ratioSpacingLayoutX = new ElementLayout(
				"ratioSpacingLayoutX");
		ratioSpacingLayoutX.setRatioSizeX(1);
		ratioSpacingLayoutX.setRatioSizeY(0);

		ElementLayout lockResizingButtonLayout = new ElementLayout(
				"lockResizingButton");
		lockResizingButtonLayout.setPixelGLConverter(pixelGLConverter);
		lockResizingButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		lockResizingButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		lockResizingButtonLayout.setRenderer(new ButtonRenderer(
				lockResizingButton, brick, brick.getTextureManager()));

		ElementLayout toggleViewSwitchingButtonLayout = new ElementLayout(
				"viewSwitchtingButtonLayout");
		toggleViewSwitchingButtonLayout.setPixelGLConverter(pixelGLConverter);
		toggleViewSwitchingButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		toggleViewSwitchingButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		toggleViewSwitchingButtonLayout.setRenderer(new ButtonRenderer(
				viewSwitchingModeButton, brick, brick.getTextureManager()));

		ElementLayout closeButtonLayout = new ElementLayout(
				"closeButtonLayout");
		closeButtonLayout.setFrameColor(1, 0, 0, 1);
		// expandButtonLayout.setDebug(true);
		closeButtonLayout.setPixelGLConverter(pixelGLConverter);
		closeButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		closeButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		closeButtonLayout.setRenderer(new ButtonRenderer(new Button(
				EPickingType.BRICK_CLOSE_BUTTON, CLOSE_BUTTON_ID,
				EIconTextures.REMOVE), brick, brick.getTextureManager()));

		toolBar.append(ratioSpacingLayoutX);
		toolBar.append(lockResizingButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(toggleViewSwitchingButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(closeButtonLayout);

		return toolBar;
	}

	@Override
	protected void registerPickingListeners() {

		for (final BrickViewSwitchingButton button : viewSwitchingButtons) {

			brick.addPickingListener(new APickingListener() {

				@Override
				public void clicked(Pick pick) {
					for (BrickViewSwitchingButton button : viewSwitchingButtons) {
						button.setSelected(false);
					}
					button.setSelected(true);
					if (viewSwitchingModeButton.isSelected()) {
						dimensionGroup.switchBrickViews(button.getViewType());
					} else {
						brick.setContainedView(button.getViewType());
					}
					dimensionGroup.updateLayout();
				}
			}, button.getPickingType(), button.getButtonID());
		}

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				boolean isResizingLocked = !lockResizingButton.isSelected();
				brick.setSizeFixed(isResizingLocked);
				lockResizingButton.setSelected(isResizingLocked);
			}
		}, EPickingType.BRICK_LOCK_RESIZING_BUTTON, LOCK_RESIZING_BUTTON_ID);

		brick.addPickingListener(
				new APickingListener() {

					@Override
					public void clicked(Pick pick) {
						boolean isGlobalViewSwitching = !viewSwitchingModeButton
								.isSelected();
						dimensionGroup
								.setGlobalViewSwitching(isGlobalViewSwitching);
						viewSwitchingModeButton
								.setSelected(isGlobalViewSwitching);
					}
				}, EPickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON,
				VIEW_SWITCHING_MODE_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dimensionGroup.hideDetailedBrick();
			}
		}, EPickingType.BRICK_CLOSE_BUTTON, CLOSE_BUTTON_ID);
	}

	@Override
	public int getMinHeightPixels() {
		return dimensionGroup.getDetailBrickHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		return dimensionGroup.getDetailBrickWidthPixels();
	}

	@Override
	public void viewTypeChanged(EContainedViewType viewType) {

		for (BrickViewSwitchingButton button : viewSwitchingButtons) {
			if (viewType == button.getViewType()) {
				button.setSelected(true);
			} else {
				button.setSelected(false);
			}
		}

	}

	@Override
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
		viewSwitchingModeButton.setSelected(isGlobalViewSwitching);
	}

	public void setViewSwitchingButtons(
			ArrayList<BrickViewSwitchingButton> buttons) {
		viewSwitchingButtons = buttons;
	}

	@Override
	public ABrickLayoutTemplate getCollapsedLayoutTemplate() {
		return this;
	}

	@Override
	public ABrickLayoutTemplate getExpandedLayoutTemplate() {
		return this;
	}

}
