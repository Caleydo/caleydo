package org.caleydo.util.graph.item;

import java.util.Collection;

/**
 * Wrapper for a HashMap.
 * 
 * @author Michael Kalkusch
 *
 */
public interface IGraphDataHandler {

//	public String getIdentifier();
//	
//	public void setIdentifier( String identifier );
	
	/**
	 * 
	 * @param key address a certain object.
	 * @return null if no object is registered for this identifier.
	 */
	public Object getData( int key );
	
	/**
	 * Get all objects.
	 * Does not return NULL but an empty List in case no objects are registered.
	 * 
	 * @return List of objects of empty collection
	 */
	public Collection <Object> getAllData();
	
	
	/**
	 * @param key index to access data
	 * @param data raw data to be stored at each node
	 * 
	 * @return TURE if identifier was not registered previously, FLASE if identifier was already stored.
	 */
	public boolean setData( int key, Object data );
	
	/**
	 * removes one object bound to identifier.
	 * 
	 * @param key address one object
	 * @return Object that was removed or NULL if identifier was not bound to an object
	 */
	public Object removeData( int key );
	
	/**
	 * removes all handled objects.
	 */
	public void removeAllData();
	
	/**
	 * test if one object is already assigned.
	 * 
	 * @param data object to be tested
	 * @return TRUE if object is already registered
	 */
	public boolean containsDataObject( Object data );
	
	/**
	 * Test if an identifier is already assigned
	 * 
	 * @param key test this id
	 * @return TURE if identifier is already used
	 */
	public boolean containsData( int key );
}
