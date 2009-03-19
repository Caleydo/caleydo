/**
 * 
 */
package org.caleydo.util.graph;

/**
 * Top level interface for all graph components.
 * 
 * @see org.caleydo.util.graph.IGraph
 * @see org.caleydo.util.graph.IGraphItem
 * @author Michael Kalkusch
 */
public interface IGraphComponent {

	/**
	 * Removes this GraphItem from all objects linked to it. Calls all other GraphItmes and removes the
	 * reference; also removed references inside all linked graphs based on the data stored inside this
	 * IGraphItem. Attention: IGraph objects ignore this call; its implementation is an empty method, because
	 * the IGraphItem are responsible for unregistering themselves at their referenced graphs. Note: If the
	 * hole graph is disposed and the IGraphItem is not linked to other graphs this method can be skipped. To
	 * test if only a graph is linked call containsOtherGraph(IGraph)
	 * 
	 * @see org.caleydo.util.graph.IGraphItem#containsOtherGraph(IGraph)
	 */
	public void disposeItem();

	/**
	 * Get Graph Type Id. Define a group of graphs by assigning the same Type Id. Default Id == 0 assigned by
	 * constructor.
	 */
	public int getId();

	/**
	 * Set Type Id. Define a group of graphs by assigning the same Type Id.
	 * 
	 * @param type
	 *            Default Id == 0 assigned by constructor.
	 */
	public void setId(int type);

}
