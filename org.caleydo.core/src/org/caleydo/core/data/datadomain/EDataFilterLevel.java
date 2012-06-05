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
package org.caleydo.core.data.datadomain;

/**
 * The level of data filtering. Determines whether all information contained in a dimension (meaning put in
 * the virtual array), or only information with some contextual information, such as mapping or occurence in
 * other data structures should be loaded.
 * 
 * @author Alexander Lex
 */
public enum EDataFilterLevel {
	/**
	 * All data in the dimension is used
	 */
	COMPLETE,

	/**
	 * Only data that has a mapping is used
	 */
	ONLY_MAPPING,

	/**
	 * Only data that has a mapping and occurs in another view is used
	 */
	ONLY_CONTEXT
}
