package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.Zoomer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.PickingType;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.configurer.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.ui.HandleRenderer;
import org.caleydo.view.visbricks.brick.ui.RelationIndicatorRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

/**
 * Default brick layout containing a toolbar, a view and a fuelbar.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * 
 */
public class DefaultBrickLayoutTemplate extends ABrickLayoutConfiguration {

	protected static final int FOOTER_BAR_HEIGHT_PIXELS = 4;
	protected static final int TOOLBAR_HEIGHT_PIXELS = 16;
	public static final int BUTTON_HEIGHT_PIXELS = 16;
	public static final int BUTTON_WIDTH_PIXELS = 16;
	public static final float BUTTON_Z = 0.22f;
	protected static final int RELATION_INDICATOR_WIDTH_PIXELS = 3;
	protected static final int HANDLE_SIZE_PIXELS = 8;

	protected static final int COLLAPSE_BUTTON_ID = 0;
	protected static final int LOCK_RESIZING_BUTTON_ID = 1;
	protected static final int DETAIL_MODE_BUTTON_ID = 1;
	protected static final int VIEW_SWITCHING_MODE_BUTTON_ID = 5;

	// protected ArrayList<BrickViewSwitchingButton> viewSwitchingButtons;
	private Row headerRow;
	protected ArrayList<ElementLayout> headerBarElements;
	protected ArrayList<ElementLayout> toolBarElements;
	protected ArrayList<ElementLayout> footerBarElements;
	protected ToolBar toolBar;
	protected Row footerBar;

	protected Button viewSwitchingModeButton;
	protected Button lockResizingButton;

	/**
	 * Flag telling whether the footer bar should be shown or not. This should
	 * not be used for dynamically hiding the bar, but as a static flag set by a
	 * {@link IBrickConfigurer}
	 */
	protected boolean showToolBar = true;

	/** Same as {@link #showToolBar} for footer bar */
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

		if (!brick.isHeaderBrick()) {
			leftRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
					visBricks, true);
			rightRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
					visBricks, false);

			brick.setRightRelationIndicatorRenderer(rightRelationIndicatorRenderer);
			brick.setLeftRelationIndicatorRenderer(leftRelationIndicatorRenderer);
		} else {
			System.out.println("Shouldn't happen");
		}

		lockResizingButton = new Button(PickingType.BRICK_LOCK_RESIZING_BUTTON.name(),
				LOCK_RESIZING_BUTTON_ID, EIconTextures.PIN);

		viewSwitchingModeButton = new Button(
				PickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON.name(),
				VIEW_SWITCHING_MODE_BUTTON_ID, EIconTextures.LOCK);
		viewSwitchingModeButton.setSelected(dimensionGroup.isGlobalViewSwitching());

		configurer.configure(this);
		registerPickingListeners();
		viewTypeChanged(getDefaultViewType());
	}

	@Override
	public void setLockResizing(boolean lockResizing) {
		lockResizingButton.setSelected(lockResizing);
	}

	@Override
	public void setStaticLayouts() {
		Column baseColumn = new Column("baseCol");
		baseColumn.setBottomUp(false);
		baseElementLayout = baseColumn;
		Row baseRow = new Row("baseRow");

		baseColumn.append(baseRow);
		if (showToolBar) {
			toolBar = createToolBar();
			baseColumn.append(toolBar);
		}

		if (!brick.isHeaderBrick()) {
			leftRelationIndicatorRenderer.updateRelations();
			rightRelationIndicatorRenderer.updateRelations();

			ElementLayout leftRelationIndicatorLayout = new ElementLayout(
					"RightRelationIndicatorLayout");
			// rightRelationIndicatorLayout.setDebug(true);
			leftRelationIndicatorLayout.setPixelSizeX(RELATION_INDICATOR_WIDTH_PIXELS);
			leftRelationIndicatorLayout.setRenderer(leftRelationIndicatorRenderer);
			baseRow.append(leftRelationIndicatorLayout);
		} else {
			System.out.println("Shouldn't happen");
		}
		Column contentColumn = new Column("contentColumn");
		contentColumn.setPriorityRendereing(true);
		contentColumn.setFrameColor(0, 1, 0, 0);

		baseRow.setRenderer(borderedAreaRenderer);

		baseRow.addForeGroundRenderer(new HandleRenderer(brick, HANDLE_SIZE_PIXELS, brick
				.getTextureManager(), HandleRenderer.ALL_RESIZE_HANDLES
				| HandleRenderer.MOVE_VERTICALLY_HANDLE | HandleRenderer.EXPAND_HANDLE));

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(contentColumn);
		baseRow.append(spacingLayoutX);

		if (viewLayout == null) {
			viewLayout = new ElementLayout("viewLayout");
			viewLayout.setFrameColor(1, 0, 0, 1);
			viewLayout
					.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
			Zoomer zoomer = new Zoomer(visBricks, viewLayout);
			viewLayout.setZoomer(zoomer);
		}
		viewLayout.setRenderer(viewRenderer);

		footerBar = createFooterBar();

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		contentColumn.append(spacingLayoutY);
		if (showFooterBar) {
			contentColumn.append(footerBar);
			contentColumn.append(spacingLayoutY);
		}
		contentColumn.append(viewLayout);
		contentColumn.append(spacingLayoutY);

		headerRow = new Row("headerRow");
		if (brick.isDefaultLabel())
			headerRow.setHidden(true);
		headerRow.setYDynamic(true);

		if (headerBarElements != null) {
			for (ElementLayout layout : headerBarElements) {
				headerRow.append(layout);
			}
		}
		contentColumn.append(headerRow);

		// baseColumn.append(spacingLayoutY);
		if (!brick.isHeaderBrick()) {
			ElementLayout rightRelationIndicatorLayout = new ElementLayout(
					"RightRelationIndicatorLayout");
			// rightRelationIndicatorLayout.setDebug(true);
			rightRelationIndicatorLayout.setPixelSizeX(RELATION_INDICATOR_WIDTH_PIXELS);
			rightRelationIndicatorLayout.setRenderer(rightRelationIndicatorRenderer);
			baseRow.append(rightRelationIndicatorLayout);
		}
	}

	/**
	 * Creates the toolbar containing buttons for view switching.
	 */

	protected ToolBar createToolBar() {

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setPixelSizeY(0);

		// ElementLayout lockResizingButtonLayout = new
		// ElementLayout("lockResizingButton");
		// lockResizingButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		// lockResizingButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		// lockResizingButtonLayout.setRenderer(new
		// ButtonRenderer(lockResizingButton,
		// brick, brick.getTextureManager(), BUTTON_Z));

		ElementLayout toggleViewSwitchingButtonLayout = new ElementLayout(
				"viewSwitchtingButtonLayout");
		toggleViewSwitchingButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		toggleViewSwitchingButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		toggleViewSwitchingButtonLayout.setRenderer(new ButtonRenderer(
				viewSwitchingModeButton, brick, brick.getTextureManager(), BUTTON_Z));

		ElementLayout collapseButtonLayout = new ElementLayout("expandButtonLayout");
		collapseButtonLayout.setFrameColor(1, 0, 0, 1);
		// expandButtonLayout.setDebug(true);
		collapseButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		collapseButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		collapseButtonLayout.setRenderer(new ButtonRenderer(new Button(
				PickingType.BRICK_COLLAPSE_BUTTON.name(), COLLAPSE_BUTTON_ID,
				EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE), brick, brick
				.getTextureManager(), ButtonRenderer.TEXTURE_ROTATION_90, BUTTON_Z));

		ToolBar toolBar = new ToolBar("ToolBarRow", brick);
		// toolBar.setDebug(true);
		// toolBar.setPixelSizeY(TOOLBAR_HEIGHT_PIXELS);
		toolBar.setPixelSizeY(0);
		toolBar.setRenderingPriority(2);

		for (ElementLayout element : toolBarElements) {
			toolBar.append(element);
		}
		// toolBar.append(lockResizingButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(toggleViewSwitchingButtonLayout);
		toolBar.append(spacingLayoutX);
		toolBar.append(collapseButtonLayout);

		return toolBar;
	}

	protected Row createFooterBar() {
		Row footerBar = new Row("footerBar");
		footerBar.setPixelSizeY(FOOTER_BAR_HEIGHT_PIXELS);

		for (ElementLayout element : footerBarElements) {
			footerBar.append(element);
		}

		return footerBar;
	}

	@Override
	protected void registerPickingListeners() {
		System.out.println("Register " + brick.getID());
		brick.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				// boolean isResizingLocked = !lockResizingButton.isSelected();
				//
				// brick.setBrickHeigthMode(EBrickHeightMode.STATIC);
				// brick.setBrickWidthMode(EBrickWidthMode.STATIC);
				// lockResizingButton.setSelected(isResizingLocked);
			}
		}, PickingType.BRICK_LOCK_RESIZING_BUTTON.name(), LOCK_RESIZING_BUTTON_ID);

		brick.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				boolean isGlobalViewSwitching = !viewSwitchingModeButton.isSelected();
				dimensionGroup.setGlobalViewSwitching(isGlobalViewSwitching);
				viewSwitchingModeButton.setSelected(isGlobalViewSwitching);
			}
		}, PickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON.name(),
				VIEW_SWITCHING_MODE_BUTTON_ID);

		brick.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.collapse();
				dimensionGroup.updateLayout();
			}
		}, PickingType.BRICK_COLLAPSE_BUTTON.name(), COLLAPSE_BUTTON_ID);

		brick.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dimensionGroup.showDetailedBrick(brick, false);
			}
		}, PickingType.EXPAND_RIGHT_HANDLE.name(), brick.getID());

		brick.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dimensionGroup.showDetailedBrick(brick, true);
			}
		}, PickingType.EXPAND_LEFT_HANDLE.name(), brick.getID());
	}

	@Override
	public int getMinHeightPixels() {
		if (viewRenderer == null)
			return 4 * SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS + TOOLBAR_HEIGHT_PIXELS;
		return 4 * SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS + TOOLBAR_HEIGHT_PIXELS
				+ viewRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {

		int toolBarWidth = showToolBar ? calcSumPixelWidth(toolBar.getElements()) : 0;
		int footerBarWidth = showFooterBar ? calcSumPixelWidth(footerBar.getElements())
				: 0;

		int guiElementsWidth = Math.max(toolBarWidth, footerBarWidth) + 2
				* SPACING_PIXELS;
		if (viewRenderer == null)
			return guiElementsWidth;
		return Math.max(guiElementsWidth,
				(2 * SPACING_PIXELS) + viewRenderer.getMinWidthPixels());
		// return pixelGLConverter.getPixelWidthForGLWidth(dimensionGroup
		// .getMinWidth());
	}

	@Override
	public void setGlobalViewSwitching(boolean isGlobalViewSwitching) {
		viewSwitchingModeButton.setSelected(isGlobalViewSwitching);
	}

	@Override
	public ABrickLayoutConfiguration getCollapsedLayoutTemplate() {
		return new CollapsedBrickLayoutTemplate(brick, visBricks, dimensionGroup,
				brick.getBrickConfigurer());
	}

	@Override
	public ABrickLayoutConfiguration getExpandedLayoutTemplate() {
		return this;
	}

	/**
	 * @param showToolBar
	 *            setter, see {@link #showToolBar}
	 */
	public void setShowToolBar(boolean showToolBar) {
		this.showToolBar = showToolBar;
	}

	/**
	 * Sets the elements that should appear in the toolbar. The elements will
	 * placed from left to right using the order of the specified list.
	 * 
	 * @param toolBarElements
	 */
	public void setToolBarElements(ArrayList<ElementLayout> toolBarElements) {
		this.toolBarElements = toolBarElements;
	}

	/**
	 * Sets the elements that should appear in the footer bar. The elements will
	 * placed from left to right using the order of the specified list.
	 * 
	 * @param footerBarElements
	 */
	public void setFooterBarElements(ArrayList<ElementLayout> footerBarElements) {
		this.footerBarElements = footerBarElements;
	}

	/**
	 * @param showFooterBar
	 *            setter, see {@link #showFooterBar}
	 */
	public void setShowFooterBar(boolean showFooterBar) {
		this.showFooterBar = showFooterBar;
	}

	/**
	 * @return The elements displayed in the header bar.
	 */
	public ArrayList<ElementLayout> getHeaderBarElements() {
		return headerBarElements;
	}

	/**
	 * Sets the elements that should appear in the header bar. The elements will
	 * placed from left to right using the order of the specified list.
	 * 
	 * @param headerBarElements
	 */
	public void setHeaderBarElements(ArrayList<ElementLayout> headerBarElements) {
		this.headerBarElements = headerBarElements;
	}

	@Override
	public void destroy() {
		System.out.println("Destroy " + brick.getID());
		brick.removeAllIDPickingListeners(PickingType.BRICK_LOCK_RESIZING_BUTTON.name(),
				LOCK_RESIZING_BUTTON_ID);

		brick.removeAllIDPickingListeners(
				PickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON.name(),
				VIEW_SWITCHING_MODE_BUTTON_ID);

		brick.removeAllIDPickingListeners(PickingType.BRICK_COLLAPSE_BUTTON.name(),
				COLLAPSE_BUTTON_ID);

		brick.removeAllIDPickingListeners(PickingType.EXPAND_RIGHT_HANDLE.name(),
				brick.getID());

		brick.removeAllIDPickingListeners(PickingType.EXPAND_LEFT_HANDLE.name(),
				brick.getID());
		// toolBar.destroy();
		super.destroy();
	}

	/**
	 * Returns the default height of the brick depending on the type of the
	 * view. If the {@link EContainedViewType#isUseProportionalHeight()} is
	 * true, a proportional height is set. Otherwise the height of the view and
	 * the toolbars etc is added and returned.
	 */
	@Override
	public int getDefaultHeightPixels() {

		return getMinHeightPixels();

	}

	public void setHideCaption(boolean hideCaption) {
		headerRow.setHidden(hideCaption);
	}
}
