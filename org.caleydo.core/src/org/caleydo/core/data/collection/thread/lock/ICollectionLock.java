package org.caleydo.core.data.collection.thread.lock;

import java.io.Serializable;

/**
 * Interface for locks used to make access to collections threadsafe.
 * 
 * FIXME: think about serialization of locking objects!
 * 
 * @author Michael Kalkusch
 *
 */
public interface ICollectionLock
extends Serializable {

	/**
	 * Before reading this method has to be called.
	 * 
	 * @see prometheus.data.collection.thread.ICollectionReadWriteLock#getReadTokenWait()
	 * @see prometheus.data.collection.thread.ICollectionReadWriteLock#returnReadToken()
	 * 
	 * @return TRUE if resource was grabbed, FALSE if resource could not be grabbed
	 */
	public boolean getReadToken();

	/**
	 * Waits for the read token. 
	 * 
	 * @see prometheus.data.collection.thread.ICollectionReadWriteLock#returnReadToken()
	 * @see prometheus.data.collection.thread.ICollectionReadWriteLock#getReadToken()
	 * 
	 * @return TRUE if read token was grabbed, FLASE if it was not grabbed.
	 */
	public boolean getReadTokenWait();

	/**
	 * Returns read token and decrements counter of listening readers.
	 * 
	 * @see prometheus.data.collection.thread.ICollectionReadWriteLock#getReadToken()
	 * @see prometheus.data.collection.thread.ICollectionReadWriteLock#getReadTokenWait()
	 * 
	 */
	public void returnReadToken();

	/**
	 * Returns read token and decrements counter of listening readers.
	 * 
	 * @see prometheus.data.collection.thread.ICollectionReadWriteLock#getReadToken()
	 * @see prometheus.data.collection.thread.ICollectionReadWriteLock#getReadTokenWait()
	 * 
	 */
	public void returnWriteToken();

	/**
	 * Before reading this method has to be called.
	 * 
	 * @see prometheus.data.collection.thread.ICollectionReadWriteLock
	 * 
	 * @return TRUE if resource was grabbed, FALSE if resource could not be grabbed
	 */
	public boolean getWriteToken();

	/**
	 * Get the ICollectionLock of the current object.
	 * Factory Design Pattern.
	 * 
	 * @return ICollectionLock of the current object
	 */
	public ICollectionLock getCollectionLock();
	
	/**
	 * ISet the ICollectionLock of the current object.
	 * Factory Design Pattern.
	 * 
	 * @return ICollectionLock of the current object
	 */
	public boolean setCollectionLock( final ICollectionLock setCollectionLock );
	
}