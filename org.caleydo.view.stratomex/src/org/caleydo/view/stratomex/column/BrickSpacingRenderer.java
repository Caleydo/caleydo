/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.column;

import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionCommands;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
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
	extends ALayoutRenderer
	implements IDropArea
{
	private BrickColumn dimensionGroup;
	private int id;
	private boolean renderDropIndicator = false;
	private GLBrick lowerBrick;
	private final APickingListener pickingListener;

	public BrickSpacingRenderer(BrickColumn dimensionGroup, int id, GLBrick lowerBrick)
	{
		// super(new float[] { 1, 0, 0, 1 });
		this.dimensionGroup = dimensionGroup;
		this.id = id;
		this.setLowerBrick(lowerBrick);

		final GLStratomex stratomex = dimensionGroup.getStratomexView();

		this.pickingListener = new APickingListener()
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

			@Override
			protected void clicked(Pick pick) {
				SelectionCommands.clearSelections();
				stratomex.hideAllBrickWidgets();
			}

		};
		stratomex.addIDPickingListener(pickingListener, EPickingType.BRICK_SPACER.name() + dimensionGroup.getID(), id);
	}

	@Override
	public void destroy(GL2 gl) {
		dimensionGroup.getStratomexView().removeIDPickingListener(pickingListener,
				EPickingType.BRICK_SPACER.name() + dimensionGroup.getID(), id);
		super.destroy(gl);
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
		gl.glVertex3f(-width / 2.0f, 0, 0.1f);
		gl.glVertex3f(width / 2.0f, 0, 0.1f);
		gl.glVertex3f(width / 2.0f, y, 0.1f);
		gl.glVertex3f(-width / 2.0f, y, 0.1f);
		gl.glEnd();

		if (renderDropIndicator)
		{
			gl.glLineWidth(3);
			gl.glColor4f(0, 0, 0, 1);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(-width / 2.0f, y / 2.0f, 2f);
			gl.glVertex3f(width / 2.0f, y / 2.0f, 2f);
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
