/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec4f;

import org.caleydo.core.view.opengl.layout2.geom.Rect;

/**
 * a layer above the content, i.e. for popups
 *
 * @author Samuel Gratzl
 *
 */
public interface IPopupLayer {
	/**
	 * closeable popup
	 */
	int FLAG_CLOSEABLE = 1 << 0;
	/**
	 * resizeable popup
	 */
	int FLAG_RESIZEABLE = 1 << 1;
	/**
	 * moveable popup
	 */
	int FLAG_MOVEABLE = 1 << 2;
	/**
	 * border around the popup
	 */
	int FLAG_BORDER = 1 << 3;
	/**
	 * collapse by double clicking the header
	 */
	int FLAG_COLLAPSABLE = 1 << 4;
	int FLAG_ALL = FLAG_CLOSEABLE | FLAG_RESIZEABLE | FLAG_MOVEABLE | FLAG_BORDER;

	/**
	 * see {@link #show(GLElement, Vec4f, int)} with all flags set
	 *
	 * @param popup
	 *            the popup to show
	 * @param bounds
	 *            its bounds
	 */
	void show(GLElement popup, Rect bounds);

	/**
	 * shows a popup with the given content and the given bounds
	 *
	 * @param popup
	 * @param bounds
	 *            (x,y: if negative count from the right side, if NaN centering), if null fullscreen
	 * @param flags
	 *            see {@link #FLAG_CLOSEABLE} and others
	 */
	void show(GLElement popup, Rect bounds, int flags);

	/**
	 * hides a popup again
	 *
	 * @param popup
	 */
	void hide(GLElement popup);
}

