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
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.view.stratomex.EPickingType;
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
import org.caleydo.view.stratomex.brick.ui.BrickViewSwitchingButton;
import org.caleydo.view.stratomex.brick.ui.DimensionBarRenderer;
import org.caleydo.view.stratomex.brick.ui.FuelBarRenderer;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * Abstract base class for brick configurers for table based data that provides mainly helper functions.
 *
 * @author Partl
 *
 */
public abstract class ATableBasedDataConfigurer implements IBrickConfigurer {

	protected static final int FUELBAR_HEIGHT_PIXELS = 4;
	public static final int CAPTION_HEIGHT_PIXELS = 14;
	protected static final int DIMENSION_BAR_HEIGHT_PIXELS = 12;
	protected static final int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int HANDLE_SIZE_PIXELS = 8;
	protected static final int SPACING_PIXELS = 4;

	protected static final int CLUSTER_BUTTON_ID = 1;

	protected TablePerspective tablePerspective;

	public ATableBasedDataConfigurer(TablePerspective dimensionGroupData) {
		this.tablePerspective = dimensionGroupData;
	}

	protected ArrayList<ElementLayout> createHeaderBarElements(HeaderBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		List<Pair<String, Integer>> pickingIDs = new ArrayList<>();
		pickingIDs.add(new Pair<String, Integer>(EPickingType.DIMENSION_GROUP.name(), layoutTemplate
				.getDimensionGroup().getID()));
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK_TITLE.name(), layoutTemplate.getBrick().getID()));

		LabelRenderer captionRenderer = new LabelRenderer(layoutTemplate.getDimensionGroup().getStratomexView(),
				layoutTemplate.getBrick(), pickingIDs);
		captionLayout.setRenderer(captionRenderer);

		headerBarElements.add(captionLayout);
		headerBarElements.add(spacingLayoutX);

		return headerBarElements;
	}

	protected ArrayList<ElementLayout> createHeaderBarElements(CompactHeaderBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		List<Pair<String, Integer>> pickingIDs = new ArrayList<>();
		pickingIDs.add(new Pair<String, Integer>(EPickingType.DIMENSION_GROUP.name(), layoutTemplate
				.getDimensionGroup().getID()));
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK_TITLE.name(), layoutTemplate.getBrick().getID()));

		LabelRenderer captionRenderer = new LabelRenderer(layoutTemplate.getDimensionGroup().getStratomexView(),
				layoutTemplate.getBrick(), pickingIDs);
		captionLayout.setRenderer(captionRenderer);

		headerBarElements.add(captionLayout);

		return headerBarElements;
	}

	protected ArrayList<ElementLayout> createHeaderBarElements(TitleOnlyHeaderBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setFrameColor(0, 0, 1, 1);

		List<Pair<String, Integer>> pickingIDs = new ArrayList<>();
		pickingIDs.add(new Pair<String, Integer>(EPickingType.DIMENSION_GROUP.name(), layoutTemplate
				.getDimensionGroup().getID()));
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK_TITLE.name(), layoutTemplate.getBrick().getID()));

		LabelRenderer captionRenderer = new LabelRenderer(layoutTemplate.getDimensionGroup(),
				layoutTemplate.getBrick(), pickingIDs);
		captionLayout.setRenderer(captionRenderer);

		headerBarElements.add(captionLayout);

		return headerBarElements;
	}

	/**
	 * Create the elements which should be shown in the heading of cluster bricks.
	 *
	 * @param layoutTemplate
	 * @return Returns null if no header bar should be shown, else the elements for the layout
	 */
	protected ArrayList<ElementLayout> createHeaderBarElements(DefaultBrickLayoutTemplate layoutTemplate) {

		ArrayList<ElementLayout> headerBarElements = new ArrayList<ElementLayout>();

		ElementLayout captionLayout = new ElementLayout("caption1");

		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);

		List<Pair<String, Integer>> pickingIDs = new ArrayList<>();
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK.name(), layoutTemplate.getBrick().getID()));
		pickingIDs.add(new Pair<String, Integer>(EPickingType.BRICK_TITLE.name(), layoutTemplate.getBrick().getID()));

		LabelRenderer captionRenderer = new LabelRenderer(layoutTemplate.getDimensionGroup().getStratomexView(),
				layoutTemplate.getBrick(), pickingIDs);

		captionLayout.setRenderer(captionRenderer);

		headerBarElements.add(captionLayout);

		return headerBarElements;
	}

	protected void registerViewSwitchingButtons(final ABrickLayoutConfiguration layoutTemplate,
			final ArrayList<BrickViewSwitchingButton> viewSwitchingButtons, final GLBrick brick,
			final BrickColumn dimensionGroup) {

		for (final BrickViewSwitchingButton button : viewSwitchingButtons) {

			brick.addIDPickingListener(new APickingListener() {

				@Override
				public void clicked(Pick pick) {
					for (BrickViewSwitchingButton button : viewSwitchingButtons) {
						button.setSelected(false);
					}
					button.setSelected(true);

					if (!(layoutTemplate instanceof HeaderBrickLayoutTemplate)
							&& dimensionGroup.isGlobalViewSwitching()) {
						dimensionGroup.switchBrickViews(button.getViewType());
					} else {
						brick.setBrickViewTypeAndConfigureSize(button.getViewType());
					}
					dimensionGroup.updateLayout();
				}
			}, button.getPickingType(), button.getButtonID());

			layoutTemplate.registerViewTypeChangeListener(button);
		}
	}

	protected ArrayList<ElementLayout> createToolBarElements(HeaderBrickLayoutTemplate layoutTemplate,
			ArrayList<BrickViewSwitchingButton> viewSwitchingButtons) {

		final GLBrick brick = layoutTemplate.getBrick();
		final BrickColumn dimensionGroup = layoutTemplate.getDimensionGroup();

		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		for (int i = 0; i < viewSwitchingButtons.size(); i++) {
			BrickViewSwitchingButton button = viewSwitchingButtons.get(i);
			ElementLayout buttonLayout = new ElementLayout();
			buttonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
			buttonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
			buttonLayout.setRenderer(new ButtonRenderer(button, brick, brick.getTextureManager(),
					DefaultBrickLayoutTemplate.BUTTON_Z));
			toolBarElements.add(buttonLayout);
			if (i != viewSwitchingButtons.size() - 1) {
				toolBarElements.add(spacingLayoutX);
			}
		}
		registerViewSwitchingButtons(layoutTemplate, viewSwitchingButtons, brick, dimensionGroup);

		return toolBarElements;
	}

	protected ArrayList<ElementLayout> createFooterBarElements(HeaderBrickLayoutTemplate layoutTemplate) {
		return createSummaryFooterBarElements(layoutTemplate);

	}

	protected ArrayList<ElementLayout> createFooterBarElements(CompactHeaderBrickLayoutTemplate layoutTemplate) {
		return createSummaryFooterBarElements(layoutTemplate);

	}

	private ArrayList<ElementLayout> createSummaryFooterBarElements(ABrickLayoutConfiguration layoutTemplate) {

		ArrayList<ElementLayout> footerBarElements = new ArrayList<ElementLayout>();

		GLBrick brick = layoutTemplate.getBrick();

		ElementLayout dimensionBarLaylout = new ElementLayout("dimensionBar");
		dimensionBarLaylout.setPixelSizeY(DIMENSION_BAR_HEIGHT_PIXELS);
		dimensionBarLaylout.setRatioSizeX(1);
		dimensionBarLaylout.setRenderer(new DimensionBarRenderer(brick.getDataDomain().getTable()
				.getDefaultDimensionPerspective().getVirtualArray(), brick.getTablePerspective()
				.getDimensionPerspective().getVirtualArray()));

		footerBarElements.add(dimensionBarLaylout);

		return footerBarElements;

	}

	protected ArrayList<ElementLayout> createFooterBarElements(DefaultBrickLayoutTemplate layoutTemplate) {

		return createDefaultFooterBarElements(layoutTemplate);
	}

	protected ArrayList<ElementLayout> createFooterBarElements(DetailBrickLayoutTemplate layoutTemplate) {

		return createDefaultFooterBarElements(layoutTemplate);
	}

	protected ArrayList<ElementLayout> createFooterBarElements(CollapsedBrickLayoutTemplate layoutTemplate) {

		return createDefaultFooterBarElements(layoutTemplate);
	}

	private ArrayList<ElementLayout> createDefaultFooterBarElements(ABrickLayoutConfiguration layoutTemplate) {
		ArrayList<ElementLayout> footerBarElements = new ArrayList<ElementLayout>();

		GLBrick brick = layoutTemplate.getBrick();

		ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		fuelBarLayout.setFrameColor(0, 1, 0, 0);
		fuelBarLayout.setPixelSizeY(FUELBAR_HEIGHT_PIXELS);
		fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

		footerBarElements.add(fuelBarLayout);

		return footerBarElements;
	}

	protected ArrayList<ElementLayout> createToolBarElements(ABrickLayoutConfiguration layoutTemplate,
			ArrayList<BrickViewSwitchingButton> viewSwitchingButtons) {

		final GLBrick brick = layoutTemplate.getBrick();
		final BrickColumn dimensionGroup = layoutTemplate.getDimensionGroup();

		ArrayList<ElementLayout> toolBarElements = new ArrayList<ElementLayout>();

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		for (int i = 0; i < viewSwitchingButtons.size(); i++) {
			BrickViewSwitchingButton button = viewSwitchingButtons.get(i);
			ElementLayout buttonLayout = new ElementLayout();
			buttonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
			buttonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);
			buttonLayout.setRenderer(new ButtonRenderer(button, brick, brick.getTextureManager(),
					DefaultBrickLayoutTemplate.BUTTON_Z));
			toolBarElements.add(buttonLayout);
			if (i != viewSwitchingButtons.size() - 1) {
				toolBarElements.add(spacingLayoutX);
			}
		}

		ElementLayout ratioSpacingLayoutX = new ElementLayout("ratioSpacingLayoutX");
		ratioSpacingLayoutX.setRatioSizeX(1);
		ratioSpacingLayoutX.setRatioSizeY(0);

		toolBarElements.add(ratioSpacingLayoutX);

		// layoutTemplate.setViewSwitchingButtons(viewSwitchingButtons);

		registerViewSwitchingButtons(layoutTemplate, viewSwitchingButtons, brick, dimensionGroup);

		return toolBarElements;
	}

	@Override
	public IBrickSortingStrategy getBrickSortingStrategy() {

		return new NoSortingSortingStrategy();
	}
}
