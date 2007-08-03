/**
 * 
 */
package org.geneview.graph;

/**
 * Graph properties like "INCOMING,OUTGOING" and "ALIAS" as well as "NONE"
 * 
 * @author Michael Kalkusch
 *
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
}
