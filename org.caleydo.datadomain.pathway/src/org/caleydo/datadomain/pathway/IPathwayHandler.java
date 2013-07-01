/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway;

import java.util.Set;

import org.caleydo.core.event.IListenerOwner;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

public interface IPathwayHandler extends IListenerOwner {

	public void loadDependentPathways(Set<PathwayGraph> pathwayGraphs);

	public void addPathwayView(final int iPathwayID, String dataDomainID);

	public void setGeneMappingEnabled(boolean geneMappingEnabled);

	public void setNeighborhoodEnabled(boolean neighborhoodEnabled);

	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled);
}
