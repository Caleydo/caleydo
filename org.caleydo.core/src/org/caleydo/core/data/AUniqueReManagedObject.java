package org.caleydo.core.data;

import org.caleydo.core.manager.IGeneralManager;

/**
 * Abstract class providing methods defined in IUniqueManagedObject.
 * Stores reference to creator of item in private variable.
 * 
 * Same as org.caleydo.core.data.AUniqueManagedObject but GeneralManger may be reassigned.
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.xml.MementiItemXML
 * @see org.caleydo.core.data.AUniqueManagedObject
 */
public abstract class AUniqueReManagedObject 
extends AUniqueItem
implements IUniqueManagedObject {

	/**
	 * Reference to manager, who created this object.
	 */
	protected IGeneralManager refGeneralManager;
	
	/**
	 * Constructor.
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
	 * @see org.caleydo.core.data.IUniqueManagedObject#getGeneralManager()
	 */
	final protected void setManager( final IGeneralManager setGeneralManager) {
		this.refGeneralManager = setGeneralManager;
	}
	
	
	//public abstract ManagerObjectType getBaseType();
	
}
