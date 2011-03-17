package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.rcp.dialog.cluster.StartClusteringDialog;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.picking.APickingListener;
import org.caleydo.view.visbricks.brick.ui.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.ui.BorderedAreaRenderer;
import org.caleydo.view.visbricks.brick.ui.Button;
import org.caleydo.view.visbricks.brick.ui.ButtonRenderer;
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
public class CentralBrickLayoutTemplate extends ABrickToolbarLayoutTemplate {

	protected static final int TOOLBAR_HEIGHT_PIXELS = 16;
	protected static final int CAPTION_HEIGHT_PIXELS = 16;
	protected static final int DIMENSION_BAR_HEIGHT_PIXELS = 12;
	protected static final int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int HANDLE_SIZE_PIXELS = 10;

	private Button clusterButton;
	private Button viewSwitchingModeButton;

	public CentralBrickLayoutTemplate(GLBrick brick,
			DimensionGroup dimensionGroup) {
		super(brick, dimensionGroup);
		clusterButton = new Button(EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON,
				1);
		viewSwitchingModeButton = new Button(
				EPickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON, 1);

	}

	@Override
	public void setStaticLayouts() {
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		setBaseElementLayout(baseRow);

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 0);

		// ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		// fuelBarLayout.setFrameColor(0, 1, 0, 0);

		baseRow.setRenderer(new BorderedAreaRenderer());

		if (showHandles) {
			baseRow.addForeGroundRenderer(new HandleRenderer(brick
					.getDimensionGroup(), pixelGLConverter, HANDLE_SIZE_PIXELS,
					brick.getTextureManager()));
		}

		// fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		// fuelBarLayout.setPixelSizeY(12);
		// fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

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
		dimensionBarLayout.setPixelSizeY(12);

		ElementLayout viewLayout = new ElementLayout("viewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer(brick));
		viewLayout.setRenderer(viewRenderer);

		Row toolBar = createBrickToolBar(TOOLBAR_HEIGHT_PIXELS);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		Row captionRow = new Row();
		captionRow.setPixelGLConverter(pixelGLConverter);
		captionRow.setPixelSizeY(CAPTION_HEIGHT_PIXELS);

		ElementLayout captionLayout = new ElementLayout("caption1");
		// captionLayout.setDebug(true);
		// captionLayout.setFrameColor(0, 0, 1, 1);
		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(18);
		// captionLayout.setRatioSizeY(0.2f);
		captionLayout.setFrameColor(0, 0, 1, 1);
		// captionLayout.setDebug(true);

		DimensionGroupCaptionRenderer captionRenderer = new DimensionGroupCaptionRenderer(
				dimensionGroup);
		captionLayout.setRenderer(captionRenderer);

		captionRow.append(captionLayout);
		captionRow.append(spacingLayoutX);

		ElementLayout clusterButtonLayout = new ElementLayout("clusterButton");
		clusterButtonLayout.setPixelGLConverter(pixelGLConverter);
		clusterButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		clusterButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		clusterButtonLayout.setRenderer(new ButtonRenderer(clusterButton,
				brick, EIconTextures.CLUSTER_ICON, brick.getTextureManager()));

		captionRow.append(clusterButtonLayout);
		captionRow.append(spacingLayoutX);

		ElementLayout toggleViewSwitchingButtonLayout = new ElementLayout(
				"clusterButton");
		toggleViewSwitchingButtonLayout.setPixelGLConverter(pixelGLConverter);
		toggleViewSwitchingButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		toggleViewSwitchingButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
		toggleViewSwitchingButtonLayout.setRenderer(new ButtonRenderer(
				viewSwitchingModeButton, brick, EIconTextures.LOCK, brick
						.getTextureManager()));

		captionRow.append(toggleViewSwitchingButtonLayout);

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

	@Override
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
		}, EPickingType.DIMENSION_GROUP_CLUSTER_BUTTON, 1);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				viewSwitchingModeButton.setSelected(!viewSwitchingModeButton
						.isSelected());
			}
		}, EPickingType.BRICK_VIEW_SWITCHING_MODE_BUTTON, 1);
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

	@Override
	protected void setValidViewTypes() {
		validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
		validViewTypes.add(EContainedViewType.HEATMAP_VIEW);
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);
	}

	@Override
	public EContainedViewType getDefaultViewType() {
		return EContainedViewType.HISTOGRAM_VIEW;
	}
}
