/**
 * 
 */
package org.geneview.graph.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.geneview.graph.EGraphItemProperty;

/**
 * @author Michael Kalkusch
 *
 */
public class GGraphContainerHierarchyOptimized <GraphItem>
implements IGraphContainerGeneric <GraphItem> {

	private static final int iInitialSizeHierarchyArray = 4;
	
	protected Collection <GraphItem> incoming;
	protected Collection <GraphItem> outcoming;
	protected Collection <GraphItem> alias;
	
	protected HashMap <EGraphItemHierarchy, ArrayList<GraphItem> > hierarchyData;
	
	/**
	 * 
	 */
	public GGraphContainerHierarchyOptimized( int iInitialSizeGraphItmes ) {
		incoming = new ArrayList <GraphItem> (iInitialSizeGraphItmes);
		outcoming = new ArrayList <GraphItem> (iInitialSizeGraphItmes);
		alias = new ArrayList <GraphItem> (2);		
		hierarchyData = new HashMap <EGraphItemHierarchy, ArrayList<GraphItem> > (3);
		
		/* fill hierarchy HashMap ... */
		hierarchyData.put(EGraphItemHierarchy.GRAPH_NEIGHBOUR, 
				new ArrayList <GraphItem> (iInitialSizeHierarchyArray));
		hierarchyData.put(EGraphItemHierarchy.GRAPH_CHILDREN, 
				new ArrayList <GraphItem> (iInitialSizeHierarchyArray));
		hierarchyData.put(EGraphItemHierarchy.GRAPH_PARENT, 
				new ArrayList <GraphItem> (iInitialSizeHierarchyArray));
		hierarchyData.put(EGraphItemHierarchy.GRAPH_ALIAS, 
				new ArrayList <GraphItem> (iInitialSizeHierarchyArray));		
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#addGraphItemByType(java.lang.Object, org.geneview.graph.EGraphItemProperty, org.geneview.graph.generic.EGraphItemHierarchy)
	 */
	public void addGraphItemByType(GraphItem item, 
			EGraphItemProperty prop,
			EGraphItemHierarchy type) {
		
		/**
		 *  add item to hierarchyData and to 
		 * one of the lists: incoming, outgoing, alias 
		 */
		
		/* add to hierarchy... */
		 ArrayList<GraphItem> arrayBuffer = hierarchyData.get(type);
		 
		 if( arrayBuffer == null ) {
			 throw new RuntimeException("unsupported type " + hierarchyData.toString() );
		 }
		 if ( ! arrayBuffer.contains(item) ) {
			 arrayBuffer.add(item);
		 } 
		 else 
		 {
			assert false : "Try to add existing element!";
		 	throw new RuntimeException("unsupported type " + hierarchyData.toString() );
		 }		 
		 
		 
		 try {
			 getHierarchyListByType(prop).add(item);
			 return;
		 }
		 catch (RuntimeException re) 
		 {
				 /* Rollback for arrayBuffer ... */
				 arrayBuffer.remove(item);		
				 
				 throw re;
		 }
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#containsGraphItemByProp(java.lang.Object, org.geneview.graph.EGraphItemProperty)
	 */
	public boolean containsGraphItemByProp(GraphItem item, EGraphItemProperty prop) {
		
		return getHierarchyListByType(prop).contains(item);
	}

	private Collection <GraphItem> getHierarchyListByType( final EGraphItemProperty prop ) {
		 switch ( prop ) {
		 case INCOMING:
			 return this.incoming;
			 
		 case OUTGOING:
			 return this.outcoming;		
			 
		 case ALIAS:
			 return this.alias;		
			 
			 default:				
				 throw new RuntimeException("unsupported type " + prop.toString() );
		 }
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#containsGraphItemByType(java.lang.Object, org.geneview.graph.generic.EGraphItemHierarchy)
	 */
	public boolean containsGraphItemByType(Object item, EGraphItemHierarchy type) {
		ArrayList <GraphItem> bufferArray = this.hierarchyData.get(type);
		
		if ( bufferArray == null ) {
			return false;
		}
		
		return bufferArray.contains(item);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#getAllGraphItemByProperty(java.lang.Object, org.geneview.graph.EGraphItemProperty)
	 */
	public Collection <GraphItem> getAllGraphItemByProperty(EGraphItemProperty prop) {
		
		return getHierarchyListByType(prop);		
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#getAllGraphItemByType(java.lang.Object, org.geneview.graph.generic.EGraphItemHierarchy)
	 */
	public Collection <GraphItem> getAllGraphItemByType(EGraphItemHierarchy type) {
		
		ArrayList <GraphItem> bufferList = this.hierarchyData.get(type);
		
		if ( bufferList != null ) {
			return bufferList;
		}
		
		/* return empty list. */
		return new ArrayList <GraphItem> (0);
	}

	/* (non-Javadoc)
	 * @see org.geneview.graph.generic.IGraphContainerGeneric#removeGraphItemByType(java.lang.Object, org.geneview.graph.generic.EGraphItemHierarchy)
	 */
	public boolean removeGraphItemByType(Object item, EGraphItemHierarchy type) {
		
		ArrayList <GraphItem> bufferList = this.hierarchyData.get(type);
		
		if ( bufferList == null ) {
			/* arrayList is empty */			
			assert false : "try to remove item ["+item.toString()+"] from empty list in node " + this.toString();
			return false;
		}
		
		return bufferList.remove(item);
	}

}
