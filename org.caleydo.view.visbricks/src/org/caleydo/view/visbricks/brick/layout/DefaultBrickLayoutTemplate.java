package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.Zoomer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;
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

	protected static final int FOOTER_BAR_HEIGHT_PIXELS = 4;
	protected static final int TOOLBAR_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int RELATION_INDICATOR_WIDTH_PIXELS = 3;
	protected static final int HANDLE_SIZE_PIXELS = 8;

	protected static final int COLLAPSE_BUTTON_ID = 0;
	protected static final int LOCK_RESIZING_BUTTON_ID = 1;
	protected static final int DETAIL_MODE_BUTTON_ID = 1;
	protected static final int VIEW_SWITCHING_MODE_BUTTON_ID = 5;

	// protected ArrayList<BrickViewSwitchingButton> viewSwitchingButtons;
	protected ArrayList<ElementLayout> toolBarElements;
	protected ArrayList<ElementLayout> footerBarElements;
	protected Row toolBar;
	protected Row footerBar;

	// protected Button heatMapButton;
	// protected Button parCoordsButton;
	// protected Button histogramButton;
	// protected Button overviewHeatMapButton;
	protected Button viewSwitchingModeButton;
	protected Button lockResizingButton;
	protected boolean showFooterBar;

	protected GLVisBricks visBricks;
	protected RelationIndicatorRenderer leftRelationIndicatorRenderer;
	protected RelationIndicatorRenderer rightRelationIndicatorRenderer;

	public DefaultBrickLayoutTemplate(GLBrick brick, GLVisBricks visBricks,
			DimensionGroup dimensionGroup, IBrickConfigurer configurer) {
		super(brick, dimensionGroup);
		this.visBricks = visBricks;
		toolBarElements = new ArrayList<ElementLayout>();
		footerBarElements = new ArrayList<ElementLayout>();
		toolBar = new Row();
		footerBar = new Row();
		leftRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
				visBricks, true);
		rightRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
				visBricks, false);

		lockResizingButton = new Button(
				PickingType.BRICK_LOCK_RESIZING_BUTTON,
				LOCK_RESIZING_BUTTON_ID, EIconTextures.PIN);
		viewSwitchingModeButton = new Button(
				PickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON,
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

		baseRow.setRenderer(borderedAreaRenderer);

		if (showHandles) {
			baseRow.addForeGroundRenderer(new HandleRenderer(brick,
					pixelGLConverter, HANDLE_SIZE_PIXELS, brick
							.getTextureManager(),
					HandleRenderer.ALL_RESIZE_HANDLES
							| HandleRenderer.MOVE_VERTICALLY_HANDLE
							| (dimensionGroup.isLeftmost() ? (0)
									: (HandleRenderer.EXPAND_LEFT_HANDLE))
							| (dimensionGroup.isRightmost() ? (0)
									: (HandleRenderer.EXPAND_RIGHT_HANDLE))));
		}

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		if (viewLayout == null) {
			viewLayout = new ElementLayout("viewLayout");
			viewLayout.setFrameColor(1, 0, 0, 1);
			viewLayout.addBackgroundRenderer(new ColorRenderer(new float[] { 1,
					1, 1, 1 }));
			Zoomer zoomer = new Zoomer(visBricks, viewLayout);
			viewLayout.setZoomer(zoomer);
		}
		viewLayout.setRenderer(viewRenderer);

		toolBar = createToolBar();
		footerBar = createFooterBar();

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		// baseColumn.appendElement(dimensionBarLayout);
		baseColumn.append(spacingLayoutY);
		if (showFooterBar) {
			baseColumn.append(footerBar);
			baseColumn.append(spacingLayoutY);
		}
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

	protected Row createToolBar() {
		Row toolBar = new Row("ToolBarRow");
		toolBar.setPixelGLConverter(pixelGLConverter);
		toolBar.setPixelSizeY(TOOLBAR_HEIGHT_PIXELS);

		for (ElementLayout element : toolBarElements) {
			toolBar.append(element);
		}

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setPixelSizeY(0);

		// for (int i = 0; i < viewSwitchingButtons.size(); i++) {
		// BrickViewSwitchingButton button = viewSwitchingButtons.get(i);
		// ElementLayout buttonLayout = new ElementLayout();
		// buttonLayout.setPixelGLConverter(pixelGLConverter);
		// buttonLayout.setPixelSizeX(pixelHeight);
		// buttonLayout.setPixelSizeY(pixelHeight);
		// buttonLayout.setRenderer(new ButtonRenderer(button, brick, brick
		// .getTextureManager()));
		// toolBar.append(buttonLayout);
		// if (i != viewSwitchingButtons.size() - 1) {
		// toolBar.append(spacingLayoutX);
		// }
		// }

		// ElementLayout detailModeButtonLayout = new ElementLayout(
		// "detailModeResizingButton");
		// detailModeButtonLayout.setPixelGLConverter(pixelGLConverter);
		// detailModeButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		// detailModeButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		// detailModeButtonLayout.setRenderer(new ButtonRenderer(new Button(
		// EPickingType.BRICK_DETAIL_MODE_BUTTON, DETAIL_MODE_BUTTON_ID,
		// EIconTextures.PIN), brick, brick.getTextureManager()));

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

		ElementLayout collapseButtonLayout = new ElementLayout(
				"expandButtonLayout");
		collapseButtonLayout.setFrameColor(1, 0, 0, 1);
		// expandButtonLayout.setDebug(true);
		collapseButtonLayout.setPixelGLConverter(pixelGLConverter);
		collapseButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		collapseButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		collapseButtonLayout.setRenderer(new ButtonRenderer(new Button(
				PickingType.BRICK_COLLAPSE_BUTTON, COLLAPSE_BUTTON_ID,
				EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE), brick, brick
				.getTextureManager(), ButtonRenderer.TEXTURE_ROTATION_90));

		// toolBar.append(ratioSpacingLayoutX);
		// toolBar.append(detailModeButtonLayout);
		// toolBar.append(spacingLayoutX);
		toolBar.append(lockResizingButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(toggleViewSwitchingButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(collapseButtonLayout);

		return toolBar;
	}

	protected Row createFooterBar() {
		Row footerBar = new Row("footerBar");
		footerBar.setPixelGLConverter(pixelGLConverter);
		footerBar.setPixelSizeY(FOOTER_BAR_HEIGHT_PIXELS);

		for (ElementLayout element : footerBarElements) {
			footerBar.append(element);
		}

		return footerBar;
	}

	@Override
	protected void registerPickingListeners() {

		brick.addSingleIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				boolean isResizingLocked = !lockResizingButton.isSelected();
				brick.setSizeFixed(isResizingLocked);
				lockResizingButton.setSelected(isResizingLocked);
			}
		}, PickingType.BRICK_LOCK_RESIZING_BUTTON.name(),
				LOCK_RESIZING_BUTTON_ID);

		brick.addSingleIDPickingListener(
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
				}, PickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON.name(),
				VIEW_SWITCHING_MODE_BUTTON_ID);

		brick.addSingleIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.collapse();
				dimensionGroup.updateLayout();
			}
		}, PickingType.BRICK_COLLAPSE_BUTTON.name(), COLLAPSE_BUTTON_ID);

		brick.addSingleIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dimensionGroup.showDetailedBrick(brick, false);
			}
		}, PickingType.EXPAND_RIGHT_HANDLE.name(), brick.getID());

		brick.addSingleIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dimensionGroup.showDetailedBrick(brick, true);
			}
		}, PickingType.EXPAND_LEFT_HANDLE.name(), brick.getID());
	}

	@Override
	public int getMinHeightPixels() {
		if (viewRenderer == null)
			return 4 * SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS
					+ TOOLBAR_HEIGHT_PIXELS;
		return 4 * SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS
				+ TOOLBAR_HEIGHT_PIXELS + viewRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		int toolBarWidth = calcSumPixelWidth(toolBar.getElements());
		int footerBarWidth = showFooterBar ? calcSumPixelWidth(footerBar
				.getElements()) : 0;

		int guiElementsWidth = Math.max(toolBarWidth, footerBarWidth) + 2
				* SPACING_PIXELS;
		if (viewRenderer == null)
			return guiElementsWidth;
		return Math.max(guiElementsWidth,
				(2 * SPACING_PIXELS) + viewRenderer.getMinWidthPixels());
		// return pixelGLConverter.getPixelWidthForGLWidth(dimensionGroup
		// .getMinWidth());
	}

	// @Override
	// protected void setValidViewTypes() {
	// validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
	// validViewTypes.add(EContainedViewType.HEATMAP_VIEW);
	// validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
	// validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);
	// }

	// @Override
	// public EContainedViewType getDefaultViewType() {
	// return EContainedViewType.HEATMAP_VIEW;
	// }

	// @Override
	// public void viewTypeChanged(EContainedViewType viewType) {
	//
	// for (BrickViewSwitchingButton button : viewSwitchingButtons) {
	// if (viewType == button.getViewType()) {
	// button.setSelected(true);
	// } else {
	// button.setSelected(false);
	// }
	// }
	//
	// }

	@Override
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
		viewSwitchingModeButton.setSelected(isGlobalViewSwitching);
	}

	// public void setViewSwitchingButtons(
	// ArrayList<BrickViewSwitchingButton> buttons) {
	// viewSwitchingButtons = buttons;
	// }

	@Override
	public ABrickLayoutTemplate getCollapsedLayoutTemplate() {
		return new CompactBrickLayoutTemplate(brick, visBricks, dimensionGroup,
				brick.getBrickConfigurer());
	}

	@Override
	public ABrickLayoutTemplate getExpandedLayoutTemplate() {
		return this;
	}

	public void setToolBarElements(ArrayList<ElementLayout> toolBarElements) {
		this.toolBarElements = toolBarElements;
	}

	public void setFooterBarElements(ArrayList<ElementLayout> footerBarElements) {
		this.footerBarElements = footerBarElements;
	}

	public void showFooterBar(boolean showFooterBar) {
		this.showFooterBar = showFooterBar;
	}

	@Override
	public void destroy() {
		super.destroy();
		brick.removeSingleIDPickingListeners(
				PickingType.BRICK_LOCK_RESIZING_BUTTON.name(),
				LOCK_RESIZING_BUTTON_ID);

		brick.removeSingleIDPickingListeners(
				PickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON.name(),
				VIEW_SWITCHING_MODE_BUTTON_ID);

		brick.removeSingleIDPickingListeners(
				PickingType.BRICK_COLLAPSE_BUTTON.name(), COLLAPSE_BUTTON_ID);

		brick.removeSingleIDPickingListeners(
				PickingType.EXPAND_RIGHT_HANDLE.name(), brick.getID());

		brick.removeSingleIDPickingListeners(
				PickingType.EXPAND_LEFT_HANDLE.name(), brick.getID());
	}
}
