/**
 * 
 */
package org.geneview.graph.algorithm;

import java.util.List;

import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;

/**
 * @author Michael Kalkusch
 *
 */
public interface IGraphVisitorSearch {

	/* --- Getter and Setter --- */
	
	public void setGraph( IGraph graph );
	
	public IGraph getGraph();
	
	public IGraphItem getItemSource();	
	
	public void setItemSource( IGraphItem item );
	
	public void setSearchDepth(final int iDepth);
	
	public int getSearchDepth();
	
	/* --- algorithm related stuff --- */
	
	/**
	 * Initialize data structures and creates and initializes temporal data inside graphs.
	 * Note: some algorithms do not require init() and wipeTemporalDataFromGraph()
	 * 
	 * @see org.geneview.graph.algorithm.IGraphVisitorSearch#wipeTemporalDataFromGraph()
	 * 
	 * @return TRUE indicates, that temporal data structures are created and thus have to be removed with wipeTemporalDataFromGraph()
	 */
	public boolean init();
	
	/**
	 * Remove temporal data from graph.
	 * 
	 * @see org.geneview.graph.algorithm.IGraphVisitorSearch#init()
	 */
	public void wipeTemporalDataFromGraph();
	
	/**
	 * run the search; if search is intensive a thread may be started.
	 * 
	 * @see org.geneview.graph.algorithm.IGraphVisitorSearch#getSearchResult()
	 */
	public void search();
	
	/**
	 * 
	 * @return list of IGraphItems
	 */
	public List <IGraphItem> getSearchResult();
	
	
}
