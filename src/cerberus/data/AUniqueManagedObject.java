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
extends AbstractUniqueItem
implements IUniqueManagedObject {

	/**
	 * Reference to manager, who created this object.
	 */
	protected final GeneralManager refGeneralManager;
	
	/**
	 * 
	 */
	protected AUniqueManagedObject( int iSetCollectionId, GeneralManager setGeneralManager ) {

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
	 * Reset GeneralManger object
	 * 
	 * @see cerberus.data.AUniqueReManagedObject#setManager(cerberus.manager.GeneralManager)
	 * @see cerberus.data.IUniqueManagedObject#getGeneralManager()
	 */
	final protected void setManager( final GeneralManager setGeneralManager) {
		throw new CerberusRuntimeException("setManager() prohibited inside this class!");
		//this.refGeneralManager = setGeneralManager;
	}
	
	
	//public abstract ManagerObjectType getBaseType();
	
}
