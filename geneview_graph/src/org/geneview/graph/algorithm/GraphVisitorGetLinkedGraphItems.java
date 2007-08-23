/**
 * 
 */
package org.geneview.graph.algorithm;

import java.util.Collection;
import java.util.Iterator;

import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraph;
import org.geneview.graph.IGraphItem;
import org.geneview.graph.algorithm.IGraphVisitorSearch;

/**
 * Search for adjacencies in a graph starting with an item.
 * 
 * @author Michael Kalkusch
 *
 */
public class GraphVisitorGetLinkedGraphItems 
extends AGraphVisitorSearch 
implements IGraphVisitorSearch {

	protected IGraphItem itemSource;
	
	/**
	 * 
	 */
	public GraphVisitorGetLinkedGraphItems(final IGraph graph,
			IGraphItem itemSource) {
		super(graph);
		setSourceItem(itemSource);
	}

	protected Collection<IGraphItem> getSearchResultFromGraphItem(IGraphItem item) {
		Collection<IGraphItem> buffer = 
			item.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT);
	
		Collection<IGraphItem> resultBuffer = null;
		
		Iterator <IGraphItem> iter = buffer.iterator();
		
		while (iter.hasNext()) {
			Collection<IGraphItem> listAllChildren_fromParent = 
				iter.next().getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD);
			
			if  ( ! listAllChildren_fromParent.isEmpty() ) {
				if ( resultBuffer == null ) {
					resultBuffer = listAllChildren_fromParent;
				} else {
					resultBuffer.addAll( listAllChildren_fromParent );
				}
			}
		}
		
		return resultBuffer;
	}
	
	public final void setSourceItem( IGraphItem item ) {
		this.itemSource = item;
	}
	
	public final IGraphItem getSourceItem() {
		return this.itemSource;
	}
	
	public Collection<IGraphItem> getSearchResult() {
		
		if ( this.iSearchDepth == 1) {
			return getSearchResultFromGraphItem(this.itemSource);
		}
		
		assert false : "not implemented yet";
		
		return null;
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public void search() {
		// TODO Auto-generated method stub
		
	}

	public void wipeTemporalDataFromGraph() {
		// TODO Auto-generated method stub
		
	}
	
}

