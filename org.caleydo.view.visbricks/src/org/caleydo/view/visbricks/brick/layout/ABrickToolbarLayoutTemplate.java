package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.picking.APickingListener;
import org.caleydo.view.visbricks.brick.ui.Button;
import org.caleydo.view.visbricks.brick.ui.ButtonRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

/**
 * Abstract base class for all brick layouts containing a toolbar.
 * 
 * @author Christian Partl
 *
 */
public abstract class ABrickToolbarLayoutTemplate extends ABrickLayoutTemplate {

	protected static final int HEATMAP_BUTTON_ID = 1;
	protected static final int PARCOORDS_BUTTON_ID = 2;
	protected static final int HISTOGRAM_BUTTON_ID = 3;
	protected static final int OVERVIEW_HEATMAP_BUTTON_ID = 4;

	
	protected Button heatMapButton;
	protected Button parCoordsButton;
	protected Button histogramButton;
	protected Button overviewHeatMapButton;
	

	public ABrickToolbarLayoutTemplate(GLBrick brick, DimensionGroup dimensionGroup) {
		super(brick, dimensionGroup);

		heatMapButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				HEATMAP_BUTTON_ID);
		parCoordsButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				PARCOORDS_BUTTON_ID);
		histogramButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				HISTOGRAM_BUTTON_ID);
		overviewHeatMapButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				OVERVIEW_HEATMAP_BUTTON_ID);

		updateToolBarButtons(getDefaultViewType());
	}
	
	/**
	 * Selects the toolbar button that corresponds to the view currently shown in the brick.
	 */
	public void updateToolBarButtons(EContainedViewType currentViewType) {
		
		heatMapButton.setSelected(false);
		parCoordsButton.setSelected(false);
		histogramButton.setSelected(false);
		overviewHeatMapButton.setSelected(false);
		
		switch (currentViewType) {
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

		toolBar.append(heatMapButtonLayout);
		toolBar.append(spacingLayoutX);

		ElementLayout parCoordsButtonLayout = new ElementLayout("parCoords");
		parCoordsButtonLayout.setPixelGLConverter(pixelGLConverter);
		parCoordsButtonLayout.setPixelSizeX(pixelHeight);
		parCoordsButtonLayout.setPixelSizeY(pixelHeight);
		parCoordsButtonLayout
				.setRenderer(new ButtonRenderer(parCoordsButton, brick,
						EIconTextures.PAR_COORDS_ICON, brick
								.getTextureManager()));

		toolBar.append(parCoordsButtonLayout);
		toolBar.append(spacingLayoutX);

		ElementLayout histogramButtonLayout = new ElementLayout(
				"histogramButton");
		histogramButtonLayout.setPixelGLConverter(pixelGLConverter);
		histogramButtonLayout.setPixelSizeX(pixelHeight);
		histogramButtonLayout.setPixelSizeY(pixelHeight);
		histogramButtonLayout
				.setRenderer(new ButtonRenderer(histogramButton, brick,
						EIconTextures.HISTOGRAM_ICON, brick.getTextureManager()));

		toolBar.append(histogramButtonLayout);
		toolBar.append(spacingLayoutX);

		ElementLayout overviewHeatMapButtonLayout = new ElementLayout(
				"overviewHeatMapButton");
		overviewHeatMapButtonLayout.setPixelGLConverter(pixelGLConverter);
		overviewHeatMapButtonLayout.setPixelSizeX(pixelHeight);
		overviewHeatMapButtonLayout.setPixelSizeY(pixelHeight);
		overviewHeatMapButtonLayout
				.setRenderer(new ButtonRenderer(overviewHeatMapButton, brick,
						EIconTextures.HEAT_MAP_ICON, brick.getTextureManager()));

		toolBar.append(overviewHeatMapButtonLayout);
		
		return toolBar;
	}

	@Override
	protected void registerPickingListeners() {

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(EContainedViewType.HEATMAP_VIEW);
				heatMapButton.setSelected(true);
				parCoordsButton.setSelected(false);
				histogramButton.setSelected(false);
				overviewHeatMapButton.setSelected(false);
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HEATMAP_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(EContainedViewType.PARCOORDS_VIEW);
				heatMapButton.setSelected(false);
				parCoordsButton.setSelected(true);
				histogramButton.setSelected(false);
				overviewHeatMapButton.setSelected(false);
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, PARCOORDS_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(EContainedViewType.HISTOGRAM_VIEW);
				heatMapButton.setSelected(false);
				parCoordsButton.setSelected(false);
				histogramButton.setSelected(true);
				overviewHeatMapButton.setSelected(false);
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HISTOGRAM_BUTTON_ID);
		
		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(EContainedViewType.OVERVIEW_HEATMAP);
				heatMapButton.setSelected(false);
				parCoordsButton.setSelected(false);
				histogramButton.setSelected(false);
				overviewHeatMapButton.setSelected(true);
				dimensionGroup.updateLayout();
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, OVERVIEW_HEATMAP_BUTTON_ID);
	}

}
