/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;

import org.caleydo.core.view.opengl.layout2.IMouseLayer;


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
	 * @param vec2f
	 */
	void onDrop(IDnDItem item);

	/**
	 * when the item changed (moved, drop type changed) this method will be called
	 *
	 * @param item
	 */
	void onItemChanged(IDnDItem item);

	/**
	 * determine the default {@link EDnDType} for a given {@link IDnDItem} item
	 *
	 * @param item
	 * @return
	 */
	EDnDType defaultSWTDnDType(IDnDItem item);

	/**
	 * triggered when the dragged item moves out of the bounds of this drop target, i.e. if the drop targets gets
	 * removed but was the active one
	 * 
	 * called within {@link IMouseLayer#removeDropTarget(IDropGLTarget)}
	 */
	void onDropLeave();
}