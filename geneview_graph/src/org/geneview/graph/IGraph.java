/**
 * 
 */
package org.geneview.graph;

import org.geneview.graph.IGraphItem;

/**
 * @author java
 *
 */
public interface IGraph {

	/* ---  add --- */
	public boolean addNode(IGraphItem addNode);
	
	public boolean addEdge(IGraphItem addEdge);
	
	public boolean addGraphObject(IGraphItem addGraphObject);
	
	public boolean addSubGraph(IGraph addSubGraph);
	
	
	/* ---  remove --- */
	public boolean removeNode(IGraphItem removeNode);
	
	public boolean removeEdge(IGraphItem removeEdge);
	
	public boolean removeGraphObject(IGraphItem removeGraphObject);
	
	public boolean removeSubGraph(IGraph subGraph);
	
	
	/* ---  contains --- */
		
	public boolean containsGraphObject(IGraphItem addGraphObject);
	
	
	/**
	 * Remove all references to graph objects and sub-graphs.
	 * 
	 * @return
	 */
	public boolean clearGraph();
	
	public boolean clearGraphObjectByType( EGraphType type );
	
	public boolean isEmpty();
	
	public boolean hasGraphProperty( EGraphProperty test );
	
	/* --- define a group of graphs by assigning the same Type Id. --- */
	
	/**
	 * Get Graph Type Id.
	 * Define a group of graphs by assigning the same Type Id.
	 * Default Id == 0 assigned by constructor.
	 */
	public int getTypeId();
	
	
	/**
	 * Set Type Id.
	 * Define a group of graphs by assigning the same Type Id.
	 * 
	 * @param type  Default Id == 0 assigned by constructor.
	 */
	public void setTypeId( int type);
	
}
