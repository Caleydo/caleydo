package org.caleydo.core.manager;

import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;

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
	 * @param iItemId uniqu id used for lookup
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
	 * Type of the manager
	 * 
	 * @return type of the manager
	 */
	public ManagerType getManagerType();
		
	/**
	 * Registers one Id and links it to the reference.
	 * 
	 * @param registerItem Object to be registered
	 * @param iItemId unique Id
	 * @param type defines type, can also be null if type is not known
	 * 
	 * @return TRUE if item was unregistered by this manager
	 */
	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type );
	
	
	/**
	 * Unregisters an item using it's Id.
	 * 
	 * @param iItemId unique Id
	 * @param type defines type, can also be null if type is not known
	 * 
	 * @return TRUE if item was unregistered by this manager
	 */
	public boolean unregisterItem( final int iItemId, 
			final ManagerObjectType type  );
	
	/**
	 * Create a new unique Id.
	 * 
	 * @return new unique Id
	 * @param setNewBaseType type of object the id shall be created for
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 */
	public int createId( final ManagerObjectType setNewBaseType );

	/**
	 * Set the current Id, what is incremented once the next time createNewId() is called.
	 * 
	 * Attention: this method must be called from a synchronized block on the actual manager!
	 * 
	 * @param setNewBaseType test if manager may create such an id
	 * @param iCurrentId set the new current Id
	 * @return true if the new current Id was valid, which is the case if it is larger than the current NewId!
	 */
	public boolean setCreateNewId(ManagerType setNewBaseType, final int iCurrentId );
	
	public IGeneralManager getManagerByObjectType(ManagerObjectType managerType);
		
	/**
	 * Remove all data and stop all threads.
	 *
	 */
	public void destroyOnExit();
}