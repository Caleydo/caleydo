package org.caleydo.datadomain.pathway;

import java.util.Set;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

public interface IPathwayLoader extends IListenerOwner {

	public void loadDependentPathways(Set<PathwayGraph> pathwayGraphs);
}
