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
/**
 * 
 */
package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.container.DataContainer;

/**
 * <p>
 * Base interface for views that use a single or multiple {@link DataContainer}
 * s.
 * </p>
 * <p>
 * Generally it's preferred to use {@link ISingleDataContainerBasedView} or
 * {@link IMultiDataContainerBasedView} instead of this interface.
 * </p>
 * 
 * @author Alexander Lex
 */
public interface IDataContainerBasedView {

	/**
	 * Returns all {@link DataContainer}s that this view and all of its possible
	 * remote views contain.
	 * 
	 * @return
	 */
	public List<DataContainer> getDataContainers();
}