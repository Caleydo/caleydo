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

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.container.DataContainer;

/**
 * Interface for views that manage (multiple) data containers
 * 
 * @author Alexander Lex
 */
public interface IDataContainerBasedView {

	/**
	 * Set the perspectives for the records and dimensions, thereby defining
	 * which perspectives the view should use. The perspective is expected to be
	 * registered with the {@link DataTable}
	 * 
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 */
	public void setDataContainer(DataContainer dataContainer);

	/**
	 * Returns all {@link DataContainer}s that this view and all of its remote
	 * views render.
	 * 
	 * @return
	 */
	public List<DataContainer> getDataContainers();
}
