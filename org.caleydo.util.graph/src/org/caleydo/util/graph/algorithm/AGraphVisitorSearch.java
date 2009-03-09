package org.caleydo.util.graph.algorithm;

import org.caleydo.util.graph.IGraph;
import org.caleydo.util.graph.IGraphItem;

/**
 * Abstract base class with setter and getter for IGraph, IGraphItem and searchDepth.
 * 
 * @author Michael Kalkusch
 */
public abstract class AGraphVisitorSearch {

	/**
	 * @see AGraphVisitorSearch#getGraph()
	 * @see AGraphVisitorSearch#setGraph(IGraph)
	 */
	protected IGraph graph;

	/**
	 * @see AGraphVisitorSearch#getItemSource()
	 * @see AGraphVisitorSearch#setItemSource(IGraphItem)
	 */
	protected IGraphItem itemSource;

	/**
	 * search depth; -1, default; 0.. local, self references; 1.. primary adjacent IGraphItems, etc.
	 * 
	 * @see AGraphVisitorSearch#getSearchDepth()
	 * @see AGraphVisitorSearch#setSearchDepth(int)
	 */
	protected int iSearchDepth = -1;

	protected AGraphVisitorSearch(IGraph graph) {
		this.graph = graph;
	}

	protected AGraphVisitorSearch(final int iSearchDepth) {
		setSearchDepth(iSearchDepth);
	}

	protected AGraphVisitorSearch(IGraphItem itemSource, final int iSearchDepth) {
		setItemSource(itemSource);
		setSearchDepth(iSearchDepth);
	}

	public final IGraph getGraph() {
		return graph;
	}

	public final void setGraph(IGraph graph) {
		this.graph = graph;
	}

	public final int getSearchDepth() {
		return iSearchDepth;
	}

	public final IGraphItem getItemSource() {
		return this.itemSource;
	}

	public final void setItemSource(IGraphItem item) {
		this.itemSource = item;
	}

	public void setSearchDepth(final int iDepth) {
		this.iSearchDepth = iDepth;
	}

}