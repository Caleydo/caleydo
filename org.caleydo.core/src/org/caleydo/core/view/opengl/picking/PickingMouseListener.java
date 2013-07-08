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

import static org.caleydo.core.util.collection.Pair.make;

import java.awt.Point;
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

	public Point getCurrentMousePos() {
		if (event.isEmpty())
			return null;
		return event.getLast().getFirst().getPoint();
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
