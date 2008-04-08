/**
 * 
 */
package org.caleydo.util.graph.core;

import java.util.List;
import java.util.Iterator;

import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.GraphRuntimeException;
import org.caleydo.util.graph.IGraph;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.util.graph.item.GraphGenericContainer;

/**
 * @author Michael Kalkusch
 *
 */
public class Graph extends AGraph {
	
	static final int initialSizeItems = 10;
	
	static final int initialSizeGraph = 3;
	
	protected GraphGenericContainer <IGraphItem,EGraphItemKind> items;
	
	protected GraphGenericContainer <IGraph,EGraphItemHierarchy> graphs;

	
	/**
	 * 
	 */
	public Graph(final int id) {
		
		super(id);
		
		/** create container for items */
		items = new GraphGenericContainer <IGraphItem,EGraphItemKind> 
		(EGraphItemKind.getActiveItems(), Graph.initialSizeItems);
		
		/** create container for graphs */
		graphs = new GraphGenericContainer <IGraph,EGraphItemHierarchy> 
		(EGraphItemHierarchy.getActiveItems(), Graph.initialSizeGraph);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#addGraph(org.caleydo.util.graph.IGraph, org.caleydo.util.graph.EGraphItemHierarchy)
	 */
	public boolean addGraph(IGraph graph, EGraphItemHierarchy type)
			throws GraphRuntimeException {
	
		graphs.addGraphComponent(graph, type);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#addItem(org.caleydo.util.graph.IGraphItem)
	 */
	public boolean addItem(IGraphItem item) {
		items.addGraphComponent(item, item.getGraphKind());
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#clearGraph()
	 */
	public void clearGraph() {
		
		/** items .. */
		Iterator <IGraphItem> iter = 
			items.getAllGraphComponent(EGraphItemKind.NODE).iterator();

		while ( iter.hasNext() ) 
		{
			iter.next().removeGraph(this, null);
		}
		
		iter = items.getAllGraphComponent(EGraphItemKind.EDGE).iterator();

		while ( iter.hasNext() ) 
		{
			iter.next().removeGraph(this, null);
		}
		
		/** graphs .. */		
		Iterator <IGraph> iterGraph = 
			graphs.getAllGraphComponent(null).iterator();

		while ( iterGraph.hasNext() ) 
		{
			iterGraph.next().removeGraph(this, null);
		}
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#containsItem(org.caleydo.util.graph.IGraphItem)
	 */
	public boolean containsItem(IGraphItem item) {
		return items.getAllGraphComponent(item.getGraphKind()).contains(item);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#isEmpty()
	 */
	public final boolean isEmpty() {
		if (( graphs.isEmpty()) &&
				(items.isEmpty()) ) 
		{
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#removeAllByKind(org.caleydo.util.graph.EGraphItemKind)
	 */
	public void removeAllByKind(EGraphItemKind kind) {
		
		switch (kind) 
		{
		case EDGE:
			break;
			
		case NODE:
			break;
				
		default:
			throw new GraphRuntimeException("unsupported type= " + kind.toString());
		}
		
		items.getAllGraphComponent(kind).clear();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#removeGraph(org.caleydo.util.graph.IGraph, org.caleydo.util.graph.EGraphItemHierarchy)
	 */
	public boolean removeGraph(IGraph graph, EGraphItemHierarchy type) {
		return graphs.removeGraphComponent(graph, type);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#removeItem(org.caleydo.util.graph.IGraphItem)
	 */
	public boolean removeItem(IGraphItem item) {
		return items.removeGraphComponent(item, item.getGraphKind());
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#updateItem(org.caleydo.util.graph.IGraphItem)
	 */
	public boolean updateItem(IGraphItem item) {
		EGraphItemKind itemNewKind = item.getGraphKind();
		if ( items.containsGraphComponent(item, itemNewKind) ) 
		{
			/** same kind as already registered. no update required */
			return true;
			
			/** do not use else but use early "return" and avoid "else" */
		}
		
		/** update is required! */
		
		/** add item to new list .. */
		items.addGraphComponent(item, itemNewKind);
		
		/** remove item from old list .. */
		if ( itemNewKind.equals(EGraphItemKind.EDGE)) 
		{
			/** new kind is EDGE thus old kind was NODE */
			return items.removeGraphComponent(item, EGraphItemKind.NODE);
			
			/** do not use else but use early "return" and avoid "else" */
		}
		
		/** new kind is NODE thus old kind was EDGE */
		return items.removeGraphComponent(item, EGraphItemKind.EDGE);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraph#getAllItemsByKind(org.caleydo.util.graph.EGraphItemKind)
	 */
	public List<IGraphItem> getAllItemsByKind(EGraphItemKind kind) {		
		return items.getAllGraphComponent(kind);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItemHierarchy#getAllGraphByType(org.caleydo.util.graph.EGraphItemHierarchy)
	 */
	public List<IGraph> getAllGraphByType(EGraphItemHierarchy type) {
		return graphs.getAllGraphComponent(type);	
	}
}
