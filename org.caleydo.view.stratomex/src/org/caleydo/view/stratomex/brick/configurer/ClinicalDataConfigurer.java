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
import java.util.HashMap;
import java.util.HashSet;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.ViewLayoutRenderer;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.brick.EContainedViewType;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.layout.ABrickLayoutConfiguration;
import org.caleydo.view.stratomex.brick.layout.CollapsedBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.CompactHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DefaultBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.DetailBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.HeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.layout.TitleOnlyHeaderBrickLayoutTemplate;
import org.caleydo.view.stratomex.brick.sorting.ExternallyProvidedSortingStrategy;
import org.caleydo.view.stratomex.brick.sorting.IBrickSortingStrategy;
import org.caleydo.view.stratomex.brick.ui.KaplanMeierSummaryRenderer;
import org.caleydo.view.stratomex.brick.viewcreation.KaplanMeierCreator;

/**
 * Configurer for bricks that display numerical clinical data, such as disease
 * free survival etc.
 * 
 * @author Marc Streit
 * 
 */
public class ClinicalDataConfigurer implements IBrickConfigurer {

	protected static final int CAPTION_HEIGHT_PIXELS = 16;
	protected static final int SPACING_PIXELS = 4;

	private ExternallyProvidedSortingStrategy sortingStrategy;
	/**
	 * The maximum time value that is mapped to the x axis of Kaplan Meier plots.
	 */
	private float maxTimeValue;

	@Override
	public void configure(HeaderBrickLayoutTemplate layoutTemplate) {

		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.KAPLAN_MEIER_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.KAPLAN_MEIER_VIEW);

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		headerBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate
				.getBrick(), EPickingType.DIMENSION_GROUP, layoutTemplate
				.getDimensionGroup().getID(), layoutTemplate.getDimensionGroup()
				.getStratomexView()));

		headerBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setHeaderBarElements(headerBarElements);

		layoutTemplate.showFooterBar(false);
		layoutTemplate.showToolBar(true);

	}

	@Override
	public void configure(CollapsedBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.KAPLAN_MEIER_VIEW_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.KAPLAN_MEIER_VIEW_COMPACT);

		layoutTemplate.showFooterBar(false);
	}

	@Override
	public void configure(CompactHeaderBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.KAPLAN_MEIER_SUMMARY_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate
				.setDefaultViewType(EContainedViewType.KAPLAN_MEIER_SUMMARY_COMPACT);

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		headerBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate
				.getBrick(), EPickingType.DIMENSION_GROUP, layoutTemplate
				.getDimensionGroup().getID(), layoutTemplate.getDimensionGroup()
				.getStratomexView()));
		headerBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setHeaderBarElements(headerBarElements);

		layoutTemplate.showFooterBar(true);
	}

	@Override
	public void configure(TitleOnlyHeaderBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.KAPLAN_MEIER_SUMMARY_COMPACT);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate
				.setDefaultViewType(EContainedViewType.KAPLAN_MEIER_SUMMARY_COMPACT);

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		headerBarElements.add(createCaptionLayout(layoutTemplate, layoutTemplate
				.getBrick(), EPickingType.DIMENSION_GROUP, layoutTemplate
				.getDimensionGroup().getID(), layoutTemplate.getDimensionGroup()
				.getStratomexView()));
		headerBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setHeaderBarElements(headerBarElements);
	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.KAPLAN_MEIER_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.KAPLAN_MEIER_VIEW);

		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		toolBarElements.add(createCaptionLayout(layoutTemplate,
				layoutTemplate.getBrick(), EPickingType.BRICK, layoutTemplate.getBrick()
						.getID(), layoutTemplate.getBrick()));
		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.setShowFooterBar(false);
	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {
		HashSet<EContainedViewType> validViewTypes = new HashSet<EContainedViewType>();
		validViewTypes.add(EContainedViewType.KAPLAN_MEIER_VIEW);

		layoutTemplate.setValidViewTypes(validViewTypes);
		layoutTemplate.setDefaultViewType(EContainedViewType.KAPLAN_MEIER_VIEW);
		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

//		toolBarElements.add(createCaptionLayout(layoutTemplate,
//				layoutTemplate.getBrick(), EPickingType.BRICK, layoutTemplate.getBrick()
//						.getID(), layoutTemplate.getBrick()));
		ElementLayout leftPaddingLayout = new ElementLayout("padding");
		leftPaddingLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		toolBarElements.add(leftPaddingLayout);
		
		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.showFooterBar(false);
	}

	private ElementLayout createCaptionLayout(ABrickLayoutConfiguration layoutTemplate,
			AGLView labelProvider, EPickingType pickingType, int pickingID, AGLView view) {

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		LabelRenderer captionRenderer = new LabelRenderer(view, 
				layoutTemplate.getBrick(), pickingType.name(), pickingID);
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

		KaplanMeierCreator viewCreator = new KaplanMeierCreator(maxTimeValue);
		AGLView kaplanMeier = viewCreator.createRemoteView(brick, gl, glMouseListener);

		LayoutRenderer kaplanMeierRenderer = new ViewLayoutRenderer(kaplanMeier);

		views.put(EContainedViewType.KAPLAN_MEIER_VIEW, kaplanMeier);
		containedViewRenderers.put(EContainedViewType.KAPLAN_MEIER_VIEW,
				kaplanMeierRenderer);

		String label = "TODO";

		brick.setLabel(label, true);

		LayoutRenderer kaplanMeierSummaryRenderer = new KaplanMeierSummaryRenderer(brick,
				label, EPickingType.BRICK.name(), brick.getID());
		containedViewRenderers.put(EContainedViewType.KAPLAN_MEIER_SUMMARY,
				kaplanMeierSummaryRenderer);

		LayoutRenderer kaplanMeierSummaryCompactRenderer = new KaplanMeierSummaryRenderer(
				brick, "TODO", EPickingType.BRICK.name(), brick.getID());
		containedViewRenderers.put(EContainedViewType.KAPLAN_MEIER_SUMMARY,
				kaplanMeierSummaryCompactRenderer);

		// FIXME this is not really a comapct view
		containedViewRenderers.put(EContainedViewType.KAPLAN_MEIER_SUMMARY_COMPACT,
				kaplanMeierSummaryRenderer);

		brick.setViews(views);
		brick.setContainedViewRenderers(containedViewRenderers);
	}

	@Override
	public IBrickSortingStrategy getBrickSortingStrategy() {

		return sortingStrategy;
	}

	/**
	 * @param sortingStrategy
	 *            setter, see {@link #sortingStrategy}
	 */
	public void setSortingStrategy(ExternallyProvidedSortingStrategy sortingStrategy) {
		this.sortingStrategy = sortingStrategy;
	}

	@Override
	public boolean useDefaultWidth() {
		return true;
	}

	@Override
	public int getDefaultWidth() {
		return 100;
	}
	
	/**
	 * @param maxTimeValue setter, see {@link #maxTimeValue}
	 */
	public void setMaxTimeValue(float maxTimeValue) {
		this.maxTimeValue = maxTimeValue;
	}
}
