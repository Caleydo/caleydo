package org.caleydo.view.datagraph.nodelayout;

import java.awt.geom.Point2D;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.datagraph.ADataContainerRenderer;
import org.caleydo.view.datagraph.GLDataGraph;

public abstract class ANodeLayout {

	protected final static int SPACING_PIXELS = 4;
	protected final static int CAPTION_HEIGHT_PIXELS = 16;
	protected final static int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	protected final static int MIN_DATA_CONTAINER_HEIGHT_PIXELS = 32;
	protected final static int MIN_DATA_CONTAINER_WIDTH_PIXELS = 200;

	protected GLDataGraph view;
	protected ADataContainerRenderer dataContainerRenderer;
	protected DragAndDropController dragAndDropController;

	public ANodeLayout(GLDataGraph view,
			DragAndDropController dragAndDropController) {
		this.view = view;
		this.dragAndDropController = dragAndDropController;
	}

	protected Row createDefaultBaseRow(float[] color, int pickingID) {
		Row baseRow = new Row("baseRow");
		baseRow.setFrameColor(0, 0, 1, 0);

		BorderedAreaRenderer borderedAreaRenderer = new BorderedAreaRenderer(
				view, PickingType.DATA_GRAPH_NODE, pickingID);
		borderedAreaRenderer.setColor(color);

		baseRow.setRenderer(borderedAreaRenderer);

		return baseRow;
	}

	protected ElementLayout createDefaultSpacingX() {
		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(view.getPixelGLConverter());
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);
		return spacingLayoutX;
	}

	protected ElementLayout createDefaultSpacingY() {
		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(view.getPixelGLConverter());
		spacingLayoutX.setRatioSizeX(0);
		spacingLayoutX.setPixelSizeY(SPACING_PIXELS);
		return spacingLayoutX;
	}

	protected ElementLayout createDefaultCaptionLayout(String caption,
			int pickingID) {
		ElementLayout captionLayout = new ElementLayout("caption");
		captionLayout.setPixelGLConverter(view.getPixelGLConverter());
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setRatioSizeX(1);
		captionLayout.setRenderer(new LabelRenderer(view, caption,
				PickingType.DATA_GRAPH_NODE, pickingID));

		return captionLayout;
	}

	protected ElementLayout createDefaultLineSeparatorLayout() {
		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelGLConverter(view.getPixelGLConverter());
		lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		return lineSeparatorLayout;
	}

	public int getHeightPixels() {
		return 3
				* SPACING_PIXELS
				+ CAPTION_HEIGHT_PIXELS
				+ LINE_SEPARATOR_HEIGHT_PIXELS
				+ Math.max(MIN_DATA_CONTAINER_HEIGHT_PIXELS,
						dataContainerRenderer.getMinHeightPixels());
	}

	public int getWidthPixels() {
		return 2
				* SPACING_PIXELS
				+ Math.max(MIN_DATA_CONTAINER_WIDTH_PIXELS,
						dataContainerRenderer.getMinWidthPixels());
	}

//	public Pair<Point2D, Point2D> getAnchorPointsOfDimensionGroup(
//			ADimensionGroupData dimensionGroupData) {
//		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
//
//		float spacingX = pixelGLConverter
//				.getGLWidthForPixelWidth(SPACING_PIXELS);
//		float spacingY = pixelGLConverter
//				.getGLHeightForPixelHeight(SPACING_PIXELS);
//
//		Pair<Point2D, Point2D> anchorPoints = dataContainerRenderer
//				.getAnchorPointsOfDimensionGroup(dimensionGroupData);
//
//		Point2D first = (Point2D) anchorPoints.getFirst().clone();
//		Point2D second = (Point2D) anchorPoints.getSecond().clone();
//
//		first.setLocation(anchorPoints.getFirst().getX() + spacingX,
//				anchorPoints.getFirst().getY() + spacingY);
//		second.setLocation(anchorPoints.getSecond().getX() + spacingX,
//				anchorPoints.getSecond().getY() + spacingY);
//
//		return new Pair<Point2D, Point2D>(first, second);
//	}

	public abstract ElementLayout setupLayout();

	public abstract void update();

	public abstract void destroy();
	
	public abstract Class<? extends IDataDomain> getDataDomainType();

}
