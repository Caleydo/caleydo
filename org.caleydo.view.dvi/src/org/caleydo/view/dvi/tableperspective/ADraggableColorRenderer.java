/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.tableperspective;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

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
	

	public ADraggableColorRenderer(float[] color, float[] borderColor, int borderWidth, AGLView view)
	{
		super(color, borderColor, borderWidth);
		this.view = view;
	}

	@Override
	public void renderContent(GL2 gl)
	{
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0);
		super.renderContent(gl);
		gl.glPopMatrix();

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
		gl.glBegin(GL2GL3.GL_QUADS);
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
