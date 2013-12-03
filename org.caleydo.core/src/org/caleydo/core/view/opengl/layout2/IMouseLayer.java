/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.base.ILabeled;
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
	 * marker interface for dragging meta data
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IDragInfo extends ILabeled {

	}

	public interface IDnDItem {
		IDragInfo getInfo();

		EDnDType getType();
	}
	/**
	 * a drop target is a place where to drop something. Note: method names containing SWT will be called witin the SWT
	 * dispatcher thread
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IDropGLTarget {
		/**
		 * called within SWT thread: can the given item be dropped here
		 *
		 * @param input
		 * @return
		 */
		boolean canSWTDrop(IDnDItem item);

		/**
		 * drop the given item
		 *
		 * @param item
		 */
		void onDrop(IDnDItem item);

		/**
		 * when the item changed (moved, drop type changed) this method will be called
		 *
		 * @param item
		 */
		void onItemChanged(IDnDItem item);

		/**
		 * @param item
		 * @return
		 */
		EDnDType defaultSWTDnDType(IDnDItem item);
	}

	/**
	 * a drag source is a place where the user can start drag something. Note: method names containing SWT will be
	 * called witin the SWT dispatcher thread
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IDragGLSource {
		/**
		 * create a drag info
		 *
		 * @param event
		 * @return the element to drag or nothing if nothing draggable
		 */
		IDragInfo startSWTDrag(IDragEvent event);

		/**
		 * when the item was dropped this method wil be called
		 *
		 * @param info
		 */
		void onDropped(IDnDItem info);

		/**
		 * create a graphical representation of this element
		 *
		 * @param info
		 * @return
		 */
		GLElement createUI(IDragInfo info);
	}

	public enum EDnDType {
		NONE, MOVE, COPY, LINK
	}

	public interface IDragEvent {
		Vec2f getOffset();
	}
}

