package cerberus.data.collection.thread;

import cerberus.data.collection.thread.lock.CollectionLock;
import cerberus.data.IUniqueObject;

public interface CollectionThreadElement 
	extends IUniqueObject {

	/** 
	 * 
	 * @see prometheus.data.collection.thread.CollectionLock#getReadToken()
	 */
	public abstract boolean getReadToken();

	/**
	 *  
	 * @see prometheus.data.collection.thread.CollectionLock#getReadTokenWait()
	 */
	public abstract boolean getReadTokenWait();

	/**
	 *  
	 * @see prometheus.data.collection.thread.CollectionLock#returnReadToken()
	 */
	public abstract void returnReadToken();

	/**
	 * 
	 * @see prometheus.data.collection.thread.CollectionLock#returnWriteToken()
	 */
	public abstract void returnWriteToken();

	/**
	 * 
	 * @see prometheus.data.collection.thread.CollectionLock#getWriteToken()
	 */
	public abstract boolean getWriteToken();

	/**
	 * @see prometheus.data.collection.thread.CollectionLock#getCollectionLock()
	 */
	public abstract CollectionLock getCollectionLock();

	/**
	 * @see prometheus.data.collection.thread.CollectionLock#setCollectionLock(prometheus.data.collection.thread.CollectionLock)
	 */
	public abstract boolean setCollectionLock(
			final CollectionLock setCollectionLock);

	
//	/*
//	 *  (non-Javadoc)
//	 * @see prometheus.data.collection.CollectionInterface#getCacheId()
//	 */
//	public abstract int getCacheId();
//
//	/*
//	 *  (non-Javadoc)
//	 * @see prometheus.data.collection.CollectionInterface#setCacheId(int)
//	 */
//	public abstract void setCacheId(final int iCacheId);
//
//	/*
//	 *  (non-Javadoc)
//	 * @see prometheus.data.collection.CollectionInterface#hasCacheChanged(int)
//	 */
//	public abstract boolean hasCacheChanged(final int iCompareCacheId);

}