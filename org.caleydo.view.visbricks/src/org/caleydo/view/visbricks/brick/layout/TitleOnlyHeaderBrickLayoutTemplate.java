package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.configurer.IBrickConfigurer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

/**
 * Layout for header brick that shows only the title.
 * 
 * @author Marc Streit
 * 
 */
public class TitleOnlyHeaderBrickLayoutTemplate extends ABrickLayoutConfiguration {

	protected static final int HEADER_BAR_HEIGHT_PIXELS = 10;

	private GLVisBricks visBricks;

	protected ArrayList<ElementLayout> headerBarElements;
	
	protected int guiElementsHeight = 0;

	public TitleOnlyHeaderBrickLayoutTemplate(GLBrick brick, DimensionGroup dimensionGroup,
			GLVisBricks visBricks, IBrickConfigurer configurer) {
		super(brick, dimensionGroup);
		this.visBricks = visBricks;
		headerBarElements = new ArrayList<ElementLayout>();

		configurer.configure(this);
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
		return visBricks.getSideArchWidthPixels();
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
		return new HeaderBrickLayoutTemplate(brick, dimensionGroup, visBricks,
				brick.getBrickConfigurer());
	}

	/**
	 * Sets the elements that should appear in the header bar. The elements will
	 * placed from left to right using the order of the specified list.
	 * 
	 * @param headerBarElements
	 */
	public void setHeaderBarElements(ArrayList<ElementLayout> headerBarElements) {
		this.headerBarElements = headerBarElements;
	}
}