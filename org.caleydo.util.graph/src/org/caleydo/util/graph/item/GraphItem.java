/**
 * 
 */
package org.caleydo.util.graph.item;

import java.util.List;
import java.util.Iterator;

import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.GraphRuntimeException;
import org.caleydo.util.graph.IGraph;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.util.graph.item.GraphGenericContainer;

/**
 * Base class for IGraphItem.
 * @author Michael Kalkusch
 *
 */
public class GraphItem extends AGraphDataHandler implements IGraphItem {

	private int iGraphItemId = 0;
	
	/**
	 * initial size for org.caleydo.util.graph.item.GraphItem#items
	 * 
	 * @see org.caleydo.util.graph.item.GraphItem#items
	 */
	private static final int iInitialSizeItemsDefault = 3;
	
	/**
	 * initial size for org.caleydo.util.graph.item.GraphItem#graphs
	 * 
	 * @see org.caleydo.util.graph.item.GraphItem#graphs
	 */
	private static final int iInitalSizeGraphsDefault = 2;
	
	protected GraphGenericContainer <IGraphItem,EGraphItemProperty> items;
	
	protected GraphGenericContainer <IGraph,EGraphItemHierarchy> graphs;
	
	private EGraphItemKind itemKind = null;
	
	/**
	 * Calls GraphItem(int, EGraphItemKind, int, int) with default settings.
	 * 
	 * @see org.caleydo.util.graph.item.GraphItem#GraphItem(int, EGraphItemKind, int, int)
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
	 * @see org.caleydo.util.graph.IGraphComponent#getId()
	 */
	public final int getId() {		
		return iGraphItemId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphComponent#setId(int)
	 */
	public final void setId(final int id) {
		iGraphItemId = id;		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItem#addItemDoubleLinked(org.caleydo.util.graph.IGraphItem, org.caleydo.util.graph.EGraphItemProperty)
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
	 * @see org.caleydo.util.graph.IGraphItem#addItem(org.caleydo.util.graph.IGraphItem, org.caleydo.util.graph.EGraphItemProperty)
	 */
	public void addItem(IGraphItem item, EGraphItemProperty prop)
			throws GraphRuntimeException {
		items.addGraphComponent(item, prop);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItem#containsItem(org.caleydo.util.graph.IGraphItem, org.caleydo.util.graph.EGraphItemProperty)
	 */
	public boolean containsItem(IGraphItem item, EGraphItemProperty prop) {
		return items.containsGraphComponent(item, prop);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItem#getAllItemsByProp(org.caleydo.util.graph.EGraphItemProperty)
	 */
	public List<IGraphItem> getAllItemsByProp(EGraphItemProperty prop) {
		return items.getAllGraphComponent(prop);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItem#getGraphKind()
	 */
	public EGraphItemKind getGraphKind() {
		return itemKind;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItem#removeItem(org.caleydo.util.graph.IGraphItem, org.caleydo.util.graph.EGraphItemProperty)
	 */
	public boolean removeItem(IGraphItem item, EGraphItemProperty prop) {
		return items.removeGraphComponent(item, prop);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItem#setGraphKind(org.caleydo.util.graph.EGraphItemType)
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
	 * @see org.caleydo.util.graph.IGraphItemHierarchy#addGraph(org.caleydo.util.graph.IGraph, org.caleydo.util.graph.EGraphItemHierarchy)
	 */
	public void addGraph(IGraph graph, EGraphItemHierarchy type) {
		graphs.addGraphComponent(graph, type);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItemHierarchy#containsGraph(org.caleydo.util.graph.IGraph, org.caleydo.util.graph.EGraphItemHierarchy)
	 */
	public boolean containsGraph(IGraph graph, EGraphItemHierarchy type) {
		return graphs.containsGraphComponent(graph, type);		
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItemHierarchy#containsOtherGraph(org.caleydo.util.graph.IGraph)
	 */
	public boolean containsOtherGraph(IGraph graph) {
		return graphs.containsGraphComponentAtAll(graph);
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItemHierarchy#getAllGraphByType(org.caleydo.util.graph.EGraphItemHierarchy)
	 */
	public List<IGraph> getAllGraphByType(EGraphItemHierarchy type) {
		return graphs.getAllGraphComponent(type);	
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphItemHierarchy#removeGraph(org.caleydo.util.graph.IGraph, org.caleydo.util.graph.EGraphItemHierarchy)
	 */
	public boolean removeGraph(IGraph graph, EGraphItemHierarchy type) {
		return graphs.removeGraphComponent(graph, type);	
	}

	/* (non-Javadoc)
	 * @see org.caleydo.util.graph.IGraphComponent#disposeItem()
	 */
	public void disposeItem() {
		Iterator <IGraph> iter = graphs.getAllGraphComponent(null).iterator();
		
		while ( iter.hasNext() ) 
		{
			iter.next().removeItem(this);
		}
	}

}
