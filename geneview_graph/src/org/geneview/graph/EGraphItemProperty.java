/**
 * 
 */
package org.geneview.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.geneview.graph.GraphRuntimeException;

/**
 * Graph properties like "INCOMING,OUTGOING" and "ALIAS" as well as "NONE"
 * 
 * @see org.geneview.graph.EGraphProperty
 * @see org.geneview.graph.EGraphItemHierarchy
 * @see org.geneview.graph.EGraphItemKind
 * 
 * @author Michael Kalkusch
 */
public enum EGraphItemProperty {

	INCOMING(),
	OUTGOING(),
	//HIERARCHY(),
	ALIAS(),
	NONE();
	
	private EGraphItemProperty() {
		
	}
	
	
	public boolean isRelation() {
		return this.equals(EGraphItemProperty.NONE) ? true : false;
	}

	/** 
	 * Get inverted property; (INCOMING -> OUTGOING),(OUTGOING -> INCOMING),(ALIAS -> ALIAS)
	 * 
	 * @return inverted property
	 * @throws GraphRuntimeException if this==NONE or an unsupported type is used this exception is thrown
	 */
	public final EGraphItemProperty getInvertProperty () throws GraphRuntimeException {
		
		switch (this) {
		case ALIAS:
			return EGraphItemProperty.ALIAS;
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
	 * @see org.geneview.graph.item.GraphItem#GraphItem(int,int)
	 * @see org.geneview.graph.EGraphItemHierarchy#getActiveItems()
	 * @see org.geneview.graph.EGraphItemKind#getActiveItems()
	 * 
	 * @return list of active EGraphItemHierarchy items
	 */
	public static final Collection<EGraphItemProperty> getActiveItems() {
		
		Collection<EGraphItemProperty>  resultList = new ArrayList<EGraphItemProperty> (3);
		resultList.add(EGraphItemProperty.INCOMING );
		resultList.add(EGraphItemProperty.OUTGOING);
		resultList.add(EGraphItemProperty.ALIAS);
		
		return resultList;
	}
}
