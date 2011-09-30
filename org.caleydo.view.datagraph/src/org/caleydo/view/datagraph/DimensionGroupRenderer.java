package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class DimensionGroupRenderer extends ARenderer implements IDraggable {

	private ADimensionGroupData dimensionGroupData;

	private AGLView view;
	private IDataGraphNode node;

	private float prevDraggingMouseX;
	private float prevDraggingMouseY;
	private Point2D draggingPosition;
	private SelectionType selectionType;

	public DimensionGroupRenderer(ADimensionGroupData dimensionGroupData,
			AGLView view, DragAndDropController dragAndDropController,
			IDataGraphNode node) {
		this.setDimensionGroupData(dimensionGroupData);
		this.view = view;
		this.node = node;
	}

	@Override
	public void render(GL2 gl) {
		CaleydoTextRenderer textRenderer = view.getTextRenderer();

		gl.glColor3fv(dimensionGroupData.getDataDomain().getColor().getRGB(), 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0.1f);
		gl.glVertex3f(x, 0, 0.1f);
		gl.glVertex3f(x, y, 0.1f);
		gl.glVertex3f(0, y, 0.1f);
		gl.glEnd();

		if (selectionType != null && selectionType != SelectionType.NORMAL) {
			gl.glColor4fv(selectionType.getColor(), 0);
			gl.glPushAttrib(GL2.GL_LINE_BIT);
			gl.glLineWidth(3);
			gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex3f(0, 0, 0.1f);
			gl.glVertex3f(x, 0, 0.1f);
			gl.glVertex3f(x, y, 0.1f);
			gl.glVertex3f(0, y, 0.1f);
			gl.glEnd();
			gl.glPopAttrib();
		}

		gl.glPushMatrix();
		gl.glTranslatef(0, y, 0.1f);
		gl.glRotatef(-90, 0, 0, 1);

		textRenderer.renderTextInBounds(gl, dimensionGroupData.getLabel(), 0,
				0, 0, y, x);
		gl.glPopMatrix();

	}

	public void setDimensionGroupData(ADimensionGroupData dimensionGroupData) {
		this.dimensionGroupData = dimensionGroupData;
	}

	public ADimensionGroupData getDimensionGroupData() {
		return dimensionGroupData;
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
		draggingPosition.setLocation(0, 0);
	}

	public void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public SelectionType getSelectionType() {
		return selectionType;
	}

}
