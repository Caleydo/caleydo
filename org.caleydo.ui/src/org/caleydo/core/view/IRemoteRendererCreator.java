/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * Interface for creators of remotely rendered {@link ALayoutRenderer}s.
 *
 * @author Christian Partl
 *
 */
public interface IRemoteRendererCreator {
	/**
	 * Subclasses shall create an object of the View to be created.
	 *
	 * @param remoteRenderingView
	 *            View that remote-renders the created renderer.
	 * @param tablePerspectives
	 *            List of {@link TablePerspective} objects that shall be displayed in the renderer.
	 * @param embeddingEventSpace
	 *            Event space that shall be used for events that only a restricted set of receivers in the embedding
	 *            should get.
	 * @return Instance of the renderer.
	 */
	public ALayoutRenderer createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace);
}
