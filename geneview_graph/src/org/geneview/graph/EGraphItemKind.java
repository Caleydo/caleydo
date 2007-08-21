/**
 * 
 */
package org.geneview.graph;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Define GRAPH or GRAPH_ITEM as well as NODES and EDGES.
 * 
 * @see org.geneview.graph.EGraphProperty
 * @see org.geneview.graph.EGraphItemHierarchy
 * @see org.geneview.graph.EGraphItemProperty
 * 
 * @author Michael Kalkusch
 */
public enum EGraphItemKind {

	NODES( false ),
	EDGES( false ),
	
	/**
	 * Consists of NODES and EDGES
	 */
	GRAPH_ITEM( false ),
	
	GRAPH( true );
	
	/**
	 * Specify if this is a graph or this is not a graph.
	 * IF it is not a graph it must be a graph-item.
	 */
	private boolean bIsGraph;
	
	/**
	 * Constructor.
	 * 
	 * @param isGraph TRUE for GRAPH else false.
	 */
	private EGraphItemKind(boolean isGraph) {
		this.bIsGraph = isGraph;
	}
	
	/**
	 * Test if this is a graph item or a graph.
	 * NODES, EDGES, and GRAPH_ITEM will return FLASE; GRAPH will return TRUE.
	 *
	 * @return NODES, EDGES, and GRAPH_ITEM will return FLASE; GRAPH will return TRUE
	 */
	public final boolean isGraphObject() {
		return this.bIsGraph;
	}
	
	/**
	 * Get a list of active EGraphItemKind items.
	 * 
	 * @see org.geneview.graph.EGraphItemHierarchy#getActiveItems()
	 * @see org.geneview.graph.EGraphItemProperty#getActiveItems()
	 * 
	 * @return list of active EGraphItemHierarchy items
	 */
	public static final Collection<EGraphItemKind> getActiveItems() {
		
		Collection<EGraphItemKind>  resultList = new ArrayList<EGraphItemKind> (3);
		resultList.add(EGraphItemKind.EDGES);
		resultList.add(EGraphItemKind.NODES);
		
		return resultList;
	}
	
}
