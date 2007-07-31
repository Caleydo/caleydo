/**
 * 
 */
package org.geneview.graph;

import java.util.Collection;

import org.geneview.graph.IGraph;

/**
 * @author java
 *
 */
public interface IGraphItem {

	public Object getData( int identifier );
	
	public Collection <Object> getAllData();
	
	public void setData( int identifier, Object data );
	
	public boolean removeData( int identifier );
	
	public void removeAllData();
	
	public boolean containsData( int identifier );
	
	public boolean containsDataObject( Object data );
	
	/* ---------------- */
	
	/**
	 * @see org.geneview.graph.IGraphItem#setIsNode(boolean)
	 * 
	 *  @return TRUE if node FALSE if edge
	 */
	public boolean isNode();
	
	/**
	 * @see org.geneview.graph.IGraphItem#isNode()
	 * 
	 * @param enable
	 */
	public void setIsNode( boolean enable );
	
	public Collection<IGraphItem> getAllGraphItemsByProp();
	
	public Collection<IGraphItem> getAllGraphItems();
	
	public void addGraphItem(IGraphItem add, EGraphItemProperty prop);
	
	public boolean removeGraphObject(IGraphItem remove);
	
	public boolean containsGraphItem(IGraphItem test);
	
	public boolean containsGraphItemByProp(IGraphItem test, EGraphItemProperty prop);
	
	/* --- parent graphs --- */
	
	public Collection<IGraph> getParentGraphs();
	
	public Collection<IGraph> getParentGraphsByType( int id );
	
	public void addParentGraph( IGraph setGraph );
	
	public void removeParentGraph( IGraph setGraph );
	
	public boolean hasParentGraph( IGraph setGraph );		
	
}
