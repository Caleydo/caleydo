/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec4f;

/**
 * a layer above the content, i.e. for popups
 *
 * @author Samuel Gratzl
 *
 */
public interface IPopupLayer {
	int FLAG_CLOSEABLE = 1 << 0;
	int FLAG_RESIZEABLE = 1 << 1;
	int FLAG_MOVEABLE = 1 << 2;
	int FLAG_BORDER = 1 << 3;
	int FLAG_ALL = FLAG_CLOSEABLE | FLAG_RESIZEABLE | FLAG_MOVEABLE | FLAG_BORDER;

	/**
	 * see {@link #show(GLElement, Vec4f, int)} with all flags set
	 *
	 * @param popup
	 *            the popup to show
	 * @param bounds
	 *            its bounds
	 */
	void show(GLElement popup, Vec4f bounds);

	/**
	 * shows a popup with the given content and the given bounds
	 *
	 * @param popup
	 * @param bounds
	 * @param flags
	 *            see {@link #FLAG_CLOSEABLE} and others
	 */
	void show(GLElement popup, Vec4f bounds, int flags);

	/**
	 * hides a popup again
	 *
	 * @param popup
	 */
	void hide(GLElement popup);
}

