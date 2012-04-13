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
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;

public abstract class ADraggableColorRenderer
	extends ColorRenderer
	implements IDraggable
{


	protected AGLView view;
	protected float mousePositionDeltaX;
	protected float mousePositionDeltaY;
//	protected String text;
	

	public ADraggableColorRenderer(float[] color, float[] borderColor, int borderWidth, AGLView view)
	{
		super(color, borderColor, borderWidth);
		this.view = view;
	}

	@Override
	public void render(GL2 gl)
	{
//		CaleydoTextRenderer textRenderer = view.getTextRenderer();
//		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0);
		super.render(gl);
		gl.glPopMatrix();

//		if (showText)
//		{
//			float textPositionX = 0;
//			switch (textRotation)
//			{
//				case TEXT_ROTATION_0:
//					textRenderer.renderTextInBounds(
//							gl,
//							text,
//							pixelGLConverter
//							.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
//							pixelGLConverter
//							.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
//							0.1f,
//							x
//									- 2
//									* pixelGLConverter
//											.getGLWidthForPixelWidth(TEXT_SPACING_PIXELS),
//							pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
//					break;
//
//				case TEXT_ROTATION_90:
//
//					gl.glPushMatrix();
//					textPositionX = pixelGLConverter
//							.getGLHeightForPixelHeight(textHeightPixels - 2)
//							+ (x - pixelGLConverter
//									.getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;
//
//					gl.glTranslatef(textPositionX,
//							pixelGLConverter.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
//							0.1f);
//					gl.glRotatef(90, 0, 0, 1);
//					textRenderer.renderTextInBounds(
//							gl,
//							text,
//							0,
//							0,
//							0,
//							y
//									- pixelGLConverter
//											.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
//							pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
//					gl.glPopMatrix();
//					break;
//				case TEXT_ROTATION_270:
//					
//					gl.glPushMatrix();
//					textPositionX = (x - pixelGLConverter
//							.getGLHeightForPixelHeight(textHeightPixels - 2)) / 2.0f;
//					gl.glTranslatef(
//							textPositionX,
//							y
//									- pixelGLConverter
//											.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
//							0.1f);
//					gl.glRotatef(-90, 0, 0, 1);
//					textRenderer.renderTextInBounds(
//							gl,
//							text,
//							0,
//							0,
//							0,
//							y
//									- pixelGLConverter
//											.getGLHeightForPixelHeight(TEXT_SPACING_PIXELS),
//							pixelGLConverter.getGLHeightForPixelHeight(textHeightPixels));
//					gl.glPopMatrix();
//					break;
//			};
//
//		}

	}

	protected abstract Point2D getPosition();

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY)
	{
		Point2D position = getPosition();

		mousePositionDeltaX = mouseCoordinateX - (float) position.getX();
		mousePositionDeltaY = mouseCoordinateY - (float) position.getY();

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX, float mouseCoordinateY)
	{
		gl.glColor4f(color[0], color[1], color[2], 0.5f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(mouseCoordinateX - mousePositionDeltaX, mouseCoordinateY
				- mousePositionDeltaY, 2);
		gl.glVertex3f(mouseCoordinateX - mousePositionDeltaX + x, mouseCoordinateY
				- mousePositionDeltaY, 2);
		gl.glVertex3f(mouseCoordinateX - mousePositionDeltaX + x, mouseCoordinateY
				- mousePositionDeltaY + y, 2);
		gl.glVertex3f(mouseCoordinateX - mousePositionDeltaX, mouseCoordinateY
				- mousePositionDeltaY + y, 2);
		gl.glEnd();

		view.setDisplayListDirty();

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY)
	{
		// TODO Auto-generated method stub

	}

	
}
