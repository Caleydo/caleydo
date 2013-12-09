/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.pathway;

import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Listener for selections of {@link PathwayVertexRep}s.
 *
 * @author Christian
 *
 */
public interface IVertexRepSelectionListener {

	/**
	 *
	 * Called when a {@link PathwayVertexRep} was picked.
	 *
	 * @param vertexRep
	 * @param pick
	 */
	public void onSelect(PathwayVertexRep vertexRep, Pick pick);

}
