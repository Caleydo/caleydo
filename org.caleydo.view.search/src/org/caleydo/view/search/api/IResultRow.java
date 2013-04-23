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

