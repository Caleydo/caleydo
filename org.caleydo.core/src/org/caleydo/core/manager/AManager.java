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
import java.util.HashMap;
import org.caleydo.core.util.base.IUniqueObject;

/**
 * Base class for manager classes.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AManager<T extends IUniqueObject>
	implements IManager<T> {
	protected GeneralManager generalManager;

	protected HashMap<Integer, T> hashItems;

	/**
	 * Constructor.
	 */
	protected AManager() {
		generalManager = GeneralManager.get();

		hashItems = new HashMap<Integer, T>();
	}

	@Override
	public T getItem(int iItemID) {
		if (!hasItem(iItemID))
			throw new IllegalArgumentException("Requested item with ID " + iItemID + " does not exist!");

		return hashItems.get(iItemID);
	}

	@Override
	public boolean hasItem(int iItemID) {
		return hashItems.containsKey(iItemID);
	}

	@Override
	public void registerItem(final T item) {
		hashItems.put(item.getID(), item);
	}

	@Override
	public int size() {
		return hashItems.size();
	}

	@Override
	public void unregisterItem(int iItemID) {
		hashItems.remove(iItemID);
	}

	@Override
	public Collection<T> getAllItems() {
		return hashItems.values();
	}

}
