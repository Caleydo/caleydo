package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;

public class DataNodeDetailState extends ADataNodeState {

	public DataNodeDetailState(DataNode node, GLDataGraph view,
			PixelGLConverter pixelGLConverter,
			DragAndDropController dragAndDropController) {
		super(node, view, pixelGLConverter, dragAndDropController);

	}

	@Override
	public void render(GL2 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setupLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getHeightPixels() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidthPixels() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Point2D, Point2D> getBottomAnchorPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Point2D, Point2D> getLeftAnchorPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<Point2D, Point2D> getRightAnchorPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
