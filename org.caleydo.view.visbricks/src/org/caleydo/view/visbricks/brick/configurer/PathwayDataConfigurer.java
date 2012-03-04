package org.caleydo.view.visbricks.brick.configurer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.view.visbricks.PickingType;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.visbricks.brick.layout.CollapsedBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.HeaderBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.layout.TitleOnlyHeaderBrickLayoutTemplate;
import org.caleydo.view.visbricks.brick.sorting.IBrickSortingStrategy;
import org.caleydo.view.visbricks.brick.sorting.NoSortingSortingStrategy;
import org.caleydo.view.visbricks.brick.ui.CompactPathwayRenderer;
import org.caleydo.view.visbricks.brick.ui.PathwaysSummaryRenderer;
import org.caleydo.view.visbricks.brick.viewcreation.PathwayCreator;

/**
 * Configurer for bricks to display pathway data.
 * 
 * @author Partl
 * 
 */
public class PathwayDataConfigurer implements IBrickConfigurer {

	protected static final int CAPTION_HEIGHT_PIXELS = 16;
	protected static final int SPACING_PIXELS = 4;

	@Override
	public void configure(HeaderBrickLayoutTemplate layoutTemplate) {

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		headerBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate
				.getBrick(), PickingType.DIMENSION_GROUP, layoutTemplate
				.getDimensionGroup().getID(), layoutTemplate.getDimensionGroup()
				.getVisBricksView()));

		headerBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setHeaderBarElements(headerBarElements);

		layoutTemplate.showFooterBar(false);
		layoutTemplate.showToolBar(false);
	}

	@Override
	public void configure(CollapsedBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW_COMPACT);

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(CompactHeaderBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAYS_SUMMARY_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAYS_SUMMARY_COMPACT);

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		headerBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate
				.getBrick(), PickingType.DIMENSION_GROUP, layoutTemplate
				.getDimensionGroup().getID(), layoutTemplate.getDimensionGroup()
				.getVisBricksView()));
		headerBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setHeaderBarElements(headerBarElements);

		layoutTemplate.showFooterBar(false);
	}
	
	@Override
	public void configure(TitleOnlyHeaderBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAYS_SUMMARY_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAYS_SUMMARY_COMPACT);

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		headerBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate
				.getBrick(), PickingType.DIMENSION_GROUP, layoutTemplate
				.getDimensionGroup().getID(), layoutTemplate.getDimensionGroup()
				.getVisBricksView()));

		layoutTemplate.setHeaderBarElements(headerBarElements);
	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		toolBarElements.add(createCaptionLayout(layoutTemplate,
				layoutTemplate.getBrick(), PickingType.BRICK, layoutTemplate.getBrick()
						.getID(), layoutTemplate.getBrick()));
		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.setShowFooterBar(false);
	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);
		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		toolBarElements.add(createCaptionLayout(layoutTemplate,
				layoutTemplate.getBrick(), PickingType.BRICK, layoutTemplate.getBrick()
						.getID(), layoutTemplate.getBrick()));
		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.showFooterBar(false);
	}

	private ElementLayout createCaptionLayout(ABrickLayoutConfiguration layoutTemplate,
			AGLView labelProvider, PickingType pickingType, int pickingID, AGLView view) {

		ElementLayout captionLayout = new ElementLayout("caption1");
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		LabelRenderer captionRenderer = new LabelRenderer(view, labelProvider,
				pickingType.name(), pickingID);
		captionLayout.setRenderer(captionRenderer);

		return captionLayout;
	}

	private ElementLayout createSpacingLayout(ABrickLayoutConfiguration layoutTemplate,
			boolean isHorizontalSpacing) {

		ElementLayout spacingLayout = new ElementLayout("spacingLayoutX");
		if (isHorizontalSpacing) {
			spacingLayout.setPixelSizeX(SPACING_PIXELS);
			spacingLayout.setRatioSizeY(0);
		} else {
			spacingLayout.setPixelSizeY(SPACING_PIXELS);
			spacingLayout.setRatioSizeX(0);
		}

		return spacingLayout;
	}

	@Override
	public void setBrickViews(GLBrick brick, GL2 gl, GLMouseListener glMouseListener,
			ABrickLayoutConfiguration brickLayout) {

		HashMap<EContainedViewType, AGLView> views = new HashMap<EContainedViewType, AGLView>();
		HashMap<EContainedViewType, LayoutRenderer> containedViewRenderers = new HashMap<EContainedViewType, LayoutRenderer>();

		PathwayCreator pathwayCreator = new PathwayCreator();
		AGLView pathway = pathwayCreator.createRemoteView(brick, gl, glMouseListener);

		LayoutRenderer pathwayRenderer = new ViewLayoutRenderer(pathway);

		views.put(EContainedViewType.PATHWAY_VIEW, pathway);
		containedViewRenderers.put(EContainedViewType.PATHWAY_VIEW, pathwayRenderer);

		String label = "";

		if (brick.getDimensionGroup().getDataContainer() instanceof PathwayDataContainer)
			label = ((PathwayDataContainer) brick.getDimensionGroup().getDataContainer())
					.getPathway().getTitle();
		else
			throw new IllegalStateException(
					"Not implemented yet for multiple pathways in a single dim group");
		// int numPathways = ((PathwayDataContainer) brick.getDimensionGroup()
		// .getDataContainer()).getPathways().size();

		// if (numPathways > 1)
		// label = "Pathways: " + numPathways;
		// else
		// label = ((PathwayDimensionGroupData) brick.getDimensionGroup()
		// .getDataContainer()).getPathways().get(0).getTitle();

		brick.setLabel(label);

		LayoutRenderer pathwaysSummaryRenderer = new PathwaysSummaryRenderer(brick,
				label, PickingType.BRICK.name(), brick.getID());
		containedViewRenderers.put(EContainedViewType.PATHWAYS_SUMMARY,
				pathwaysSummaryRenderer);

		LayoutRenderer pathwaysSummaryCompactRenderer = new PathwaysSummaryRenderer(
				brick, label, PickingType.BRICK.name(), brick.getID());
		containedViewRenderers.put(EContainedViewType.PATHWAYS_SUMMARY_COMPACT,
				pathwaysSummaryCompactRenderer);

		if (brick.getDataContainer() instanceof PathwayDataContainer) {
			PathwayDataContainer brickData = (PathwayDataContainer) brick
					.getDataContainer();
			if (brickData.getPathway() != null) {
				PathwayDatabaseType dataBaseType = brickData.getPathway().getType();
				EIconTextures texture;
				if (dataBaseType == PathwayDatabaseType.KEGG) {
					texture = EIconTextures.CM_KEGG;
				} else {
					texture = EIconTextures.CM_BIOCARTA;
				}
				LayoutRenderer compactPathwayRenderer = new CompactPathwayRenderer(brick,
						brick.getDataContainer().getLabel(), PickingType.BRICK.name(),
						brick.getID(), brick.getTextureManager(), texture);

				containedViewRenderers.put(EContainedViewType.PATHWAY_VIEW_COMPACT,
						compactPathwayRenderer);
			}
		}

		brick.setViews(views);
		brick.setContainedViewRenderers(containedViewRenderers);
	}

	@Override
	public IBrickSortingStrategy getBrickSortingStrategy() {
		// replace with ExternallyProvidedSortingStrategy
		return new NoSortingSortingStrategy();
	}

	@Override
	public boolean useDefaultWidth() {
		return false;
	}

	@Override
	public int getDefaultWidth() {
		return 0;
	}
}
