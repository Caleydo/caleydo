/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway;

import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * Interface for pathway parsers. A pathway parser is responsible to create a full {@link PathwayGraph} for each
 * pathway.
 *
 * @author Christian Partl
 *
 */
public interface IPathwayLoader {

	/**
	 * Creates all {@link PathwayGraph}s for a specific type of pathways, i.e., a pathway database. These graphs must be
	 * created using the {@link PathwayManager} and {@link PathwayVertex}, {@link PathwayVertexRep}, etc. objects must
	 * be created using the {@link PathwayItemManager}.
	 */
	public void parse(EPathwayDatabaseType type);

}
