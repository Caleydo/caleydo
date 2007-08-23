/**
 * 
 */
package org.geneview.graph.algorithm;

import java.util.Collection;

import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;

/**
 * @author Michael Kalkusch
 *
 */
public interface IGraphVisitorSearch {

	public void setGraph( IGraph graph );
	
	public IGraph getGraph();
	
	public void init();
	
	public void wipeTemporalDataFromGraph();
	
	public void search();
	
	public Collection <IGraphItem> getSearchResult();
	
}
