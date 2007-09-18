/**
 * 
 */
package org.geneview.core.data.collection.set;

import java.util.Iterator;
import java.util.Vector;

import org.geneview.core.data.collection.ICollectionCache;

/**
 * @author Michael Kalkusch
 *
 */
public final class SetUpdateChacheId < T, V > {

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
	
	public final int updateCacheIdSecondary( final Vector< Vector< V >> inVectorVector,
			final int iCurrentCacheId ) {
		
		int iCacheId = iCurrentCacheId;
		
		Iterator< Vector< V > > iterDim = inVectorVector.iterator();
		
		while ( iterDim.hasNext() ) {
			Vector< V > refVecSelect = iterDim.next();
			
			Iterator< V > iterSel = refVecSelect.iterator();
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
