/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.layout2.dnd.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.dnd.IRemoteDragInfoUICreator;
import org.caleydo.core.view.opengl.picking.PickingMode;

/**
 * a abstract form of the mouse layer, i.e. the canvas that moves with the mouse
 *
 * @author Samuel Gratzl
 *
 */
public interface IMouseLayer {
	/**
	 * checks if any draggable element exists, which has {@link IDragInfo} of the given type
	 *
	 * @param type
	 *            the type to check
	 * @return if any draggable element exists, fulfilling the criteria
	 */
	boolean isDragging(Class<? extends IDragInfo> type);

	/**
	 * adds a {@link IDragGLSource} to the mouse layer, this should be typically called within the
	 * {@link PickingMode#MOUSE_OVER} case
	 *
	 * @param dragSource
	 */
	void addDragSource(IDragGLSource dragSource);

	/**
	 * removes a {@link IDragGLSource} from the mouse layer, this should be typically called within the
	 * {@link PickingMode#MOUSE_OUT} case
	 *
	 * @param dragSource
	 */
	void removeDragSource(IDragGLSource dragSource);

	/**
	 * adds a {@link IDropGLTarget} to the mouse layer, this should be typically called within the
	 * {@link PickingMode#MOUSE_OVER} case
	 *
	 * @param dropTarget
	 */
	void addDropTarget(IDropGLTarget dropTarget);

	/**
	 * removes a {@link IDropGLTarget} from the mouse layer, this should be typically called within the
	 * {@link PickingMode#MOUSE_OUT} case
	 *
	 * @param dropTarget
	 */
	void removeDropTarget(IDropGLTarget dropTarget);

	/**
	 * register a {@link IRemoteDragInfoUICreator} for creating a visual representation, for external {@link IDragInfo}s
	 * 
	 * @param creator
	 */
	void addRemoteDragInfoUICreator(IRemoteDragInfoUICreator creator);
}

