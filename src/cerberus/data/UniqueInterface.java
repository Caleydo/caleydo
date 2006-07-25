/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

//import prometheus.manager.BaseManagerType;
//import prometheus.manager.GeneralManager;


/**
 * Interface to access prometheus.data.manager.CollectionManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface UniqueInterface {
	
	/**
	 * Resets the selectionId.
	 * @param iSetCollectionId new unique collection Id
	 */
	public void setId( int iSetCollectionId );
	
	/**
	 * Get a unique Id
	 * 
	 * @return unique Id
	 */
	public int getId();
	
//	/**
//	 * Get the type of this object.
//	 * 
//	 * @return type of this object
//	 */
//	//public ManagerObjectType getBaseType();

	
}
