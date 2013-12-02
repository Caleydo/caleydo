/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.base.ILabeled;

/**
 * a abstract form of the mouse layer, i.e. the canvas that moves with the mouse
 *
 * @author Samuel Gratzl
 *
 */
public interface IMouseLayer {
	/**
	 * checks if any draggable element exists, which has {@link IMultiViewDragInfo} of the given type
	 *
	 * @param type
	 *            the type to check
	 * @return if any draggable element exists, fulfilling the criteria
	 */
	boolean isDragging(Class<? extends IDragInfo> type);

	void addDragSource(IDragGLSource dragSource);

	void removeDragSource(IDragGLSource dragSource);

	/**
	 * @param dropTarget
	 */
	void addDropTarget(IDropGLTarget dropTarget);

	/**
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

	public interface IMultiViewDragInfo extends IDragInfo {

	}

	public interface IDropGLTarget {
		boolean canDrop(IDnDItem input);

		void onDrop(IDnDItem input);

		/**
		 * @param input
		 */
		void onItemChanged(IDnDItem input);
	}


	public interface IDnDItem {
		IDragInfo getInfo();

		EDnDType getType();
	}

	public interface IDragGLSource {
		IDragInfo startDrag(IDragEvent event);

		void onDropped(IDnDItem info);

		GLElement createUI(IDragInfo info);
	}

	public enum EDnDType {
		NONE, MOVE, COPY, LINK
	}

	public interface IDragEvent {
		Vec2f getOffset();
	}
}

