package org.caleydo.view.datagraph.node;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.datagraph.ForceDirectedGraphLayout;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;

public abstract class ADefaultTemplateNode extends ADraggableDataGraphNode {

	protected final static int SPACING_PIXELS = 4;
	protected final static int CAPTION_HEIGHT_PIXELS = 16;
	protected final static int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	protected final static int MIN_DATA_CONTAINER_HEIGHT_PIXELS = 32;
	protected final static int MIN_DATA_CONTAINER_WIDTH_PIXELS = 180;

	protected LayoutManager layoutManager;
	protected boolean isUpsideDown = true;
	protected Column baseColumn;
	protected Column bodyColumn;

	public ADefaultTemplateNode(ForceDirectedGraphLayout graphLayout,
			GLDataGraph view, DragAndDropController dragAndDropController,
			int id) {
		super(graphLayout, view, dragAndDropController, id);

	}

	@Override
	public void init() {
		// layout = nodeLayout;
		layoutManager = new LayoutManager(new ViewFrustum());
		LayoutTemplate layoutTemplate = new LayoutTemplate();

		ElementLayout baseLayout = setupLayout();

		layoutTemplate.setBaseElementLayout(baseLayout);
		layoutManager.setTemplate(layoutTemplate);
	}

	@Override
	public Pair<Point2D, Point2D> getBottomDimensionGroupAnchorPoints(
			ADimensionGroupData dimensionGroup) {

		Pair<Point2D, Point2D> anchorPoints = getDataContainerRenderer()
				.getBottomAnchorPointsOfDimensionGroup(dimensionGroup);

		return getAbsoluteDimensionGroupAnchorPoints(anchorPoints);
	}

	@Override
	public Pair<Point2D, Point2D> getTopDimensionGroupAnchorPoints(
			ADimensionGroupData dimensionGroup) {

		Pair<Point2D, Point2D> anchorPoints = getDataContainerRenderer()
				.getTopAnchorPointsOfDimensionGroup(dimensionGroup);

		return getAbsoluteDimensionGroupAnchorPoints(anchorPoints);
	}

	protected Pair<Point2D, Point2D> getAbsoluteDimensionGroupAnchorPoints(
			Pair<Point2D, Point2D> anchorPoints) {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());
		float spacingX = pixelGLConverter
				.getGLWidthForPixelWidth(SPACING_PIXELS);
		float spacingY = pixelGLConverter
				.getGLHeightForPixelHeight(SPACING_PIXELS);

		Point2D first = (Point2D) anchorPoints.getFirst().clone();
		Point2D second = (Point2D) anchorPoints.getSecond().clone();

		first.setLocation(anchorPoints.getFirst().getX() + x + spacingX - width
				/ 2.0f, anchorPoints.getFirst().getY() + y + spacingY - height
				/ 2.0f);
		second.setLocation(anchorPoints.getSecond().getX() + x + spacingX
				- width / 2.0f, anchorPoints.getSecond().getY() + y + spacingY
				- height / 2.0f);

		return new Pair<Point2D, Point2D>(first, second);
	}

	@Override
	public Pair<Point2D, Point2D> getTopAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y + height
				/ 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y + height
				/ 2.0f));

		return anchorPoints;
	}

	@Override
	public Pair<Point2D, Point2D> getBottomAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y - height
				/ 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y - height
				/ 2.0f));

		return anchorPoints;
	}

	@Override
	public Pair<Point2D, Point2D> getLeftAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y + height
				/ 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x - width / 2.0f, y - height
				/ 2.0f));

		return anchorPoints;
	}

	@Override
	public Pair<Point2D, Point2D> getRightAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x + width / 2.0f, y + height
				/ 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y - height
				/ 2.0f));

		return anchorPoints;
	}

	@Override
	public Point2D getPosition() {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		return new Point2D.Float(x, y);
	}

	@Override
	public float getHeight() {
		return pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());
	}

	@Override
	public float getWidth() {
		return pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
	}

	@Override
	public int getHeightPixels() {
		return 4
				* SPACING_PIXELS
				+ CAPTION_HEIGHT_PIXELS
				+ LINE_SEPARATOR_HEIGHT_PIXELS
				+ Math.max(MIN_DATA_CONTAINER_HEIGHT_PIXELS,
						getDataContainerRenderer().getMinHeightPixels());
		// return layout.getHeightPixels();
	}

	@Override
	public int getWidthPixels() {
		return 2
				* SPACING_PIXELS
				+ Math.max(MIN_DATA_CONTAINER_WIDTH_PIXELS,
						getDataContainerRenderer().getMinWidthPixels());
		// return layout.getWidthPixels();
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

	@Override
	public void render(GL2 gl) {
		Point2D position = graphLayout.getNodePosition(this, true);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position
				.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position
				.getY());
		float width = pixelGLConverter
				.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter
				.getGLHeightForPixelHeight(getHeightPixels());
		gl.glPushMatrix();
		gl.glTranslatef(x - width / 2.0f, y - height / 2.0f, 0f);

		// layoutManager.setViewFrustum(new ViewFrustum(
		// ECameraProjectionMode.ORTHOGRAPHIC, x - spacingWidth, x
		// + spacingWidth, y - spacingHeight, y + spacingHeight,
		// -1, 20));
		layoutManager
				.setViewFrustum(new ViewFrustum(
						CameraProjectionMode.ORTHOGRAPHIC, 0, width, 0, height,
						-1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();
		// GLHelperFunctions.drawPointAt(gl, x, y, 0);

	}

	@Override
	public Rectangle2D getBoundingBox() {

		Point2D position = getPosition();
		double x = position.getX() - getWidth() / 2 - 0.2;
		double y = position.getY() - getHeight() / 2 - 0.2;

		return new Rectangle2D.Double(x, y, getWidth() + 0.4, getHeight() + 0.4);
	}

	@Override
	public boolean isUpsideDown() {
		return isUpsideDown;
	}

	@Override
	public void setUpsideDown(boolean isUpsideDown) {
		this.isUpsideDown = isUpsideDown;

		baseColumn.setBottomUp(!isUpsideDown);
		bodyColumn.setBottomUp(!isUpsideDown);

		view.setDisplayListDirty();
		getDataContainerRenderer().setUpsideDown(isUpsideDown);
	}

	protected abstract ElementLayout setupLayout();

	protected abstract ADataContainerRenderer getDataContainerRenderer();

}
