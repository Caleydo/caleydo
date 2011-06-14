package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.manager.picking.APickingListener;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.ui.BorderedAreaRenderer;
import org.caleydo.view.visbricks.brick.ui.HandleRenderer;
import org.caleydo.view.visbricks.brick.ui.LineSeparatorRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

/**
 * Brick layout for central brick in {@link DimensionGroup} conaining a caption
 * bar, toolbar and view.
 * 
 * @author Christian Partl
 * 
 */
public class CentralBrickLayoutTemplate extends ABrickLayoutTemplate {

	protected static final int TOOLBAR_HEIGHT_PIXELS = 16;
	protected static final int HEADER_BAR_HEIGHT_PIXELS = 16;
	protected static final int FOOTER_BAR_HEIGHT_PIXELS = 12;
	protected static final int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int HANDLE_SIZE_PIXELS = 8;

	protected static final int CLUSTER_BUTTON_ID = 0;
	protected static final int LOCK_RESIZING_BUTTON_ID = 1;

	// protected ArrayList<BrickViewSwitchingButton> viewSwitchingButtons;
	protected ArrayList<ElementLayout> headerBarElements;
	protected ArrayList<ElementLayout> toolBarElements;
	protected ArrayList<ElementLayout> footerBarElements;

	protected GLVisBricks visBricks;

	// protected Button heatMapButton;
	// protected Button parCoordsButton;
	// protected Button histogramButton;
	// protected Button overviewHeatMapButton;

	protected Button clusterButton;
	protected Button lockResizingButton;
	protected boolean showToolBar;
	protected boolean showFooterBar;

	protected int guiElementsHeight = 0;
	protected Row headerBar;
	protected Row toolBar;
	protected Row footerBar;

	public CentralBrickLayoutTemplate(GLBrick brick,
			DimensionGroup dimensionGroup, GLVisBricks visBricks,
			IBrickConfigurer configurer) {
		super(brick, dimensionGroup);
		// viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		this.visBricks = visBricks;
		clusterButton = new Button(EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON,
				CLUSTER_BUTTON_ID, EIconTextures.CLUSTER_ICON);

		lockResizingButton = new Button(
				EPickingType.BRICK_LOCK_RESIZING_BUTTON,
				LOCK_RESIZING_BUTTON_ID, EIconTextures.PIN);
		headerBarElements = new ArrayList<ElementLayout>();
		footerBarElements = new ArrayList<ElementLayout>();
		toolBarElements = new ArrayList<ElementLayout>();
		headerBar = new Row();
		toolBar = new Row();
		footerBar = new Row();
		configurer.configure(this);
		registerPickingListeners();
		viewTypeChanged(getDefaultViewType());

	}

	public void setLockResizing(boolean lockResizing) {
		lockResizingButton.setSelected(lockResizing);
	}

	@Override
	public void setStaticLayouts() {
		guiElementsHeight = 0;
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		setBaseElementLayout(baseRow);

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 0);

		baseRow.setRenderer(new BorderedAreaRenderer(brick));

		if (showHandles) {
			baseRow.addForeGroundRenderer(new HandleRenderer(brick,
					pixelGLConverter, HANDLE_SIZE_PIXELS, brick
							.getTextureManager(),
					HandleRenderer.ALL_MOVE_HANDLES
							| HandleRenderer.ALL_RESIZE_HANDLES));
		}

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		dimensionBarLayout.setFrameColor(1, 0, 1, 0);
		dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLayout.setPixelSizeY(FOOTER_BAR_HEIGHT_PIXELS);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer(brick));
		viewLayout.setRenderer(viewRenderer);

		Row headerBar = createHeaderBar();
		Row toolBar = createToolBar();
		Row footerBar = createFooterBar();

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		// captionRow.append(spacingLayoutX);

		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
		lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		// ElementLayout dimensionBarLaylout = new
		// ElementLayout("dimensionBar");
		// dimensionBarLaylout.setPixelGLConverter(pixelGLConverter);
		// dimensionBarLaylout.setPixelSizeY(FOOTER_BAR_HEIGHT_PIXELS);
		// dimensionBarLaylout.setRatioSizeX(1);
		// dimensionBarLaylout.setRenderer(new DimensionBarRenderer(brick));

		baseColumn.append(spacingLayoutY);
		guiElementsHeight += SPACING_PIXELS;
		if (showFooterBar) {
			baseColumn.append(footerBar);
			baseColumn.append(spacingLayoutY);
			guiElementsHeight += SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS;
		}
		baseColumn.append(viewLayout);
		if (showToolBar) {
			baseColumn.append(spacingLayoutY);
			baseColumn.append(toolBar);
			baseColumn.append(spacingLayoutY);
			baseColumn.append(lineSeparatorLayout);
			guiElementsHeight += (2 * SPACING_PIXELS) + TOOLBAR_HEIGHT_PIXELS
					+ LINE_SEPARATOR_HEIGHT_PIXELS;
		}
		baseColumn.append(spacingLayoutY);
		baseColumn.append(headerBar);
		baseColumn.append(spacingLayoutY);
		guiElementsHeight += (2 * SPACING_PIXELS) + HEADER_BAR_HEIGHT_PIXELS;

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

	protected Row createHeaderBar() {
		Row headerBar = new Row();
		headerBar.setPixelGLConverter(pixelGLConverter);
		headerBar.setPixelSizeY(HEADER_BAR_HEIGHT_PIXELS);

		for (ElementLayout element : headerBarElements) {
			headerBar.append(element);
		}

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		headerBar.append(spacingLayoutX);

		ElementLayout lockResizingButtonLayout = new ElementLayout(
				"lockResizingButton");
		lockResizingButtonLayout.setPixelGLConverter(pixelGLConverter);
		lockResizingButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		lockResizingButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		lockResizingButtonLayout.setRenderer(new ButtonRenderer(
				lockResizingButton, brick, brick.getTextureManager()));

		headerBar.append(lockResizingButtonLayout);

		return headerBar;
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

		// ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		// spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		// spacingLayoutX.setPixelSizeX(4);
		// spacingLayoutX.setPixelSizeY(0);
		//
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

		return toolBar;
	}

	@Override
	protected void registerPickingListeners() {

		// for (final BrickViewSwitchingButton button : viewSwitchingButtons) {
		//
		// brick.addPickingListener(new APickingListener() {
		//
		// @Override
		// public void clicked(Pick pick) {
		// for (BrickViewSwitchingButton button : viewSwitchingButtons) {
		// button.setSelected(false);
		// }
		// button.setSelected(true);
		//
		// brick.setContainedView(button.getViewType());
		// dimensionGroup.updateLayout();
		// }
		// }, button.getPickingType(), button.getButtonID());
		// }

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				boolean isResizingLocked = !lockResizingButton.isSelected();
				brick.setSizeFixed(isResizingLocked);
				lockResizingButton.setSelected(isResizingLocked);
			}
		}, EPickingType.BRICK_LOCK_RESIZING_BUTTON, LOCK_RESIZING_BUTTON_ID);

		// brick.addPickingListener(new APickingListener() {
		//
		// @Override
		// public void clicked(Pick pick) {
		// System.out.println("cluster");
		//
		// brick.getParentGLCanvas().getParentComposite().getDisplay()
		// .asyncExec(new Runnable() {
		// @Override
		// public void run() {
		// StartClusteringDialog dialog = new StartClusteringDialog(
		// new Shell(), brick.getDataDomain());
		// dialog.open();
		// ClusterState clusterState = dialog
		// .getClusterState();
		// if (clusterState == null)
		// return;
		//
		// StartClusteringEvent event = null;
		// // if (clusterState != null && set != null)
		//
		// event = new StartClusteringEvent(clusterState,
		// brick.getSet().getID());
		// event.setDataDomainType(brick.getDataDomain()
		// .getDataDomainType());
		// GeneralManager.get().getEventPublisher()
		// .triggerEvent(event);
		// }
		// });
		// }
		// }, EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON, CLUSTER_BUTTON_ID);

	}

	@Override
	public int getMinHeightPixels() {
		if (viewRenderer == null)
			return 20;

		return guiElementsHeight + viewRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		int headerBarWidth = calcSumPixelWidth(headerBar.getElements());
		int toolBarWidth = showToolBar ? calcSumPixelWidth(toolBar
				.getElements()) : 0;
		int footerBarWidth = showFooterBar ? calcSumPixelWidth(footerBar
				.getElements()) : 0;

		int minGuiElementWidth = Math.max(headerBarWidth,
				Math.max(toolBarWidth, footerBarWidth))  + 2 * SPACING_PIXELS;
		if (viewRenderer == null)
			return minGuiElementWidth;

		return Math.max(minGuiElementWidth,
				(2 * SPACING_PIXELS) + viewRenderer.getMinWidthPixels());
		// return pixelGLConverter.getPixelWidthForGLWidth(dimensionGroup
		// .getMinWidth());
	}

	// @Override
	// protected void setValidViewTypes() {
	// validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
	// validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
	// validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);
	// }

	// @Override
	// public EContainedViewType getDefaultViewType() {
	// return EContainedViewType.PARCOORDS_VIEW;
	// }

	// @Override
	// public void viewTypeChanged(EContainedViewType viewType) {
	// for (BrickViewSwitchingButton button : viewSwitchingButtons) {
	// if (viewType == button.getViewType()) {
	// button.setSelected(true);
	// } else {
	// button.setSelected(false);
	// }
	// }
	// }

	// @Override
	// public void configure(IBrickLayoutConfigurer configurer) {
	// configurer.configure(this);
	// }

	// public void setViewSwitchingButtons(
	// ArrayList<BrickViewSwitchingButton> buttons) {
	// viewSwitchingButtons = buttons;
	// }

	@Override
	public ABrickLayoutTemplate getCollapsedLayoutTemplate() {
		return new CompactCentralBrickLayoutTemplate(brick, dimensionGroup,
				visBricks, brick.getBrickConfigurer());
	}

	@Override
	public ABrickLayoutTemplate getExpandedLayoutTemplate() {
		return this;
	}

	public ArrayList<ElementLayout> getHeaderBarElements() {
		return headerBarElements;
	}

	public void setHeaderBarElements(ArrayList<ElementLayout> headerBarElements) {
		this.headerBarElements = headerBarElements;
	}

	public ArrayList<ElementLayout> getToolBarElements() {
		return toolBarElements;
	}

	public void setToolBarElements(ArrayList<ElementLayout> toolBarElements) {
		this.toolBarElements = toolBarElements;
	}

	public ArrayList<ElementLayout> getFooterBarElements() {
		return footerBarElements;
	}

	public void setFooterBarElements(ArrayList<ElementLayout> footerBarElements) {
		this.footerBarElements = footerBarElements;
	}

	public boolean isShowToolBar() {
		return showToolBar;
	}

	public void showToolBar(boolean showToolBar) {
		this.showToolBar = showToolBar;
	}

	public boolean isShowFooterBar() {
		return showFooterBar;
	}

	public void showFooterBar(boolean showFooterBar) {
		this.showFooterBar = showFooterBar;
	}

}
