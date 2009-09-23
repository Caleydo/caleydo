/**
 * 
 */
package org.caleydo.util.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Define GRAPH or GRAPH_ITEM as well as NODE and EDGE.
 * 
 * @see org.caleydo.util.graph.EGraphProperty
 * @see org.caleydo.util.graph.EGraphItemHierarchy
 * @see org.caleydo.util.graph.EGraphItemProperty
 * @author Michael Kalkusch
 */
public enum EGraphItemKind {

	NODE(false), EDGE(false),

	// /**
	// * Consists of NODE and EDGE
	// */
	// GRAPH_ITEM( false ),

	GRAPH(true);

	/**
	 * Specify if this is a graph or this is not a graph. IF it is not a graph
	 * it must be a graph-item.
	 */
	private boolean bIsGraph;

	/**
	 * Constructor.
	 * 
	 * @param isGraph
	 *            TRUE for GRAPH else false.
	 */
	private EGraphItemKind(boolean isGraph) {
		this.bIsGraph = isGraph;
	}

	/**
	 * Test if this is a graph item or a graph. NODE and EDGE will return FLASE;
	 * GRAPH will return TRUE.
	 * 
	 * @return NODE, EDGE will return FLASE; GRAPH will return TRUE
	 */
	public final boolean isGraphObject() {
		return this.bIsGraph;
	}

	/**
	 * Get a list of active EGraphItemKind items.
	 * 
	 * @see org.caleydo.util.graph.EGraphItemHierarchy#getActiveItems()
	 * @see org.caleydo.util.graph.EGraphItemProperty#getActiveItems()
	 * @return list of active EGraphItemHierarchy items
	 */
	public static final List<EGraphItemKind> getActiveItems() {

		List<EGraphItemKind> resultList = new ArrayList<EGraphItemKind>(3);
		resultList.add(EGraphItemKind.EDGE);
		resultList.add(EGraphItemKind.NODE);

		return resultList;
	}

}
