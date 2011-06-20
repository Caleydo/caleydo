package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.ZoomableViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.view.visbricks.brick.EContainedViewType;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.data.PathwayBrickData;
import org.caleydo.view.visbricks.brick.ui.CompactPathwayRenderer;
import org.caleydo.view.visbricks.brick.ui.PathwaysSummaryRenderer;
import org.caleydo.view.visbricks.brick.viewcreation.PathwayCreator;

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

		headerBarElements.add(createCaptionLayout(layoutTemplate, "sdfsd",
				EPickingType.DIMENSION_GROUP, layoutTemplate
						.getDimensionGroup().getID(), layoutTemplate
						.getDimensionGroup().getVisBricksView()));

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

		headerBarElements.add(createCaptionLayout(layoutTemplate, "sdfsd",
				EPickingType.DIMENSION_GROUP, layoutTemplate
						.getDimensionGroup().getID(), layoutTemplate
						.getDimensionGroup().getVisBricksView()));
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

		toolBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate
				.getBrick().getBrickData().getLabel(), EPickingType.BRICK,
				layoutTemplate.getBrick().getID(), layoutTemplate.getBrick()));
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

		toolBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate
				.getBrick().getBrickData().getLabel(), EPickingType.BRICK,
				layoutTemplate.getBrick().getID(), layoutTemplate.getBrick()));
		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.showFooterBar(false);
	}

	private ElementLayout createCaptionLayout(
			ABrickLayoutTemplate layoutTemplate, String caption,
			EPickingType pickingType, int pickingID, AGLView view) {
		PixelGLConverter pixelGLConverter = layoutTemplate
				.getPixelGLConverter();

		ElementLayout captionLayout = new ElementLayout("caption1");

		// captionLayout.setDebug(true);
		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		// captionLayout.setGrabX(true);
		captionLayout.setFrameColor(0, 0, 1, 1);

		LabelRenderer captionRenderer = new LabelRenderer(view, caption,
				pickingType, pickingID);
		captionLayout.setRenderer(captionRenderer);

		return captionLayout;
	}

	private ElementLayout createSpacingLayout(
			ABrickLayoutTemplate layoutTemplate, boolean isHorizontalSpacing) {
		PixelGLConverter pixelGLConverter = layoutTemplate
				.getPixelGLConverter();

		ElementLayout spacingLayout = new ElementLayout("spacingLayoutX");
		spacingLayout.setPixelGLConverter(pixelGLConverter);
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
	public void setBrickViews(GLBrick brick, GL2 gl,
			GLMouseListener glMouseListener, ABrickLayoutTemplate brickLayout) {

		HashMap<EContainedViewType, AGLView> views = new HashMap<EContainedViewType, AGLView>();
		HashMap<EContainedViewType, LayoutRenderer> containedViewRenderers = new HashMap<EContainedViewType, LayoutRenderer>();

		PathwayCreator pathwayCreator = new PathwayCreator();
		AGLView pathway = pathwayCreator.createRemoteView(brick, gl,
				glMouseListener);

		LayoutRenderer pathwayRenderer = new ZoomableViewLayoutRenderer(
				pathway, brick);
		views.put(EContainedViewType.PATHWAY_VIEW, pathway);
		containedViewRenderers.put(EContainedViewType.PATHWAY_VIEW,
				pathwayRenderer);

		int numPathways = brick.getDimensionGroup().getDimensionGroupData()
				.getGroups().size();
		LayoutRenderer pathwaysSummaryRenderer = new PathwaysSummaryRenderer(
				brick, "Pathways: " + numPathways, EPickingType.BRICK,
				brick.getID());
		containedViewRenderers.put(EContainedViewType.PATHWAYS_SUMMARY,
				pathwaysSummaryRenderer);

		LayoutRenderer pathwaysSummaryCompactRenderer = new PathwaysSummaryRenderer(
				brick, "PWs: " + numPathways, EPickingType.BRICK,
				brick.getID());
		containedViewRenderers.put(EContainedViewType.PATHWAYS_SUMMARY_COMPACT,
				pathwaysSummaryCompactRenderer);
		
		if (brick.getBrickData() instanceof PathwayBrickData) {
			PathwayBrickData brickData = (PathwayBrickData) brick
					.getBrickData();
			if (brickData.getPathway() != null) {
				EPathwayDatabaseType dataBaseType = brickData.getPathway()
						.getType();
				EIconTextures texture;
				if (dataBaseType == EPathwayDatabaseType.KEGG) {
					texture = EIconTextures.CM_KEGG;
				} else {
					texture = EIconTextures.CM_BIOCARTA;
				}
				LayoutRenderer compactPathwayRenderer = new CompactPathwayRenderer(
						brick, brick.getBrickData().getLabel(),
						EPickingType.BRICK, brick.getID(),
						brick.getTextureManager(), texture);

				containedViewRenderers.put(EContainedViewType.PATHWAY_COMPACT,
						compactPathwayRenderer);
			}
		}

		brick.setViews(views);
		brick.setContainedViewRenderers(containedViewRenderers);

	}

}
