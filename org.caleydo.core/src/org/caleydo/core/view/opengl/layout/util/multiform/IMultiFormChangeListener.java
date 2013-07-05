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
package org.caleydo.core.view.opengl.layout.util.multiform;

/**
 * Listener that is informed about changes within a {@link MultiFormRenderer}.
 *
 * @author Christian Partl
 *
 */
public interface IMultiFormChangeListener {

	/**
	 * Called, when the renderer that is currently active was changed.
	 *
	 * @param multiFormRenderer
	 * @param rendererID
	 *            ID of the renderer that was set active.
	 * @param previousRendererID
	 *            ID of the renderer that was active before. -1 if there was no active renderer before.
	 */
	public void activeRendererChanged(MultiFormRenderer multiFormRenderer, int rendererID, int previousRendererID);

	/**
	 * Called, when a renderer was added.
	 *
	 * @param multiFormRenderer
	 * @param rendererID
	 *            ID of the renderer that was added.
	 */
	public void rendererAdded(MultiFormRenderer multiFormRenderer, int rendererID);

	/**
	 * Called, when a renderer was removed.
	 *
	 * @param multiFormRenderer
	 * @param rendererID
	 *            ID of the renderer that was removed.
	 */
	public void rendererRemoved(MultiFormRenderer multiFormRenderer, int rendererID);

	/**
	 * Called, when the multiform renderer is destroyed.
	 *
	 * @param multiFormRenderer
	 */
	public void destroyed(MultiFormRenderer multiFormRenderer);
}
