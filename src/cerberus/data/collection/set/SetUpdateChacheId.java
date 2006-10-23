/**
 * 
 */
package cerberus.data.collection.set;

import java.util.Iterator;
import java.util.Vector;

import cerberus.data.collection.ICollectionCache;

/**
 * @author java
 *
 */
public final class SetUpdateChacheId < T > {

	/**
	 * 
	 */
	public SetUpdateChacheId() {

	}
	
	public final int updateCacheId( final Vector< Vector< T >> inVectorVector,
			final int iCurrentCacheId ) {
		
		int iCacheId = iCurrentCacheId;
		
		Iterator< Vector< T > > iterDim = inVectorVector.iterator();
		
		while ( iterDim.hasNext() ) {
			Vector< T > refVecSelect = iterDim.next();
			
			Iterator< T > iterSel = refVecSelect.iterator();
			while ( iterSel.hasNext() ) {
				ICollectionCache buffer = 
					(ICollectionCache) iterSel.next();
				
				if ( iCacheId < buffer.getCacheId() ) {
					iCacheId =  buffer.getCacheId();
				}
			}
		}
		
		return iCacheId;
	}

}
