/**
 * 
 */
package org.geneview.graph;

import java.util.ArrayList;
import java.util.Collection;

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
