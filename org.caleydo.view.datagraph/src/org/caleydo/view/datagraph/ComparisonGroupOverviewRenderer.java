package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.manager.picking.APickingListener;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class ComparisonGroupOverviewRenderer extends LayoutRenderer implements
		IDraggable {

	private final static String DIMENSION_GROUP_PICKING_TYPE = "org.caleydo.view.datagraph.dimensiongroup";

	private final static int SPACING_PIXELS = 2;
	private final static int MIN_COMP_GROUP_WIDTH_PIXELS = 16;

	private IDataGraphNode node;
	private AGLView view;
	private Map<ADimensionGroupData, Pair<Point2D, Point2D>> dimensionGroupPositions;
	private float prevDraggingMouseX;
	private float prevDraggingMouseY;
	private float currentDimGroupWidth;
	private ADimensionGroupData draggedDimensionGroupData;
	private Point2D draggingPosition;
	private DragAndDropController dragAndDropController;

	public ComparisonGroupOverviewRenderer(IDataGraphNode node, AGLView view,
			DragAndDropController dragAndDropController) {

		this.node = node;
		this.view = view;
		this.dragAndDropController = dragAndDropController;
		dimensionGroupPositions = new HashMap<ADimensionGroupData, Pair<Point2D, Point2D>>();
		createPickingListener();
	}

	private void createPickingListener() {
		view.addMultiIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				draggedDimensionGroupData = null;
				int dimensionGroupID = pick.getID();
				List<ADimensionGroupData> dimensionGroupDatas = node
						.getDimensionGroups();
				for (ADimensionGroupData data : dimensionGroupDatas) {
					if (data.getID() == dimensionGroupID) {
						draggedDimensionGroupData = data;
						break;
					}
				}
				if (draggedDimensionGroupData == null)
					return;

				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingStartPosition(pick
						.getPickedPoint());
				dragAndDropController
						.addDraggable(ComparisonGroupOverviewRenderer.this);

			}

			@Override
			public void mouseOver(Pick pick) {
			}

			@Override
			public void dragged(Pick pick) {
				if (!dragAndDropController.isDragging()) {
					dragAndDropController.startDragging();
				}
			}

		}, DIMENSION_GROUP_PICKING_TYPE + node.getID());
	}

	@Override
	public void render(GL2 gl) {

		PixelGLConverter pixelGLConverter = view.getParentGLCanvas()
				.getPixelGLConverter();
		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		float currentPosX = 0;
		float step = pixelGLConverter.getGLWidthForPixelWidth(SPACING_PIXELS
				+ MIN_COMP_GROUP_WIDTH_PIXELS);

		List<ADimensionGroupData> dimensionGroupData = node
				.getDimensionGroups();
		dimensionGroupPositions.clear();

		for (ADimensionGroupData data : dimensionGroupData) {
			currentDimGroupWidth = pixelGLConverter
					.getGLWidthForPixelWidth(MIN_COMP_GROUP_WIDTH_PIXELS);

			int pickingID = view.getPickingManager().getPickingID(view.getID(),
					DIMENSION_GROUP_PICKING_TYPE + node.getID(), data.getID());

			gl.glPushName(pickingID);

			gl.glColor3f(0.6f, 0.6f, 0.6f);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(currentPosX, 0, 0.1f);
			gl.glVertex3f(currentPosX + currentDimGroupWidth, 0, 0.1f);
			gl.glVertex3f(currentPosX + currentDimGroupWidth, y, 0.1f);
			gl.glVertex3f(currentPosX, y, 0.1f);
			gl.glEnd();
			gl.glPushMatrix();
			gl.glTranslatef(currentPosX, y, 0.1f);
			gl.glRotatef(-90, 0, 0, 1);

			textRenderer.renderTextInBounds(gl, data.getLabel(), 0, 0, 0, y,
					currentDimGroupWidth);
			gl.glPopMatrix();

			gl.glPopName();

			Point2D position1 = new Point2D.Float(currentPosX, 0);
			Point2D position2 = new Point2D.Float(currentPosX
					+ currentDimGroupWidth, 0);
			dimensionGroupPositions.put(data, new Pair<Point2D, Point2D>(
					position1, position2));

			currentPosX += step;
		}

	}

	@Override
	public int getMinWidthPixels() {
		return Math
				.max(200,
						(node.getDimensionGroups().size() * MIN_COMP_GROUP_WIDTH_PIXELS)
								+ ((node.getDimensionGroups().size() - 1) * SPACING_PIXELS));
	}

	public Pair<Point2D, Point2D> getAnchorPointsOfDimensionGroup(
			ADimensionGroupData dimensionGroupData) {
		return dimensionGroupPositions.get(dimensionGroupData);
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY) {
		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;
		draggingPosition = node.getBottomDimensionGroupAnchorPoints(draggedDimensionGroupData).getFirst();
//		Point2D anchorPoint = getAnchorPointsOfDimensionGroup(
//				draggedDimensionGroupData).getFirst();
//		draggingPosition.setLocation(
//				draggingPosition.getX() + anchorPoint.getX(),
//				draggingPosition.getY() + anchorPoint.getY());

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		
		gl.glColor4f(0.6f, 0.6f, 0.6f, 0.5f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f((float) draggingPosition.getX(),
				(float) draggingPosition.getY(), 0);
		gl.glVertex3f((float) draggingPosition.getX() + currentDimGroupWidth,
				(float) draggingPosition.getY(), 0);
		gl.glVertex3f((float) draggingPosition.getX() + currentDimGroupWidth,
				(float) draggingPosition.getY() + y, 0);
		gl.glVertex3f((float) draggingPosition.getX(),
				(float) draggingPosition.getY() + y, 0);
		gl.glEnd();
		
		if ((prevDraggingMouseX >= mouseCoordinateX - 0.01 && prevDraggingMouseX <= mouseCoordinateX + 0.01)
				&& (prevDraggingMouseY >= mouseCoordinateY - 0.01 && prevDraggingMouseY <= mouseCoordinateY + 0.01))
			return;

		float mouseDeltaX = prevDraggingMouseX - mouseCoordinateX;
		float mouseDeltaY = prevDraggingMouseY - mouseCoordinateY;

		draggingPosition.setLocation(draggingPosition.getX()
				- mouseDeltaX, draggingPosition.getY()
				- mouseDeltaY);

		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

		view.setDisplayListDirty();

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		dragAndDropController.clearDraggables();
		draggedDimensionGroupData = null;
	}

}
