/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.data;

import org.geneview.core.manager.IGeneralManager;
//import org.geneview.core.manager.type.BaseManagerType;
import org.geneview.core.data.IUniqueManagedObject;
import org.geneview.core.data.AUniqueItem;

/**
 * Abstract class providing methods defiend in IUniqueManagedObject.
 * Stores reference to creator of item in private variable.
 * 
 * Same as org.geneview.core.data.AUniqueManagedObject but GeneralManger may be reassinged.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.xml.MementiItemXML
 * @see org.geneview.core.data.AUniqueManagedObject
 */
public abstract class AUniqueReManagedObject 
extends AUniqueItem
implements IUniqueManagedObject {

	/**
	 * Reference to manager, who created this object.
	 */
	protected IGeneralManager refGeneralManager;
	
	/**
	 * 
	 */
	protected AUniqueReManagedObject( int iSetCollectionId, IGeneralManager setGeneralManager ) {

		super( iSetCollectionId );
		
		assert setGeneralManager != null: "SetFlatSimple() with null pointer";
		
		refGeneralManager = setGeneralManager;

	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.UniqueManagedInterface#getManager()
	 */
	public final IGeneralManager getManager() {
		return this.refGeneralManager;
	}

	/**
	 * Reset the IGeneralManager.
	 * 
	 * @see org.geneview.core.data.IUniqueManagedObject#getGeneralManager()
	 */
	final protected void setManager( final IGeneralManager setGeneralManager) {
		this.refGeneralManager = setGeneralManager;
	}
	
	
	//public abstract ManagerObjectType getBaseType();
	
}
