package cerberus.data.collection.thread.lock;

/**
 * Interface for locks used to make access to collections threadsafe.
 * 
 * @author kalkusch
 *
 */
public interface ICollectionLock {

	/**
	 * Before reading this methode has to be called.
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
	 * Before reading this methode has to be called.
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