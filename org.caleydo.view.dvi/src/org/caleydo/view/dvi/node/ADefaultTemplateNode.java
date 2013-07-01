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
package org.caleydo.view.dvi.node;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.layout.AGraphLayout;
import org.caleydo.view.dvi.tableperspective.AMultiTablePerspectiveRenderer;

public abstract class ADefaultTemplateNode extends ADraggableDataGraphNode {

	protected final static int SPACING_PIXELS = 4;
	protected final static int CAPTION_HEIGHT_PIXELS = 16;
	protected final static int LINE_SEPARATOR_HEIGHT_PIXELS = 3;
	protected final static int MIN_DATA_CONTAINER_HEIGHT_PIXELS = 32;
	protected final static int MIN_TITLE_BAR_WIDTH_PIXELS = 100;
	protected final static int BOUNDING_BOX_SPACING_PIXELS = 40;

	protected LayoutManager layoutManager;
	protected boolean isUpsideDown = false;
	protected Column baseColumn;
	protected Column bodyColumn;
	protected int heightPixels = -1;
	protected int widthPixels = -1;

	public ADefaultTemplateNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, int id) {
		super(graphLayout, view, dragAndDropController, id);

	}

	@Override
	public void init() {
		super.init();
		// layout = nodeLayout;
		layoutManager = new LayoutManager(new ViewFrustum(), view.getPixelGLConverter());
		ElementLayout baseLayout = setupLayout();
		layoutManager.setBaseElementLayout(baseLayout);

	}

	@Override
	public Pair<Point2D, Point2D> getBottomTablePerspectiveAnchorPoints(
			TablePerspective tablePerspective) {

		if (getTablePerspectiveRenderer() == null)
			return null;

		Pair<Point2D, Point2D> anchorPoints = getTablePerspectiveRenderer()
				.getBottomAnchorPointsOfTablePerspective(tablePerspective);

		return getAbsoluteDimensionGroupAnchorPoints(
				anchorPoints,
				SPACING_PIXELS,
				isUpsideDown ? (3 * SPACING_PIXELS + CAPTION_HEIGHT_PIXELS + LINE_SEPARATOR_HEIGHT_PIXELS)
						: (SPACING_PIXELS));
	}

	@Override
	public Pair<Point2D, Point2D> getTopTablePerspectiveAnchorPoints(
			TablePerspective tablePerspective) {

		if (getTablePerspectiveRenderer() == null)
			return null;

		Pair<Point2D, Point2D> anchorPoints = getTablePerspectiveRenderer()
				.getTopAnchorPointsOfTablePerspective(tablePerspective);

		return getAbsoluteDimensionGroupAnchorPoints(
				anchorPoints,
				SPACING_PIXELS,
				isUpsideDown ? (3 * SPACING_PIXELS + CAPTION_HEIGHT_PIXELS + LINE_SEPARATOR_HEIGHT_PIXELS)
						: (SPACING_PIXELS));
	}

	@Override
	public Point2D getAbsolutPositionOfRelativeTablePerspectiveRendererCoordinates(
			Point2D coordinates) {
		Point2D position = graphLayout.getNodePosition(this);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position.getY());
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());
		float spacingX = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS);
		float spacingY = pixelGLConverter.getGLHeightForPixelHeight(isUpsideDown ? (3
				* SPACING_PIXELS + CAPTION_HEIGHT_PIXELS + LINE_SEPARATOR_HEIGHT_PIXELS)
				: (SPACING_PIXELS));

		return new Point2D.Float(
				(float) coordinates.getX() + x + spacingX - width / 2.0f,
				(float) coordinates.getY() + y + spacingY - height / 2.0f);

	}

	protected Pair<Point2D, Point2D> getAbsoluteDimensionGroupAnchorPoints(
			Pair<Point2D, Point2D> anchorPoints, int spacingXPixels, int spacingYPixels) {
		Point2D position = graphLayout.getNodePosition(this);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position.getY());
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());
		float spacingX = pixelGLConverter.getGLWidthForPixelWidth(spacingXPixels);
		float spacingY = pixelGLConverter.getGLHeightForPixelHeight(spacingYPixels);

		// FIXME Why is this null in some cases??
		if (anchorPoints == null)
			return new Pair<Point2D, Point2D>(new Point2D.Float(0,0), new Point2D.Float(0,0));

		Point2D first = (Point2D) anchorPoints.getFirst().clone();
		Point2D second = (Point2D) anchorPoints.getSecond().clone();

		first.setLocation(anchorPoints.getFirst().getX() + x + spacingX - width / 2.0f,
				anchorPoints.getFirst().getY() + y + spacingY - height / 2.0f);
		second.setLocation(anchorPoints.getSecond().getX() + x + spacingX - width / 2.0f,
				anchorPoints.getSecond().getY() + y + spacingY - height / 2.0f);

		return new Pair<Point2D, Point2D>(first, second);
	}

	@Override
	public Pair<Point2D, Point2D> getTopAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position.getY());
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y + height / 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y + height / 2.0f));

		return anchorPoints;
	}

	@Override
	public Pair<Point2D, Point2D> getBottomAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position.getY());
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y - height / 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y - height / 2.0f));

		return anchorPoints;
	}

	@Override
	public Pair<Point2D, Point2D> getLeftAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position.getY());
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x - width / 2.0f, y + height / 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x - width / 2.0f, y - height / 2.0f));

		return anchorPoints;
	}

	@Override
	public Pair<Point2D, Point2D> getRightAnchorPoints() {
		Point2D position = graphLayout.getNodePosition(this);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position.getY());
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());

		Pair<Point2D, Point2D> anchorPoints = new Pair<Point2D, Point2D>();

		anchorPoints.setFirst(new Point2D.Float(x + width / 2.0f, y + height / 2.0f));
		anchorPoints.setSecond(new Point2D.Float(x + width / 2.0f, y - height / 2.0f));

		return anchorPoints;
	}

	@Override
	public Point2D getPosition() {
		Point2D position = graphLayout.getNodePosition(this);
		if (position == null)
			return new Point2D.Float(0, 0);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position.getY());
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

		// heightPixels = 4
		// * SPACING_PIXELS
		// + CAPTION_HEIGHT_PIXELS
		// + LINE_SEPARATOR_HEIGHT_PIXELS
		// + Math.max(MIN_DATA_CONTAINER_HEIGHT_PIXELS,
		// ((getTablePerspectiveRenderer() == null) ? 0 :
		// getTablePerspectiveRenderer()
		// .getMinHeightPixels()));

		if (heightPixels < 0)
			recalculateNodeSize();

		return heightPixels;
		// return layout.getHeightPixels();
	}

	@Override
	public int getWidthPixels() {

		// widthPixels = 2
		// * SPACING_PIXELS
		// + Math.max(getMinTitleBarWidthPixels(),
		// ((getTablePerspectiveRenderer() == null) ? 0 :
		// getTablePerspectiveRenderer()
		// .getMinWidthPixels()));
		if (widthPixels < 0)
			recalculateNodeSize();

		return widthPixels;
		// return layout.getWidthPixels();
	}

	protected Row createDefaultBaseRow(Color color, int pickingID) {
		Row baseRow = new Row("baseRow");
		baseRow.setFrameColor(0, 0, 1, 0);

		BorderedAreaRenderer borderedAreaRenderer = new BorderedAreaRenderer(view,
				createNodePickingTypeList());
		borderedAreaRenderer.setColor(color);

		baseRow.setRenderer(borderedAreaRenderer);

		return baseRow;
	}

	protected List<Pair<String, Integer>> createNodePickingTypeList() {
		List<Pair<String, Integer>> pickingIDs = new ArrayList<Pair<String, Integer>>(2);
		pickingIDs.add(new Pair<String, Integer>(DATA_GRAPH_NODE_PICKING_TYPE, id));
		pickingIDs.add(new Pair<String, Integer>(
				DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id));
		return pickingIDs;
	}

	protected ElementLayout createDefaultSpacingX() {
		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setPixelSizeX(SPACING_PIXELS);
		spacingLayoutX.setRatioSizeY(0);
		return spacingLayoutX;
	}

	protected ElementLayout createDefaultSpacingY() {
		ElementLayout spacingLayoutX = new ElementLayout("spacingLayoutX");
		spacingLayoutX.setRatioSizeX(0);
		spacingLayoutX.setPixelSizeY(SPACING_PIXELS);
		return spacingLayoutX;
	}

	protected ElementLayout createDefaultCaptionLayout(int pickingID) {
		ElementLayout captionLayout = new ElementLayout("caption");
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setRatioSizeX(1);
		captionLayout.setRenderer(new LabelRenderer(view, view.getTextRenderer(), this,
				createNodePickingTypeList()));

		return captionLayout;
	}

	protected ElementLayout createDefaultLineSeparatorLayout() {
		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		return lineSeparatorLayout;
	}

	@Override
	public void render(GL2 gl) {
		Point2D position = graphLayout.getNodePosition(this);
		float x = pixelGLConverter.getGLWidthForPixelWidth((int) position.getX());
		float y = pixelGLConverter.getGLHeightForPixelHeight((int) position.getY());
		float width = pixelGLConverter.getGLWidthForPixelWidth(getWidthPixels());
		float height = pixelGLConverter.getGLHeightForPixelHeight(getHeightPixels());

		gl.glPushMatrix();
		gl.glTranslatef(x - width / 2.0f, y - height / 2.0f, 0f);
		layoutManager.setViewFrustum(new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC,
				0, width, 0, height, -1, 20));

		layoutManager.render(gl);
		gl.glPopMatrix();

	}

	@Override
	public Rectangle2D getBoundingBox() {
		float boundingBoxSpacingX = pixelGLConverter
				.getGLWidthForPixelWidth(BOUNDING_BOX_SPACING_PIXELS);
		float boundingBoxSpacingY = pixelGLConverter
				.getGLHeightForPixelHeight(BOUNDING_BOX_SPACING_PIXELS);
		Point2D position = getPosition();
		float width = getWidth();
		float height = getHeight();
		double x = position.getX() - width / 2 - boundingBoxSpacingX;
		double y = position.getY() - height / 2 - boundingBoxSpacingY;

		return new Rectangle2D.Double(x, y, width + 2 * boundingBoxSpacingX, height + 2
				* boundingBoxSpacingY);
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
		if (getTablePerspectiveRenderer() != null) {
			getTablePerspectiveRenderer().setUpsideDown(isUpsideDown);
		}
	}

	@Override
	public float getSpacingX(IDVINode node) {

		IDVINode leftNode = null;
		IDVINode rightNode = null;

		if (getPosition().getX() < node.getPosition().getX()) {
			leftNode = this;
			rightNode = node;
		} else {
			leftNode = node;
			rightNode = this;
		}

		return (float) ((rightNode.getPosition().getX() - rightNode.getWidth() / 2.0f) - (leftNode
				.getPosition().getX() + leftNode.getWidth() / 2.0f));
	}

	@Override
	public float getSpacingY(IDVINode node) {
		IDVINode topNode = null;
		IDVINode bottomNode = null;

		if (getPosition().getY() < node.getPosition().getY()) {
			topNode = this;
			bottomNode = node;
		} else {
			topNode = node;
			bottomNode = this;
		}

		return (float) ((topNode.getPosition().getY() - topNode.getHeight() / 2.0f) - (bottomNode
				.getPosition().getY() + bottomNode.getHeight() / 2.0f));
	}

	@Override
	public void recalculateNodeSize() {
		heightPixels = 4
				* SPACING_PIXELS
				+ CAPTION_HEIGHT_PIXELS
				+ LINE_SEPARATOR_HEIGHT_PIXELS
				+ Math.max(MIN_DATA_CONTAINER_HEIGHT_PIXELS,
						((getTablePerspectiveRenderer() == null) ? 0
								: getTablePerspectiveRenderer().getMinHeightPixels()));

		widthPixels = 2
				* SPACING_PIXELS
				+ Math.max(getMinTitleBarWidthPixels(),
						((getTablePerspectiveRenderer() == null) ? 0
								: getTablePerspectiveRenderer().getMinWidthPixels()));
	}

	protected abstract ElementLayout setupLayout();

	protected abstract AMultiTablePerspectiveRenderer getTablePerspectiveRenderer();

	protected abstract int getMinTitleBarWidthPixels();

}
