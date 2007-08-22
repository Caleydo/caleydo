package org.geneview.graph;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * Graph hierarchy properties like "PARENT, CHILD, NEIGHBOUR" as well as "GRAPH_ALIAS" and "GRAPH_NONE".
 * Describe properties of a graph.
 * 
 * @see org.geneview.graph.EGraphProperty
 * @see org.geneview.graph.EGraphItemProperty
 * @see org.geneview.graph.EGraphItemKind
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
	
	public static boolean isPartOfHierachy(EGraphItemHierarchy test) {
		return test.equals(EGraphItemHierarchy.GRAPH_NONE) ? false : true;
	}

	/**
	 * Get a list of active EGraphItemHierarchy items.
	 * 
	 * @see org.geneview.graph.item.GraphItem#GraphItem(EGraphItemKind)
	 * @see org.geneview.graph.EGraphItemProperty#getActiveItems()
	 * @see org.geneview.graph.EGraphItemKind#getActiveItems()
	 * 
	 * @return list of active EGraphItemHierarchy items
	 */
	public static final Collection<EGraphItemHierarchy> getActiveItems() {
		
		Collection<EGraphItemHierarchy>  resultList = new ArrayList<EGraphItemHierarchy> (4);
		resultList.add(EGraphItemHierarchy.GRAPH_PARENT );
		resultList.add(EGraphItemHierarchy.GRAPH_NEIGHBOUR);
		resultList.add(EGraphItemHierarchy.GRAPH_CHILDREN);
		resultList.add(EGraphItemHierarchy.GRAPH_ALIAS);
		
		return resultList;
	}
}
