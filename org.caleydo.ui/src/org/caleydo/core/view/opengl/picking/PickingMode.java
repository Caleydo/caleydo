/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
	 * better methods to detect drag starts, supported just by {@link SimplePickingManager} and {@link PickingManager2}
	 */
	DRAG_DETECTED,
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
	MOUSE_RELEASED,

	/**
	 * special event, when the mouse wheel was moved, supported just by {@link SimplePickingManager} and
	 * {@link PickingManager2}
	 */
	MOUSE_WHEEL
}
