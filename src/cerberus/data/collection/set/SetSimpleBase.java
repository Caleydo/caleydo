/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.set;

import cerberus.manager.GeneralManager;
import cerberus.data.collection.CollectionThreadItem;
import cerberus.data.collection.Set;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class SetSimpleBase 
extends CollectionThreadItem
implements Set {
	

	/**
	 * Cache Id to identify changes inside the object.
	 */
	protected int iCacheId;
	
	/**
	 * 
	 */
	public SetSimpleBase( int iSetCollectionId, GeneralManager setGeneralManager) {
		
		super( iSetCollectionId, setGeneralManager, null );
		
	}
	

//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.Set#getStorage()
//	 */
//	public final Storage getStorage() {
//		return refFlatStorage[0];
//	}
//	
//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.Set#setStorage(cerberus.data.collection.Storage)
//	 */
//	public final void setStorage(Storage setStorage) {
//		
//		assert setStorage != null: "setStorage() with null-pointer";
//		
//		refStorage = setStorage;
//	}
	
	/**
	 * Create "Header" for all Selections.
	 * 
	 * @see cerberus.data.xml.MementoXML#createMementoXML()
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
	 * @see cerberus.data.collection.CollectionInterface#getCacheId()
	 */
	public abstract int getCacheId();
	
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.CollectionInterface#hasCacheChanged(int)
	 */
	public final boolean hasCacheChanged( final int iCompareCacheId ) {
		return (iCompareCacheId < this.getCacheId());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#hasCacheChangedReadOnly(int)
	 */
	public final boolean hasCacheChangedReadOnly( final int iCompareCacheId ) {
		return (iCompareCacheId > this.iCacheId);
	}

}
