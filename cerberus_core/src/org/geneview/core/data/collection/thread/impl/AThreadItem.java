/**
 * 
 */
package org.geneview.core.data.collection.thread.impl;

import org.geneview.core.data.AUniqueItem;
import org.geneview.core.data.collection.thread.ICollectionThreadItem;
import org.geneview.core.data.collection.thread.lock.ICollectionLock;
import org.geneview.core.data.collection.thread.lock.ICollectionReadWriteLock;

/**
 * Provides a UniqueID and a ICollectionLock.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AThreadItem
  extends AUniqueItem
  implements ICollectionThreadItem {

	
	/**
	 * Lock for read-write access.
	 */
	protected ICollectionLock collectionLock;
	

	/**
	 * Constructor sets UniqueId.
	 * 
	 * @param iSetCollectionId new unique Id of this object.
	 * 
	 * @see org.geneview.core.data.AUniqueItem#UniqueItem(int)
	 */
	protected AThreadItem( int iSetCollectionId ) {
		super( iSetCollectionId );
		
		collectionLock = new ICollectionReadWriteLock();
	}
	
	/**
	 * Constructor sets UniqueId.
	 * 
	 * @param iSetCollectionId new unique Id of this object.
	 * 
	 * @see org.geneview.core.data.AUniqueItem#UniqueItem(int)
	 */
	protected AThreadItem( int iSetCollectionId,
			final ICollectionLock setCollectionLock ) {
		super( iSetCollectionId );
		
		if ( setCollectionLock == null ) {
			collectionLock = new ICollectionReadWriteLock();
		}
		else {
			collectionLock = setCollectionLock;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#getReadToken()
	 */
	public boolean getReadToken() {
		return this.collectionLock.getReadToken();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#getReadTokenWait()
	 */
	public boolean getReadTokenWait(){
		return this.collectionLock.getReadTokenWait();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#returnReadToken()
	 */
	public void returnReadToken() {
		this.collectionLock.returnReadToken();		
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#returnWriteToken()
	 */
	public void returnWriteToken() {
		this.collectionLock.returnWriteToken();			
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#getWriteToken()
	 */
	public boolean getWriteToken() {
		return this.collectionLock.getWriteToken();			
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#getCollectionLock()
	 */
	public ICollectionLock getCollectionLock() {
		return this.collectionLock;
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#setCollectionLock(prometheus.data.collection.thread.CollectionLock)
	 */
	public boolean setCollectionLock( final ICollectionLock setCollectionLock ) {
		this.collectionLock = setCollectionLock;
		
		return true;
	}
	
}
