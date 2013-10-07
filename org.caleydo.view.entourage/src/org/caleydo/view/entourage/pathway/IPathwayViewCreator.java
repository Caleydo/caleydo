/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.pathway;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Factory for pathway views.
 *
 * @author Christian Partl
 *
 */
public interface IPathwayViewCreator {

	/**
	 * Creates a pathway view.
	 *
	 * @param remoteRenderingView
	 *            View that remote-renders the created view.
	 * @param pathway
	 *            The pathway for the view.
	 * @param tablePerspectives
	 *            The tableperspectives associated with this pathway. Can be used for multi-table perspective on-node
	 *            mappings. May be null.
	 * @param mappingTablePerspective
	 *            The tableperspective for single dataset on-node mappings. May be null.
	 * @param embeddingEventSpace
	 *            Event space that shall be used for events that only a restricted set of receivers in the embedding
	 *            should get.
	 * @return the The layout renderer to visualize the pathway.
	 */
	public AGLView create(AGLView remoteRenderingView, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, TablePerspective mappingTablePerspective,
			String embeddingEventSpace);
}
