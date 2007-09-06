/**
 * 
 */
package org.geneview.graph.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.IGraphItem;
import org.geneview.graph.algorithm.AGraphVisitorSearch;

/**
 * Breadth First Search Algorithm.
 * 
 * @author Michael Kalkusch
 *
 */
public class GraphVisitorSearchBFS 
extends AGraphVisitorSearch
implements IGraphVisitorSearch {

	protected EGraphItemProperty prop;
	
	/**
	 *  
	 */
	protected List < List<IGraphItem> > depthSortedList;
	
	/**
	 * 
	 */
	public GraphVisitorSearchBFS(IGraphItem item, final int iSearchDepth) {
		super(item, iSearchDepth);
	}
	
	private List < List<IGraphItem> > createDepthSortedList( final int iSetDepth ) {
		
		/** create list and initialize it .. */
		List < List<IGraphItem> > resultDepthSortedList = 
			new ArrayList < List<IGraphItem> > (iSetDepth);
		for ( int i=0; i<iSearchDepth; i++ ) {
			resultDepthSortedList.add(i, new ArrayList <IGraphItem> ()); 		
		}
		
		return resultDepthSortedList;
	}

	@Override
	public void setSearchDepth(final int iSearchDepth) {
		
		super.setSearchDepth(iSearchDepth);
		
		if (depthSortedList != null) {
			if ( depthSortedList.size() == iSearchDepth) {
				/** List has already the required size */
				return;
			}		
		}
			
		/** create list and initialize it .. */
		depthSortedList = createDepthSortedList(iSearchDepth);
	}

	

	/* (non-Javadoc)
	 * @see org.geneview.graph.algorithm.IGraphVisitorSearch#getSearchResult()
	 */
	public List<IGraphItem> getSearchResult() {
			
		if ( iSearchDepth < 1 ) {
			/** special case, no elements, return empty list */
			return new ArrayList <IGraphItem> (0);
		}
		
		List<IGraphItem> resultList = new ArrayList<IGraphItem>(); 
			
		List<IGraphItem> tmpList = this.itemSource.getAllItemsByProp(prop);
		Iterator<IGraphItem> iterTmpGraphItems = tmpList.iterator();
		while(iterTmpGraphItems.hasNext())
		{
			resultList.add(iterTmpGraphItems.next());
		}
		
		
		if ( iSearchDepth == 1 ) {
			/** special case; only direct adjacent elements, return list */
			depthSortedList.add(0, resultList);
			return resultList;
		}
		
		/** general case: */
					
		List <IGraphItem> currentSourceList = new ArrayList <IGraphItem> (resultList);
		
		/** insert elements with adjacency ==1 */
		depthSortedList.add(0, currentSourceList);
		
		for ( int iCurrentDepthIndex=1; iCurrentDepthIndex< this.iSearchDepth; iCurrentDepthIndex++) {
			
			List <IGraphItem> currentLevel = new ArrayList <IGraphItem> ();
			
			Iterator <IGraphItem> iter = currentSourceList.iterator();
			while ( iter.hasNext() ) {
				Iterator <IGraphItem> iterInner = 
					iter.next().getAllItemsByProp(prop).iterator();
				
				while ( iterInner.hasNext() ) {
					IGraphItem item = iterInner.next();
					if ( ! resultList.contains(item) ) {
						/** add to list of visited nodes .. */
						resultList.add(item);
						/** add to list of nodes of current level .. */
						currentLevel.add(item);
					}
					/** else the item was already visited and will be skipped. */
					
				} // while ( iterInner.hasNext() )
				
			} // while ( iter.hasNext() ) {
			
			/** store current level as result */
			depthSortedList.add(iCurrentDepthIndex, currentLevel);
			currentSourceList = currentLevel;
			
		} // for ( int iCurrentDepthIndex=1; iCurrentDepthIndex< this.iSearchDepth; iCurrentDepthIndex++) {
		
		return resultList;
	}

	/**
	 * Create a deep copy of teh search result list.
	 * 
	 * @return deep copy of the search result list
	 */
	public final List<List<IGraphItem>> getSearchResultDepthOrdered() {
		
		/* depthSortedList.size() != iSearchDepth */
		List<List<IGraphItem>> resultList = createDepthSortedList(depthSortedList.size());		
		Iterator<List<IGraphItem>> iterRawDataTopLevel = depthSortedList.iterator();
		
		int index=0;
		while (iterRawDataTopLevel.hasNext()) {
			
			/* raw data */
			List <IGraphItem> currentRawDataDepthList = iterRawDataTopLevel.next();
			Iterator <IGraphItem> iterRawDataInnerLoop = currentRawDataDepthList.iterator();

			/* result list, deep copy */
			ArrayList <IGraphItem> resultListDeepCopy = 
				new ArrayList <IGraphItem> (currentRawDataDepthList.size()); 			
						
			while ( iterRawDataInnerLoop.hasNext() ) {
				resultListDeepCopy.add(iterRawDataInnerLoop.next());
			}

			/* set ArrayList <IGraphItem> resultListDeepCopy ==> resultListDeepCopy[index]= resultListDeepCopy */
			resultList.set(index, resultListDeepCopy);			
			index++;
			
		} //while (iterRawDataTopLevel.hasNext()) {
		
		return resultList;
	}
	
	protected final List<List<IGraphItem>> exposeDepthResultList() {
		
		return depthSortedList;
	}
	
	/**
	 * @return the prop
	 */
	public final EGraphItemProperty getProp() {
		return prop;
	}

	/**
	 * @param prop the prop to set
	 */
	public final void setProp(EGraphItemProperty prop) {
		this.prop = prop;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.graph.algorithm.IGraphVisitorSearch#init()
	 */
	public boolean init() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.graph.algorithm.IGraphVisitorSearch#search()
	 */
	public void search() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.graph.algorithm.IGraphVisitorSearch#wipeTemporalDataFromGraph()
	 */
	public void wipeTemporalDataFromGraph() {
		// TODO Auto-generated method stub
		
	}


}
