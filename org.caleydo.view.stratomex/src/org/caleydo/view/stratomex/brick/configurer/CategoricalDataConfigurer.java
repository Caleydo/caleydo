/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.brick.configurer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
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
 * Configurer for bricks to display categroical data
 *
 * @author Partl
 *
 */
public class CategoricalDataConfigurer extends ATableBasedDataConfigurer {

	protected static final int HEATMAP_BUTTON_ID = 1;
	protected static final int HISTOGRAM_BUTTON_ID = 3;

	public CategoricalDataConfigurer(TablePerspective tablePerspective) {
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
		layoutTemplate.showToolBar(false);

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

		layoutTemplate.setShowToolBar(false);
		layoutTemplate.setShowFooterBar(false);

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
	public void setBrickViews(GLBrick brick, ABrickLayoutConfiguration brickLayout) {

		EEmbeddingID embeddingID;
		if (brick.isHeaderBrick()) {
			embeddingID = EEmbeddingID.CATEGORICAL_HEADER_BRICK;
		} else {
			embeddingID = EEmbeddingID.CATEGORICAL_SEGMENT_BRICK;
		}
		Set<String> remoteRenderedViewIDs = ViewManager.get().getRemotePlugInViewIDs(GLStratomex.VIEW_TYPE,
				embeddingID.id());

		List<String> viewIDs = new ArrayList<>(remoteRenderedViewIDs);
		Collections.sort(viewIDs);

		MultiFormRenderer multiFormRenderer = new MultiFormRenderer(brick.getStratomex(), true);
		List<TablePerspective> tablePerspectives = brick.getTablePerspectives();

		int globalRendererID = 0;
		int localRendererID = -1;
		for (String viewID : remoteRenderedViewIDs) {
			localRendererID = multiFormRenderer.addPluginVisualization(viewID, brick.getStratomex().getViewType(),
					embeddingID.id(), tablePerspectives);
			brick.associateIDs(globalRendererID++, localRendererID);
		}

		ALayoutRenderer overviewHeatMapRenderer = new OverviewHeatMapRenderer(brick.getTablePerspective(), brick
				.getDataDomain().getTable(), true);

		ALayoutRenderer compactOverviewHeatMapRenderer = new OverviewHeatMapRenderer(brick.getTablePerspective(), brick
				.getDataDomain().getTable(), false);

		IEmbeddedVisualizationInfo visInfo = new IEmbeddedVisualizationInfo() {

			@Override
			public EScalingEntity getPrimaryWidthScalingEntity() {
				return null;
			}

			@Override
			public EScalingEntity getPrimaryHeightScalingEntity() {
				return null;
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
		return 50;
	}
}
