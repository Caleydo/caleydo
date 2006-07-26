/**
 * 
 */
package cerberus.data.collection.thread.impl;

import cerberus.manager.GeneralManager;
import cerberus.data.UniqueManagedObject;
import cerberus.data.collection.thread.CollectionThreadObject;
import cerberus.data.collection.thread.lock.CollectionLock;
import cerberus.data.collection.thread.lock.CollectionReadWriteLock;
//import prometheus.manager.BaseManagerType;

/**
 * @author kalkusch
 *
 */
public abstract class CollectionThreadItem 
	extends UniqueManagedObject 
	implements CollectionLock, CollectionThreadObject {

	/**
	 * Lock for read-write access.
	 */
	protected CollectionLock collectionLock;
	
	/**
	 * Cache Id to identify changes inside the object.
	 */
	protected int iCacheId;
	
	protected String sLabel;
	
	protected CollectionThreadItem( final int iSetCollectionId, 
			final GeneralManager setGeneralManager,
			final CollectionLock setCollectionLock ) {
		super(iSetCollectionId,setGeneralManager);
		
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
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.data.collection.CollectionInterface#getCacheId()
	 */
	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#getCacheId()
	 */
	public abstract int getCacheId();
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.data.collection.CollectionInterface#setCacheId(int)
	 */
	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#setCacheId(int)
	 */
	public final void setCacheId(final int iCacheId) {
		this.iCacheId = iCacheId;
	}
	
	/**
	 * Sets new cacheId to iSetCacheIdCompared if iSetCacheIdCompared if larger than this.cacheId.
	 * Called by getCacheId() if hildren are linked to current object.
	 * 
	 * @see prometheus.data.collection.CollectionInterface#getCacheId()
	 * 
	 * @param iSetCacheIdCompared set this as new cacheId if it is largen than current cacheId
	 */
	final protected void setCacheIdCompared(final int iSetCacheIdCompared) {
		if ( this.iCacheId < iSetCacheIdCompared ) {
			this.iCacheId = iSetCacheIdCompared;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.data.collection.CollectionInterface#hasCacheChanged(int)
	 */
	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionThread#hasCacheChanged(int)
	 */
	public boolean hasCacheChanged( final int iCompareCacheId ) {
		return (iCompareCacheId < this.iCacheId);
	}
	
	/**
	 * @see prometheus.data.collection.CollectionInterface#setLabel(java.lang.String)
	 */
	public final void setLabel( final String setLabel) {
		this.sLabel = setLabel;
	}
	
	/**
	 * @see prometheus.data.collection.CollectionInterface#getLabel()
	 */
	public final String getLabel() {
		return this.sLabel;
	}
	
}
