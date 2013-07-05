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
package org.caleydo.view.stratomex.column;

import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;

/**
 * Renderer for spacings between bricks inside of a {@link BrickColumn}. This
 * class is responsible for handling drops of bricks that shall be reordered.
 *
 * @author Christian
 *
 */
public class BrickSpacingRenderer
	extends LayoutRenderer
	implements IDropArea
{
	private BrickColumn dimensionGroup;
	private int id;
	private boolean renderDropIndicator = false;
	private GLBrick lowerBrick;

	public BrickSpacingRenderer(BrickColumn dimensionGroup, int id, GLBrick lowerBrick)
	{
		// super(new float[] { 1, 0, 0, 1 });
		this.dimensionGroup = dimensionGroup;
		this.id = id;
		this.setLowerBrick(lowerBrick);

		final GLStratomex stratomex = dimensionGroup.getStratomexView();

		stratomex.addIDPickingListener(new APickingListener()
		{
			@Override
			public void dragged(Pick pick)
			{
				DragAndDropController dragAndDropController = stratomex
						.getDragAndDropController();
				if (dragAndDropController.isDragging()
						&& dragAndDropController.getDraggingMode() != null
						&& dragAndDropController.getDraggingMode()
								.equals("BrickDrag"
										+ BrickSpacingRenderer.this.dimensionGroup.getID()))
				{
					dragAndDropController.setDropArea(BrickSpacingRenderer.this);
				}
			}

		}, EPickingType.BRICK_SPACER.name() + dimensionGroup.getID(), id);
	}

	@Override
	public void renderContent(GL2 gl)
	{

		float width = dimensionGroup.getGroupColumn().getSizeScaledX();
		GLStratomex stratomex = dimensionGroup.getStratomexView();
		gl.glPushName(stratomex.getPickingManager().getPickingID(stratomex.getID(),
				EPickingType.BRICK_SPACER.name() + dimensionGroup.getID(), id));

		gl.glColor4f(1, 1, 1, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(-width / 2.0f, 0, 0f);
		gl.glVertex3f(width / 2.0f, 0, 0f);
		gl.glVertex3f(width / 2.0f, y, 0f);
		gl.glVertex3f(-width / 2.0f, y, 0f);
		gl.glEnd();

		if (renderDropIndicator)
		{
			gl.glLineWidth(3);
			gl.glColor4f(0, 0, 0, 1);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(-width / 2.0f, y / 2.0f, 0);
			gl.glVertex3f(width / 2.0f, y / 2.0f, 0);
			gl.glEnd();
		}
		gl.glPopName();
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY)
	{
		setRenderDropIndicator(true);
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController)
	{
		setRenderDropIndicator(false);
		for (IDraggable draggable : draggables)
		{
			GLBrick draggedBrick = (GLBrick) draggable;
			dimensionGroup.moveBrick(draggedBrick, lowerBrick);
		}

	}

	public boolean isRenderDropIndicator()
	{
		return renderDropIndicator;
	}

	public void setRenderDropIndicator(boolean renderDropIndicator)
	{
		this.renderDropIndicator = renderDropIndicator;
	}

	@Override
	public void handleDropAreaReplaced()
	{
		setRenderDropIndicator(false);

	}

	public GLBrick getLowerBrick()
	{
		return lowerBrick;
	}

	public void setLowerBrick(GLBrick lowerBrick)
	{
		this.lowerBrick = lowerBrick;
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

}
