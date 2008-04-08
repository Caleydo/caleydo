/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.data.collection.set;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.thread.impl.ACollectionThreadItem;
import org.caleydo.core.data.collection.thread.lock.ICollectionLock;

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
//	 * @see org.caleydo.core.data.collection.ISet#getStorage()
//	 */
//	final public IStorage getStorage() {
//		return refFlatStorage[0];
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.caleydo.core.data.collection.ISet#setStorage(org.caleydo.core.data.collection.IStorage)
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
	 * @see org.caleydo.core.data.xml.IMementoXML#createMementoXML()
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
//	 * @see org.caleydo.core.data.collection.BaseManagerLabeledItem#getCacheId()
//	 */
//	abstract public int getCacheId();
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.BaseManagerLabeledItem#hasCacheChanged(int)
	 */
	final public boolean hasCacheChanged( final int iCompareCacheId ) {
		return (iCompareCacheId > this.iCacheId);
	}

}
