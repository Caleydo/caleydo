/**
 * 
 */
package org.geneview.graph;

import java.util.Collection;

import org.geneview.graph.EGraphItemHierarchy;
import org.geneview.graph.IGraphComponent;
import org.geneview.graph.GraphRuntimeException;

/**
 * Interface for graphs.
 * 
 * @author Michael Kalkusch
 *
 */
public interface IGraph extends IGraphComponent {

	/* ---  IGraphItem --- */
	
	/**
	 * Adds a new IGraphItem.
	 * 
	 * @param item IGraphItem to be added
	 * @return TRUE if element was added and did not exist before.
	 */
	public boolean addItem(IGraphItem item);
	
	/**
	 * Updates state of IGraphItem like EGraphItemKind. If item was not registered false is returned.
	 * 
	 * @param item IGraphItem to be updated
	 * @return TRUE if update was successful
	 */
	public boolean updateItem(IGraphItem item);
	
	/**
	 * Removes item
	 * 
	 * @param item IGraphItem to be removed
	 * * @return TURE if item was removed and existed inside graph.
	 */
	public boolean removeItem(IGraphItem item);
	
	/**
	 * Test if item is registered to this graph.
	 * 
	 * @param item IGraphItem to be tested
	 * @return TRUE if item is registered
	 */
	public boolean containsItem(IGraphItem item);
	
	/**
	 * Get all items matching a EGraphItemKind; if EGraphItemKind prop==null all stored IGraphItem will be returned.
	 * 
	 * @param kind define which items, if prop==null all IGraphItem objects will be returned.
	 * @return list of IGraphItem
	 */
	public Collection<IGraphItem> getAllItemsByKind(EGraphItemKind kind);


	/* ---  IGraph --- */	
	

	/**
	 * Adds a <key,value> pair of <type,graph>.
	 * 
	 * @param graph add reference this graph
	 * @param type define EGraphItemHierarchy; EGraphItemHierarchy.GRAPH_NONE is invalid and cause a GraphRuntimeException
	 * @return TRUE if graph was added and was not already part of this graph
	 */
	public boolean addGraph(IGraph graph, EGraphItemHierarchy type) throws GraphRuntimeException;
	
	/**
	 * Removes a reference to another graph depending on the EGraphItemHierarchy type.
	 * Note: only if (type,graph) matches, the graph is removed and TRUE is returned.
	 * 
	 * If
	 * 
	 * @param graph graph to be removed
	 * @param type define type; only if type,graph matches the graph is removed and TRUE is returned; if type==null or EGraphItemHierarchy.GRAPH_NONE graph is removed from any lists.
	 * @return TRUE if graph was found and removed
	 */
	public boolean removeGraph(IGraph graph, EGraphItemHierarchy type);
	
	
	/**
	 * Remove all references to graph objects and sub-graphs.
	 * Note: All IGraphItem objects are called and the reference to this graph is removed.
	 * 
	 * Attention: Only required, if nodes and edges used inside this graph are used inside other graphs to.
	 * 
	 */
	public void clearGraph();
	
	/**
	 * Remove all references to graph objects matching EGraphItemType type.
	 * 
	 * @param kind specify type
	 */
	public void removeAllByKind( EGraphItemKind kind );
	
	/* ---  properties of the graph --- */
	
	/**
	 * Test if graph contain any GraphItems or references to other graphs.
	 *  
	 * @return TRUE if this graph does not contain any GraphItems nor references to other graphs.
	 */
	public boolean isEmpty();
	
	/**
	 * Test if graph has a certain EGraphProperty.
	 * 
	 * @see org.geneview.graph.IGraph#setGraphProperty(EGraphProperty, boolean)
	 * 
	 * @param test property to be tested
	 * @return TRUE if property is set, FALSE else
	 */
	public boolean hasGraphProperty( EGraphProperty test );
	
	/**
	 * Set a graph property.
	 * 
	 * @see org.geneview.graph.IGraph#hasGraphProperty(EGraphProperty)
	 * 
	 * @param prop property to be set
	 * @param value TURE of FALSE
	 */
	public void setGraphProperty(final EGraphProperty prop, final boolean value);
	
	/* --- define a group of graphs by assigning the same Type Id. --- */
	

	
}
