package org.geneview.graph.generic;

/**
 * 
 * Graph hierarchy properties like "PARENT, CHILD, NEIGHBOUR" as well as "GRAPH_ALIAS" and "GRAPH_NONE".
 * Describe properties of a graph.
 * 
 * @author Michael Kalkusch
 *
 */
public enum EGraphItemHierarchy {
	
	GRAPH_PARENT,
	GRAPH_CHILDREN,
	GRAPH_NEIGHBOUR,
	
	GRAPH_ALIAS,
	//GRAPH_DATA,
	GRAPH_NONE;
}
