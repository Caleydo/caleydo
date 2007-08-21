/**
 * 
 */
package org.geneview.graph.item;

import java.util.Collection;

import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;
import org.geneview.graph.generic.IGraphItemGeneric;
import org.geneview.graph.generic.GraphItemGeneric;
import org.geneview.graph.generic.EGraphItemHierarchy;


/**
 * @author Michael Kalkusch
 *
 */
public class GraphItem extends AGraphObjectHeavyweight implements IGraphItem {

	protected IGraphItemGeneric<IGraph, IGraphItem, Object> graphItem;
	
	
	public GraphItem( IGraph parent ) {
		graphItem = new GraphItemGeneric<IGraph,IGraphItem,Object> (parent);
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#addGraphObject(org.geneview.graph.IGraphItem)
	 */	
	public void addGraphItem(IGraphItem add, EGraphItemProperty prop) {
		
		graphItem.addGraphItemByType( add, EGraphItemHierarchy.ITEM_NEIGHBOUR);

	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#addParentGraph(org.geneview.graph.IGraph)
	 */
	@Override
	public void addParentGraph(IGraph setGraph) {
		graphItem.addParentGraph(setGraph);

	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#containsData(int)
	 */
	@Override
	public boolean containsData(int identifier) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#containsDataObject(java.lang.Object)
	 */
	@Override
	public boolean containsDataObject(Object data) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#containsGraphObject(org.geneview.graph.IGraphItem)
	 */
	@Override
	public boolean containsGraphItem(IGraphItem test) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#getAllData()
	 */
	@Override
	public Collection<Object> getAllData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#getAllEdges()
	 */
	@Override
	public Collection<IGraphItem> getAllGraphItemsByProp() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#getAllGraphObject()
	 */
	@Override
	public Collection<IGraphItem> getAllGraphItems() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#getAllNodes()
	 */
	@Override
	public Collection<IGraphItem> getAllNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#getData(int)
	 */
	@Override
	public Object getData(int identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#getParentGraphs()
	 */
	@Override
	public Collection<IGraph> getParentGraphs() {
		graphItem.getAllGraphItemByType(item, type);
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#getParentGraphsByType(int)
	 */
	@Override
	public Collection<IGraph> getParentGraphsByType(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#hasParentGraph(org.geneview.graph.IGraph)
	 */
	@Override
	public boolean hasParentGraph(IGraph setGraph) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#isNode()
	 */
	@Override
	public boolean isNode() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#removeAllData()
	 */
	@Override
	public void removeAllData() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#removeData(int)
	 */
	@Override
	public boolean removeData(int identifier) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#removeGraphObject(org.geneview.graph.IGraphItem)
	 */
	@Override
	public boolean removeGraphObject(IGraphItem remove) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#removeParentGraph(org.geneview.graph.IGraph)
	 */
	@Override
	public void removeParentGraph(IGraph setGraph) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#setData(int, java.lang.Object)
	 */
	@Override
	public void setData(int identifier, Object data) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#setIsNode(boolean)
	 */
	@Override
	public void setIsNode(boolean enable) {
		// TODO Auto-generated method stub

	}

}
