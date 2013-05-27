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

public enum PickingMode {
	/**
	 * aka mouse down
	 */
	CLICKED,
	DOUBLE_CLICKED,
	/**
	 * when the mouse enters the element
	 */
	MOUSE_OVER,
	/**
	 * aka MOUSE_DRAGGED_MOVED
	 */
	DRAGGED,

	RIGHT_CLICKED,
	/**
	 * when the mouse exists the element
	 */
	MOUSE_OUT,
	/**
	 * special event, when the mouse was moved and is over the element, supported just by {@link SimplePickingManager}
	 * and {@link PickingManager2}
	 */
	MOUSE_MOVED,
	/**
	 * special event, when the mouse was released, supported just by {@link SimplePickingManager} and
	 * {@link PickingManager2}
	 */
	MOUSE_RELEASED
}
