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
import cerberus.data.UniqueManagedInterface;
import cerberus.data.AbstractUniqueItem;

/**
 * Abstract class providing methodes defiend in UniqueManagedInterface.
 * Stores reference to creator of item in private variable.
 * 
 * Same as cerberus.data.UniqueManagedObject but GeneralManger may be reassinged.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.xml.MementiItemXML
 * @see cerberus.data.UniqueManagedObject
 */
public abstract class UniqueReManagedObject 
extends AbstractUniqueItem
implements UniqueManagedInterface {

	/**
	 * Reference to manager, who created this object.
	 */
	protected GeneralManager refGeneralManager;
	
	/**
	 * 
	 */
	protected UniqueReManagedObject( int iSetCollectionId, GeneralManager setGeneralManager ) {

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
	 * @see cerberus.data.UniqueManagedInterface#getGeneralManager()
	 */
	final protected void setManager( final GeneralManager setGeneralManager) {
		this.refGeneralManager = setGeneralManager;
	}
	
	
	//public abstract ManagerObjectType getBaseType();
	
}
