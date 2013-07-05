/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.manager;

import java.util.Collection;

import org.caleydo.core.util.base.IUniqueObject;

/**
 * Interface for all managers that allow classes to access managed objects.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IManager<T extends IUniqueObject> {

	/**
	 * Tests, if a certain iItemId is handled by the manager.
	 * 
	 * @param iItemID
	 *            to identify an item that is tested
	 * @return TRUE if iItemId exists
	 */
	public boolean hasItem(final int iItemID);

	/**
	 * Return the item bound to the iItemId.
	 * 
	 * @param iItemID
	 *            unique id used for lookup
	 * @return object bound to iItemID
	 * @throws IllegalArgumentException
	 *             if item does not exist
	 */
	public T getItem(final int iItemID);

	/**
	 * Return a collection of all items stored in the manager.
	 * 
	 * @return the collection of the type the manager manges
	 */
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
	 * @param registerItem
	 *            Object to be registered
	 * @return TRUE if item was unregistered by this manager
	 */
	public void registerItem(final T item);

	/**
	 * Unregisters an item using it's Id.
	 * 
	 * @param iItemID
	 *            unique Id
	 * @param type
	 *            defines type, can also be null if type is not known
	 * @return TRUE if item was unregistered by this manager
	 */
	public void unregisterItem(final int iItemID);
}
