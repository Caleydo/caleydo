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
package org.caleydo.core.data.collection.ccontainer;

/**
 * Interface for iterators on CContainers
 * 
 * @author Alexander Lex
 */
public interface ICContainerIterator {

	/**
	 * Returns true if another element exists in the container
	 * 
	 * @return false if no more elements exist
	 */
	public boolean hasNext();

	/**
	 * Removes the element last called by next or previous from the virtual array. Works only if virtual
	 * arrays are enabled, throws exception if called without an enabled virtual array.
	 */
	public void remove();

}
