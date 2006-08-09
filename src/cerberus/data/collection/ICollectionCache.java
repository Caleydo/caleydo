package cerberus.data.collection;


/**
 * Interface for threaded cached objects.
 * 
 * @author java
 *
 */
public interface ICollectionCache {

	/**
	 * Get the current cacheId.
	 * Querys linked child objects for hier cacheId and sets largest cacheId as current cacheId.
	 * 
	 * @see prometheus.data.collection.ICollection#getCacheId()
	 * @see prometheus.data.collection.ICollection#hasCacheChanged(int)
	 * 
	 * @return current updated cacheId
	 */
	public abstract int getCacheId();
	
	/**
	 * Sets a new cacheId.
	 * The cacheId is not propagated to other linked cache objects 
	 * (PULL from paretn objects not PUSH of childrens).
	 * 
	 * @see prometheus.data.collection.ICollection#getCacheId()
	 * @see prometheus.data.collection.ICollection#hasCacheChanged(int)
	 * 
	 * @param iCacheId new cacheId for this object.
	 */
	public void setCacheId(final int iCacheId);
	
	/**
	 * Test if cache has changed by comparing it with current cacheId.
	 * Internally calls getCacheId() to update its children if nessecary.
	 * If cacheId of object is larger than iCompareCacheId TRUE is returned, indicating that object has changed.
	 * 
	 * @see prometheus.data.collection.ICollection#getCacheId()
	 * @see prometheus.data.collection.ICollection#setCacheId(int)
	 * 
	 * @param iCompareCacheId current cacheId to be compared to internal cacheId of the object
	 * @return TRUE if cacheId of object is larger than iCompareCacheId and object has changed.
	 */
	public boolean hasCacheChanged( final int iCompareCacheId );
	
}