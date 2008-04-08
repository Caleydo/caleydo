/**
 * 
 */
package org.geneview.data.graph.item;

import org.geneview.util.graph.EGraphItemKind;
import org.geneview.util.graph.item.GraphItem;

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
		super(666,kind);
		
		/** extend the Graph Item based on your requirements */
	}

}
