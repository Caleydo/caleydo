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
package org.caleydo.view.dvi.tableperspective;

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
