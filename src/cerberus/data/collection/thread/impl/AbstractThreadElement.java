/**
 * 
 */
package cerberus.data.collection.thread.impl;

import cerberus.data.AbstractUniqueItem;
import cerberus.data.collection.thread.CollectionThreadElement;
import cerberus.data.collection.thread.lock.CollectionLock;
import cerberus.data.collection.thread.lock.CollectionReadWriteLock;

/**
 * Provides a UniqueID and a CollectionLock.
 * 
 * @author kalkusch
 *
 */
public abstract class AbstractThreadElement
  extends AbstractUniqueItem
  implements CollectionThreadElement {

	
	/**
	 * Lock for read-write access.
	 */
	protected CollectionLock collectionLock;
	

	/**
	 * Constructor sets UniqueId.
	 * 
	 * @param iSetCollectionId new unique Id of this object.
	 * 
	 * @see cerberus.data.AbstractUniqueItem#UniqueItem(int)
	 */
	protected AbstractThreadElement( int iSetCollectionId ) {
		super( iSetCollectionId );
		
		collectionLock = new CollectionReadWriteLock();
	}
	
	/**
	 * Constructor sets UniqueId.
	 * 
	 * @param iSetCollectionId new unique Id of this object.
	 * 
	 * @see cerberus.data.AbstractUniqueItem#UniqueItem(int)
	 */
	protected AbstractThreadElement( int iSetCollectionId,
			final CollectionLock setCollectionLock ) {
		super( iSetCollectionId );
		
		if ( setCollectionLock == null ) {
			collectionLock = new CollectionReadWriteLock();
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
	public CollectionLock getCollectionLock() {
		return this.collectionLock;
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#setCollectionLock(prometheus.data.collection.thread.CollectionLock)
	 */
	public boolean setCollectionLock( final CollectionLock setCollectionLock ) {
		this.collectionLock = setCollectionLock;
		
		return true;
	}
	
}
