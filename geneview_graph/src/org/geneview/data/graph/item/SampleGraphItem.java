/**
 * 
 */
package org.geneview.data.graph.item;

import org.geneview.graph.EGraphItemKind;
import org.geneview.graph.item.GraphItem;

/**
 * Example for a graph item such as a node or an edge.
 * 
 * @author Michael Kalkusch
 *
 */
public class SampleGraphItem extends GraphItem {

	/**
	 * @param kind define NODE or EDGE
	 */
	public SampleGraphItem(EGraphItemKind kind) {
		super(kind);
		
		/** extend the Graph Item based on your requirements */
	}

}
