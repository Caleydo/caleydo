/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

import cerberus.manager.GeneralManager;
//import cerberus.manager.type.BaseManagerType;
import cerberus.data.IUniqueManagedObject;
import cerberus.data.AbstractUniqueItem;

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
extends AbstractUniqueItem
implements IUniqueManagedObject {

	/**
	 * Reference to manager, who created this object.
	 */
	protected GeneralManager refGeneralManager;
	
	/**
	 * 
	 */
	protected AUniqueReManagedObject( int iSetCollectionId, GeneralManager setGeneralManager ) {

		super( iSetCollectionId );
		
		assert setGeneralManager != null: "SetFlatSimple() with null pointer";
		
		refGeneralManager = setGeneralManager;

	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.UniqueManagedInterface#getManager()
	 */
	public final GeneralManager getManager() {
		return this.refGeneralManager;
	}

	/**
	 * Reset the GeneralManager.
	 * 
	 * @see cerberus.data.IUniqueManagedObject#getGeneralManager()
	 */
	final protected void setManager( final GeneralManager setGeneralManager) {
		this.refGeneralManager = setGeneralManager;
	}
	
	
	//public abstract ManagerObjectType getBaseType();
	
}
