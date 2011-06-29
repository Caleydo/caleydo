package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;

public class DataNode extends ADraggableDataGraphNode {

	private final static int SPACING_PIXELS = 4;
	private final static int CAPTION_HEIGHT_PIXELS = 16;
	private final static int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	private final static int OVERVIEW_COMP_GROUP_HEIGHT_PIXELS = 32;

	private IDataDomain dataDomain;
	private LayoutManager layoutManager;
	private ComparisonGroupOverviewRenderer compGroupOverviewRenderer;

	public DataNode(ForceDirectedGraphLayout graphLayout, GLDataGraph view,
			final DragAndDropController dragAndDropController, int id,
			IDataDomain dataDomain) {
		super(graphLayout, view, dragAndDropController, id);
		this.dataDomain = dataDomain;

		setupLayout();

	}

	private void setupLayout() {
		layoutManager = new LayoutManager(new ViewFrustum());
		LayoutTemplate layoutTemplate = new LayoutTemplate();

		Row baseRow = new Row("baseRow");

		baseRow.setFrameColor(0, 0, 1, 0);

		baseRow.setRenderer(new BorderedAreaRenderer(view,
				EPickingType.DATA_GRAPH_NODE, id));

		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelGLConverter(pixelGLConverter);
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);

		Column baseColumn = new Column();
		baseColumn.setPixelGLConverter(pixelGLConverter);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		ElementLayout captionLayout = new ElementLayout("caption");
		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setRatioSizeX(1);
		captionLayout.setRenderer(new LabelRenderer(view, dataDomain
				.getDataDomainType(), EPickingType.DATA_GRAPH_NODE, id));

		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
		lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		ElementLayout compGroupLayout = new ElementLayout("compGroupOverview");
		compGroupOverviewRenderer = new ComparisonGroupOverviewRenderer(
				getDimensionGroups(), view);
		compGroupLayout.setPixelGLConverter(pixelGLConverter);
		compGroupLayout.setPixelSizeY(OVERVIEW_COMP_GROUP_HEIGHT_PIXELS);
		// compGroupLayout.setPixelSizeX(compGroupOverviewRenderer.getMinWidthPixels());
		compGroupLayout.setRenderer(compGroupOverviewRenderer);

		ElementLayout spacingLayoutY = new ElementLayout("spacingY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setRatioSizeX(0);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(compGroupLayout);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(captionLayout);
		baseColumn.append(spacingLayoutY);
		layoutTemplate.setBaseElementLayout(baseRow);
		layoutManager.setTemplate(layoutTemplate);
	}

	@Override
	public List<ADimensionGroupData> getDimensionGroups() {
		// TODO Implement correctly
		List<ADimensionGroupData> groups = new ArrayList<ADimensionGroupData>();
		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);

		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);

		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);

		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);
		groups.add(null);
		return groups;
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
		gl.glTranslatef(x - width / 2.0f, y - height / 2.0f, 0);

		// layoutManager.setViewFrustum(new ViewFrustum(
		// ECameraProjectionMode.ORTHOGRAPHIC, x - spacingWidth, x
		// + spacingWidth, y - spacingHeight, y + spacingHeight,
		// -1, 20));
		layoutManager
				.setViewFrustum(new ViewFrustum(
						ECameraProjectionMode.ORTHOGRAPHIC, 0, width, 0,
						height, -1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();
		// GLHelperFunctions.drawPointAt(gl, x, y, 0);

	}

	@Override
	public int getHeightPixels() {
		return 2 * SPACING_PIXELS + CAPTION_HEIGHT_PIXELS
				+ LINE_SEPARATOR_HEIGHT_PIXELS
				+ OVERVIEW_COMP_GROUP_HEIGHT_PIXELS;
	}

	@Override
	public int getWidthPixels() {
		return 2 * SPACING_PIXELS
				+ compGroupOverviewRenderer.getMinWidthPixels();
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public Pair<Point2D, Point2D> getTopDimensionGroupAnchorPoints(
			ADimensionGroupData dimensionGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Point2D, Point2D> getBottomDimensionGroupAnchorPoints(
			ADimensionGroupData dimensionGroup) {
		// TODO Auto-generated method stub
		return null;
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
}
