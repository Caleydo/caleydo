package org.caleydo.view.datagraph.datacontainer;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class DimensionGroupRenderer extends ColorRenderer implements IDraggable {

	private static final int TEXT_SPACING_PIXELS = 2;

	private DataContainer dataContainer;

	private AGLView view;
	private IDataGraphNode node;

	private float prevDraggingMouseX;
	private float prevDraggingMouseY;
	private Point2D draggingPosition;
	private SelectionType selectionType;
	private boolean renderDimensionGroupLabel;
	private boolean isUpsideDown = false;

	private int textHeightPixels;

	public DimensionGroupRenderer(DataContainer dataContainer,
			AGLView view, DragAndDropController dragAndDropController,
			IDataGraphNode node, float[] color) {
		super(color, new float[] { color[0] - 0.2f, color[1] - 0.2f,
				color[2] - 0.2f, 1f }, 2);
		this.setDataContainer(dataContainer);
		this.view = view;
		this.node = node;
		renderDimensionGroupLabel = true;
	}

	@Override
	public void render(GL2 gl) {
		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		// FIXME: Use color from data domain

		// float[] color = new float[] { 0.5f, 0.5f, 0.5f };
		// if (dimensionGroupData.getDataDomain() != null) {
		// color = dimensionGroupData.getDataDomain().getColor().getRGB();
		// }
		//
		// gl.glColor4f(color[0], color[1], color[2], 1f);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(0, 0, 0.1f);
		// gl.glVertex3f(x, 0, 0.1f);
		// gl.glVertex3f(x, y, 0.1f);
		// gl.glVertex3f(0, y, 0.1f);
		// gl.glEnd();
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0.1f);
		super.render(gl);
		gl.glPopMatrix();

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

		// gl.glPushAttrib(GL2.GL_LINE_BIT);
		// gl.glLineWidth(2);
		//
		// // gl.glColor3f(0.3f, 0.3f, 0.3f);
		// gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
		// gl.glBegin(GL2.GL_LINES);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(x, 0, 0);
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, y, 0);
		//
		// // gl.glColor3f(0.7f, 0.7f, 0.7f);
		// gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
		// gl.glVertex3f(0, y, 0);
		// gl.glVertex3f(x, y, 0);
		// gl.glVertex3f(x, 0, 0);
		// gl.glVertex3f(x, y, 0);
		//
		// gl.glEnd();
		//
		// gl.glPopAttrib();

		if (renderDimensionGroupLabel) {
			gl.glPushMatrix();
			
			if(isUpsideDown) {
				float textPositionX = pixelGLConverter
						.getGLHeightForPixelHeight(textHeightPixels - 2)
						+ (x - pixelGLConverter
								.getGLHeightForPixelHeight(textHeightPixels - 2))
						/ 2.0f;

				gl.glTranslatef(textPositionX, pixelGLConverter
						.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS), 0.1f);
				gl.glRotatef(90, 0, 0, 1);
			} else {
				float textPositionX = (x - pixelGLConverter
						.getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;
				gl.glTranslatef(
						textPositionX,
						y
								- pixelGLConverter
										.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
						0.1f);
				gl.glRotatef(-90, 0, 0, 1);
			}

			textRenderer
					.renderTextInBounds(
							gl,
							dataContainer.getLabel(),
							0,
							0,
							0,
							y
									- pixelGLConverter
											.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
							pixelGLConverter
									.getGLHeightForPixelHeight(textHeightPixels));
			gl.glPopMatrix();
		}

	}

	public void setDataContainer(DataContainer dimensionGroupData) {
		this.dataContainer = dimensionGroupData;
	}

	public DataContainer getDataContainer() {
		return dataContainer;
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY) {
		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;
		draggingPosition = node.getBottomDataContainerAnchorPoints(
				dataContainer).getFirst();

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		gl.glColor4f(color[0], color[1], color[2], 0.5f);
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

	public void setRenderDimensionGroupLabel(boolean renderDimensionGroupLabel) {
		this.renderDimensionGroupLabel = renderDimensionGroupLabel;
	}

	public boolean isRenderDimensionGroupLabel() {
		return renderDimensionGroupLabel;
	}

	public void setTextHeightPixels(int textHeightPixels) {
		this.textHeightPixels = textHeightPixels;
	}

	public int getTextHeightPixels() {
		return textHeightPixels;
	}

	public boolean isUpsideDown() {
		return isUpsideDown;
	}

	public void setUpsideDown(boolean isUpsideDown) {
		this.isUpsideDown = isUpsideDown;
	}

}
