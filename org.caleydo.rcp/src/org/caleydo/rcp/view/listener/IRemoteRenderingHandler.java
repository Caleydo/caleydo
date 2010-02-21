package org.caleydo.rcp.view.listener;

import java.util.Set;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.manager.event.IListenerOwner;

public interface IRemoteRenderingHandler
	extends IListenerOwner {

	public void loadDependentPathways(Set<PathwayGraph> newPathwayGraphs);

	public void setConnectionLinesEnabled(boolean enabled);

	public void addPathwayView(final int iPathwayID);

	public void setGeneMappingEnabled(boolean geneMappingEnabled);

	public void setNeighborhoodEnabled(boolean neighborhoodEnabled);

	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled);

	public void toggleNavigationMode();

	public void toggleZoom();

}
