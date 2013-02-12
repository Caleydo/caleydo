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

import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.eclipse.swt.SWT;

/**
 * basic interface for the context of a element hierarchy, e.g. one {@link IGLElementContext} per opengl canvas
 *
 * @author Samuel Gratzl
 *
 */
public interface IGLElementContext {
	/**
	 * register a picking listener
	 *
	 * @param l
	 * @return the pickingID to use for rendering
	 */
	public int registerPickingListener(IPickingListener l);

	/**
	 * see {@link #registerPickingListener(IPickingListener)} with a dedicated objectId
	 *
	 * @param l
	 * @param objectId
	 * @return
	 */
	public int registerPickingListener(IPickingListener l, int objectId);

	/**
	 * unregisters a picking listener
	 *
	 * @param l
	 */
	public void unregisterPickingListener(IPickingListener l);

	/**
	 * unregisters a picking listener, given by its pickingId
	 *
	 * @param pickingID
	 */
	public void unregisterPickingListener(int pickingID);

	/**
	 * returns the {@link TextureManager} of this context
	 *
	 * @return
	 */
	public TextureManager getTextureManager();

	/**
	 * returns the {@link DisplayListPool} of this context
	 *
	 * @return
	 */
	public DisplayListPool getDisplayListPool();

	/**
	 * returns a special {@link GLElementContainer} that will be positioned at the mouse position
	 *
	 * @return
	 */
	public IMouseLayer getMouseLayer();

	/**
	 * chance for a context to initialize an added element
	 *
	 * @param element
	 */
	public void init(GLElement element);

	/**
	 * chance for a context to undo the initialization of added element, see {@link #init(GLElement)}
	 *
	 * @param element
	 */
	public void takeDown(GLElement element);

	/**
	 * the the current cursor, using {@link SWT} constants
	 *
	 * @param the
	 *            SWT constant or -1 for the default
	 */
	public void setCursor(int swtCursorConst);
}
