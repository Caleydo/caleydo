package org.caleydo.core.data.collection.set;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.manager.IGeneralManager;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ASetSimple 
extends ASetRawData
implements ISet {
	

	/**
	 * Cache Id to identify changes inside the object.
	 */
	protected int iCacheId;
	
	/**
	 * 
	 */
	protected ASetSimple( int iSetCollectionId, 
			final IGeneralManager setGeneralManager,
			final SetType setType) {
		
		super( iSetCollectionId, 
				setGeneralManager, 
				null, 
				setType );
		
	}

//	/* (non-Javadoc)
//	 * @see org.caleydo.core.data.collection.ISet#getStorage()
//	 */
//	public final IStorage getStorage() {
//		return flatStorage[0];
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.caleydo.core.data.collection.ISet#setStorage(org.caleydo.core.data.collection.IStorage)
//	 */
//	public final void setStorage(IStorage setStorage) {
//		
//		assert setStorage != null: "setStorage() with null-pointer";
//		
//		storage = setStorage;
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
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICollection#getCacheId()
	 */
	public abstract int getCacheId();
	
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICollection#hasCacheChanged(int)
	 */
	public final boolean hasCacheChanged( final int iCompareCacheId ) {
		return (iCompareCacheId < this.getCacheId());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#hasCacheChangedReadOnly(int)
	 */
	public final boolean hasCacheChangedReadOnly( final int iCompareCacheId ) {
		return (iCompareCacheId > this.iCacheId);
	}
}
