/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
	 * @param wasTriggeredByUser
	 *            Determines whether the user directly set the active renderer, e.g., by clicking a button in a view
	 *            switching bar.
	 */
	public void activeRendererChanged(MultiFormRenderer multiFormRenderer, int rendererID, int previousRendererID,
			boolean wasTriggeredByUser);

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
