/**
 * 
 */
package cerberus.data.collection.selection.iterator;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import cerberus.data.collection.ISelection;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.data.collection.selection.iterator.ISelectionIterator;
import cerberus.data.collection.selection.iterator.SelectionNullIterator;


/**
 * Iterator for a several ISelection.
 * 
 * @author kalkusch
 *
 */
public class SelectionVectorIterator implements ISelectionIterator {

	/**
	 * Vector storing all Selections.
	 */
	private Vector <ISelection> vecSelection;
	
	/**
	 * Define curretn ISelection in Vector vecSelection
	 */
	private int iCurrentSelection = 0;
	
	/**
	 * Current iterator.
	 * It is bound to the ISelection at vecSelection.get(iCurrentSelection)
	 */
	private ISelectionIterator iterator;
	
	/**
	 * Iterator for Vector vecSelection
	 */
	private Iterator <ISelection> iteratorSelection;
	
	
	
	/**
	 * 
	 */
	public SelectionVectorIterator() {
		vecSelection = new Vector <ISelection> (3);
		
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
	public void addSelection( final ISelection addSelection) {
		if ( ! vecSelection.contains( addSelection ) ) {
			vecSelection.addElement( addSelection );
		}
	}
	
	/**
	 * Assign a hole Vector <ISelection> to this iterator.
	 * Note: begin() is called inside this methode.
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @param addSelectionVector
	 */
	public void addSelectionVector( final Vector <ISelection> addSelectionVector) {
		vecSelection = addSelectionVector;
		begin();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.selection.iterator.ISelectionIterator#size()
	 */
	public int size() {
		Iterator <ISelection> iter = vecSelection.iterator();

		int iSize = 0;
		
		while ( iter.hasNext() ) {
			iSize += iter.next().length();
		}
		
		return iSize;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.selection.iterator.ISelectionIterator#remaining()
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
	 * @see cerberus.data.collection.selection.iterator.ISelectionIterator#getVirtualIndex()
	 */
	public int getVirtualIndex() {
		return iterator.getVirtualIndex();		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.selection.iterator.ISelectionIterator#setVirtualIndex(int)
	 */
	public void setVirtualIndex(final int iSetVirtualIndex)
			throws CerberusRuntimeException {
		iterator.setVirtualIndex( iSetVirtualIndex );
	}

	/**
	 * Resets the iterator to the begin.
	 * Note: must be called if Selections are set using addSelection().
	 * 
	 * @see cerberus.data.collection.iterator.ICollectionIterator#begin()
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#addSelection(ISelection)
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
	 * @see cerberus.data.collection.iterator.ICollectionIterator#next()
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
	 * Returns true, if the current ISelection has mor elements, or if 
	 * there are any other Selections left, that have mor elements.
	 * If the crrent selection does not have any more elements a new
	 * iterator from the next ISelection is created unde the hood.
	 * 
	 * Note: begin() must be called before pulling the frist index with next()
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @see cerberus.data.collection.iterator.ICollectionIterator#hasNext()
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
				 * found an non-empty ISelection..
				 */
				return true;
			}
		}
		
		return false;
	}

}
