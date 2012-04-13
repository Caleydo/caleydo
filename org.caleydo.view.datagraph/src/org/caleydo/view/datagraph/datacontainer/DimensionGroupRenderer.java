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
package org.caleydo.view.datagraph.datacontainer;

import java.awt.geom.Point2D;
import javax.media.opengl.GL2;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.view.datagraph.node.IDVINode;

public class DimensionGroupRenderer
	extends ADraggableColorRenderer
{

	private static final int TEXT_SPACING_PIXELS = 2;

	public final static int TEXT_ROTATION_0 = 0;
	public final static int TEXT_ROTATION_90 = 90;
	public final static int TEXT_ROTATION_180 = 180;
	public final static int TEXT_ROTATION_270 = 270;

	// private static final int TEXT_SPACING_PIXELS = 2;

	private DataContainer dataContainer;

	// private AGLView view;
	private IDVINode node;
	protected boolean showText = true;
	protected int textRotation = 0;
	protected int textHeightPixels;

	// private float mousePositionDeltaX;
	// private float mousePositionDeltaY;
	// private Point2D draggingPosition;
	// private SelectionType selectionType;
	// private boolean renderDimensionGroupLabel;
	// private boolean isUpsideDown = false;
	//
	// private int textHeightPixels;

	public DimensionGroupRenderer(DataContainer dataContainer, AGLView view,
			IDVINode node, float[] color)
	{
		super(color, new float[] { color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f, 1f }, 2,
				view);
		this.setDataContainer(dataContainer);
		this.view = view;
		this.node = node;
		// renderDimensionGroupLabel = true;
	}

	@Override
	public void render(GL2 gl)
	{
		CaleydoTextRenderer textRenderer = view.getTextRenderer();
		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0.1f);
		super.render(gl);
		gl.glPopMatrix();

		if (showText)
		{
			float textPositionX = 0;
			switch (textRotation)
			{
				case TEXT_ROTATION_0:
					textRenderer.renderTextInBounds(
							gl,
							dataContainer.getLabel(),
							pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
							pixelGLConverter.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
							0.1f,
							x
									- 2
									* pixelGLConverter
											.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
							pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
					break;

				case TEXT_ROTATION_90:

					gl.glPushMatrix();
					textPositionX = pixelGLConverter
							.getGLHeightForPixelHeight(textHeightPixels - 2)
							+ (x - pixelGLConverter
									.getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;

					gl.glTranslatef(textPositionX,
							pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
							0.2f);
					gl.glRotatef(90, 0, 0, 1);
					textRenderer.renderTextInBounds(gl, dataContainer.getLabel(), 0, 0, 0, y
							- pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
							pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
					gl.glPopMatrix();
					break;
				case TEXT_ROTATION_270:

					gl.glPushMatrix();
					textPositionX = (x - pixelGLConverter
							.getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;
					gl.glTranslatef(
							textPositionX,
							y
									- pixelGLConverter
											.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
							0.2f);
					gl.glRotatef(-90, 0, 0, 1);
					textRenderer.renderTextInBounds(gl, dataContainer.getLabel(), 0, 0, 0, y
							- pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
							pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
					gl.glPopMatrix();
					break;
			}
			;

		}

	}

	// @Override
	// public void render(GL2 gl)
	// {
	// CaleydoTextRenderer textRenderer = view.getTextRenderer();
	// PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
	//
	// // FIXME: Use color from data domain
	//
	// // float[] color = new float[] { 0.5f, 0.5f, 0.5f };
	// // if (dimensionGroupData.getDataDomain() != null) {
	// // color = dimensionGroupData.getDataDomain().getColor().getRGB();
	// // }
	// //
	// // gl.glColor4f(color[0], color[1], color[2], 1f);
	// // gl.glBegin(GL2.GL_QUADS);
	// // gl.glVertex3f(0, 0, 0.1f);
	// // gl.glVertex3f(x, 0, 0.1f);
	// // gl.glVertex3f(x, y, 0.1f);
	// // gl.glVertex3f(0, y, 0.1f);
	// // gl.glEnd();
	// gl.glPushMatrix();
	// gl.glTranslatef(0, 0, 0.1f);
	// super.render(gl);
	// gl.glPopMatrix();
	//
	// // if (selectionType != null && selectionType != SelectionType.NORMAL) {
	// // gl.glColor4fv(selectionType.getColor(), 0);
	// // gl.glPushAttrib(GL2.GL_LINE_BIT);
	// // gl.glLineWidth(3);
	// // gl.glBegin(GL2.GL_LINE_LOOP);
	// // gl.glVertex3f(0, 0, 0.1f);
	// // gl.glVertex3f(x, 0, 0.1f);
	// // gl.glVertex3f(x, y, 0.1f);
	// // gl.glVertex3f(0, y, 0.1f);
	// // gl.glEnd();
	// // gl.glPopAttrib();
	// // }
	//
	// // gl.glPushAttrib(GL2.GL_LINE_BIT);
	// // gl.glLineWidth(2);
	// //
	// // // gl.glColor3f(0.3f, 0.3f, 0.3f);
	// // gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
	// // gl.glBegin(GL2.GL_LINES);
	// // gl.glVertex3f(0, 0, 0);
	// // gl.glVertex3f(x, 0, 0);
	// // gl.glVertex3f(0, 0, 0);
	// // gl.glVertex3f(0, y, 0);
	// //
	// // // gl.glColor3f(0.7f, 0.7f, 0.7f);
	// // gl.glColor3f(color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f);
	// // gl.glVertex3f(0, y, 0);
	// // gl.glVertex3f(x, y, 0);
	// // gl.glVertex3f(x, 0, 0);
	// // gl.glVertex3f(x, y, 0);
	// //
	// // gl.glEnd();
	// //
	// // gl.glPopAttrib();
	//
	// if (renderDimensionGroupLabel)
	// {
	// gl.glPushMatrix();
	//
	// if (isUpsideDown)
	// {
	// float textPositionX = pixelGLConverter
	// .getGLHeightForPixelHeight(textHeightPixels - 2)
	// + (x - pixelGLConverter
	// .getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;
	//
	// gl.glTranslatef(textPositionX,
	// pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS), 0.1f);
	// gl.glRotatef(90, 0, 0, 1);
	// }
	// else
	// {
	// float textPositionX = (x - pixelGLConverter
	// .getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;
	// gl.glTranslatef(textPositionX,
	// y - pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
	// 0.1f);
	// gl.glRotatef(-90, 0, 0, 1);
	// }
	//
	// textRenderer.renderTextInBounds(gl, dataContainer.getLabel(), 0, 0, 0, y
	// - pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
	// pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
	// gl.glPopMatrix();
	// }
	//
	// }

	public void setDataContainer(DataContainer dimensionGroupData)
	{
		this.dataContainer = dimensionGroupData;
	}

	public DataContainer getDataContainer()
	{
		return dataContainer;
	}

	// @Override
	// public void setDraggingStartPoint(float mouseCoordinateX, float
	// mouseCoordinateY)
	// {
	//
	// draggingPosition =
	// node.getBottomDataContainerAnchorPoints(dataContainer).getFirst();
	//
	// mousePositionDeltaX = mouseCoordinateX - (float) draggingPosition.getX();
	// mousePositionDeltaY = mouseCoordinateY - (float) draggingPosition.getY();
	//
	// }
	//
	// @Override
	// public void handleDragging(GL2 gl, float mouseCoordinateX, float
	// mouseCoordinateY)
	// {
	//
	// gl.glColor4f(color[0], color[1], color[2], 0.5f);
	// gl.glBegin(GL2.GL_QUADS);
	// gl.glVertex3f(mouseCoordinateX - mousePositionDeltaX, mouseCoordinateY
	// - mousePositionDeltaY, 2);
	// gl.glVertex3f(mouseCoordinateX - mousePositionDeltaX + x,
	// mouseCoordinateY
	// - mousePositionDeltaY, 2);
	// gl.glVertex3f(mouseCoordinateX - mousePositionDeltaX + x,
	// mouseCoordinateY
	// - mousePositionDeltaY + y, 2);
	// gl.glVertex3f(mouseCoordinateX - mousePositionDeltaX, mouseCoordinateY
	// - mousePositionDeltaY + y, 2);
	// gl.glEnd();
	//
	// // if ((mousePositionDeltaX >= mouseCoordinateX - 0.01 &&
	// // mousePositionDeltaX <= mouseCoordinateX + 0.01)
	// // && (mousePositionDeltaY >= mouseCoordinateY - 0.01 &&
	// // mousePositionDeltaY <= mouseCoordinateY + 0.01))
	// // return;
	// //
	// // float mouseDeltaX = mousePositionDeltaX - mouseCoordinateX;
	// // float mouseDeltaY = mousePositionDeltaY - mouseCoordinateY;
	// //
	// // draggingPosition.setLocation(draggingPosition.getX() - mouseDeltaX,
	// // draggingPosition.getY() - mouseDeltaY);
	// //
	// // mousePositionDeltaX = mouseCoordinateX;
	// // mousePositionDeltaY = mouseCoordinateY;
	//
	// view.setDisplayListDirty();
	//
	// }
	//
	// @Override
	// public void handleDrop(GL2 gl, float mouseCoordinateX, float
	// mouseCoordinateY)
	// {
	// draggingPosition.setLocation(0, 0);
	// }

	// public void setSelectionType(SelectionType selectionType)
	// {
	// this.selectionType = selectionType;
	// }
	//
	// public SelectionType getSelectionType()
	// {
	// return selectionType;
	// }
	//
	// public void setRenderDimensionGroupLabel(boolean
	// renderDimensionGroupLabel)
	// {
	// this.renderDimensionGroupLabel = renderDimensionGroupLabel;
	// }
	//
	// public boolean isRenderDimensionGroupLabel()
	// {
	// return renderDimensionGroupLabel;
	// }

	// public boolean isUpsideDown()
	// {
	// return isUpsideDown;
	// }
	//
	// public void setUpsideDown(boolean isUpsideDown)
	// {
	// this.isUpsideDown = isUpsideDown;
	// }

	@Override
	protected Point2D getPosition()
	{
		return node.getBottomDataContainerAnchorPoints(dataContainer).getFirst();
	}

	public boolean isShowText()
	{
		return showText;
	}

	public void setShowText(boolean showText)
	{
		this.showText = showText;
	}

	public int getTextRotation()
	{
		return textRotation;
	}

	public void setTextRotation(int textRotation)
	{
		this.textRotation = textRotation;
	}

	public int getTextHeightPixels()
	{
		return textHeightPixels;
	}

	public void setTextHeightPixels(int textHeightPixels)
	{
		this.textHeightPixels = textHeightPixels;
	}

}
