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

		float vd = this.size / callback.getHeight(this);
		vd *= mouseDelta;
		callback.onScrollBarMoved(this, clamp(offset + vd));

		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;
	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
	}

}