/**
 * 
 */
package org.geneview.graph.item;

import java.util.Collection;
import java.util.Iterator;

import org.geneview.graph.EGraphItemHierarchy;
import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.EGraphItemKind;
import org.geneview.graph.GraphRuntimeException;
import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;
import org.geneview.graph.item.GraphGenericContainer;

/**
 * @author Michael Kalkusch
 *
 */
public class GraphItem extends AGraphDataHandler implements IGraphItem {

	protected GraphGenericContainer <IGraphItem,EGraphItemProperty> items;
	
	protected GraphGenericContainer <IGraph,EGraphItemHierarchy> graphs;
	
	private EGraphItemKind itemKind = null;
	

	/**
	 * @param initialSizeItems estimated number of items
	 * @param iInitalSizeGraphs estimated number of graphs
	 */
	public GraphItem(int initialSizeItems, int iInitalSizeGraphs) {
		
		/** create graph-data objects .. */
		super(initialSizeItems);
		
		/** create container for items */
		items = new GraphGenericContainer <IGraphItem,EGraphItemProperty> 
		(EGraphItemProperty.getActiveItems(), initialSizeItems);
		
		/** create container for graphs */
		graphs = new GraphGenericContainer <IGraph,EGraphItemHierarchy> 
		(EGraphItemHierarchy.getActiveItems(), iInitalSizeGraphs);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#addItemDoubleLinked(org.geneview.graph.IGraphItem, org.geneview.graph.EGraphItemProperty)
	 */
	public final void addItemDoubleLinked(IGraphItem item, EGraphItemProperty prop)
	throws GraphRuntimeException {
		try {
			/** assign prop.getInvertProperty() to test if prop has an inverse EGraphItemProperty */
			EGraphItemProperty prop_inverted = prop.getInvertProperty();
			
			/** add item */
			this.addItem(item, prop);
			
			/** add reverse with inverted property */
			item.addItem(this, prop_inverted );
			
		} catch ( GraphRuntimeException ge ) {
			throw new GraphRuntimeException("Exception during addItemDoubleLinked(); " + ge.toString() );
		}
	
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#addItem(org.geneview.graph.IGraphItem, org.geneview.graph.EGraphItemProperty)
	 */
	public void addItem(IGraphItem item, EGraphItemProperty prop)
			throws GraphRuntimeException {
		items.addGraphComponent(item, prop);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#containsItem(org.geneview.graph.IGraphItem, org.geneview.graph.EGraphItemProperty)
	 */
	public boolean containsItem(IGraphItem item, EGraphItemProperty prop) {
		return items.containsGraphComponent(item, prop);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#getAllItemsByProp(org.geneview.graph.EGraphItemProperty)
	 */
	public Collection<IGraphItem> getAllItemsByProp(EGraphItemProperty prop) {
		return items.getAllGraphComponent(prop);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#getGraphKind()
	 */
	public EGraphItemKind getGraphKind() {
		return itemKind;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#removeItem(org.geneview.graph.IGraphItem, org.geneview.graph.EGraphItemProperty)
	 */
	public boolean removeItem(IGraphItem item, EGraphItemProperty prop) {
		return items.removeGraphComponent(item, prop);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItem#setGraphKind(org.geneview.graph.EGraphItemType)
	 */
	public void setGraphKind(EGraphItemKind type) {
		if ( itemKind == null ) {
			itemKind = type;
		}
		
		if ( ! itemKind.equals( type )) {
			/** need to update all references! */
			assert false : "not implemented yet!";
		
			throw new GraphRuntimeException("setGraphKind() not implemented yet!");
		}
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemHierarchy#addGraph(org.geneview.graph.IGraph, org.geneview.graph.EGraphItemHierarchy)
	 */
	public void addGraph(IGraph graph, EGraphItemHierarchy type) {
		graphs.addGraphComponent(graph, type);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemHierarchy#containsGraph(org.geneview.graph.IGraph, org.geneview.graph.EGraphItemHierarchy)
	 */
	public boolean containsGraph(IGraph graph, EGraphItemHierarchy type) {
		return graphs.containsGraphComponent(graph, type);		
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemHierarchy#containsOtherGraph(org.geneview.graph.IGraph)
	 */
	public boolean containsOtherGraph(IGraph graph) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemHierarchy#getAllGraphByType(org.geneview.graph.EGraphItemHierarchy)
	 */
	public Collection<IGraph> getAllGraphByType(EGraphItemHierarchy type) {
		return graphs.getAllGraphComponent(type);	
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemHierarchy#removeGraph(org.geneview.graph.IGraph, org.geneview.graph.EGraphItemHierarchy)
	 */
	public boolean removeGraph(IGraph graph, EGraphItemHierarchy type) {
		return graphs.removeGraphComponent(graph, type);	
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphComponent#disposeItem()
	 */
	public void disposeItem() {
		Iterator <IGraph> iter = graphs.getAllGraphComponent(null).iterator();
		
		
		while ( iter.hasNext() ) {
			iter.next().removeItem(this);
		}
	}

}
