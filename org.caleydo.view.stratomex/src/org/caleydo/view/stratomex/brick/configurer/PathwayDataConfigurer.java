/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.configurer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.util.text.ITextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.view.stratomex.EEmbeddingID;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.stratomex.brick.layout.CollapsedBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.HeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.TitleOnlyHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.sorting.IBrickSortingStrategy;
import org.caleydo.view.stratomex.brick.sorting.NoSortingSortingStrategy;
import org.caleydo.view.stratomex.brick.ui.CompactPathwayRenderer;
import org.caleydo.view.stratomex.brick.ui.PathwaysSummaryRenderer;

/**
 * Configurer for bricks to display pathway data.
 *
 * @author Christian Partl
 *
 */
public class PathwayDataConfigurer extends ABrickConfigurer {

	protected static final int CAPTION_HEIGHT_PIXELS = 16;
	protected static final int SPACING_PIXELS = 4;

	@Override
	public void configure(HeaderBrickLayoutTemplate layoutTemplate) {
		layoutTemplate.setHeaderBarElements(createHeaderBarElements(layoutTemplate));

		layoutTemplate.showFooterBar(false);
		layoutTemplate.showToolBar(true);
	}

	@Override
	public void configure(CollapsedBrickLayoutTemplate layoutTemplate) {
		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(CompactHeaderBrickLayoutTemplate layoutTemplate) {
		layoutTemplate.setHeaderBarElements(createHeaderBarElements(layoutTemplate));

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(TitleOnlyHeaderBrickLayoutTemplate layoutTemplate) {
		layoutTemplate.setHeaderBarElements(createHeaderBarElements(layoutTemplate));
	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		ElementLayout leftPaddingLayout = new ElementLayout("padding");
		leftPaddingLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		toolBarElements.add(leftPaddingLayout);
		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.setShowFooterBar(false);
	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {
		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();
		List<Pair<String, Integer>> pickingIDs = new ArrayList<>();
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK.name(), layoutTemplate.getBrick().getID()));
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK_TITLE.name(), layoutTemplate.getBrick().getID()));

		toolBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate.getBrick(), pickingIDs,
 layoutTemplate
				.getBrick().getBrickColumn().getStratomexView()));
		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.showFooterBar(false);
	}

	private ElementLayout createCaptionLayout(ABrickLayoutConfiguration layoutTemplate, AGLView labelProvider,
			List<Pair<String, Integer>> pickingIDs, AGLView view) {

		ElementLayout captionLayout = new ElementLayout("caption1");
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		ITextRenderer textRenderer = layoutTemplate.getBrick().getTextRenderer();
		LabelRenderer captionRenderer = new LabelRenderer(view, textRenderer, labelProvider, pickingIDs);
		captionLayout.setRenderer(captionRenderer);

		return captionLayout;
	}

	private ElementLayout createSpacingLayout(ABrickLayoutConfiguration layoutTemplate, boolean isHorizontalSpacing) {

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

	private List<ElementLayout> createHeaderBarElements(ABrickLayoutConfiguration layoutTemplate) {
		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		List<Pair<String, Integer>> pickingIDs = new ArrayList<>();
		pickingIDs.add(new Pair<String, Integer>(EPickingType.DIMENSION_GROUP.name(), layoutTemplate
				.getDimensionGroup().getID()));
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK_TITLE.name(), layoutTemplate.getBrick().getID()));

		headerBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate.getBrick(), pickingIDs, layoutTemplate
				.getDimensionGroup().getStratomexView()));
		return headerBarElements;
	}

	@Override
	public void setBrickViews(GLBrick brick, ABrickLayoutConfiguration brickLayout) {

		String label = "";

		if (brick.getBrickColumn().getTablePerspective() instanceof PathwayTablePerspective)
			label = ((PathwayTablePerspective) brick.getBrickColumn().getTablePerspective()).getPathway().getTitle();
		else
			throw new IllegalStateException("Not implemented yet for multiple pathways in a single dim group");

		brick.setLabel(label, false);

		EEmbeddingID embeddingID;
		if (brick.isHeaderBrick()) {
			embeddingID = EEmbeddingID.PATHWAY_HEADER_BRICK;

		} else {
			embeddingID = EEmbeddingID.PATHWAY_HEADER_BRICK;
		}
		Set<String> remoteRenderedViewIDs = ViewManager.get().getRemotePlugInViewIDs(GLStratomex.VIEW_TYPE,
				embeddingID.id());

		List<String> viewIDs = new ArrayList<>(remoteRenderedViewIDs);
		Collections.sort(viewIDs);

		MultiFormRenderer multiFormRenderer = new MultiFormRenderer(brick.getStratomex(), true);
		List<TablePerspective> tablePerspectives = brick.getTablePerspectives();

		int globalRendererID = 0;
		int localRendererID = -1;
		String brickEventSpace = GeneralManager.get().getEventPublisher().createUniqueEventSpace();
		for (String viewID : remoteRenderedViewIDs) {
			localRendererID = multiFormRenderer.addPluginVisualization(viewID, brick.getStratomex().getViewType(),
					embeddingID.id(), tablePerspectives, brickEventSpace);
			brick.associateIDs(globalRendererID++, localRendererID);
		}

		int compactRendererID = -1;
		if (brick.isHeaderBrick()) {
			ALayoutRenderer pathwaysSummaryCompactRenderer = new PathwaysSummaryRenderer(brick.getBrickColumn()
					.getStratomexView(), label,
					EPickingType.BRICK.name(), brick.getID());
			compactRendererID = multiFormRenderer.addLayoutRenderer(pathwaysSummaryCompactRenderer, null,
					new DefaultVisInfo(), false);
			brick.setCompactRendererID(compactRendererID);
		} else {
			if (brick.getTablePerspective() instanceof PathwayTablePerspective) {
				PathwayTablePerspective brickData = (PathwayTablePerspective) brick.getTablePerspective();
				if (brickData.getPathway() != null) {
					EPathwayDatabaseType dataBaseType = brickData.getPathway().getType();
					EIconTextures texture = null;
					if (dataBaseType == EPathwayDatabaseType.KEGG) {
						texture = EIconTextures.CM_KEGG;
					}

					if (texture != null) {
						ALayoutRenderer compactPathwayRenderer = new CompactPathwayRenderer(brick, brick
								.getTablePerspective().getLabel(), EPickingType.BRICK.name(), brick.getID(),
								brick.getTextureManager(), texture);
						compactRendererID = multiFormRenderer.addLayoutRenderer(compactPathwayRenderer, null,
								new DefaultVisInfo(), false);
					}
				}
			}
		}

		brick.associateIDs(globalRendererID++, compactRendererID);

		configureBrick(multiFormRenderer, brick, compactRendererID);

		// MultiFormViewSwitchingBar viewSwitchingBar = new MultiFormViewSwitchingBar(multiFormRenderer, brick);
		//
		// // There should be no view switching button for the visualization that is used in compact mode, as there is a
		// // dedicated button to switch to this mode.
		// viewSwitchingBar.removeButton(compactRendererID);
		//
		// brick.setMultiFormRenderer(multiFormRenderer);
		// brick.setViewSwitchingBar(viewSwitchingBar);
		// brick.setCompactRendererID(compactRendererID);
		// multiFormRenderer.addChangeListener(brick);
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
