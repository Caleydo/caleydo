package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.rcp.dialog.cluster.StartClusteringDialog;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.picking.APickingListener;
import org.caleydo.view.visbricks.brick.ui.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.ui.BorderedAreaRenderer;
import org.caleydo.view.visbricks.brick.ui.BrickViewSwitchingButton;
import org.caleydo.view.visbricks.brick.ui.DimensionBarRenderer;
import org.caleydo.view.visbricks.brick.ui.HandleRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroupCaptionRenderer;
import org.caleydo.view.visbricks.dimensiongroup.LineSeparatorRenderer;
import org.eclipse.swt.widgets.Shell;

/**
 * Brick layout for central brick in {@link DimensionGroup} conaining a caption
 * bar, toolbar and view.
 * 
 * @author Christian Partl
 * 
 */
public class CentralBrickLayoutTemplate extends ABrickLayoutTemplate {

	protected static final int TOOLBAR_HEIGHT_PIXELS = 16;
	protected static final int CAPTION_HEIGHT_PIXELS = 16;
	protected static final int DIMENSION_BAR_HEIGHT_PIXELS = 12;
	protected static final int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int HANDLE_SIZE_PIXELS = 10;

	protected static final int CLUSTER_BUTTON_ID = 0;
	protected static final int LOCK_RESIZING_BUTTON_ID = 1;

	protected ArrayList<BrickViewSwitchingButton> viewSwitchingButtons;

	// protected Button heatMapButton;
	// protected Button parCoordsButton;
	// protected Button histogramButton;
	// protected Button overviewHeatMapButton;

	private Button clusterButton;
	protected Button lockResizingButton;

	public CentralBrickLayoutTemplate(GLBrick brick,
			DimensionGroup dimensionGroup, IBrickConfigurer configurer) {
		super(brick, dimensionGroup);
		clusterButton = new Button(EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON,
				CLUSTER_BUTTON_ID, EIconTextures.CLUSTER_ICON);

		// heatMapButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
		// HEATMAP_BUTTON_ID);
		// parCoordsButton = new
		// Button(EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
		// PARCOORDS_BUTTON_ID);
		// histogramButton = new
		// Button(EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
		// HISTOGRAM_BUTTON_ID);
		// overviewHeatMapButton = new
		// Button(EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
		// OVERVIEW_HEATMAP_BUTTON_ID);
		lockResizingButton = new Button(
				EPickingType.BRICK_LOCK_RESIZING_BUTTON,
				LOCK_RESIZING_BUTTON_ID, EIconTextures.NAVIGATION_DRAG_VIEW);
		configurer.configure(this);
		registerPickingListeners();
		viewTypeChanged(getDefaultViewType());

	}

	public void setLockResizing(boolean lockResizing) {
		lockResizingButton.setSelected(lockResizing);
	}

	@Override
	public void setStaticLayouts() {
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		setBaseElementLayout(baseRow);

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 0);

		baseRow.setRenderer(new BorderedAreaRenderer());

		if (showHandles) {
			baseRow.addForeGroundRenderer(new HandleRenderer(brick,
					pixelGLConverter, HANDLE_SIZE_PIXELS, brick
							.getTextureManager(), HandleRenderer.ALL_HANDLES));
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
		dimensionBarLayout.setPixelSizeY(DIMENSION_BAR_HEIGHT_PIXELS);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer(brick));
		viewLayout.setRenderer(viewRenderer);

		Row toolBar = createBrickToolBar(TOOLBAR_HEIGHT_PIXELS);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		Row captionRow = createCaptionRow();
		// captionRow.append(spacingLayoutX);

		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
		lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		ElementLayout dimensionBarLaylout = new ElementLayout("dimensionBar");
		dimensionBarLaylout.setPixelGLConverter(pixelGLConverter);
		dimensionBarLaylout.setPixelSizeY(DIMENSION_BAR_HEIGHT_PIXELS);
		dimensionBarLaylout.setRatioSizeX(1);
		dimensionBarLaylout.setRenderer(new DimensionBarRenderer(brick));

		baseColumn.append(spacingLayoutY);
		baseColumn.append(dimensionBarLaylout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(viewLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(toolBar);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(captionRow);
		baseColumn.append(spacingLayoutY);

	}

	protected Row createCaptionRow() {
		Row captionRow = new Row();
		captionRow.setPixelGLConverter(pixelGLConverter);
		captionRow.setPixelSizeY(CAPTION_HEIGHT_PIXELS);

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		DimensionGroupCaptionRenderer captionRenderer = new DimensionGroupCaptionRenderer(
				dimensionGroup);
		captionLayout.setRenderer(captionRenderer);

		captionRow.append(captionLayout);
		captionRow.append(spacingLayoutX);

		ElementLayout lockResizingButtonLayout = new ElementLayout(
				"lockResizingButton");
		lockResizingButtonLayout.setPixelGLConverter(pixelGLConverter);
		lockResizingButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		lockResizingButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		lockResizingButtonLayout.setRenderer(new ButtonRenderer(
				lockResizingButton, brick, brick.getTextureManager()));

		captionRow.append(lockResizingButtonLayout);
		captionRow.append(spacingLayoutX);

		ElementLayout clusterButtonLayout = new ElementLayout("clusterButton");
		clusterButtonLayout.setPixelGLConverter(pixelGLConverter);
		clusterButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		clusterButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		clusterButtonLayout.setRenderer(new ButtonRenderer(clusterButton,
				brick, brick.getTextureManager()));

		captionRow.append(clusterButtonLayout);

		return captionRow;
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

					brick.setContainedView(button.getViewType());
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

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				System.out.println("cluster");

				brick.getParentGLCanvas().getParentComposite().getDisplay()
						.asyncExec(new Runnable() {
							@Override
							public void run() {
								StartClusteringDialog dialog = new StartClusteringDialog(
										new Shell(), brick.getDataDomain());
								dialog.open();
								ClusterState clusterState = dialog
										.getClusterState();
								if (clusterState == null)
									return;

								StartClusteringEvent event = null;
								// if (clusterState != null && set != null)

								event = new StartClusteringEvent(clusterState,
										brick.getSet().getID());
								event.setDataDomainType(brick.getDataDomain()
										.getDataDomainType());
								GeneralManager.get().getEventPublisher()
										.triggerEvent(event);
							}
						});
			}
		}, EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON, CLUSTER_BUTTON_ID);

	}

	@Override
	public int getMinHeightPixels() {
		return 6 * SPACING_PIXELS + TOOLBAR_HEIGHT_PIXELS
				+ LINE_SEPARATOR_HEIGHT_PIXELS + CAPTION_HEIGHT_PIXELS
				+ DIMENSION_BAR_HEIGHT_PIXELS
				+ viewRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		// TODO: implement
		return 0;
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

	// @Override
	// public void configure(IBrickLayoutConfigurer configurer) {
	// configurer.configure(this);
	// }

	public void setViewSwitchingButtons(
			ArrayList<BrickViewSwitchingButton> buttons) {
		viewSwitchingButtons = buttons;
	}

	@Override
	public ABrickLayoutTemplate getCollapsedLayoutTemplate() {
		return new CompactCentralBrickLayoutTemplate(brick, dimensionGroup,
				brick.getLayoutConfigurer());
	}

	@Override
	public ABrickLayoutTemplate getExpandedLayoutTemplate() {
		return this;
	}

}
