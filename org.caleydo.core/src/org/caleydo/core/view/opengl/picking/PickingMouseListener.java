/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import static org.caleydo.core.util.collection.Pair.make;
import gleem.linalg.Vec2f;

import java.util.ArrayDeque;
import java.util.Deque;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;

/**
 * specialized listener for picking
 *
 * @author Samuel Gratzl
 *
 */
final class PickingMouseListener implements IGLMouseListener {

	private final Deque<Pair<IMouseEvent, PickingMode>> event = new ArrayDeque<>(5);
	private boolean isMouseIn = false;


	/**
	 * @return the isMouseIn, see {@link #isMouseIn}
	 */
	public boolean isMouseIn() {
		return isMouseIn;
	}

	public Vec2f getCurrentMousePos() {
		if (event.isEmpty())
			return null;
		return event.getLast().getFirst().getDIPPoint();
	}

	public synchronized Deque<Pair<IMouseEvent, PickingMode>> fetchEvents() {
		Deque<Pair<IMouseEvent, PickingMode>> tmp = new ArrayDeque<>(event);
		event.clear();
		return tmp;
	}

	private synchronized void add(IMouseEvent mouseEvent, PickingMode mode) {
		event.add(make(mouseEvent, mode));
	}

	@Override
	public void mousePressed(IMouseEvent mouseEvent) {
		isMouseIn = true;
		add(mouseEvent, PickingMode.CLICKED);
	}

	@Override
	public void mouseMoved(IMouseEvent mouseEvent) {
		isMouseIn = true;
		add(mouseEvent, PickingMode.MOUSE_MOVED);
	}

	@Override
	public void mouseClicked(IMouseEvent mouseEvent) {

	}

	@Override
	public void mouseReleased(IMouseEvent mouseEvent) {
		isMouseIn = true;
		add(mouseEvent, PickingMode.MOUSE_RELEASED);
	}

	@Override
	public void mouseDragged(IMouseEvent mouseEvent) {
		isMouseIn = true;
		add(mouseEvent, PickingMode.DRAGGED);
	}

	@Override
	public void mouseWheelMoved(IMouseEvent e) {

	}

	@Override
	public void mouseEntered(IMouseEvent mouseEvent) {
		isMouseIn = true;
		add(mouseEvent, PickingMode.MOUSE_OVER);
	}

	@Override
	public void mouseExited(IMouseEvent mouseEvent) {
		isMouseIn = false;
		add(mouseEvent, PickingMode.MOUSE_OUT);
	}

}
