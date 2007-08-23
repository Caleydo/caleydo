package org.geneview.graph.algorithm;

import org.geneview.graph.IGraph;

/**
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AGraphVisitorSearch {

	protected IGraph graph;
	
	protected int iSearchDepth;

	protected AGraphVisitorSearch(IGraph graph) {
		this.graph = graph;	
	}

	public final IGraph getGraph() {
		return graph;
	}

	public final void setGraph(IGraph graph) {
		this.graph = graph;		
	}

	public final void setSearchDepth(final int iDepth) {
		this.iSearchDepth = iDepth;
	}

	public final int getSearchDepth() {
		return iSearchDepth;
	}

}