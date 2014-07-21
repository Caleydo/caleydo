/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * scrollbar implementation for the advanced picking managers
 *
 * @author Samuel Gratzl
 *
 */
public class ScrollBar extends AScrollBar {
	private final float mouseWheelFactor;

	public ScrollBar(boolean isHorizontal) {
		this(isHorizontal, 0.1f);
	}

	public ScrollBar(boolean isHorizontal, float mouseWheelFactor) {
		super(isHorizontal);
		this.mouseWheelFactor = mouseWheelFactor;
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
		case DRAG_DETECTED:
			Vec2f relative = callback.toRelative(pick.getPickedPoint());
			if (!jump(dim.select(relative)))
				pick.setDoDragging(true);
			callback.repaint();
			break;
		case DRAGGED:
			if (!pick.isDoDragging())
				return;
			drag(dim.select(pick.getD()));
			break;
		case MOUSE_RELEASED:
			break;
		case MOUSE_OUT:
			hovered = false;
			callback.repaint();
			break;
		case MOUSE_WHEEL:
			// drag(-((IMouseEvent) pick).getWheelRotation() * mouseWheelFactor);
			move(-((IMouseEvent) pick).getWheelRotation() * callback.getHeight(this) * mouseWheelFactor);
			break;
		default:
			break;
		}
	}

}
