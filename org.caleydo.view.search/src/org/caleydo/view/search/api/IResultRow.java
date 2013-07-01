/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.search.api;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;

/**
 * abstraction of the current search result row
 *
 * @author Samuel Gratzl
 *
 */
public interface IResultRow {
	/**
	 * the {@link IDCategory} of this row
	 *
	 * @return
	 */
	IDCategory getIDCategory();

	/**
	 * the primary id
	 *
	 * @return
	 */
	Object getPrimaryId();

	/**
	 * returns whether the primary id has a mapped version for the given {@link IDType}
	 *
	 * @param idType
	 * @return
	 */
	boolean has(IDType idType);

	/**
	 * getter for {@link #has(IDType)}
	 * 
	 * @param idType
	 * @return
	 */
	Object get(IDType idType);
}

