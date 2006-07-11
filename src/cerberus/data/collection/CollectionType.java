/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection;

/**
 * Base calss for typse used to define collections.
 * 
 * @author Michael Kalkusch
 *
 */
public interface CollectionType {

	/**
	 * Tells if this type provides data.
	 * 
	 * @return TRUE if type provides data.
	 */
	public boolean isDataType();
	
}
