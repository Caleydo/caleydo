/**
 * 
 */
package org.geneview.graph.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.generic.EGraphItemHierarchy;

/**
 * @author Michael Kalkusch
 *
 */
public class GGraphContainerInOutOptimized <GraphItem>
implements IGraphContainerGeneric <GraphItem> {

	private static final int iInitialSizeInOutAliasArray = 4;
	
	protected Collection <GraphItem> alias;
	protected Collection <GraphItem> parent;
	protected Collection <GraphItem> children;
	protected Collection <GraphItem> neighbour;
	
	protected HashMap <EGraphItemProperty, ArrayList<GraphItem> > incommingOutgoingAliasData;
	
	/**
	 * 
	 */
	public GGraphContainerInOutOptimized( int iInitialSizeHiearachies ) {		
		alias = new ArrayList <GraphItem> (iInitialSizeHiearachies);
		parent = new ArrayList <GraphItem> (iInitialSizeHiearachies);
		children = new ArrayList <GraphItem> (iInitialSizeHiearachies);
		neighbour = new ArrayList <GraphItem> (iInitialSizeHiearachies);		
		incommingOutgoingAliasData = new HashMap <EGraphItemProperty, ArrayList<GraphItem> > (3);
		
		/* fill in,out,alias HashMap ... */
		incommingOutgoingAliasData.put(EGraphItemProperty.INCOMING, 
				new ArrayList <GraphItem> (iInitialSizeInOutAliasArray));
		incommingOutgoingAliasData.put(EGraphItemProperty.OUTGOING, 
				new ArrayList <GraphItem> (iInitialSizeInOutAliasArray));
		incommingOutgoingAliasData.put(EGraphItemProperty.ALIAS, 
				new ArrayList <GraphItem> (iInitialSizeInOutAliasArray));
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#addGraphItemByType(java.lang.Object, org.geneview.graph.EGraphItemProperty, org.geneview.graph.generic.EGraphItemHierarchy)
	 */
	public void addGraphItemByType(GraphItem item, 
			EGraphItemProperty prop,
			EGraphItemHierarchy type) {
		
		/* handle incoming, outgoing alias ... */
		 ArrayList<GraphItem> arrayBuffer = incommingOutgoingAliasData.get(prop);
		 
		 if ( ! arrayBuffer.contains(item) ) {
			 arrayBuffer.add(item);
		 } else {			 
			assert false : "Try to add existing element!";
		 	throw new RuntimeException("Try to add existing element!");
		 }
		 		 
		 getHierarchyListByType(type).add(item);
	}

	private Collection <GraphItem> getHierarchyListByType( final EGraphItemHierarchy type ) {
		switch (type) {
		case GRAPH_ALIAS:
			return this.alias;
			
		case GRAPH_CHILDREN:
			return this.children;
			
		case GRAPH_NEIGHBOUR:
			return this.neighbour;
			
		case GRAPH_PARENT:
			return this.parent;
			
			default:
				throw new RuntimeException("unsupported type " + type.toString());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#containsGraphItemByProp(java.lang.Object, org.geneview.graph.EGraphItemProperty)
	 */
	public boolean containsGraphItemByProp(GraphItem item, EGraphItemProperty prop) {
		
		ArrayList <GraphItem> bufferArray = this.incommingOutgoingAliasData.get(prop);
		
		if ( bufferArray == null ) {
			return false;
		}
		
		return bufferArray.contains(item);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#containsGraphItemByType(java.lang.Object, org.geneview.graph.generic.EGraphItemHierarchy)
	 */
	public boolean containsGraphItemByType(Object item, EGraphItemHierarchy type) {
		
		return getHierarchyListByType(type).contains(item);		
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#getAllGraphItemByProperty(java.lang.Object, org.geneview.graph.EGraphItemProperty)
	 */
	public Collection <GraphItem> getAllGraphItemByProperty(EGraphItemProperty prop) {
		
		ArrayList <GraphItem> bufferList = this.incommingOutgoingAliasData.get(prop);
		
		if ( bufferList != null ) {
			return bufferList;
		}
		
		/* return empty list. */
		return new ArrayList <GraphItem> (0);			
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#getAllGraphItemByType(java.lang.Object, org.geneview.graph.generic.EGraphItemHierarchy)
	 */
	public Collection <GraphItem> getAllGraphItemByType(EGraphItemHierarchy type) {
				
		return getHierarchyListByType(type);			
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#removeGraphItemByType(java.lang.Object, org.geneview.graph.generic.EGraphItemHierarchy)
	 */
	public boolean removeGraphItemByType(Object item, EGraphItemHierarchy type) {
		
		return getHierarchyListByType(type).remove(item);
	}

}
