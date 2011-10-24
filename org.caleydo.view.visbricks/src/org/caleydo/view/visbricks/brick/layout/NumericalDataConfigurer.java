package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.BrickViewSwitchingButton;
import org.caleydo.view.visbricks.brick.ui.OverviewHeatMapRenderer;
import org.caleydo.view.visbricks.brick.viewcreation.HeatMapCreator;
import org.caleydo.view.visbricks.brick.viewcreation.HistogramCreator;
import org.caleydo.view.visbricks.brick.viewcreation.ParCoordsCreator;

/**
 * Configurer for bricks to display numerical data.
 * 
 * @author Partl
 * 
 */
public class NumericalDataConfigurer extends ATableBasedDataConfigurer {

	protected static final int HEATMAP_BUTTON_ID = 1;
	protected static final int PARCOORDS_BUTTON_ID = 2;
	protected static final int HISTOGRAM_BUTTON_ID = 3;
	protected static final int OVERVIEW_HEATMAP_BUTTON_ID = 4;

	public NumericalDataConfigurer(ADimensionGroupData dimensionGroupData) {
		super(dimensionGroupData);
	}

	@Override
	public void configure(CentralBrickLayoutTemplate layoutTemplate) {

		BrickViewSwitchingButton parCoordsButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				PARCOORDS_BUTTON_ID, EIconTextures.PAR_COORDS_ICON,
				EContainedViewType.PARCOORDS_VIEW);
		BrickViewSwitchingButton histogramButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				HISTOGRAM_BUTTON_ID, EIconTextures.HISTOGRAM_ICON,
				EContainedViewType.HISTOGRAM_VIEW);
		BrickViewSwitchingButton overviewHeatMapButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				OVERVIEW_HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.OVERVIEW_HEATMAP);

		ArrayList<BrickViewSwitchingButton> viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		viewSwitchingButtons.add(parCoordsButton);
		viewSwitchingButtons.add(histogramButton);
		viewSwitchingButtons.add(overviewHeatMapButton);

		ArrayList<ElementLayout> headerBarElements = createHeaderBarElements(layoutTemplate);
		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate,
				viewSwitchingButtons);
		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);

		layoutTemplate.setHeaderBarElements(headerBarElements);
		layoutTemplate.setToolBarElements(toolBarElements);
		layoutTemplate.setFooterBarElements(footerBarElements);

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PARCOORDS_VIEW);

		layoutTemplate.showFooterBar(true);
		layoutTemplate.showToolBar(true);

	}

	@Override
	public void configure(CompactBrickLayoutTemplate layoutTemplate) {

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);

		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {

		BrickViewSwitchingButton heatMapButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.HEATMAP_VIEW);
		BrickViewSwitchingButton parCoordsButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				PARCOORDS_BUTTON_ID, EIconTextures.PAR_COORDS_ICON,
				EContainedViewType.PARCOORDS_VIEW);
		BrickViewSwitchingButton histogramButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				HISTOGRAM_BUTTON_ID, EIconTextures.HISTOGRAM_ICON,
				EContainedViewType.HISTOGRAM_VIEW);
		BrickViewSwitchingButton overviewHeatMapButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				OVERVIEW_HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.OVERVIEW_HEATMAP);

		ArrayList<BrickViewSwitchingButton> viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		viewSwitchingButtons.add(heatMapButton);
		viewSwitchingButtons.add(parCoordsButton);
		viewSwitchingButtons.add(histogramButton);
		viewSwitchingButtons.add(overviewHeatMapButton);

		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate,
				viewSwitchingButtons);
		layoutTemplate.setToolBarElements(toolBarElements);

		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
		validViewTypes.add(EContainedViewType.HEATMAP_VIEW);
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.OVERVIEW_HEATMAP);

		layoutTemplate.showFooterBar(true);

	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {
		BrickViewSwitchingButton heatMapButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.HEATMAP_VIEW);
		BrickViewSwitchingButton parCoordsButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				PARCOORDS_BUTTON_ID, EIconTextures.PAR_COORDS_ICON,
				EContainedViewType.PARCOORDS_VIEW);
		BrickViewSwitchingButton histogramButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				HISTOGRAM_BUTTON_ID, EIconTextures.HISTOGRAM_ICON,
				EContainedViewType.HISTOGRAM_VIEW);
		BrickViewSwitchingButton overviewHeatMapButton = new BrickViewSwitchingButton(
				PickingType.BRICK_TOOLBAR_VIEW_SWITCHING_BUTTONS.name(),
				OVERVIEW_HEATMAP_BUTTON_ID, EIconTextures.HEAT_MAP_ICON,
				EContainedViewType.OVERVIEW_HEATMAP);

		ArrayList<BrickViewSwitchingButton> viewSwitchingButtons = new ArrayList<BrickViewSwitchingButton>();
		viewSwitchingButtons.add(heatMapButton);
		viewSwitchingButtons.add(parCoordsButton);
		viewSwitchingButtons.add(histogramButton);
		viewSwitchingButtons.add(overviewHeatMapButton);

		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate,
				viewSwitchingButtons);
		layoutTemplate.setToolBarElements(toolBarElements);

		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.HISTOGRAM_VIEW);
		validViewTypes.add(EContainedViewType.HEATMAP_VIEW);
		validViewTypes.add(EContainedViewType.PARCOORDS_VIEW);
		validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PARCOORDS_VIEW);

		layoutTemplate.showFooterBar(true);

	}

	@Override
	public void setBrickViews(GLBrick brick, GL2 gl, GLMouseListener glMouseListener,
			ABrickLayoutTemplate brickLayout) {

		HashMap<EContainedViewType, AGLView> views = new HashMap<EContainedViewType, AGLView>();
		HashMap<EContainedViewType, LayoutRenderer> containedViewRenderers = new HashMap<EContainedViewType, LayoutRenderer>();

		if (!(brickLayout instanceof CentralBrickLayoutTemplate)) {
			HeatMapCreator heatMapCreator = new HeatMapCreator();
			AGLView heatMap = heatMapCreator.createRemoteView(brick, gl, glMouseListener);
			LayoutRenderer heatMapLayoutRenderer = new ViewLayoutRenderer(heatMap);
			views.put(EContainedViewType.HEATMAP_VIEW, heatMap);
			containedViewRenderers.put(EContainedViewType.HEATMAP_VIEW,
					heatMapLayoutRenderer);
		}

		ParCoordsCreator parCoordsCreator = new ParCoordsCreator();
		AGLView parCoords = parCoordsCreator.createRemoteView(brick, gl, glMouseListener);
		LayoutRenderer parCoordsLayoutRenderer = new ViewLayoutRenderer(parCoords);
		views.put(EContainedViewType.PARCOORDS_VIEW, parCoords);
		containedViewRenderers.put(EContainedViewType.PARCOORDS_VIEW,
				parCoordsLayoutRenderer);

		HistogramCreator histogramCreator = new HistogramCreator();
		AGLView histogram = histogramCreator.createRemoteView(brick, gl, glMouseListener);
		LayoutRenderer histogramLayoutRenderer = new ViewLayoutRenderer(histogram);
		views.put(EContainedViewType.HISTOGRAM_VIEW, histogram);
		containedViewRenderers.put(EContainedViewType.HISTOGRAM_VIEW,
				histogramLayoutRenderer);

		LayoutRenderer overviewHeatMapRenderer = new OverviewHeatMapRenderer(
				brick.getDataContainer(), brick.getSegmentData().getDataDomain()
						.getTable(), true);

		containedViewRenderers.put(EContainedViewType.OVERVIEW_HEATMAP,
				overviewHeatMapRenderer);

		LayoutRenderer compactOverviewHeatMapRenderer = new OverviewHeatMapRenderer(
				brick.getDataContainer(), brick.getSegmentData().getDataDomain()
						.getTable(), false);

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
		layoutTemplate.setDefaultViewType(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);

		ArrayList<ElementLayout> headerBarElements = createHeaderBarElements(layoutTemplate);
		layoutTemplate.setHeaderBarElements(headerBarElements);
		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

		layoutTemplate.showFooterBar(true);

	}

}
