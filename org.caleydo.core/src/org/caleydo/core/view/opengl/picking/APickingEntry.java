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
package org.caleydo.core.view.opengl.picking;

import java.awt.Point;

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
	private Point dragStart;
	/**
	 * the last mouse position used by dragging
	 */
	private Point lastPoint;

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
	public void fire(PickingMode mode, Point mouse, float depth, boolean isAnyDragging, IMouseEvent event) {
		if ((mode == PickingMode.CLICKED || mode == PickingMode.DRAGGED || mode == PickingMode.MOUSE_MOVED)
				&& dragStart == null) {
			dragStart = lastPoint = (Point) mouse.clone();
		}

		Pick pick;
		if (mode == PickingMode.DRAGGED || mode == PickingMode.MOUSE_MOVED) {
			int dx = mouse.x - lastPoint.x;
			int dy = mouse.y - lastPoint.y;
			pick = new AdvancedPick(objectId, mode, mouse, dragStart, depth, dx, dy, isAnyDragging, event);
			lastPoint = mouse;
		} else
			pick = new AdvancedPick(objectId, mode, mouse, dragStart, depth, 0, 0, isAnyDragging, event);
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

}
