/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

import cerberus.manager.IGeneralManager;
//import cerberus.manager.type.BaseManagerType;
import cerberus.data.IUniqueManagedObject;
import cerberus.data.AUniqueItem;

/**
 * Abstract class providing methodes defiend in IUniqueManagedObject.
 * Stores reference to creator of item in private variable.
 * 
 * Same as cerberus.data.AUniqueManagedObject but GeneralManger may be reassinged.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.xml.MementiItemXML
 * @see cerberus.data.AUniqueManagedObject
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
	 * @see cerberus.data.IUniqueManagedObject#getGeneralManager()
	 */
	final protected void setManager( final IGeneralManager setGeneralManager) {
		this.refGeneralManager = setGeneralManager;
	}
	
	
	//public abstract ManagerObjectType getBaseType();
	
}
