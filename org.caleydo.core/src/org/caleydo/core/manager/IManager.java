package org.caleydo.core.manager;

import java.util.Collection;
import org.caleydo.core.manager.type.EManagerObjectType;

/**
 * Interface for all managers that allow classes to access managed objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IManager<T>
{

	/**
	 * Tests, if a certain iItemId is handled by the manager.
	 * 
	 * @param iItemID to identify an item that is tested
	 * @return TRUE if iItemId exists
	 */
	public boolean hasItem(final int iItemID);

	/**
	 * Return the item bound to the iItemId or null if the id is not bound to an
	 * item.
	 * 
	 * @param iItemID unique id used for lookup
	 * @return object bound to iItemID
	 */
	public T getItem(final int iItemID);
	
	public Collection<T> getAllItems();

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
	 * @param iItemID unique Id
	 * @return TRUE if item was unregistered by this manager
	 */
	public void registerItem(final T item, final int iItemID);

	/**
	 * Unregisters an item using it's Id.
	 * 
	 * @param iItemID unique Id
	 * @param type defines type, can also be null if type is not known
	 * @return TRUE if item was unregistered by this manager
	 */
	public void unregisterItem(final int iItemID);

	/**
	 * Create a new unique Id.
	 * 
	 * @return new unique Id
	 * @param setNewBaseType type of object the id shall be created for
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_TypeOffsetMultiplyer
	 * @see org.caleydo.core.manager.IGeneralManager#iUniqueId_WorkspaceOffset
	 */
	public int createId(final EManagerObjectType setNewBaseType);
}