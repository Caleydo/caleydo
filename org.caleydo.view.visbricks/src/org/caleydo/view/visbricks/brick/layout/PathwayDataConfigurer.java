package org.caleydo.view.visbricks.brick.layout;

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
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.datadomain.pathway.data.PathwayDimensionGroupData;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
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
	public void configure(CentralBrickLayoutTemplate layoutTemplate) {

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAYS_SUMMARY);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAYS_SUMMARY);

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		headerBarElements.add(createCaptionLayout(layoutTemplate, "Pathway",
				PickingType.DIMENSION_GROUP, layoutTemplate.getDimensionGroup().getID(),
				layoutTemplate.getDimensionGroup().getVisBricksView()));

		headerBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setHeaderBarElements(headerBarElements);

		layoutTemplate.showFooterBar(false);
		layoutTemplate.showToolBar(false);

	}

	@Override
	public void configure(CompactBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_COMPACT);

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(CompactCentralBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAYS_SUMMARY_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAYS_SUMMARY_COMPACT);

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		headerBarElements.add(createCaptionLayout(layoutTemplate, "Pathway",
				PickingType.DIMENSION_GROUP, layoutTemplate.getDimensionGroup().getID(),
				layoutTemplate.getDimensionGroup().getVisBricksView()));
		headerBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setHeaderBarElements(headerBarElements);

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);

		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		toolBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate.getBrick()
				.getDataContainer().getLabel(), PickingType.BRICK, layoutTemplate
				.getBrick().getID(), layoutTemplate.getBrick()));
		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.PATHWAY_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.PATHWAY_VIEW);
		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		toolBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate.getBrick()
				.getDataContainer().getLabel(), PickingType.BRICK, layoutTemplate
				.getBrick().getID(), layoutTemplate.getBrick()));
		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.showFooterBar(false);
	}

	private ElementLayout createCaptionLayout(ABrickLayoutConfiguration layoutTemplate,
			String caption, PickingType pickingType, int pickingID, AGLView view) {

		ElementLayout captionLayout = new ElementLayout("caption1");

		// captionLayout.setDebug(true);
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		// captionLayout.setGrabX(true);
		captionLayout.setFrameColor(0, 0, 1, 1);

		LabelRenderer captionRenderer = new LabelRenderer(view, caption,
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

		int numPathways = ((PathwayDimensionGroupData) brick.getDimensionGroup()
				.getDataContainer()).getPathways().size();

		String label = "";
		if (numPathways > 1)
			label = "Pathways: " + numPathways;
		else
			label = ((PathwayDimensionGroupData) brick.getDimensionGroup()
					.getDataContainer()).getPathways().get(0).getTitle();

		LayoutRenderer pathwaysSummaryRenderer = new PathwaysSummaryRenderer(brick,
				label, PickingType.BRICK, brick.getID());
		containedViewRenderers.put(EContainedViewType.PATHWAYS_SUMMARY,
				pathwaysSummaryRenderer);

		LayoutRenderer pathwaysSummaryCompactRenderer = new PathwaysSummaryRenderer(
				brick, "PWs: " + numPathways, PickingType.BRICK, brick.getID());
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
						brick.getDataContainer().getLabel(), PickingType.BRICK,
						brick.getID(), brick.getTextureManager(), texture);

				containedViewRenderers.put(EContainedViewType.PATHWAY_COMPACT,
						compactPathwayRenderer);
			}
		}

		brick.setViews(views);
		brick.setContainedViewRenderers(containedViewRenderers);
	}
}
