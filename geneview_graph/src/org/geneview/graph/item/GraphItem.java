/**
 * 
 */
package org.geneview.graph.item;

import java.util.List;
import java.util.Iterator;

import org.geneview.graph.EGraphItemHierarchy;
import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.EGraphItemKind;
import org.geneview.graph.GraphRuntimeException;
import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;
import org.geneview.graph.item.GraphGenericContainer;

/**
 * Base class for IGraphItem.
 * @author Michael Kalkusch
 *
 */
public class GraphItem extends AGraphDataHandler implements IGraphItem {

	private int iGraphItemId = 0;
	
	/**
	 * initial size for org.geneview.graph.item.GraphItem#items
	 * 
	 * @see org.geneview.graph.item.GraphItem#items
	 */
	private static final int iInitialSizeItemsDefault = 3;
	
	/**
	 * initial size for org.geneview.graph.item.GraphItem#graphs
	 * 
	 * @see org.geneview.graph.item.GraphItem#graphs
	 */
	private static final int iInitalSizeGraphsDefault = 2;
	
	protected GraphGenericContainer <IGraphItem,EGraphItemProperty> items;
	
	protected GraphGenericContainer <IGraph,EGraphItemHierarchy> graphs;
	
	private EGraphItemKind itemKind = null;
	
	/**
	 * Calls GraphItem(int, EGraphItemKind, int, int) with default settings.
	 * 
	 * @see org.geneview.graph.item.GraphItem#GraphItem(int, EGraphItemKind, int, int)
	 * @param kind use EGraphItemKind.NODE or EGraphItemKind.EDGE
	 */
	public GraphItem(final int id,
			final EGraphItemKind kind) {
		this(id, 
				kind,
				GraphItem.iInitialSizeItemsDefault,
				GraphItem.iInitalSizeGraphsDefault);
		
	}

	/**
	 * Specify initial size of IGraphItems and IGraphs.
	 * 
	 * @param iInitialSizeItems estimated number of items
	 * @param iInitalSizeGraphs estimated number of graphs
	 * @param kind use EGraphItemKind.NODE or EGraphItemKind.EDGE
	 */
	public GraphItem(final int id,
			final EGraphItemKind kind, 
			final int iInitialSizeItems, 
			final int iInitalSizeGraphs) {
		
		/** create graph-data objects .. */
		super(iInitialSizeItems);
		
		/** create container for items */
		items = new GraphGenericContainer <IGraphItem,EGraphItemProperty> 
		(EGraphItemProperty.getActiveItems(), iInitialSizeItems);
		
		/** create container for graphs */
		graphs = new GraphGenericContainer <IGraph,EGraphItemHierarchy> 
		(EGraphItemHierarchy.getActiveItems(), iInitalSizeGraphs);
		
		this.iGraphItemId = id;
		this.itemKind = kind;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.graph.IGraphComponent#getId()
	 */
	public final int getId() {		
		return iGraphItemId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.graph.IGraphComponent#setId(int)
	 */
	public final void setId(final int id) {
		iGraphItemId = id;		
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
			
		} 
		catch ( GraphRuntimeException ge )
		{
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
	public List<IGraphItem> getAllItemsByProp(EGraphItemProperty prop) {
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
		if ( itemKind == null ) 
		{
			itemKind = type;
		}
		
		if ( ! itemKind.equals( type )) 
		{
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
		return graphs.containsGraphComponentAtAll(graph);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.IGraphItemHierarchy#getAllGraphByType(org.geneview.graph.EGraphItemHierarchy)
	 */
	public List<IGraph> getAllGraphByType(EGraphItemHierarchy type) {
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
		
		while ( iter.hasNext() ) 
		{
			iter.next().removeItem(this);
		}
	}

}
