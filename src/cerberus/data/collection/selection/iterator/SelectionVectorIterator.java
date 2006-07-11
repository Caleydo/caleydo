/**
 * 
 */
package cerberus.data.collection.selection.iterator;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import cerberus.data.collection.Selection;
import cerberus.util.exception.PrometheusVirtualArrayException;
import cerberus.data.collection.selection.iterator.SelectionIterator;
import cerberus.data.collection.selection.iterator.SelectionNullIterator;


/**
 * Iterator for a several Selection.
 * 
 * @author kalkusch
 *
 */
public class SelectionVectorIterator implements SelectionIterator {

	/**
	 * Vector storing all Selections.
	 */
	private Vector <Selection> vecSelection;
	
	/**
	 * Define curretn Selection in Vector vecSelection
	 */
	private int iCurrentSelection = 0;
	
	/**
	 * Current iterator.
	 * It is bound to the Selection at vecSelection.get(iCurrentSelection)
	 */
	private SelectionIterator iterator;
	
	/**
	 * Iterator for Vector vecSelection
	 */
	private Iterator <Selection> iteratorSelection;
	
	
	
	/**
	 * 
	 */
	public SelectionVectorIterator() {
		vecSelection = new Vector <Selection> (3);
		
		begin();
	}

	/**
	 * 
	 * Note: must call begin() before using this iterator!
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @param addSelection
	 */
	public void addSelection( final Selection addSelection) {
		if ( ! vecSelection.contains( addSelection ) ) {
			vecSelection.addElement( addSelection );
		}
	}
	
	/**
	 * Assign a hole Vector <Selection> to this iterator.
	 * Note: begin() is called inside this methode.
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @param addSelectionVector
	 */
	public void addSelectionVector( final Vector <Selection> addSelectionVector) {
		vecSelection = addSelectionVector;
		begin();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.selection.iterator.SelectionIterator#size()
	 */
	public int size() {
		Iterator <Selection> iter = vecSelection.iterator();

		int iSize = 0;
		
		while ( iter.hasNext() ) {
			iSize += iter.next().length();
		}
		
		return iSize;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.selection.iterator.SelectionIterator#remaining()
	 */
	public int remaining() {
		
		int iItemsRemaining = iterator.remaining();
		
		for ( int iIterateSelectionIndex  = iCurrentSelection; 
			iIterateSelectionIndex < vecSelection.size(); 
			iIterateSelectionIndex++ ) {
			
			iItemsRemaining += vecSelection.get( iIterateSelectionIndex ).length();
		}
		
		return iItemsRemaining;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.selection.iterator.SelectionIterator#getVirtualIndex()
	 */
	public int getVirtualIndex() {
		return iterator.getVirtualIndex();		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.selection.iterator.SelectionIterator#setVirtualIndex(int)
	 */
	public void setVirtualIndex(final int iSetVirtualIndex)
			throws PrometheusVirtualArrayException {
		iterator.setVirtualIndex( iSetVirtualIndex );
	}

	/**
	 * Resets the iterator to the begin.
	 * Note: must be called if Selections are set using addSelection().
	 * 
	 * @see cerberus.data.collection.iterator.CollectionIterator#begin()
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#addSelection(Selection)
	 * 
	 */
	public void begin() {
		
		iCurrentSelection = 0;
		iteratorSelection = vecSelection.iterator();
		
		if ( vecSelection.isEmpty() ) {
			iterator = new SelectionNullIterator();
			return;
		}
		
		iterator = vecSelection.get(iCurrentSelection).iterator();
	}

	/**
	 * Get the next index.
	 * Note: begin() must be called before pulling the frist index with next()
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @see cerberus.data.collection.iterator.CollectionIterator#next()
	 */
	public int next() {

		try {
			
			return iterator.next();
			
		} catch (NoSuchElementException nsee) {
			
			while (( iteratorSelection.hasNext() )&&
					( iCurrentSelection < vecSelection.size() )){	
				iterator = vecSelection.get(iCurrentSelection++).iterator();
				
				/**
				 * Avoid recursion by test hasNext() condition...
				 */
				if ( iterator.hasNext() ) {
					return iterator.next();
				}
			}
			
			/*
			 * no more elements available!
			 */
			throw nsee;
		}
	}
		
		

	/**
	 * Returns true, if the current Selection has mor elements, or if 
	 * there are any other Selections left, that have mor elements.
	 * If the crrent selection does not have any more elements a new
	 * iterator from the next Selection is created unde the hood.
	 * 
	 * Note: begin() must be called before pulling the frist index with next()
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @see cerberus.data.collection.iterator.CollectionIterator#hasNext()
	 */
	public boolean hasNext() {
		
		if ( iterator.hasNext() ) {
			return true;
		}
		
		while (( iteratorSelection.hasNext() )&&
				( iCurrentSelection < vecSelection.size() )){
			
			/**
			 * assign new iterator..
			 */
			iterator = vecSelection.get(iCurrentSelection++).iterator();
			
			if ( iterator.hasNext() ) {
				/*
				 * found an non-empty Selection..
				 */
				return true;
			}
		}
		
		return false;
	}

}
