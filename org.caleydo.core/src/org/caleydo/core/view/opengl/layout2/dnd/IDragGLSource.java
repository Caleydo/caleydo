/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * a drag source is a place where the user can start drag something. Note: method names containing SWT will be called
 * witin the SWT dispatcher thread
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

	public interface IDragEvent {
		Vec2f getOffset();
	}
}