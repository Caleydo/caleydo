/**
 * 
 */
package org.geneview.graph;

/**
 * @author java
 *
 */
public enum EGraphType {

	NODES(),
	EDGES(),
	
	/**
	 * Consists of NODES and EDGES
	 */
	GRAPH_OBJECT(),
	
	SUB_GRAPHS();
	
	private EGraphType() {
		
	}
	
}
