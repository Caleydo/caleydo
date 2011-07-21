package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class ComparisonGroupRepresentation implements IDraggable {

	private ADimensionGroupData dimensionGroupData;
	private float x;
	private float y;
	private AGLView view;
	private IDataGraphNode node;

	private float prevDraggingMouseX;
	private float prevDraggingMouseY;
	private Point2D draggingPosition;

	public ComparisonGroupRepresentation(
			ADimensionGroupData dimensionGroupData, AGLView view,
			DragAndDropController dragAndDropController, IDataGraphNode node) {
		this.setDimensionGroupData(dimensionGroupData);
		this.view = view;
		this.node = node;
	}

	public void render(GL2 gl) {
		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		gl.glColor3f(0.6f, 0.6f, 0.6f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0.1f);
		gl.glVertex3f(x, 0, 0.1f);
		gl.glVertex3f(x, y, 0.1f);
		gl.glVertex3f(0, y, 0.1f);
		gl.glEnd();
		gl.glPushMatrix();
		gl.glTranslatef(0, y, 0.1f);
		gl.glRotatef(-90, 0, 0, 1);

		textRenderer.renderTextInBounds(gl, dimensionGroupData.getLabel(), 0,
				0, 0, y, x);
		gl.glPopMatrix();

		gl.glPopName();
	}

	public void setDimensionGroupData(ADimensionGroupData dimensionGroupData) {
		this.dimensionGroupData = dimensionGroupData;
	}

	public ADimensionGroupData getDimensionGroupData() {
		return dimensionGroupData;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY) {
		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;
		draggingPosition = node.getBottomDimensionGroupAnchorPoints(
				dimensionGroupData).getFirst();

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		gl.glColor4f(0.6f, 0.6f, 0.6f, 0.5f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f((float) draggingPosition.getX(),
				(float) draggingPosition.getY(), 0);
		gl.glVertex3f((float) draggingPosition.getX() + x,
				(float) draggingPosition.getY(), 0);
		gl.glVertex3f((float) draggingPosition.getX() + x,
				(float) draggingPosition.getY() + y, 0);
		gl.glVertex3f((float) draggingPosition.getX(),
				(float) draggingPosition.getY() + y, 0);
		gl.glEnd();

		if ((prevDraggingMouseX >= mouseCoordinateX - 0.01 && prevDraggingMouseX <= mouseCoordinateX + 0.01)
				&& (prevDraggingMouseY >= mouseCoordinateY - 0.01 && prevDraggingMouseY <= mouseCoordinateY + 0.01))
			return;

		float mouseDeltaX = prevDraggingMouseX - mouseCoordinateX;
		float mouseDeltaY = prevDraggingMouseY - mouseCoordinateY;

		draggingPosition.setLocation(draggingPosition.getX() - mouseDeltaX,
				draggingPosition.getY() - mouseDeltaY);

		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

		view.setDisplayListDirty();

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		draggingPosition.setLocation(0,0);
	}

}
