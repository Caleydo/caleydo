/**
 * 
 */
package org.geneview.core.data.collection.virtualarray.iterator;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.geneview.core.data.collection.virtualarray.iterator.VirtualArrayNullIterator;


/**
 * Iterator for a several IVirtualArray.
 * 
 * @author Michael Kalkusch
 *
 */
public class VirtualArrayVectorIterator implements IVirtualArrayIterator {

	/**
	 * Vector storing all Selections.
	 */
	private Vector <IVirtualArray> vecSelection;
	
	/**
	 * Define curretn IVirtualArray in Vector vecSelection
	 */
	private int iCurrentSelection = 0;
	
	/**
	 * Current iterator.
	 * It is bound to the IVirtualArray at vecSelection.get(iCurrentSelection)
	 */
	private IVirtualArrayIterator iterator;
	
	/**
	 * Iterator for Vector vecSelection
	 */
	private Iterator <IVirtualArray> iteratorSelection;
	
	
	
	/**
	 * 
	 */
	public VirtualArrayVectorIterator() {
		vecSelection = new Vector <IVirtualArray> (3);
		
		begin();
	}

	/**
	 * 
	 * Note: must call begin() before using this iterator!
	 * 
	 * @see org.geneview.core.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#begin()
	 * 
	 * @param addSelection
	 */
	public void addSelection( final IVirtualArray addSelection) {
		if ( ! vecSelection.contains( addSelection ) ) {
			vecSelection.addElement( addSelection );
		}
	}
	
	/**
	 * Assign a hole Vector <IVirtualArray> to this iterator.
	 * Note: begin() is called inside this method.
	 * 
	 * @see org.geneview.core.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#begin()
	 * 
	 * @param addSelectionVector
	 */
	public void addSelectionVector( final Vector <IVirtualArray> addSelectionVector) {
		vecSelection = addSelectionVector;
		begin();
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.iterator.IVirtualArrayIterator#size()
	 */
	public int size() {
		Iterator <IVirtualArray> iter = vecSelection.iterator();

		int iSize = 0;
		
		while ( iter.hasNext() ) {
			iSize += iter.next().length();
		}
		
		return iSize;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.iterator.IVirtualArrayIterator#remaining()
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
	 * @see org.geneview.core.data.collection.selection.iterator.IVirtualArrayIterator#getVirtualIndex()
	 */
	public int getVirtualIndex() {
		return iterator.getVirtualIndex();		
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.iterator.IVirtualArrayIterator#setVirtualIndex(int)
	 */
	public void setVirtualIndex(final int iSetVirtualIndex)
			throws GeneViewRuntimeException {
		iterator.setVirtualIndex( iSetVirtualIndex );
	}

	/**
	 * Resets the iterator to the begin.
	 * Note: must be called if Selections are set using addSelection().
	 * 
	 * @see org.geneview.core.data.collection.iterator.ICollectionIterator#begin()
	 * 
	 * @see org.geneview.core.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#addSelection(IVirtualArray)
	 * 
	 */
	public void begin() {
		
		iCurrentSelection = 0;
		iteratorSelection = vecSelection.iterator();
		
		if ( vecSelection.isEmpty() ) {
			iterator = new VirtualArrayNullIterator();
			return;
		}
		
		iterator = vecSelection.get(iCurrentSelection).iterator();
	}

	/**
	 * Get the next index.
	 * Note: begin() must be called before pulling the frist index with next()
	 * 
	 * @see org.geneview.core.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#begin()
	 * 
	 * @see org.geneview.core.data.collection.iterator.ICollectionIterator#next()
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
	 * Returns true, if the current IVirtualArray has mor elements, or if 
	 * there are any other Selections left, that have mor elements.
	 * If the crrent selection does not have any more elements a new
	 * iterator from the next IVirtualArray is created unde the hood.
	 * 
	 * Note: begin() must be called before pulling the frist index with next()
	 * 
	 * @see org.geneview.core.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#begin()
	 * 
	 * @see org.geneview.core.data.collection.iterator.ICollectionIterator#hasNext()
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
				 * found an non-empty IVirtualArray..
				 */
				return true;
			}
		}
		
		return false;
	}

	public void setToEnd()
	{
		while ( iterator.hasNext() ) {
			iterator.next();
		}
		
		iCurrentSelection = vecSelection.size();
	}

	
}
