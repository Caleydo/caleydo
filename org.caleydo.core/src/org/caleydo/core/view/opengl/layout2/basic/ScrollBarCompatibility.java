/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;

/**
 * scrollbar implementation for the "old" picking manager
 *
 * @author Samuel Gratzl
 *
 */
public class ScrollBarCompatibility extends AScrollBar implements IDraggable {
	private final DragAndDropController dragAndDropController;

	private float prevDraggingMouseX;
	private float prevDraggingMouseY;

	public ScrollBarCompatibility(boolean isHorizontal, DragAndDropController dragAndDropController) {
		super(isHorizontal);
		this.dragAndDropController = dragAndDropController;
	}

	@Override
	public void pick(Pick pick) {
		if (pick.isAnyDragging() && !pick.isDoDragging())
			return;
		if (callback == null)
			return;
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			hovered = true;
			break;
		case CLICKED:
			dragAndDropController.clearDraggables();
			dragAndDropController.setDraggingProperties(pick.getPickedPoint(), "ScrollbarDrag");
			dragAndDropController.addDraggable(this);
			break;
		case MOUSE_OUT:
			hovered = false;
			break;
		default:
			break;
		}
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY) {
		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
		float mouseDelta;
		if (isHorizontal) {
			if (prevDraggingMouseX >= mouseCoordinateX - 0.01 && prevDraggingMouseX <= mouseCoordinateX + 0.01)
				return;
			mouseDelta = prevDraggingMouseX - mouseCoordinateX;
		} else {
			if (prevDraggingMouseY >= mouseCoordinateY - 0.01 && prevDraggingMouseY <= mouseCoordinateY + 0.01)
				return;
			mouseDelta = prevDraggingMouseY - mouseCoordinateY;
		}

		drag(mouseDelta);

		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;
	}
	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
	}

}
