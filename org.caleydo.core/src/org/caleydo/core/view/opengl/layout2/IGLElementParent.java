/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.layout.IHasGLLayoutData;

/**
 * an element that can be the parent of another element
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLElementParent extends IHasGLLayoutData {
	/**
	 * triggers that the parent should be re layouted
	 */
	void relayout();

	/**
	 * triggers that the parent hierarchy will be repainted
	 */
	void repaint();

	/**
	 * triggers that the parents hierarchy will be repainted just the picking
	 */
	void repaintPick();

	/**
	 * notification that the child will be moved to another parent
	 *
	 * @param child
	 * @return whether the child was a already initialized
	 */
	boolean moved(GLElement child);

	/**
	 * @return the parent of this parent
	 */
	IGLElementParent getParent();

	/**
	 * converts the relative location in the parent coordinate system to an absolute one
	 *
	 * @param relative
	 * @return
	 */
	Vec2f toAbsolute(Vec2f relative);

	/**
	 * converts the absolute location in relative to the parent coordinate system
	 *
	 * @param absolute
	 * @return
	 */
	Vec2f toRelative(Vec2f absolute);

	Vec2f getSize();
}
