/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
	public void setIDType(IDType idType);

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
