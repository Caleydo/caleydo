package org.caleydo.core.util.clusterer;

import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.core.Graph;

/**
 * The Hierarchy manager is in charge of creating and handling the clustered hierarchy
 * 
 * @author Bernhard Schlegl
 */
public class HierarchyManager {

	private Graph rootGraph;
	
	public HierarchyManager() {

		setRootGraph(new Graph(0));
	}

	public void setRootGraph(Graph rootGraph) {
		this.rootGraph = rootGraph;
	}

	public Graph getRootGraph() {
		return rootGraph;
	}
	
	public void addChildren(HierarchyGraph graph)
	{
		rootGraph.addGraph(graph, EGraphItemHierarchy.GRAPH_CHILDREN);
	}

}
