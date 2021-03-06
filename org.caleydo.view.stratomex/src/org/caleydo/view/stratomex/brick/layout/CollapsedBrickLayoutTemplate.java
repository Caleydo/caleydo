/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer.ETextureRotation;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.ui.HandleRenderer;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * Brick layout for a compact overview containing a view and a small fuel bar.
 *
 * @author Christian Partl
 *
 */
public class CollapsedBrickLayoutTemplate extends ABrickLayoutConfiguration {

	protected static final int BUTTON_HEIGHT_PIXELS = 16;
	protected static final int BUTTON_WIDTH_PIXELS = 16;
	protected static final int HANDLE_SIZE_PIXELS = 10;
	protected static final int FOOTER_BAR_HEIGHT_PIXELS = 4;

	protected static final int DEFAULT_HANDLES = HandleRenderer.MOVE_VERTICALLY_HANDLE;

	private static final int EXPAND_BUTTON_ID = 0;

	protected ArrayList<ElementLayout> footerBarElements;
	protected boolean showFooterBar;
	protected Row footerBar;

	protected int guiElementsHeight = 0;

	// private RelationIndicatorRenderer leftRelationIndicatorRenderer;
	// private RelationIndicatorRenderer rightRelationIndicatorRenderer;

	public CollapsedBrickLayoutTemplate(GLBrick brick, BrickColumn brickColumn, GLStratomex stratomex) {
		super(brick, brickColumn, stratomex);
		footerBarElements = new ArrayList<ElementLayout>();
		footerBar = new Row();
		registerPickingListeners();
		// leftRelationIndicatorRenderer = new RelationIndicatorRenderer(brick,
		// visBricks, true);
		// rightRelationIndicatorRenderer = new RelationIndicatorRenderer(
		// brick, visBricks, false);
	}

	// public void setLeftRelationIndicatorRenderer(
	// RelationIndicatorRenderer leftRelationIndicatorRenderer) {
	// this.leftRelationIndicatorRenderer = leftRelationIndicatorRenderer;
	// }
	//
	// public void setRightRelationIndicatorRenderer(
	// RelationIndicatorRenderer rightRelationIndicatorRenderer) {
	// this.rightRelationIndicatorRenderer = rightRelationIndicatorRenderer;
	// }

	@Override
	public void setStaticLayouts() {
		guiElementsHeight = 0;
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		baseElementLayout = baseRow;

		// leftRelationIndicatorRenderer.updateRelations();
		// rightRelationIndicatorRenderer.updateRelations();

		// ElementLayout leftRelationIndicatorLayout = new ElementLayout(
		// "RightRelationIndicatorLayout");
		// // rightRelationIndicatorLayout.setDebug(true);
		// leftRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		// leftRelationIndicatorLayout.setPixelSizeX(3);
		// leftRelationIndicatorLayout.setRenderer(leftRelationIndicatorRenderer);
		// baseRow.append(leftRelationIndicatorLayout);

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 1);

		// ElementLayout fuelBarLayout = new ElementLayout("fuelBarLayout");
		// fuelBarLayout.setFrameColor(0, 1, 0, 1);
		//
		// fuelBarLayout.setPixelGLConverter(pixelGLConverter);
		// fuelBarLayout.setPixelSizeY(FUEL_BAR_HEIGHT_PIXELS);
		// fuelBarLayout.setRenderer(new FuelBarRenderer(brick));

		baseRow.setRenderer(borderedAreaRenderer);

		footerBar = createFooterBar();

		if (handles == null) {
			handles = DEFAULT_HANDLES;
		}

		handleRenderer = new HandleRenderer(brick, 10, brick.getTextureManager(), handles);
		baseRow.addForeGroundRenderer(handleRenderer);

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setPixelSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		Row viewRow = new Row("compactViewRow");
		viewRow.setFrameColor(0, 0, 1, 1);
		// viewRow.setPixelGLConverter(pixelGLConverter);
		// viewRow.setPixelSizeY(16);

		if (viewLayout == null) {
			viewLayout = new ElementLayout("compactViewLayout");
			viewLayout.setFrameColor(1, 0, 0, 1);
			viewLayout.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
		}
		viewLayout.setRenderer(viewRenderer);

		ElementLayout expandButtonLayout = new ElementLayout("expandButtonLayout");
		expandButtonLayout.setFrameColor(1, 0, 0, 1);
		expandButtonLayout.setPixelSizeX(BUTTON_WIDTH_PIXELS);
		// expandButtonLayout.setRatioSizeX(0.2f);
		expandButtonLayout.setPixelSizeY(BUTTON_HEIGHT_PIXELS);

		expandButtonLayout.setRenderer(new ButtonRenderer.Builder(brick, new Button(EPickingType.BRICK_EXPAND_BUTTON
				.name(), EXPAND_BUTTON_ID, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE))
				.textureManager(brick.getTextureManager()).textureRotation(ETextureRotation.TEXTURE_ROTATION_180)
				.build());

		viewRow.append(viewLayout);
		viewRow.append(spacingLayoutX);
		viewRow.append(expandButtonLayout);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		// baseColumn.appendElement(dimensionBarLayout);
		baseColumn.append(spacingLayoutY);
		guiElementsHeight += SPACING_PIXELS;
		if (showFooterBar) {
			baseColumn.append(footerBar);
			baseColumn.append(spacingLayoutY);
			guiElementsHeight += SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS;
		}
		baseColumn.append(viewRow);
		baseColumn.append(spacingLayoutY);
		guiElementsHeight += SPACING_PIXELS;

		// ElementLayout rightRelationIndicatorLayout = new ElementLayout(
		// "RightRelationIndicatorLayout");
		// // rightRelationIndicatorLayout.setDebug(true);
		// rightRelationIndicatorLayout.setPixelGLConverter(pixelGLConverter);
		// rightRelationIndicatorLayout.setPixelSizeX(3);
		// rightRelationIndicatorLayout
		// .setRenderer(rightRelationIndicatorRenderer);
		// baseRow.append(rightRelationIndicatorLayout);

	}

	protected Row createFooterBar() {
		Row footerBar = new Row("footerBar");
		footerBar.setPixelSizeY(FOOTER_BAR_HEIGHT_PIXELS);

		for (ElementLayout element : footerBarElements) {
			footerBar.append(element);
		}

		return footerBar;
	}

	@Override
	protected void registerPickingListeners() {
		brick.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				brick.expand();

			}
		}, EPickingType.BRICK_EXPAND_BUTTON.name(), EXPAND_BUTTON_ID);
		brick.addIDPickingTooltipListener("Expand", EPickingType.BRICK_EXPAND_BUTTON.name(), EXPAND_BUTTON_ID);
	}

	@Override
	public int getMinHeightPixels() {
		if (viewRenderer == null)
			return guiElementsHeight + BUTTON_HEIGHT_PIXELS;
		return guiElementsHeight + Math.max(viewRenderer.getMinHeightPixels(), BUTTON_HEIGHT_PIXELS);
	}

	@Override
	public int getMinWidthPixels() {
		int footerBarWidth = showFooterBar ? calcSumPixelWidth(footerBar) : 0;
		footerBarWidth += 2 * SPACING_PIXELS;

		if (viewRenderer == null)
			return Math.max(footerBarWidth, (2 * SPACING_PIXELS) + BUTTON_WIDTH_PIXELS);
		return Math.max(footerBarWidth, (3 * SPACING_PIXELS) + viewRenderer.getMinWidthPixels() + BUTTON_WIDTH_PIXELS);
	}

	// @Override
	// protected void setValidViewTypes() {
	// validViewTypes.add(EContainedViewType.OVERVIEW_HEATMAP_COMPACT);
	// }

	// @Override
	// public EContainedViewType getDefaultViewType() {
	// return EContainedViewType.OVERVIEW_HEATMAP_COMPACT;
	// }

	// @Override
	// public void viewTypeChanged(EContainedViewType viewType) {
	//
	// }

	@Override
	public void setLockResizing(boolean lockResizing) {
		// TODO Auto-generated method stub

	}

	@Override
	public ABrickLayoutConfiguration getCollapsedLayoutTemplate() {
		return this;
	}

	@Override
	public ABrickLayoutConfiguration getExpandedLayoutTemplate() {
		return new DefaultBrickLayoutTemplate(brick, brickColumn, stratomex);
	}

	/**
	 * Sets the elements that should appear in the footer bar. The elements will placed from left to right using the
	 * order of the specified list.
	 *
	 * @param footerBarElements
	 */
	public void setFooterBarElements(ArrayList<ElementLayout> footerBarElements) {
		this.footerBarElements = footerBarElements;
	}

	/**
	 * Sets whether the footer bar shall be displayed.
	 *
	 * @param showFooterBar
	 */
	public void showFooterBar(boolean showFooterBar) {
		this.showFooterBar = showFooterBar;
	}

	@Override
	public void destroy() {
		super.destroy();
		brick.removeAllIDPickingListeners(EPickingType.BRICK_EXPAND_BUTTON.name(), EXPAND_BUTTON_ID);
	}

	@Override
	public void configure(IBrickConfigurer configurer) {
		configurer.configure(this);
	}

	@Override
	public ToolBar getToolBar() {
		// No toolbar
		return null;
	}

}
