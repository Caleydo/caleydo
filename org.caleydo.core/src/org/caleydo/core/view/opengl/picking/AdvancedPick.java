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

import java.awt.Dimension;
import java.awt.Point;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;

/**
 * simpler wrapper to signalizes, whether this kind of pickings are coming from the "better" picking manager
 *
 */
public class AdvancedPick extends Pick implements IMouseEvent {
	private final IMouseEvent event;

	public AdvancedPick(int objectID, PickingMode ePickingMode, Point pickedPoint, Point dragStartPoint, float depth,
			int dx, int dy, boolean isAnyDragging, IMouseEvent event) {
		super(objectID, ePickingMode, pickedPoint, dragStartPoint, depth, dx, dy, isAnyDragging);
		this.event = event;
	}

	public AdvancedPick(int objectID, PickingMode ePickingMode, Point pickedPoint, Point dragStartPoint, float depth,
			IMouseEvent event) {
		super(objectID, ePickingMode, pickedPoint, dragStartPoint, depth);
		this.event = event;
	}

	@Override
	public Point getPoint() {
		return getPickedPoint();
	}

	@Override
	public int getClickCount() {
		return event == null ? -1 : event.getClickCount();
	}


	@Override
	public int getWheelRotation() {
		return event == null ? 0 : event.getWheelRotation();
	}


	@Override
	public int getButton() {
		return event == null ? 0 : event.getButton();
	}


	@Override
	public boolean isButtonDown(int button) {
		return event == null ? false : event.isButtonDown(button);
	}


	@Override
	@Deprecated
	public Dimension getParentSize() {
		return null;
	}

	@Override
	public boolean isShiftDown() {
		return event == null ? false : event.isShiftDown();
	}

	@Override
	public boolean isAltDown() {
		return event == null ? false : event.isAltDown();
	}

	@Override
	public boolean isCtrlDown() {
		return event == null ? false : event.isCtrlDown();
	}
}
