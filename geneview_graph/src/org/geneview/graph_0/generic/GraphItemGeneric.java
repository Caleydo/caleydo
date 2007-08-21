/**
 * 
 */
package org.geneview.graph.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;



/**
 * @author java
 *
 */
public class GraphItemGeneric <GraphParent,GraphItem,GraphData> implements IGraphItemGeneric<GraphParent, GraphItem, GraphData> {

	private HashMap<Integer, GraphData> hashId2GraphData;
	
	protected final Collection<GraphParent> listHierarchy;
	
	//protected Collection<GraphData> listData;
	protected Collection<GraphItem> listParent;
	protected Collection<GraphItem> listNeighbour;
	protected Collection<GraphItem> listChildren;

	public GraphItemGeneric( final GraphParent parentGraph ) {
		this(parentGraph, 5, 3, 3, 3);
	}
	
	public enum EGraphItemHierarchy {
		ITEM_PARENT,
		ITEM_CHILDREN,
		ITEM_NEIGHBOUR,
		
		ITEM_DATA,
		
		HIERARCHY_PARENT,
		HIERARCHY_CHILDREN,
		HIERARCHY_ROOT;
	}
	
	/**
	 * @param dataSize
	 * @param nodeSize
	 * @param edgeSize
	 */
	public GraphItemGeneric(  final GraphParent parentGraph, 
			int iDataSize, 
			int iParentSize, 
			int iChildrenSize,
			int iNeighboureSize) {
		
		//listData = new ArrayList <GraphData> (iDataSize);
		listParent = new ArrayList <GraphItem> (iParentSize);
		listChildren = new ArrayList <GraphItem> (iChildrenSize);
		listNeighbour = new ArrayList <GraphItem> (iNeighboureSize);	
		hashId2GraphData = new HashMap <Integer, GraphData> (iDataSize);
		
		listHierarchy = new ArrayList <GraphParent> (2);
		
		addParentGraph(parentGraph);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#addGraphItemByType(GraphItem, org.geneview.graph.GraphItemGeneric.EGraphItemHierarchy)
	 */
	public void addGraphItemByType(GraphItem item, 
			EGraphItemHierarchy type) {
		switch (type) {
		case ITEM_PARENT:
			if (listParent.contains(item)) {
				return;
			}
			listParent.add(item);
			break;
		case ITEM_CHILDREN:
			if (listChildren.contains(item)) {
				return;
			}
			listChildren.add(item);
			break;
		case ITEM_NEIGHBOUR:
			if (listNeighbour.contains(item)) {
				return;
			}
			listNeighbour.add(item);
			break;
			
		default:
			throw new RuntimeException("unsupported type " + type);
		}

	}
	

	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#removeGraphItemByType(GraphItem, org.geneview.graph.GraphItemGeneric.EGraphItemHierarchy)
	 */
	public boolean removeGraphItemByType(GraphItem item, 
			EGraphItemHierarchy type) {
		
		switch (type) {
		case ITEM_PARENT:
			return listParent.remove(item);
			
		case ITEM_CHILDREN:
			return listChildren.remove(item);
			
		case ITEM_NEIGHBOUR:
			return listNeighbour.remove(item);
			
		default:
			throw new RuntimeException("unsupported type " + type);
		}

	}
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#getAllGraphItemByType(GraphItem, org.geneview.graph.GraphItemGeneric.EGraphItemHierarchy)
	 */
	public Collection<GraphItem> getAllGraphItemByType(GraphItem item, 
			EGraphItemHierarchy type) {

		int iIteratorSize = 0;
		Iterator <GraphItem> iter;
		
		switch (type) {
		case ITEM_PARENT:
			iIteratorSize = listParent.size();
			iter = listParent.iterator();
			break;
			
		case ITEM_CHILDREN:
			iIteratorSize = listChildren.size();
			iter = listChildren.iterator();
			break;
			
		case ITEM_NEIGHBOUR:
			iIteratorSize = listNeighbour.size();
			iter = listNeighbour.iterator();
			break;
			
		default:
			throw new RuntimeException("unsupported type " + type);
		}

		if ( iIteratorSize == 0 ) {
			/* return empty collection */
			return new ArrayList <GraphItem> (0);
		}
		
		/* create container for copy */
		Collection<GraphItem> container = 
			new ArrayList <GraphItem> (iIteratorSize);
		
		while (iter.hasNext()) {
			container.add( iter.next() );
		}
		
		return container;
	}
	
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#containsGraphItemByType(GraphItem, org.geneview.graph.GraphItemGeneric.EGraphItemHierarchy)
	 */
	public boolean containsGraphItemByType(GraphItem item, 
			EGraphItemHierarchy type) {
		
		switch (type) {
		case ITEM_PARENT:
			return listParent.contains(item);
			
		case ITEM_CHILDREN:
			return listChildren.contains(item);
			
		case ITEM_NEIGHBOUR:
			return listNeighbour.contains(item);
			
		default:
			throw new RuntimeException("unsupported type " + type);
		}

	}
	
	/* ---  GRAPH DATA --- */
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#addGraphDataById(GraphData, int)
	 */
	public boolean addGraphDataById(GraphData data, int id) {
		
		if ( hashId2GraphData.containsKey(id) ) {
			return false;
		}
		
		hashId2GraphData.put(id,data);		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
	 */
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#setGraphDataById(GraphData, int)
	 */
	public void setGraphDataById(GraphData data, int id) {
		
		hashId2GraphData.put(id,data);	
	}

	
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#getGraphDataById(int)
	 */
	public GraphData getGraphDataById( int id) {
		
		return hashId2GraphData.get(id);		
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#removeGraphDataById(int)
	 */
	public GraphData removeGraphDataById( int id) {
		
		return hashId2GraphData.remove(id);	
	}


	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#getAllData()
	 */
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#getAllGraphDataCopy()
	 */
	public Collection<GraphData> getAllGraphDataCopy() {
		Collection <GraphData> buffer_values = hashId2GraphData.values();
		ArrayList <GraphData> clone = new ArrayList <GraphData> (buffer_values.size());
		
		Iterator <GraphData> iter= buffer_values.iterator();
		while ( iter.hasNext() ) {
			clone.add( iter.next() );
		}
	
		return clone;
	}


	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#removeAllData()
	 */
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#removeAllData()
	 */
	public synchronized void removeAllData() {
		this.hashId2GraphData.clear();
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#removeAllGraphItem()
	 */
	public synchronized void removeAllGraphItem() {
		this.listChildren.clear();
		this.listParent.clear();
		this.listNeighbour.clear();
	}
	
	/* ---  PARENT GRAPH --- */
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#addParentGraph(GraphParent)
	 */
	public final boolean addParentGraph( GraphParent parent ) {
		if ( listHierarchy.contains(parent)) {
			return false;
		}
		
		listHierarchy.add(parent);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#setParentGraph(GraphParent)
	 */
	public final void setParentGraph( GraphParent parent ) {
		if ( listHierarchy.contains(parent)) {
			return;
		}
		
		listHierarchy.add(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#removeParentGraph(GraphParent)
	 */
	public boolean removeParentGraph( GraphParent parent ) {
		return listHierarchy.remove(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemGeneric#containsParentGraph(GraphParent)
	 */
	public boolean containsParentGraph( GraphParent parent ) {
		return listHierarchy.contains(parent);
	}

}
