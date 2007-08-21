/**
 * 
 */
package org.geneview.graph.core;

import java.util.Iterator;

import org.geneview.graph.EGraphItemHierarchy;
import org.geneview.graph.EGraphItemKind;
import org.geneview.graph.GraphRuntimeException;
import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;
import org.geneview.graph.item.GGraphContainer;

/**
 * @author Michael Kalkusch
 *
 */
public class Graph extends AGraph {
	
	static final int initialSizeItems = 10;
	
	static final int initialSizeGraph = 3;
	
	protected GGraphContainer <IGraphItem,EGraphItemKind> items;
	
	protected GGraphContainer <IGraph,EGraphItemHierarchy> graphs;

	
	/**
	 * 
	 */
	public Graph() {
		
		super();
		
		/** create container for items */
		items = new GGraphContainer <IGraphItem,EGraphItemKind> 
		(EGraphItemKind.getActiveItems(), Graph.initialSizeItems);
		
		/** create container for graphs */
		graphs = new GGraphContainer <IGraph,EGraphItemHierarchy> 
		(EGraphItemHierarchy.getActiveItems(), Graph.initialSizeGraph);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#addGraph(org.geneview.graph.IGraph, org.geneview.graph.EGraphItemHierarchy)
	 */
	public boolean addGraph(IGraph graph, EGraphItemHierarchy type)
			throws GraphRuntimeException {
	
		graphs.addGraphComponent(graph, type);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#addItem(org.geneview.graph.IGraphItem)
	 */
	public boolean addItem(IGraphItem item) {
		items.addGraphComponent(item, item.getGraphKind());
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#clearGraph()
	 */
	public void clearGraph() {
		
		/** items .. */
		Iterator <IGraphItem> iter = 
			items.getAllGraphComponent(EGraphItemKind.NODES).iterator();

		while ( iter.hasNext() ) {
			iter.next().removeGraph(this, null);
		}
		
		iter = items.getAllGraphComponent(EGraphItemKind.EDGES).iterator();

		while ( iter.hasNext() ) {
			iter.next().removeGraph(this, null);
		}
		
		/** graphs .. */		
		Iterator <IGraph> iterGraph = 
			graphs.getAllGraphComponent(null).iterator();

		while ( iterGraph.hasNext() ) {
			iterGraph.next().removeGraph(this, null);
		}
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#containsItem(org.geneview.graph.IGraphItem)
	 */
	public boolean containsItem(IGraphItem item) {
		return items.getAllGraphComponent(item.getGraphKind()).contains(item);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#isEmpty()
	 */
	public final boolean isEmpty() {
		if (( graphs.isEmpty()) &&
				(items.isEmpty()) ) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#removeAllByKind(org.geneview.graph.EGraphItemKind)
	 */
	public void removeAllByKind(EGraphItemKind kind) {
		
		switch (kind) {
		case EDGES:
			break;
			
		case NODES:
			break;
				
		default:
			throw new GraphRuntimeException("unsupported type= " + kind.toString());
		}
		
		items.getAllGraphComponent(kind).clear();
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#removeGraph(org.geneview.graph.IGraph, org.geneview.graph.EGraphItemHierarchy)
	 */
	public boolean removeGraph(IGraph graph, EGraphItemHierarchy type) {
		return graphs.removeGraphComponent(graph, type);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#removeItem(org.geneview.graph.IGraphItem)
	 */
	public boolean removeItem(IGraphItem item) {
		return items.removeGraphComponent(item, item.getGraphKind());
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraph#updateItem(org.geneview.graph.IGraphItem)
	 */
	public boolean updateItem(IGraphItem item) {
		EGraphItemKind itemNewKind = item.getGraphKind();
		if ( items.containsGraphComponent(item, itemNewKind) ) {
			/** same kind as already registered. no update required */
			return true;
			
			/** do not use else but use early "return" and avoid "else" */
		}
		
		/** update is required! */
		
		/** add item to new list .. */
		items.addGraphComponent(item, itemNewKind);
		
		/** remove item from old list .. */
		if ( itemNewKind.equals(EGraphItemKind.EDGES)) {
			/** new kind is EDGES thus old kind was NODES */
			return items.removeGraphComponent(item, EGraphItemKind.NODES);
			
			/** do not use else but use early "return" and avoid "else" */
		}
		
		/** new kind is NODES thus old kind was EDGES */
		return items.removeGraphComponent(item, EGraphItemKind.EDGES);		
	}

}
