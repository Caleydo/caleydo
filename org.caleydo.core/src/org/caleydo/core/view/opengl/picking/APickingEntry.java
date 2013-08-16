/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;

/**
 * basic class for picking entry handling
 *
 * @author Samuel Gratzl
 *
 */
abstract class APickingEntry {

	protected int pickingId;

	// extra identifier
	protected final int objectId;

	/**
	 * is this entry does currently dragging, i.e. it listens to mouse events til the mouse_release occur
	 */
	private boolean dragging = false;

	/**
	 * the first mouse position used by dragging
	 */
	private Vec2f dragStart;
	/**
	 * the last mouse position used by dragging
	 */
	private Vec2f lastPoint;

	public APickingEntry(int objectId) {
		this.objectId = objectId;
	}

	/**
	 * fires an event to all listeners using the given information
	 *
	 * @param mode
	 * @param mouse
	 * @param depth
	 */
	public void fire(PickingMode mode, float depth, boolean isAnyDragging, IMouseEvent event) {
		Vec2f mouse = event.getPoint();
		if ((mode == PickingMode.CLICKED || mode == PickingMode.DRAGGED || mode == PickingMode.MOUSE_MOVED)
				&& dragStart == null) {
			dragStart = lastPoint = mouse.copy();
		}

		Pick pick;
		if (mode == PickingMode.DRAGGED || mode == PickingMode.MOUSE_MOVED) {
			Vec2f dv = mouse.minus(lastPoint);
			pick = new AdvancedPick(objectId, mode, dragStart, depth, dv, isAnyDragging, event);
			lastPoint = mouse;
		} else
			pick = new AdvancedPick(objectId, mode, dragStart, depth, new Vec2f(0, 0), isAnyDragging, event);
		pick.setDoDragging(dragging);

		fire(pick);
		if (mode == PickingMode.CLICKED)
			dragging = pick.isDoDragging();
		else if (mode == PickingMode.MOUSE_RELEASED) {
			dragStart = null;
			dragging = false;
		}
	}

	/**
	 * @return the dragging, see {@link #dragging}
	 */
	public boolean isDragging() {
		return dragging;
	}

	/**
	 * fires the given pick object to all listeners of this picking id
	 *
	 * @param pick
	 */
	protected abstract void fire(Pick pick);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pickingId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		APickingEntry other = (APickingEntry) obj;
		if (pickingId != other.pickingId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("APickingEntry [pickingId=");
		builder.append(pickingId);
		builder.append(", objectId=");
		builder.append(objectId);
		builder.append(", dragging=");
		builder.append(dragging);
		builder.append(", lastPoint=");
		builder.append(lastPoint);
		builder.append("]");
		return builder.toString();
	}
}
