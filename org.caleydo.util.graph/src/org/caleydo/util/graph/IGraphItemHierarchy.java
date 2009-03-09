package org.caleydo.util.graph;

import java.util.List;

/**
 * Handling of IGraph inside IGraphItem.
 * 
 * @see org.caleydo.util.graph.IGraph
 * @see org.caleydo.util.graph.IGraphItem
 * @author Michael Kalkusch
 */
public interface IGraphItemHierarchy {

	/* --- Graphs --- */

	/**
	 * Get a list if graphs depending on EGraphItemHierarchy type. Note, if prop ==
	 * EGraphItemHierarchy.GRAPH_NONE or null all types are matched.
	 * 
	 * @param type
	 *          define type; if prop == EGraphItemHierarchy.NONE or null all types are matched.
	 * @return List of IGraph matching type; if no match is found an empty List is returned, null is never
	 *         retrunred.
	 */
	public List<IGraph> getAllGraphByType(EGraphItemHierarchy type);

	/**
	 * Note, if type == EGraphItemHierarchy.GRAPH_NONE or null all types are matched.
	 * 
	 * @param graph
	 *          graph to be added
	 * @param type
	 *          define type; if type == EGraphItemHierarchy.GRAPH_NONE or null all types are matched
	 */
	public void addGraph(IGraph graph, EGraphItemHierarchy type);

	/**
	 * Remove IGraph graph. Note, if type == EGraphItemHierarchy.GRAPH_NONE or null all types are matched.
	 * 
	 * @param graph
	 *          graph to be removed
	 * @param type
	 *          define type; if type == EGraphItemHierarchy.GRAPH_NONE or null all types are matched
	 * @return TRUE if
	 */
	public boolean removeGraph(IGraph graph, EGraphItemHierarchy type);

	/**
	 * Test if IGraph graph is contained. Note, if type == EGraphItemHierarchy.GRAPH_NONE or null all types are
	 * matched.
	 * 
	 * @see org.caleydo.util.graph.IGraphItem#containsOtherGraph(IGraph)
	 * @param graph
	 *          graph to be tested
	 * @param type
	 *          define type; if type == EGraphItemHierarchy.GRAPH_NONE or null all types are matched
	 * @return TURE if graph is contained matching type
	 */
	public boolean containsGraph(IGraph graph, EGraphItemHierarchy type);

	/**
	 * Test if references to other graphs that to IGraph graph exist ignoring the EGraphItemHierarchy type. Note
	 * this method is intended for IGraphItem#disposeItem().
	 * 
	 * @see org.caleydo.util.graph.IGraphItem#containsGraph(IGraph, EGraphItemHierarchy)
	 * @see org.caleydo.util.graph.IGraphItem#disposeItem()
	 * @param graph
	 *          test if only this graph is referenced by this IGraphItem object
	 * @return FALSE if no other graphs are linked from this IGraphItem, TURE if other graphs than the tested
	 *         graph are linked
	 */
	public boolean containsOtherGraph(IGraph graph);

}
