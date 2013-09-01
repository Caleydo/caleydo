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
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;
import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.stratomex.EEmbeddingID;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.stratomex.brick.layout.CollapsedBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.HeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.TitleOnlyHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.ui.OverviewHeatMapRenderer;

/**
 * Configurer for bricks to display numerical data.
 *
 * @author Partl
 *
 */
public class NumericalDataConfigurer extends ATableBasedDataConfigurer {

	public NumericalDataConfigurer(TablePerspective tablePerspective) {
		super(tablePerspective);
	}

	@Override
	public void configure(HeaderBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> headerBarElements = createHeaderBarElements(layoutTemplate);
		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate);
		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);

		layoutTemplate.setHeaderBarElements(headerBarElements);
		layoutTemplate.setToolBarElements(toolBarElements);
		layoutTemplate.setFooterBarElements(footerBarElements);

		layoutTemplate.showFooterBar(false);
		layoutTemplate.showToolBar(true);
		layoutTemplate.showClusterButton(true);
	}

	@Override
	public void configure(CollapsedBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);
	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> headerBarElements = createHeaderBarElements(layoutTemplate);
		if (headerBarElements != null)
			layoutTemplate.setHeaderBarElements(headerBarElements);

		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate);
		layoutTemplate.setToolBarElements(toolBarElements);

		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

		layoutTemplate.setShowFooterBar(true);
	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate);
		layoutTemplate.setToolBarElements(toolBarElements);

		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

		layoutTemplate.showFooterBar(true);

	}

	@Override
	public void configure(CompactHeaderBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> headerBarElements = createHeaderBarElements(layoutTemplate);
		layoutTemplate.setHeaderBarElements(headerBarElements);
		ArrayList<ElementLayout> footerBarElements = createFooterBarElements(layoutTemplate);
		layoutTemplate.setFooterBarElements(footerBarElements);

		layoutTemplate.showFooterBar(true);
	}

	@Override
	public void configure(TitleOnlyHeaderBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> headerBarElements = createHeaderBarElements(layoutTemplate);
		layoutTemplate.setHeaderBarElements(headerBarElements);
	}

	@Override
	public void setBrickViews(final GLBrick brick, ABrickLayoutConfiguration brickLayout) {

		EEmbeddingID embeddingID;
		if (brick.isHeaderBrick()) {
			embeddingID = EEmbeddingID.NUMERICAL_HEADER_BRICK;

		} else {
			embeddingID = EEmbeddingID.NUMERICAL_SEGMENT_BRICK;
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

		ALayoutRenderer overviewHeatMapRenderer = new OverviewHeatMapRenderer(brick.getTablePerspective(), brick
				.getDataDomain().getTable(), true);

		ALayoutRenderer compactOverviewHeatMapRenderer = new OverviewHeatMapRenderer(brick.getTablePerspective(), brick
				.getDataDomain().getTable(), false);

		IEmbeddedVisualizationInfo visInfo = new DefaultVisInfo() {

			@Override
			public String getLabel() {
				return "Summary Heatmap";
			}
		};

		localRendererID = multiFormRenderer.addLayoutRenderer(overviewHeatMapRenderer,
				EIconTextures.HEAT_MAP_ICON.getFileName(), visInfo, false);
		brick.associateIDs(globalRendererID++, localRendererID);

		int compactRendererID = multiFormRenderer.addLayoutRenderer(compactOverviewHeatMapRenderer, null, visInfo,
				false);
		brick.associateIDs(globalRendererID++, compactRendererID);

		configureBrick(multiFormRenderer, brick, compactRendererID);

		// MultiFormViewSwitchingBar viewSwitchingBar = new MultiFormViewSwitchingBar(multiFormRenderer, brick);
		//
		// // There should be no view switching button for the visualization that is used in compact mode, as there is a
		// // dedicated button to switch to this mode.
		// viewSwitchingBar.removeButton(compactRendererID);
		//
		// APickingListener pickingListener = new APickingListener() {
		// @Override
		// public void clicked(Pick pick) {
		// if (brick.getBrickColumn().isGlobalViewSwitching()) {
		// brick.getBrickColumn().switchBrickViews(brick.getGlobalRendererID(pick.getObjectID()));
		// }
		// }
		// };
		//
		// for (Integer rendererID : multiFormRenderer.getRendererIDs()) {
		// viewSwitchingBar.addButtonPickingListener(pickingListener, rendererID);
		// }
		//
		// brick.setMultiFormRenderer(multiFormRenderer);
		// brick.setViewSwitchingBar(viewSwitchingBar);
		// brick.setCompactRendererID(compactRendererID);
		// multiFormRenderer.addChangeListener(brick);
	}

	@Override
	public boolean useDefaultWidth() {
		return true;
	}

	@Override
	public int getDefaultWidth() {
		return 120;
	}

	@Override
	public boolean distributeBricksUniformly() {
		return false;
	}
}
