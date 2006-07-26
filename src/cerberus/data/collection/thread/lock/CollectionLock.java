package cerberus.data.collection.thread.lock;

/**
 * Interface for locks used to make access to collections threadsafe.
 * 
 * @author kalkusch
 *
 */
public interface CollectionLock {

	/**
	 * Before reading this methode has to be called.
	 * 
	 * @see prometheus.data.collection.thread.CollectionReadWriteLock#getReadTokenWait()
	 * @see prometheus.data.collection.thread.CollectionReadWriteLock#returnReadToken()
	 * 
	 * @return TRUE if resource was grabbed, FALSE if resource could not be grabbed
	 */
	public boolean getReadToken();

	/**
	 * Waits for the read token. 
	 * 
	 * @see prometheus.data.collection.thread.CollectionReadWriteLock#returnReadToken()
	 * @see prometheus.data.collection.thread.CollectionReadWriteLock#getReadToken()
	 * 
	 * @return TRUE if read token was grabbed, FLASE if it was not grabbed.
	 */
	public boolean getReadTokenWait();

	/**
	 * Returns read token and decrements counter of listening readers.
	 * 
	 * @see prometheus.data.collection.thread.CollectionReadWriteLock#getReadToken()
	 * @see prometheus.data.collection.thread.CollectionReadWriteLock#getReadTokenWait()
	 * 
	 */
	public void returnReadToken();

	/**
	 * Returns read token and decrements counter of listening readers.
	 * 
	 * @see prometheus.data.collection.thread.CollectionReadWriteLock#getReadToken()
	 * @see prometheus.data.collection.thread.CollectionReadWriteLock#getReadTokenWait()
	 * 
	 */
	public void returnWriteToken();

	/**
	 * Before reading this methode has to be called.
	 * 
	 * @see prometheus.data.collection.thread.CollectionReadWriteLock
	 * 
	 * @return TRUE if resource was grabbed, FALSE if resource could not be grabbed
	 */
	public boolean getWriteToken();

	/**
	 * Get the CollectionLock of the current object.
	 * Factory Design Pattern.
	 * 
	 * @return CollectionLock of the current object
	 */
	public CollectionLock getCollectionLock();
	
	/**
	 * Set the CollectionLock of the current object.
	 * Factory Design Pattern.
	 * 
	 * @return CollectionLock of the current object
	 */
	public boolean setCollectionLock( final CollectionLock setCollectionLock );
	
}