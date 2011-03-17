package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.picking.APickingListener;
import org.caleydo.view.visbricks.brick.ui.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.ui.BorderedAreaRenderer;
import org.caleydo.view.visbricks.brick.ui.Button;
import org.caleydo.view.visbricks.brick.ui.ButtonRenderer;
import org.caleydo.view.visbricks.brick.ui.FuelBarRenderer;
import org.caleydo.view.visbricks.brick.ui.HandleRenderer;
import org.caleydo.view.visbricks.brick.ui.RelationIndicatorRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

/**
 * Default brick layout containing a toolbar, a view and a fuelbar.
 * 
 * @author Christian Partl
 * 
 */
public class DefaultBrickLayoutTemplate extends ABrickLayoutTemplate {

	protected static final int FUEL_BAR_HEIGHT_PIXELS = 4;
	protected static final int TOOLBAR_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int RELATION_INDICATOR_WIDTH_PIXELS = 3;
	protected static final int HANDLE_SIZE_PIXELS = 10;

	private static final int COLLAPSE_BUTTON_ID = 0;
	protected static final int HEATMAP_BUTTON_ID = 1;
	protected static final int PARCOORDS_BUTTON_ID = 2;
	protected static final int HISTOGRAM_BUTTON_ID = 3;
	protected static final int OVERVIEW_HEATMAP_BUTTON_ID = 4;
	protected static final int VIEW_SWITCHING_MODE_BUTTON_ID = 5;

	protected Button heatMapButton;
	protected Button parCoordsButton;
	protected Button histogramButton;
	protected Button overviewHeatMapButton;
	protected Button viewSwitchingModeButton;

	private GLVisBricks visBricks;
	private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	public DefaultBrickLayoutTemplate(GLBrick brick, GLVisBricks visBricks,
			DimensionGroup dimensionGroup) {
		super(brick, dimensionGroup);
		this.visBricks = visBricks;
		leftRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
				visBricks, true);
		rightRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
				visBricks, false);

		heatMapButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				HEATMAP_BUTTON_ID);
		parCoordsButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				PARCOORDS_BUTTON_ID);
		histogramButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				HISTOGRAM_BUTTON_ID);
		overviewHeatMapButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				OVERVIEW_HEATMAP_BUTTON_ID);
		viewSwitchingModeButton = new Button(
				EPickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON,
				VIEW_SWITCHING_MODE_BUTTON_ID);
		viewSwitchingModeButton.setSelected(dimensionGroup
				.isGlobalViewSwitching());

		viewTypeChanged(getDefaultViewType());
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
			baseRow.addForeGroundRenderer(new HandleRenderer(brick
					.getDimensionGroup(), pixelGLConverter, HANDLE_SIZE_PIXELS,
					brick.getTextureManager()));
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
		spacingLayoutX.setPixelSizeX(4);
		spacingLayoutX.setPixelSizeY(0);

		ElementLayout heatMapButtonLayout = new ElementLayout("heatMapButton");
		heatMapButtonLayout.setPixelGLConverter(pixelGLConverter);
		heatMapButtonLayout.setPixelSizeX(pixelHeight);
		heatMapButtonLayout.setPixelSizeY(pixelHeight);
		heatMapButtonLayout.setRenderer(new ButtonRenderer(heatMapButton,
				brick, EIconTextures.HEAT_MAP_ICON, brick.getTextureManager()));

		ElementLayout parCoordsButtonLayout = new ElementLayout("parCoords");
		parCoordsButtonLayout.setPixelGLConverter(pixelGLConverter);
		parCoordsButtonLayout.setPixelSizeX(pixelHeight);
		parCoordsButtonLayout.setPixelSizeY(pixelHeight);
		parCoordsButtonLayout
				.setRenderer(new ButtonRenderer(parCoordsButton, brick,
						EIconTextures.PAR_COORDS_ICON, brick
								.getTextureManager()));

		ElementLayout histogramButtonLayout = new ElementLayout(
				"histogramButton");
		histogramButtonLayout.setPixelGLConverter(pixelGLConverter);
		histogramButtonLayout.setPixelSizeX(pixelHeight);
		histogramButtonLayout.setPixelSizeY(pixelHeight);
		histogramButtonLayout
				.setRenderer(new ButtonRenderer(histogramButton, brick,
						EIconTextures.HISTOGRAM_ICON, brick.getTextureManager()));

		ElementLayout overviewHeatMapButtonLayout = new ElementLayout(
				"overviewHeatMapButton");
		overviewHeatMapButtonLayout.setPixelGLConverter(pixelGLConverter);
		overviewHeatMapButtonLayout.setPixelSizeX(pixelHeight);
		overviewHeatMapButtonLayout.setPixelSizeY(pixelHeight);
		overviewHeatMapButtonLayout.setRenderer(new ButtonRenderer(
				overviewHeatMapButton, brick, EIconTextures.HEAT_MAP_ICON,
				brick.getTextureManager()));

		ElementLayout ratioSpacingLayoutX = new ElementLayout(
				"ratioSpacingLayoutX");
		ratioSpacingLayoutX.setRatioSizeX(1);
		ratioSpacingLayoutX.setRatioSizeY(0);

		ElementLayout toggleViewSwitchingButtonLayout = new ElementLayout(
				"clusterButton");
		toggleViewSwitchingButtonLayout.setPixelGLConverter(pixelGLConverter);
		toggleViewSwitchingButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		toggleViewSwitchingButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		toggleViewSwitchingButtonLayout.setRenderer(new ButtonRenderer(
				viewSwitchingModeButton, brick, EIconTextures.LOCK, brick
						.getTextureManager()));

		ElementLayout collapseButtonLayout = new ElementLayout(
				"expandButtonLayout");
		collapseButtonLayout.setFrameColor(1, 0, 0, 1);
		// expandButtonLayout.setDebug(true);
		collapseButtonLayout.setPixelGLConverter(pixelGLConverter);
		collapseButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		collapseButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		collapseButtonLayout.setRenderer(new ButtonRenderer(new Button(
				EPickingType.BRICK_COLLAPSE_BUTTON, COLLAPSE_BUTTON_ID), brick,
				EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE, brick
						.getTextureManager(),
				ButtonRenderer.TEXTURE_ROTATION_90));

		toolBar.append(heatMapButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(parCoordsButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(histogramButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(overviewHeatMapButtonLayout);
		toolBar.append(ratioSpacingLayoutX);
		toolBar.append(toggleViewSwitchingButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(collapseButtonLayout);

		return toolBar;
	}

	protected void registerPickingListeners() {
		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				heatMapButton.setSelected(true);
				parCoordsButton.setSelected(false);
				histogramButton.setSelected(false);
				overviewHeatMapButton.setSelected(false);
				if (viewSwitchingModeButton.isSelected()) {
					dimensionGroup
							.switchBrickViews(EContainedViewType.HEATMAP_VIEW);
				} else {
					brick.setRemoteView(EContainedViewType.HEATMAP_VIEW);
				}
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HEATMAP_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				heatMapButton.setSelected(false);
				parCoordsButton.setSelected(true);
				histogramButton.setSelected(false);
				overviewHeatMapButton.setSelected(false);
				if (viewSwitchingModeButton.isSelected()) {
					dimensionGroup
							.switchBrickViews(EContainedViewType.PARCOORDS_VIEW);
				} else {
					brick.setRemoteView(EContainedViewType.PARCOORDS_VIEW);
				}
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, PARCOORDS_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				heatMapButton.setSelected(false);
				parCoordsButton.setSelected(false);
				histogramButton.setSelected(true);
				overviewHeatMapButton.setSelected(false);
				if (viewSwitchingModeButton.isSelected()) {
					dimensionGroup
							.switchBrickViews(EContainedViewType.HISTOGRAM_VIEW);
				} else {
					brick.setRemoteView(EContainedViewType.HISTOGRAM_VIEW);
				}
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HISTOGRAM_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				heatMapButton.setSelected(false);
				parCoordsButton.setSelected(false);
				histogramButton.setSelected(false);
				overviewHeatMapButton.setSelected(true);
				if (viewSwitchingModeButton.isSelected()) {
					dimensionGroup
							.switchBrickViews(EContainedViewType.OVERVIEW_HEATMAP);
				} else {
					brick.setRemoteView(EContainedViewType.OVERVIEW_HEATMAP);
				}
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, OVERVIEW_HEATMAP_BUTTON_ID);

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
				brick.setToOverviewMode();
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_COLLAPSE_BUTTON, COLLAPSE_BUTTON_ID);
	}

	@Override
	public int getMinHeightPixels() {
		return 4 * SPACING_PIXELS + FUEL_BAR_HEIGHT_PIXELS
				+ TOOLBAR_HEIGHT_PIXELS + viewRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		// TODO: implement
		return 0;
	}

	@Override
	protected void setValidViewTypes() {
		validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
		validViewTypes.add(EContainedViewType.HEATMAP_VIEW);
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);
	}

	@Override
	public EContainedViewType getDefaultViewType() {
		return EContainedViewType.HEATMAP_VIEW;
	}

	@Override
	public void viewTypeChanged(EContainedViewType viewType) {
		heatMapButton.setSelected(false);
		parCoordsButton.setSelected(false);
		histogramButton.setSelected(false);
		overviewHeatMapButton.setSelected(false);

		switch (viewType) {
		case HEATMAP_VIEW:
			heatMapButton.setSelected(true);
			break;
		case PARCOORDS_VIEW:
			parCoordsButton.setSelected(true);
			break;
		case HISTOGRAM_VIEW:
			histogramButton.setSelected(true);
			break;
		case OVERVIEW_HEATMAP:
			overviewHeatMapButton.setSelected(true);
			break;
		}

	}

	@Override
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
		viewSwitchingModeButton.setSelected(isGlobalViewSwitching);
	}
}
