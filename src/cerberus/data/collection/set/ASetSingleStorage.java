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
import cerberus.data.collection.thread.lock.ICollectionLock;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ASetSingleStorage 
extends ACollectionThreadItem
implements ISet {
	

	/**
	 * Cache Id to identify changes inside the object.
	 */
	protected int iCacheId;
	
	/**
	 * 
	 */
	protected ASetSingleStorage( final int iSetCollectionId, 
			final IGeneralManager setGeneralManager,
			final ICollectionLock setCollectionLock ) {
		
		super( iSetCollectionId,setGeneralManager, setCollectionLock );
		
	}
	

//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.ISet#getStorage()
//	 */
//	final public IStorage getStorage() {
//		return refFlatStorage[0];
//	}
//	
//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.ISet#setStorage(cerberus.data.collection.IStorage)
//	 */
//	final public void setStorage(IStorage setStorage) {
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
	
//	/*
//	 *  (non-Javadoc)
//	 * @see cerberus.data.collection.BaseManagerLabeledItem#getCacheId()
//	 */
//	abstract public int getCacheId();
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.BaseManagerLabeledItem#hasCacheChanged(int)
	 */
	final public boolean hasCacheChanged( final int iCompareCacheId ) {
		return (iCompareCacheId > this.iCacheId);
	}

}
