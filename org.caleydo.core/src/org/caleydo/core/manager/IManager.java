package org.caleydo.core.manager;

import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.manager.type.EManagerType;

/**
 * Interface for all managers that allow classes to 
 * access managed objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public interface IManager {
			
	/**
	 * Tests, if a certain iItemId is handled by the manager.
	 * 
	 * @param iItemId to identify an item that is tested
	 * @return TRUE if iItemId exists
	 */
	public boolean hasItem(final int iItemId);

	/**
	 * Return the item bound to the iItemId or null if the id is not 
	 * bound to an item.
	 * 
	 * @param iItemId unique id used for lookup
	 * @return object bound to iItemId
	 */
	public Object getItem( final int iItemId);
	
	/**
	 * Get the number of current handled items.
	 * 
	 * @return number of items
	 */
	public int size();
		
	/**
	 * Registers one Id and links it to the reference.
	 * 
	 * @param registerItem Object to be registered
	 * @param iItemId unique Id
	 * 
	 * @return TRUE if item was unregistered by this manager
	 */
	public boolean registerItem( final Object registerItem, 
			final int iItemId);	
	
	/**
	 * Unregisters an item using it's Id.
	 * 
	 * @param iItemId unique Id
	 * @param type defines type, can also be null if type is not known
	 * 
	 * @return TRUE if item was unregistered by this manager
	 */
	public boolean unregisterItem(final int iItemId);
	
	/**
	 * Create a new unique Id.
	 * 
	 * @return new unique Id
	 * @param setNewBaseType type of object the id shall be created for
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 */
	public int createId( final EManagerObjectType setNewBaseType );
}