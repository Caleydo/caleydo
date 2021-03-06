/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.configurer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.TablePerspectiveActions;
import org.caleydo.core.data.perspective.table.TableDoubleLists;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.function.IDoubleIterator;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;
import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormViewSwitchingBar;
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
import org.caleydo.view.stratomex.brick.sorting.ExternallyProvidedSortingStrategy;
import org.caleydo.view.stratomex.brick.sorting.IBrickSortingStrategy;
import org.caleydo.view.stratomex.brick.sorting.NoSortingSortingStrategy;
import org.caleydo.view.stratomex.brick.ui.KaplanMeierSummaryRenderer;
import org.caleydo.view.stratomex.column.BrickColumn;

import com.google.common.collect.Maps;

/**
 * Configurer for bricks that display numerical clinical data, such as disease free survival etc.
 *
 * @author Marc Streit
 *
 */
public class ClinicalDataConfigurer extends ABrickConfigurer {

	protected static final int CAPTION_HEIGHT_PIXELS = 16;
	protected static final int SPACING_PIXELS = 32;

	private IBrickSortingStrategy sortingStrategy = new NoSortingSortingStrategy();

	/**
	 *
	 */
	public ClinicalDataConfigurer() {
	}

	public static ClinicalDataConfigurer create(GLStratomex handler, TablePerspective underlying,
			TablePerspective kaplan) {
		ClinicalDataConfigurer dataConfigurer = null;
		BrickColumn brickColumn = handler.getBrickColumnManager().getBrickColumn(underlying);
		if (brickColumn != null) {
			// dependent sorting
			dataConfigurer = new ClinicalDataConfigurer();
			ExternallyProvidedSortingStrategy sortingStrategy = new ExternallyProvidedSortingStrategy();
			sortingStrategy.setExternalBrick(brickColumn);
			HashMap<Perspective, Perspective> m = Maps.newHashMap();
			m.put(kaplan.getRecordPerspective(), underlying.getRecordPerspective());
			sortingStrategy.setHashConvertedRecordPerspectiveToOrginalRecordPerspective(m);
			dataConfigurer.setSortingStrategy(sortingStrategy);
		}
		return dataConfigurer;
	}

	@Override
	public void configure(HeaderBrickLayoutTemplate layoutTemplate) {
		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate);

		layoutTemplate.setHeaderBarElements(createHeaderBarElements(layoutTemplate));
		layoutTemplate.setToolBarElements(toolBarElements);
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

		layoutTemplate.showFooterBar(true);
	}

	@Override
	public void configure(TitleOnlyHeaderBrickLayoutTemplate layoutTemplate) {
		layoutTemplate.setHeaderBarElements(createHeaderBarElements(layoutTemplate));
	}

	@Override
	public void configure(DefaultBrickLayoutTemplate layoutTemplate) {
		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate);

		List<Pair<String, Integer>> pickingIDs = new ArrayList<>();
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK.name(), layoutTemplate.getBrick().getID()));
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK_TITLE.name(), layoutTemplate.getBrick().getID()));

//		toolBarElements.add(createCaptionLayout(layoutTemplate, pickingIDs, layoutTemplate.getBrick().getBrickColumn()
//				.getStratomexView()));
//		toolBarElements.add(createSpacingLayout(layoutTemplate, true));

		layoutTemplate.setToolBarElements(toolBarElements);

		layoutTemplate.setShowFooterBar(false);
	}

	@Override
	public void configure(DetailBrickLayoutTemplate layoutTemplate) {
		ArrayList<ElementLayout> toolBarElements = createToolBarElements(layoutTemplate);

		layoutTemplate.setToolBarElements(toolBarElements);
		layoutTemplate.showFooterBar(false);
	}

	private List<ElementLayout> createHeaderBarElements(ABrickLayoutConfiguration layoutTemplate) {
		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		List<Pair<String, Integer>> pickingIDs = new ArrayList<>();
		pickingIDs.add(new Pair<String, Integer>(EPickingType.DIMENSION_GROUP.name(), layoutTemplate
				.getDimensionGroup().getID()));
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK_TITLE.name(), layoutTemplate.getBrick().getID()));

		headerBarElements.add(createCaptionLayout(layoutTemplate, pickingIDs, layoutTemplate.getDimensionGroup()
				.getStratomexView()));
		return headerBarElements;
	}

	private ElementLayout createCaptionLayout(ABrickLayoutConfiguration layoutTemplate,
			List<Pair<String, Integer>> pickingIDs, AGLView view) {

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		LabelRenderer captionRenderer = new LabelRenderer(view, layoutTemplate.getBrick().getTextRenderer(),
				layoutTemplate.getBrick(), pickingIDs);
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

	@Override
	public void setBrickViews(GLBrick brick, ABrickLayoutConfiguration brickLayout) {
		List<TablePerspective> tablePerspectives = brick.getTablePerspectives();
		boolean hasMixedValues = hasMixedValues(tablePerspectives);
		EEmbeddingID embeddingID;
		if (brick.isHeaderBrick()) {
			embeddingID = hasMixedValues ? EEmbeddingID.CLINICAL_NUMERICAL_HEADER_BRICK
					: EEmbeddingID.CLINICAL_HEADER_BRICK;
		} else {
			embeddingID = hasMixedValues ? EEmbeddingID.CLINICAL_NUMERICAL_SEGMENT_BRICK
					: EEmbeddingID.CLINICAL_SEGMENT_BRICK;
		}

		Set<String> remoteRenderedViewIDs = ViewManager.get().getRemotePlugInViewIDs(GLStratomex.VIEW_TYPE,
				embeddingID.id());

		List<String> viewIDs = new ArrayList<>(remoteRenderedViewIDs);
		Collections.sort(viewIDs);

		MultiFormRenderer multiFormRenderer = new MultiFormRenderer(brick.getStratomex(), true);

		int globalRendererID = 0;
		int localRendererID = -1;
		
		String brickEventSpace = EventPublisher.INSTANCE.createUniqueEventSpace();
		for (String viewID : remoteRenderedViewIDs) {
			localRendererID = multiFormRenderer.addPluginVisualization(viewID, brick.getStratomex().getViewType(),
					embeddingID.id(), tablePerspectives, brickEventSpace);
			brick.associateIDs(globalRendererID++, localRendererID);
		}

		ALayoutRenderer kaplanMeierSummaryRenderer = new KaplanMeierSummaryRenderer(brick.getBrickColumn()
				.getStratomexView(), brick.getLabel(), EPickingType.BRICK.name(), brick.getID());

		IEmbeddedVisualizationInfo visInfo = new DefaultVisInfo() {
			@Override
			public String getLabel() {
				return "Kaplan-Meier Summary";
			}
		};

		int compactRendererID = multiFormRenderer.addLayoutRenderer(kaplanMeierSummaryRenderer, null, visInfo, false);
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

	/**
	 * checks whether the data behind the first {@link TablePerspective} has positive as well as negative raw values
	 *
	 * @param tablePerspectives
	 * @return
	 */
	private static boolean hasMixedValues(List<TablePerspective> tablePerspectives) {
		if (tablePerspectives.isEmpty())
			return false;
		TablePerspective p = tablePerspectives.get(0);
		if (p.getParentTablePerspective() != null)
			p = p.getParentTablePerspective();
		boolean hasNegative = false;
		boolean hasPositive = false;
		IDoubleList list = TableDoubleLists.asRawList(p);
		for (IDoubleIterator it = list.iterator(); it.hasNext();) {
			double d = it.nextPrimitive();
			hasNegative = hasNegative || d < 0;
			hasPositive = hasPositive || d >= 0;
			if (hasNegative && hasPositive) // early abort
				return true;
		}
		return false;
	}

	protected ArrayList<ElementLayout> createToolBarElements(ABrickLayoutConfiguration layoutTemplate) {

		final GLBrick brick = layoutTemplate.getBrick();
		MultiFormViewSwitchingBar viewSwitchingBar = brick.getViewSwitchingBar();
		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		toolBarElements.add(viewSwitchingBar);

		return toolBarElements;
	}

	@Override
	public IBrickSortingStrategy getBrickSortingStrategy() {

		return sortingStrategy;
	}

	/**
	 * @param sortingStrategy
	 *            setter, see {@link #sortingStrategy}
	 */
	public void setSortingStrategy(IBrickSortingStrategy sortingStrategy) {
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

	@Override
	public boolean distributeBricksUniformly() {
		return true;
	}

	@Override
	public void addDataSpecificContextMenuEntries(ContextMenuCreator creator, GLBrick brick) {
		TablePerspectiveActions.add(creator, brick.getTablePerspective(), this, true);
	}

}
