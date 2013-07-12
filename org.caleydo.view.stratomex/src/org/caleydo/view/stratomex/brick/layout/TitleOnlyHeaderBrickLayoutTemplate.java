/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.brick.layout;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * Layout for header brick that shows only the title.
 *
 * @author Marc Streit
 *
 */
public class TitleOnlyHeaderBrickLayoutTemplate extends ABrickLayoutConfiguration {

	protected static final int HEADER_BAR_HEIGHT_PIXELS = 10;

	protected List<ElementLayout> headerBarElements;

	protected int guiElementsHeight = 0;

	public TitleOnlyHeaderBrickLayoutTemplate(GLBrick brick, BrickColumn brickColumn, GLStratomex stratomex) {
		super(brick, brickColumn, stratomex);
		headerBarElements = new ArrayList<ElementLayout>();
		registerPickingListeners();
	}

	@Override
	public void setStaticLayouts() {
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		baseElementLayout = baseRow;

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 1);

		baseRow.setRenderer(borderedAreaRenderer);

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setPixelSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		Row headerBar = createHeaderBar();

		baseColumn.append(spacingLayoutY);
		guiElementsHeight += SPACING_PIXELS;

		baseColumn.append(spacingLayoutY);
		baseColumn.append(headerBar);
		baseColumn.append(spacingLayoutY);
		guiElementsHeight += (2 * SPACING_PIXELS) + HEADER_BAR_HEIGHT_PIXELS;

	}

	protected Row createHeaderBar() {
		Row headerBar = new Row();
		headerBar.setPixelSizeY(HEADER_BAR_HEIGHT_PIXELS);

		for (ElementLayout element : headerBarElements) {
			headerBar.append(element);
		}

		return headerBar;
	}

	@Override
	protected void registerPickingListeners() {

	}

	@Override
	public int getMinHeightPixels() {
		if (viewRenderer == null) {
			return 50;
		}
		return guiElementsHeight + viewRenderer.getMinHeightPixels();
	}

	@Override
	public int getMinWidthPixels() {
		// TODO: maybe something different
		return stratomex.getSideArchWidthPixels();
	}

	@Override
	public void setLockResizing(boolean lockResizing) {

	}

	@Override
	public ABrickLayoutConfiguration getCollapsedLayoutTemplate() {
		return this;
	}

	@Override
	public ABrickLayoutConfiguration getExpandedLayoutTemplate() {
		return new HeaderBrickLayoutTemplate(brick, brickColumn, stratomex);
	}

	/**
	 * Sets the elements that should appear in the header bar. The elements will placed from left to right using the
	 * order of the specified list.
	 *
	 * @param headerBarElements
	 */
	public void setHeaderBarElements(List<ElementLayout> headerBarElements) {
		this.headerBarElements = headerBarElements;
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
