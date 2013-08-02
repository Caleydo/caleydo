/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.awt.Point;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.canvas.Units;

/**
 * simpler wrapper to signalizes, whether this kind of pickings are coming from the "better" picking manager
 *
 */
public class AdvancedPick extends Pick implements IMouseEvent {
	private final IMouseEvent event;

	public AdvancedPick(int objectID, PickingMode ePickingMode, Vec2f dragStartPoint, float depth,
 Vec2f dv,
			boolean isAnyDragging, IMouseEvent event) {
		super(objectID, ePickingMode, event.getPoint(), dragStartPoint, depth, dv, isAnyDragging);
		this.event = event;
	}

	public AdvancedPick(int objectID, PickingMode ePickingMode, Vec2f dragStartPoint, float depth,
			IMouseEvent event) {
		super(objectID, ePickingMode, event.getPoint(), dragStartPoint, depth);
		this.event = event;
	}

	@Override
	public Vec2f getPoint() {
		return event.getPoint();
	}

	@Override
	public Vec2f getPoint(Units unit) {
		return event.getPoint(unit);
	}

	@Override
	public Point getRAWPoint() {
		return event.getRAWPoint();
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
