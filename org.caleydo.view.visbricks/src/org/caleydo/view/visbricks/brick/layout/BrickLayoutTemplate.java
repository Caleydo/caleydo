package org.caleydo.view.visbricks.brick.layout;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.brick.APickingListener;
import org.caleydo.view.visbricks.brick.Button;
import org.caleydo.view.visbricks.brick.ButtonRenderer;
import org.caleydo.view.visbricks.brick.GLBrick;

public abstract class BrickLayoutTemplate extends LayoutTemplate {

	protected static final int HEATMAP_BUTTON_ID = 1;
	protected static final int PARCOORDS_BUTTON_ID = 2;
	protected static final int HISTOGRAM_BUTTON_ID = 3;
	protected static final int OVERVIEW_HEATMAP_BUTTON_ID = 4;

	protected GLBrick brick;
	protected LayoutRenderer viewRenderer;
	protected Button heatMapButton;
	protected Button parCoordsButton;
	protected Button histogramButton;
	protected Button overviewHeatMapButton;
	protected boolean showHandles;

	public BrickLayoutTemplate(GLBrick brick) {
		this.brick = brick;
		showHandles = false;

		heatMapButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				HEATMAP_BUTTON_ID);
		parCoordsButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				PARCOORDS_BUTTON_ID);
		histogramButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				HISTOGRAM_BUTTON_ID);
		overviewHeatMapButton = new Button(EPickingType.BRICK_TOOLBAR_BUTTONS,
				OVERVIEW_HEATMAP_BUTTON_ID);

		switch (brick.getCurrentViewType()) {
		case GLBrick.HEATMAP_VIEW:
			heatMapButton.setSelected(true);
			break;
		case GLBrick.PARCOORDS_VIEW:
			parCoordsButton.setSelected(true);
			break;
		case GLBrick.HISTOGRAM_VIEW:
			histogramButton.setSelected(true);
			break;
		case GLBrick.OVERVIEW_HEATMAP:
			overviewHeatMapButton.setSelected(true);
			break;
		}
		
		registerPickingListeners();
	}

	public void setViewRenderer(LayoutRenderer viewRenderer) {
		this.viewRenderer = viewRenderer;
	}

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

	protected void registerPickingListeners() {

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.HEATMAP_VIEW);
				heatMapButton.setSelected(true);
				parCoordsButton.setSelected(false);
				histogramButton.setSelected(false);
				overviewHeatMapButton.setSelected(false);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HEATMAP_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.PARCOORDS_VIEW);
				heatMapButton.setSelected(false);
				parCoordsButton.setSelected(true);
				histogramButton.setSelected(false);
				overviewHeatMapButton.setSelected(false);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, PARCOORDS_BUTTON_ID);

		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.HISTOGRAM_VIEW);
				heatMapButton.setSelected(false);
				parCoordsButton.setSelected(false);
				histogramButton.setSelected(true);
				overviewHeatMapButton.setSelected(false);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, HISTOGRAM_BUTTON_ID);
		
		brick.addPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.setRemoteView(GLBrick.OVERVIEW_HEATMAP);
				heatMapButton.setSelected(false);
				parCoordsButton.setSelected(false);
				histogramButton.setSelected(false);
				overviewHeatMapButton.setSelected(true);
			}
		}, EPickingType.BRICK_TOOLBAR_BUTTONS, OVERVIEW_HEATMAP_BUTTON_ID);
	}

	public boolean isShowHandles() {
		return showHandles;
	}

	public void setShowHandles(boolean showHandles) {
		this.showHandles = showHandles;
	}

}
