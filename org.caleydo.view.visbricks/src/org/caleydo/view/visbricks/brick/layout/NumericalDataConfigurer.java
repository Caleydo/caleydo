package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.AContainedViewRenderer;
import org.caleydo.view.visbricks.brick.ui.BrickRemoteViewRenderer;
import org.caleydo.view.visbricks.brick.ui.BrickViewSwitchingButton;
import org.caleydo.view.visbricks.brick.ui.OverviewHeatMapRenderer;
import org.caleydo.view.visbricks.brick.viewcreation.HeatMapCreator;
import org.caleydo.view.visbricks.brick.viewcreation.HistogramCreator;
import org.caleydo.view.visbricks.brick.viewcreation.ParCoordsCreator;

public class NumericalDataConfigurer implements IBrickConfigurer {

	protected static final int HEATMAP_BUTTON_ID = 1;
	protected static final int PARCOORDS_BUTTON_ID = 2;
	protected static final int HISTOGRAM_BUTTON_ID = 3;
	protected static final int OVERVIEW_HEATMAP_BUTTON_ID = 4;

	@Override
	public void configure(CentralBrickLayoutTemplate layoutTemplate) {
		BrickViewSwitchingButton parCoordsButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				PARCOORDS_BUTTON_ID, EIconTextures.PAR_COORDS_ICON,
				EContainedViewType.PARCOORDS_VIEW);
		BrickViewSwitchingButton histogramButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				HISTOGRAM_BUTTON_ID, EIconTextures.HISTOGRAM_ICON,
				EContainedViewType.HISTOGRAM_VIEW);
		BrickViewSwitchingButton overviewHeatMapButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				OVERVIEW_HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.OVERVIEW_HEATMAP);

		ArrayList<BrickViewSwitchingButton> viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		viewSwitchingButtons.add(parCoordsButton);
		viewSwitchingButtons.add(histogramButton);
		viewSwitchingButtons.add(overviewHeatMapButton);

		layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PARCOORDS_VIEW);

	}

	@Override
	public void configure(CompactBrickLayoutTemplate layoutTemplate) {

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate
				.setDefaultViewType(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);

	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {
		BrickViewSwitchingButton heatMapButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.HEATMAP_VIEW);
		BrickViewSwitchingButton parCoordsButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				PARCOORDS_BUTTON_ID, EIconTextures.PAR_COORDS_ICON,
				EContainedViewType.PARCOORDS_VIEW);
		BrickViewSwitchingButton histogramButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				HISTOGRAM_BUTTON_ID, EIconTextures.HISTOGRAM_ICON,
				EContainedViewType.HISTOGRAM_VIEW);
		BrickViewSwitchingButton overviewHeatMapButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				OVERVIEW_HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.OVERVIEW_HEATMAP);

		ArrayList<BrickViewSwitchingButton> viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		viewSwitchingButtons.add(heatMapButton);
		viewSwitchingButtons.add(parCoordsButton);
		viewSwitchingButtons.add(histogramButton);
		viewSwitchingButtons.add(overviewHeatMapButton);

		layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
		validViewTypes.add(EContainedViewType.HEATMAP_VIEW);
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.OVERVIEW_HEATMAP);

	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {
		BrickViewSwitchingButton heatMapButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.HEATMAP_VIEW);
		BrickViewSwitchingButton parCoordsButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				PARCOORDS_BUTTON_ID, EIconTextures.PAR_COORDS_ICON,
				EContainedViewType.PARCOORDS_VIEW);
		BrickViewSwitchingButton histogramButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				HISTOGRAM_BUTTON_ID, EIconTextures.HISTOGRAM_ICON,
				EContainedViewType.HISTOGRAM_VIEW);
		BrickViewSwitchingButton overviewHeatMapButton = new BrickViewSwitchingButton(
				EPickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS,
				OVERVIEW_HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.OVERVIEW_HEATMAP);

		ArrayList<BrickViewSwitchingButton> viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		viewSwitchingButtons.add(heatMapButton);
		viewSwitchingButtons.add(parCoordsButton);
		viewSwitchingButtons.add(histogramButton);
		viewSwitchingButtons.add(overviewHeatMapButton);

		layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
		validViewTypes.add(EContainedViewType.HEATMAP_VIEW);
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.OVERVIEW_HEATMAP);

	}

	@Override
	public void setBrickViews(GLBrick brick, GL2 gl,
			GLMouseListener glMouseListener, ABrickLayoutTemplate brickLayout) {

		HashMap<EContainedViewType, AGLView> views = new HashMap<EContainedViewType, AGLView>();
		HashMap<EContainedViewType, AContainedViewRenderer> containedViewRenderers = new HashMap<EContainedViewType, AContainedViewRenderer>();

		if (!(brickLayout instanceof CentralBrickLayoutTemplate)) {
			HeatMapCreator heatMapCreator = new HeatMapCreator();
			AGLView heatMap = heatMapCreator.createRemoteView(brick, gl,
					glMouseListener);
			AContainedViewRenderer heatMapLayoutRenderer = new BrickRemoteViewRenderer(
					heatMap, brick);
			views.put(EContainedViewType.HEATMAP_VIEW, heatMap);
			containedViewRenderers.put(EContainedViewType.HEATMAP_VIEW,
					heatMapLayoutRenderer);
		}

		ParCoordsCreator parCoordsCreator = new ParCoordsCreator();
		AGLView parCoords = parCoordsCreator.createRemoteView(brick, gl,
				glMouseListener);
		AContainedViewRenderer parCoordsLayoutRenderer = new BrickRemoteViewRenderer(
				parCoords, brick);
		views.put(EContainedViewType.PARCOORDS_VIEW, parCoords);
		containedViewRenderers.put(EContainedViewType.PARCOORDS_VIEW,
				parCoordsLayoutRenderer);

		HistogramCreator histogramCreator = new HistogramCreator();
		AGLView histogram = histogramCreator.createRemoteView(brick, gl,
				glMouseListener);
		AContainedViewRenderer histogramLayoutRenderer = new BrickRemoteViewRenderer(
				histogram, brick);
		views.put(EContainedViewType.HISTOGRAM_VIEW, histogram);
		containedViewRenderers.put(EContainedViewType.HISTOGRAM_VIEW,
				histogramLayoutRenderer);

		AContainedViewRenderer overviewHeatMapRenderer = new OverviewHeatMapRenderer(
				brick.getContentVA(), brick.getStorageVA(), brick.getSet(),
				true);

		containedViewRenderers.put(EContainedViewType.OVERVIEW_HEATMAP,
				overviewHeatMapRenderer);

		AContainedViewRenderer compactOverviewHeatMapRenderer = new OverviewHeatMapRenderer(
				brick.getContentVA(), brick.getStorageVA(), brick.getSet(),
				false);

		containedViewRenderers.put(EContainedViewType.OVERVIEW_HEATMAP_COMPACT,
				compactOverviewHeatMapRenderer);

		brick.setViews(views);
		brick.setContainedViewRenderers(containedViewRenderers);

	}

	@Override
	public void configure(CompactCentralBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate
				.setDefaultViewType(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);
	}

}
