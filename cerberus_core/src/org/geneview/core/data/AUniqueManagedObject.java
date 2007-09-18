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
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Abstract class providing methods defiend in IUniqueManagedObject.
 * Stores reference to creator of item in private variable.
 * 
 * @author Michael Kalkusch
 *
 * @see org.geneview.core.data.AUniqueReManagedObject
 * @see prometheus.data.xml.MementiItemXML
 */
public abstract class AUniqueManagedObject 
extends AUniqueItem
implements IUniqueManagedObject {

	/**
	 * Reference to manager, who created this object.
	 */
	protected final IGeneralManager refGeneralManager;
	
	/**
	 * 
	 */
	protected AUniqueManagedObject( final int iUniqueId, 
			final IGeneralManager setGeneralManager ) {

		super( iUniqueId );
		
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
	 * Reset GeneralManger object
	 * 
	 * @see org.geneview.core.data.AUniqueReManagedObject#setManager(org.geneview.core.manager.IGeneralManager)
	 * @see org.geneview.core.data.IUniqueManagedObject#getGeneralManager()
	 */
	final protected void setManager( final IGeneralManager setGeneralManager) {
		throw new GeneViewRuntimeException("setManager() prohibited inside this class!");
		//this.refGeneralManager = setGeneralManager;
	}
	
	
	//public abstract ManagerObjectType getBaseType();
	
}
