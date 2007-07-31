/**
 * 
 */
package org.geneview.graph;

/**
 * @author Michael Kalkusch
 *
 */
public enum EGraphItemProperty {

	INCOMING(),
	OUTGOING(),
	HIERARCHY(),
	ALIAS(),
	NONE();
	
	private EGraphItemProperty() {
		
	}
	
	public boolean isRelation() {
		return this.equals(EGraphItemProperty.NONE) ? true : false;
	}
}
