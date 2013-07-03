/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.path;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IRemoteRendererCreator;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;

/**
 * Creator for {@link ContextualPathsRenderer}.
 *
 * @author Christian Partl
 *
 */
public class ThumbnailedContextualPathsCreator implements IRemoteRendererCreator {

	@Override
	public ALayoutRenderer createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace) {

		if (tablePerspectives.size() > 0) {
			TablePerspective tablePerspective = tablePerspectives.get(0);
			if (!(tablePerspective instanceof PathwayTablePerspective)) {
				throw new IllegalArgumentException(
						"The provided table perspective must be of type PathwayTablePerspective.");
			}
			ContextualPathsRenderer renderer = new ContextualPathsRenderer(remoteRenderingView, embeddingEventSpace,
					((PathwayTablePerspective) tablePerspective).getPathway(), tablePerspectives, true);
			renderer.init();

			return renderer;
		}

		return null;
	}

}
