package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

/**
 * Layout for central brick that is displayed in the arch.
 * 
 * @author Partl
 * 
 */
public class CompactHeaderBrickLayoutTemplate extends ABrickLayoutConfiguration {

	protected static final int HEADER_BAR_HEIGHT_PIXELS = 16;
	protected static final int FOOTER_BAR_HEIGHT_PIXELS = 12;
	protected static final int LINE_SEPARATOR_HEIGHT_PIXELS = 3;

	private GLVisBricks visBricks;

	protected ArrayList<ElementLayout> headerBarElements;
	protected ArrayList<ElementLayout> footerBarElements;

	// protected Row headerBar;
	// protected Row footerBar;

	protected boolean showFooterBar;

	protected int guiElementsHeight = 0;

	public CompactHeaderBrickLayoutTemplate(GLBrick brick,
			DimensionGroup dimensionGroup, GLVisBricks visBricks,
			IBrickConfigurer configurer) {
		super(brick, dimensionGroup);
		this.visBricks = visBricks;
		headerBarElements = new ArrayList<ElementLayout>();
		footerBarElements = new ArrayList<ElementLayout>();
		// headerBar = new Row();
		// footerBar = new Row();
		configurer.configure(this);
		registerPickingListeners();
	}

	@Override
	public void setStaticLayouts() {
		guiElementsHeight = 0;
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		baseElementLayout = baseRow;

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 1);
		// baseColumn.setDebug(true);

		baseRow.setRenderer(borderedAreaRenderer);

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setPixelSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		// viewRow.setDebug(true);
		// viewRow.setPixelGLConverter(pixelGLConverter);
		// viewRow.setPixelSizeY(16);

		if (viewLayout == null) {
			viewLayout = new ElementLayout("compactViewLayout");
			viewLayout.setFrameColor(1, 0, 0, 1);
			// viewLayout.setDebug(true);
			viewLayout
					.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
		}
		viewLayout.setRenderer(viewRenderer);

		// ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		// dimensionBarLayout.setFrameColor(1, 0, 1, 0);
		// dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		// dimensionBarLayout.setPixelSizeY(DIMENSION_BAR_HEIGHT_PIXELS);
		// dimensionBarLayout.setRenderer(new DimensionBarRenderer(brick));

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setPixelSizeX(0);

		Row headerBar = createHeaderBar();
		Row footerBar = createFooterBar();
		// captionRow.append(spacingLayoutX);

		// ElementLayout lineSeparatorLayout = new
		// ElementLayout("lineSeparator");
		// lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
		// lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		// lineSeparatorLayout.setRatioSizeX(1);
		// lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		// baseColumn.appendElement(dimensionBarLayout);
		baseColumn.append(spacingLayoutY);
		guiElementsHeight += SPACING_PIXELS;
		if (showFooterBar) {
			baseColumn.append(footerBar);
			baseColumn.append(spacingLayoutY);
			guiElementsHeight += SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS;
		}
		baseColumn.append(viewLayout);
		// baseColumn.append(spacingLayoutY);
		// baseColumn.append(lineSeparatorLayout);
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

	// @Override
	// public void viewTypeChanged(EContainedViewType viewType) {
	//
	// }

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
	 * @return True, if the footer bar is shown, false otherwise.
	 */
	public boolean isShowFooterBar() {
		return showFooterBar;
	}

	/**
	 * Specifies whether the footer bar shall be shown.
	 * 
	 * @param showFooterBar
	 */
	public void showFooterBar(boolean showFooterBar) {
		this.showFooterBar = showFooterBar;
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

	/**
	 * Sets the elements that should appear in the footer bar. The elements will
	 * placed from left to right using the order of the specified list.
	 * 
	 * @param footerBarElements
	 */
	public void setFooterBarElements(ArrayList<ElementLayout> footerBarElements) {
		this.footerBarElements = footerBarElements;
	}

}
