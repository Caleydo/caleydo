/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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