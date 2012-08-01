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
package org.caleydo.core.data.selection.delta;

import java.util.Collection;

import org.caleydo.core.id.IDType;

/**
 * Interface for all deltas that contain information on changes and are used to submit information to other
 * views. A delta contains a number of {@link IDeltaItem}s.
 * 
 * @author Alexander Lex
 */
public interface IDelta<T extends IDeltaItem>
	extends Iterable<T> {
	/**
	 * Return an array list of {@link SelectionDeltaItem}. This contains data on what selections have changed
	 * 
	 * @return
	 */
	public Collection<T> getAllItems();

	/** Set the id type of the delta */
	public void tableIDType(IDType idType);

	/**
	 * Get the type of the id, which has to be listed in {@link EIDType}
	 * 
	 * @return the type of the id
	 */
	public IDType getIDType();

	/**
	 * Returns the number of elements in the selection delta
	 * 
	 * @return the size
	 */
	public int size();

	/**
	 * Add a new item to the delta
	 * 
	 * @param deltaItem
	 *            the delta item
	 */
	public void add(T deltaItem);

}
