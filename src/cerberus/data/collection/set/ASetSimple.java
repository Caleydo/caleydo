/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.set;

import cerberus.manager.IGeneralManager;
import cerberus.data.collection.ISet;
import cerberus.data.collection.thread.impl.ACollectionThreadItem;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ASetSimple 
extends ACollectionThreadItem
implements ISet {
	

	/**
	 * Cache Id to identify changes inside the object.
	 */
	protected int iCacheId;
	
	/**
	 * 
	 */
	public ASetSimple( int iSetCollectionId, IGeneralManager setGeneralManager) {
		
		super( iSetCollectionId, setGeneralManager, null );
		
	}
	

//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.ISet#getStorage()
//	 */
//	public final IStorage getStorage() {
//		return refFlatStorage[0];
//	}
//	
//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.ISet#setStorage(cerberus.data.collection.IStorage)
//	 */
//	public final void setStorage(IStorage setStorage) {
//		
//		assert setStorage != null: "setStorage() with null-pointer";
//		
//		refStorage = setStorage;
//	}
	
	/**
	 * Create "Header" for all Selections.
	 * 
	 * @see cerberus.data.xml.IMementoXML#createMementoXML()
	 * 
	 * @return String containign the XML-header for this selection
	 */
	protected final String createMementoXML_Intro( 
			final String sSelectionType ) {		
		
		return "<DataComponentItem data_Id=\""
			+ getId() + 
			"\" type=\"" +
			sSelectionType + "\">\n";
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ICollection#getCacheId()
	 */
	public abstract int getCacheId();
	
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ICollection#hasCacheChanged(int)
	 */
	public final boolean hasCacheChanged( final int iCompareCacheId ) {
		return (iCompareCacheId < this.getCacheId());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ISet#hasCacheChangedReadOnly(int)
	 */
	public final boolean hasCacheChangedReadOnly( final int iCompareCacheId ) {
		return (iCompareCacheId > this.iCacheId);
	}
}
