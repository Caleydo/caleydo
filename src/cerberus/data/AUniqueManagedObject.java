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
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Abstract class providing methodes defiend in IUniqueManagedObject.
 * Stores reference to creator of item in private variable.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.AUniqueReManagedObject
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
	protected AUniqueManagedObject( final int iUniqueId, final IGeneralManager setGeneralManager ) {

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
	 * @see cerberus.data.AUniqueReManagedObject#setManager(cerberus.manager.IGeneralManager)
	 * @see cerberus.data.IUniqueManagedObject#getGeneralManager()
	 */
	final protected void setManager( final IGeneralManager setGeneralManager) {
		throw new CerberusRuntimeException("setManager() prohibited inside this class!");
		//this.refGeneralManager = setGeneralManager;
	}
	
	
	//public abstract ManagerObjectType getBaseType();
	
}
