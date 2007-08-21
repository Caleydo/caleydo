/**
 * 
 */
package org.geneview.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author java
 *
 */
public abstract class AGraphObjectHeavyweight extends AGraphObject {

	private HashMap<Integer, IGraphData> hashId2GraphData;
	
	protected final Collection<IGraph> listParentGraph;
	
	protected Collection<IGraphData> listData;
	protected Collection<IGraphItem> listEdges;
	protected Collection<IGraphItem> listNodes;

	public AGraphObjectHeavyweight( final IGraph parentGraph ) {
		this(parentGraph, 5, 3, 3);
	}
	
	/**
	 * @param dataSize
	 * @param nodeSize
	 * @param edgeSize
	 */
	public AGraphObjectHeavyweight(  final IGraph parentGraph, 
			int iDataSize, 
			int iNodeSize, 
			int iEdgeSize) {
		
		listData = new ArrayList <IGraphData> (iDataSize);
		listEdges = new ArrayList <IGraphItem> (iEdgeSize);
		listNodes = new ArrayList <IGraphItem> (iNodeSize);		
		hashId2GraphData = new HashMap <Integer,IGraphData> (iDataSize);
		
		listParentGraph = new ArrayList <IGraph> (2);
		
		addParentGraph(parentGraph);
	}

//	/* (non-Javadoc)
//	 * @see org.geneview.graph.AGraphObject#addGraphEdge()
//	 */
//	@Override
//	protected void addGraphEdge() {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see org.geneview.graph.AGraphObject#addGraphNode()
//	 */
//	@Override
//	protected void addGraphNode() {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see org.geneview.graph.AGraphObject#removeGraphEdge()
//	 */
//	@Override
//	protected void removeGraphEdge() {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see org.geneview.graph.AGraphObject#removeGraphNode()
//	 */
//	@Override
//	protected void removeGraphNode() {
//		// TODO Auto-generated method stub
//
//	}

	public void addGraphItem(IGraphItem add, EGraphItemProperty prop) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#containsData(int)
	 */
	public boolean containsData(int identifier) {
		return hashId2GraphData.containsKey(identifier);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#containsDataObject(java.lang.Object)
	 */
	public boolean containsDataObject(Object data) {
		return listData.contains( data );
	}

	public boolean containsGraphItem(IGraphItem test) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#getAllData()
	 */
	public Collection<Object> getAllData() {
		ArrayList <Object> clone = new ArrayList <Object> (listData.size());
		
		Iterator <IGraphData> iter= listData.iterator();
		while ( iter.hasNext() ) {
			clone.add( iter.next() );
		}
	
		return clone;
	}

	public Collection<IGraphItem> getAllGraphItemsByProp() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IGraphItem> getAllGraphItems() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IGraphItem> getAllNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#getData(int)
	 */
	public Object getData(int identifier) {
		// TODO Auto-generated method stub
		return this.hashId2GraphData.get( identifier );
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#removeAllData()
	 */
	public void removeAllData() {
		this.hashId2GraphData.clear();
		this.listData.clear();
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#removeData(int)
	 */
	public boolean removeData(int identifier) {
		IGraphData buffer = this.hashId2GraphData.remove(identifier);
		
		if ( buffer != null ) {
			return listData.remove(buffer);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphObject#setData(int, java.lang.Object)
	 */
	public void setData(int identifier, Object data) {
		IGraphData buffer = (IGraphData) data;
		this.listData.add( (IGraphData) buffer );
		this.hashId2GraphData.put(identifier, buffer);
	}

	public boolean removeGraphObject(IGraphItem remove) {
		if ( remove.isNode() ) 
		{
			return this.listNodes.remove( remove );
		} 
		else 
		{
			return this.listEdges.remove( remove );
		}
	}
	
	
	public Collection<IGraph> getParentGraphs() {
		Iterator <IGraph> iter = listParentGraph.iterator();		
		Collection<IGraph> container = 
			new ArrayList <IGraph> (listParentGraph.size());
		
		while (iter.hasNext()) {
			container.add( iter.next() );
		}
		
		return container;
	}
	
	public Collection <IGraph> getParentGraphsByType( int id ) {
		Iterator <IGraph> iter = listParentGraph.iterator();
		
		Collection<IGraph> container = 
			new ArrayList <IGraph> (listParentGraph.size());
		
		while (iter.hasNext()) {
			IGraph buffer = iter.next();
			
			if ( buffer.getTypeId() == id) {
				container.add(buffer);
			}
		}
		
		return container;
	}
	
	public final void addParentGraph( IGraph setGraph ) {
		listParentGraph.add(setGraph);
	}
	
	public void removeParentGraph( IGraph setGraph ) {
		
	}
	
	public boolean hasParentGraph( IGraph setGraph ) {
		return false;
	}
	
	

}
