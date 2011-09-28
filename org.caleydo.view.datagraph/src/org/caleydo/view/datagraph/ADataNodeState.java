package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;

public abstract class ADataNodeState {

	protected DataNode node;
	protected GLDataGraph view;
	protected PixelGLConverter pixelGLConverter;
	protected LayoutManager layoutManager;
	protected DragAndDropController dragAndDropController;

	public ADataNodeState(DataNode node, GLDataGraph view,
			PixelGLConverter pixelGLConverter,
			DragAndDropController dragAndDropController) {
		this.node = node;
		this.view = view;
		this.pixelGLConverter = pixelGLConverter;
		this.dragAndDropController = dragAndDropController;

	}

	public abstract void render(GL2 gl);

	protected abstract void setupLayout();

	public abstract int getHeightPixels();

	public abstract int getWidthPixels();

	public abstract Pair<Point2D, Point2D> getTopDimensionGroupAnchorPoints(
			ADimensionGroupData dimensionGroup);

	public abstract Pair<Point2D, Point2D> getBottomDimensionGroupAnchorPoints(
			ADimensionGroupData dimensionGroup);

	public abstract Pair<Point2D, Point2D> getTopAnchorPoints();

	public abstract Pair<Point2D, Point2D> getBottomAnchorPoints();

	public abstract Pair<Point2D, Point2D> getLeftAnchorPoints();

	public abstract Pair<Point2D, Point2D> getRightAnchorPoints();

	public abstract void update();
}
