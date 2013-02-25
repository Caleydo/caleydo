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

