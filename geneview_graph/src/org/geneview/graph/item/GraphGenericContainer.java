/**
 * 
 */
package org.geneview.graph.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.geneview.graph.GraphRuntimeException;
import org.geneview.graph.IGraphComponent;

/**
 * General generic container for IGraph and IGraphItem objects.
 * GraphEnum has to be an Enumeration of type EGraphItemProperty or EGraphItemHierarchy.
 * 
 * Note: <GraphComponent extends IGraphComponent,..> ensures, that generic "GraphComponent" extends IGraphComponent;
 * this is required for the method disposeItem().
 * 
 * @see org.geneview.graph.EGraphItemProperty
 * @see org.geneview.graph.EGraphItemHierarchy
 * 
 * @author Michael Kalkusch
 *
 */
public class GraphGenericContainer <GraphComponent extends IGraphComponent,GraphEnum> {

	/**
	 * initial size for type2ArrayList.
	 * 
	 * @see org.geneview.graph.item.GraphGenericContainer#type2ArrayList
	 */
	private static final int iInitialSizeHierarchyArray = 4;
	
	//private ArrayList <GraphEnum> keyList;
	
	protected HashMap <GraphEnum, ArrayList<GraphComponent> > type2ArrayList;
	
	/**
	 * Constructor.
	 * 
	 * @param colGraphEnum Collection of all Enumeration that will be used; Skip type NONE or GRAPH_NONE
	 * @param iInitialSizeGraphComponents specify initial size of ArrayList<GraphComponent>
	 */
	public GraphGenericContainer( Collection <GraphEnum> colGraphEnum, int iInitialSizeGraphComponents ) {
		
		assert colGraphEnum != null : "Can not create a GraphGenericContainer with no GraphEnum elements and empty Collection";
		
		/* count number of Enum's to be registered */
		int iCountGraphEnumItems = colGraphEnum.size();
		assert iCountGraphEnumItems > 0 : "can not create a GraphGenericContainer with no GraphEnum elements";
		
		/* create new HashMap */
		type2ArrayList = new HashMap <GraphEnum, ArrayList<GraphComponent> > (iCountGraphEnumItems);
		
		/* iterator for all GraphEnum items ... */
		Iterator <GraphEnum> iter = colGraphEnum.iterator();
		
		/* fill hierarchy HashMap ... */
		for ( ;iter.hasNext();) {
			type2ArrayList.put( iter.next(), 
				new ArrayList <GraphComponent> (iInitialSizeHierarchyArray));
		}	
		
		//keyList = new ArrayList <GraphEnum> (iCountGraphEnumItems);
	}

	/**
	 * @see org.geneview.graph.item.GraphItem#addGraph(org.geneview.graph.IGraph, org.geneview.graph.EGraphItemHierarchy)
	 * @see org.geneview.graph.item.GraphItem#addItem(org.geneview.graph.IGraphItem, org.geneview.graph.EGraphItemProperty)
	 */
	public void addGraphComponent(GraphComponent item, final GraphEnum key) {
		
		
		/* add to hierarchy... */
		 ArrayList<GraphComponent> arrayBuffer = type2ArrayList.get(key);
		 
		 if( arrayBuffer == null ) {
			 throw new GraphRuntimeException("unsupported type " + type2ArrayList.toString() );
		 }
		 
		 if ( ! arrayBuffer.contains(item) ) {
			 arrayBuffer.add(item);
		 } 
		 else 
		 {
			assert false : "Try to add existing element!";
		 	throw new GraphRuntimeException("unsupported type " + type2ArrayList.toString() );
		 }		
	}

	/**
	 * @see org.geneview.graph.item.GraphItem#containsGraph(org.geneview.graph.IGraph, org.geneview.graph.EGraphItemHierarchy)
	 * @see org.geneview.graph.item.GraphItem#containsItem(org.geneview.graph.IGraphItem, org.geneview.graph.EGraphItemProperty)
	 */
	public boolean containsGraphComponent(final GraphComponent item, final GraphEnum key) {
		
		try {
			return type2ArrayList.get(key).contains(item);
		}
		catch (NullPointerException npe) {
			/** Handle case if type2ArrayList.get(key) returns null */
			
			throw new GraphRuntimeException( " key: " + key.toString() + 
					" is not registered. Check type and Constructor GraphGenericContainer(..,..); ERROR= " +
					npe.toString() );
		}
	}
	
	/**
	 * @see org.geneview.graph.item.GraphItem#containsGraph(org.geneview.graph.IGraph, org.geneview.graph.EGraphItemHierarchy)
	 * @see org.geneview.graph.item.GraphItem#containsItem(org.geneview.graph.IGraphItem, org.geneview.graph.EGraphItemProperty)
	 */
	public boolean containsGraphComponentAtAll(final GraphComponent item) {
		Iterator <ArrayList <GraphComponent>> iter =
			type2ArrayList.values().iterator();
		
		while ( iter.hasNext() ) {
			if ( iter.next().contains(item) ) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @see org.geneview.graph.item.GraphItem#getAllGraphByType(org.geneview.graph.EGraphItemHierarchy)
	 * @see org.geneview.graph.item.GraphItem#getAllItemsByProp(org.geneview.graph.EGraphItemProperty)
	 */
	public Collection <GraphComponent> getAllGraphComponent( final GraphEnum key ) {
		
		
		if ( key == null ) {
			/** special case: return all GraphComponent from all ArrayList's */
			
			/** Calculate total size .. */
			int iTotalSize = 0;
			Iterator <ArrayList <GraphComponent>> bufferArrayListIter = 
				type2ArrayList.values().iterator();
			while ( bufferArrayListIter.hasNext() ) {
				iTotalSize += bufferArrayListIter.next().size();
			}
			
			/** create result ArrayList */
			ArrayList <GraphComponent> resultList = new ArrayList <GraphComponent> (iTotalSize);
			
			/** fill result array .. */
			bufferArrayListIter = type2ArrayList.values().iterator();
			while ( bufferArrayListIter.hasNext() ) {				
				resultList.addAll(bufferArrayListIter.next());				
			}
			
			return resultList;
		}
		
		/** regular case */
		
		Collection <GraphComponent> buffer = type2ArrayList.get(key);
		
		return ( buffer != null ) ? buffer :  new ArrayList <GraphComponent> (0);
	}
	

	/**
	 * @see org.geneview.graph.item.GraphItem#removeGraph(org.geneview.graph.IGraph, org.geneview.graph.EGraphItemHierarchy)
	 * @see org.geneview.graph.item.GraphItem#removeItem(org.geneview.graph.IGraphItem, org.geneview.graph.EGraphItemProperty)
	 * 
	 */
	public boolean removeGraphComponent(final GraphComponent item, GraphEnum key) {
		
		if ( key == null ) {
			/** special case: remove GraphComponent from all ArrayList's */
			
			/** test if item will be removed from any ArrayList .. */
			boolean bRemovedFromAnyList = false;
			
			/** fill result array .. */
			Iterator <ArrayList <GraphComponent>> bufferArrayListIter = 
				type2ArrayList.values().iterator();
			while ( bufferArrayListIter.hasNext() ) {
				
					if ( bufferArrayListIter.next().remove(item) ) {
						bRemovedFromAnyList = true;
					}
			}
			
			return bRemovedFromAnyList;
		}
		
		/** regular case */
		
		Collection <GraphComponent> buffer = type2ArrayList.get(key);
		
		return ( buffer != null ) ? buffer.remove(item) :  false;
	}
	
	/**
	 * @see org.geneview.graph.item.GraphItem#disposeItem()
	 */
	public void disposeItem() {
		/** fill result array .. */
		Iterator <ArrayList <GraphComponent>> bufferArrayListIter = 
			type2ArrayList.values().iterator();
		while ( bufferArrayListIter.hasNext() ) {
			Iterator <GraphComponent> innerIter = 
				bufferArrayListIter.next().iterator();
			
			while ( innerIter.hasNext() ) {
				((IGraphComponent) innerIter).disposeItem();
			}
		}
	}
	
	/**
	 * are there any items stored inside this object?
	 * 
	 * @return TRUE if at least one item is stored
	 */
	public boolean isEmpty() {
		Iterator <ArrayList<GraphComponent>> iter = 
			type2ArrayList.values().iterator();
		
		while ( iter.hasNext() ) {
			if ( ! iter.next().isEmpty() ) {
				return false;
			}
		}
		
		return true;
	}

}
