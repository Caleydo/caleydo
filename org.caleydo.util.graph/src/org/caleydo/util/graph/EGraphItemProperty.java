/**
 * 
 */
package org.geneview.util.graph;

import java.util.ArrayList;
import java.util.List;

import org.geneview.util.graph.GraphRuntimeException;

/**
 * Graph properties like "INCOMING","OUTGOING" and "ALIAS_PARENT","ALIAS_CHILD" as well as "NONE"
 * 
 * "INCOMING","OUTGOING" are one pair; ALIAS_PARENT","ALIAS_CHILD" is also one pair specifying hierarchies of nodes/edges.
 * A hierarchy of nodes or edges consists of a root node, that refers to all its children. The child refers only to the parent node.
 * 
 * @see org.geneview.util.graph.IGraphItem#getAllItemsByProp(EGraphItemProperty)
 * @see org.geneview.util.graph.EGraphProperty
 * @see org.geneview.util.graph.EGraphItemHierarchy
 * @see org.geneview.util.graph.EGraphItemKind
 * 
 * @author Michael Kalkusch
 */
public enum EGraphItemProperty {

	INCOMING(),
	OUTGOING(),
	//HIERARCHY(),
	ALIAS_PARENT(),
	ALIAS_CHILD(),
	NONE();
	
	/**
	 * Constructor; no values necessary yet.
	 */
	private EGraphItemProperty() {
		/** no values necessary yet. */
	}
	
	
	public boolean isRelation() {
		return this.equals(EGraphItemProperty.NONE) ? true : false;
	}

	/** 
	 * Get inverted property; (INCOMING -> OUTGOING),(OUTGOING -> INCOMING),(ALIAS_CHILD -> ALIAS_PARENT), (ALIAS_CHILD -> ALIAS_PARENT)
	 * 
	 * @return inverted property
	 * @throws GraphRuntimeException if this==NONE or an unsupported type is used this exception is thrown
	 */
	public final EGraphItemProperty getInvertProperty () throws GraphRuntimeException {
		
		switch (this) {
		case ALIAS_CHILD:
			return EGraphItemProperty.ALIAS_PARENT;
		case ALIAS_PARENT:
			return EGraphItemProperty.ALIAS_CHILD;
		case INCOMING:
			return EGraphItemProperty.OUTGOING;
		case OUTGOING:
			return EGraphItemProperty.INCOMING;
			
			default:
				throw new GraphRuntimeException("getInvertProperty() can not handle type=[" 
						+ this.toString() + "]");				
		}
	}
	
	/**
	 * Get a list of active EGraphItemProperty items.
	 * 
	 * @see org.geneview.util.graph.item.GraphItem#GraphItem(int,EGraphItemKind)
	 * @see org.geneview.util.graph.EGraphItemHierarchy#getActiveItems()
	 * @see org.geneview.util.graph.EGraphItemKind#getActiveItems()
	 * 
	 * @return list of active EGraphItemHierarchy items
	 */
	public static final List<EGraphItemProperty> getActiveItems() {
		
		List<EGraphItemProperty>  resultList = new ArrayList<EGraphItemProperty> (3);
		resultList.add(EGraphItemProperty.INCOMING );
		resultList.add(EGraphItemProperty.OUTGOING);
		resultList.add(EGraphItemProperty.ALIAS_PARENT);
		resultList.add(EGraphItemProperty.ALIAS_CHILD);
		
		return resultList;
	}
}
