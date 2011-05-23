package org.caleydo.view.visbricks.brick.layout;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.brick.ui.BackGroundRenderer;
import org.caleydo.view.visbricks.brick.ui.BorderedAreaRenderer;
import org.caleydo.view.visbricks.dimensiongroup.DimensionGroup;

public class CompactCentralBrickLayoutTemplate extends ABrickLayoutTemplate {

	protected static final int CAPTION_HEIGHT_PIXELS = 16;
	protected static final int FOOTER_BAR_HEIGHT_PIXELS = 12;
	protected static final int LINE_SEPARATOR_HEIGHT_PIXELS = 3;

	private GLVisBricks visBricks;

	protected ArrayList<ElementLayout> headerBarElements;
	protected ArrayList<ElementLayout> footerBarElements;

	protected boolean showFooterBar;

	public CompactCentralBrickLayoutTemplate(GLBrick brick,
			DimensionGroup dimensionGroup, GLVisBricks visBricks,
			IBrickConfigurer configurer) {
		super(brick, dimensionGroup);
		this.visBricks = visBricks;
		headerBarElements = new ArrayList<ElementLayout>();
		footerBarElements = new ArrayList<ElementLayout>();
		configurer.configure(this);
		registerPickingListeners();
	}

	@Override
	public void setStaticLayouts() {
		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);
		setBaseElementLayout(baseRow);

		Column baseColumn = new Column("baseColumn");
		baseColumn.setFrameColor(0, 1, 0, 1);
		// baseColumn.setDebug(true);

		baseRow.setRenderer(new BorderedAreaRenderer(brick));

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setPixelSizeY(0);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		// viewRow.setDebug(true);
		// viewRow.setPixelGLConverter(pixelGLConverter);
		// viewRow.setPixelSizeY(16);

		ElementLayout viewLayout = new ElementLayout("compactViewLayout");
		viewLayout.setFrameColor(1, 0, 0, 1);
		// viewLayout.setDebug(true);
		viewLayout.addBackgroundRenderer(new BackGroundRenderer(brick));
		viewLayout.setRenderer(viewRenderer);

		// ElementLayout dimensionBarLayout = new ElementLayout("dimensionBar");
		// dimensionBarLayout.setFrameColor(1, 0, 1, 0);
		// dimensionBarLayout.setPixelGLConverter(pixelGLConverter);
		// dimensionBarLayout.setPixelSizeY(DIMENSION_BAR_HEIGHT_PIXELS);
		// dimensionBarLayout.setRenderer(new DimensionBarRenderer(brick));

		ElementLayout spacingLayoutY = new ElementLayout("spacingLayoutY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
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
		if (showFooterBar) {
			baseColumn.append(footerBar);
			baseColumn.append(spacingLayoutY);
		}
		baseColumn.append(viewLayout);
		// baseColumn.append(spacingLayoutY);
		// baseColumn.append(lineSeparatorLayout);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(headerBar);
		baseColumn.append(spacingLayoutY);

	}

	protected Row createHeaderBar() {
		Row headerBar = new Row();
		headerBar.setPixelGLConverter(pixelGLConverter);
		headerBar.setPixelSizeY(CAPTION_HEIGHT_PIXELS);

		for (ElementLayout element : headerBarElements) {
			headerBar.append(element);
		}

		return headerBar;
	}

	protected Row createFooterBar() {
		Row footerBar = new Row("footerBar");
		footerBar.setPixelGLConverter(pixelGLConverter);
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
		return 5 * SPACING_PIXELS + FOOTER_BAR_HEIGHT_PIXELS
				+ LINE_SEPARATOR_HEIGHT_PIXELS + CAPTION_HEIGHT_PIXELS
				+ viewRenderer.getMinHeightPixels();
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
	public ABrickLayoutTemplate getCollapsedLayoutTemplate() {
		return this;
	}

	@Override
	public ABrickLayoutTemplate getExpandedLayoutTemplate() {
		return new CentralBrickLayoutTemplate(brick, dimensionGroup, visBricks,
				brick.getBrickConfigurer());
	}

	public boolean isShowFooterBar() {
		return showFooterBar;
	}

	public void showFooterBar(boolean showFooterBar) {
		this.showFooterBar = showFooterBar;
	}

	public void setHeaderBarElements(ArrayList<ElementLayout> headerBarElements) {
		this.headerBarElements = headerBarElements;
	}

	public void setFooterBarElements(ArrayList<ElementLayout> footerBarElements) {
		this.footerBarElements = footerBarElements;
	}

}
