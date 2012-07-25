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
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.view.listener.AddDataContainersEvent;
import org.caleydo.core.view.listener.AddDataContainersListener;

/**
 * Interface for views that manage multiple data containers. </p>
 * <p>
 * Setting data containers can be achieved using the
 * {@link AddDataContainersEvent} and {@link AddDataContainersListener}.
 * </p>
 * 
 * @author Alexander Lex
 */
public interface IMultiDataContainerBasedView extends IDataContainerBasedView, IListenerOwner {

	/** Adds a single data container to the view */
	public void addDataContainer(DataContainer newDataContainer);

	/**
	 * Add a list of dataContainers to the view.
	 * 
	 * @param newDataContainers
	 */
	public void addDataContainers(List<DataContainer> newDataContainers);

	/** Returns all {@link DataContainer}s of this view */
	public List<DataContainer> getDataContainers();

	/**
	 * Removes the data container that has the specified id from the view
	 * 
	 * @param dataContainer
	 */
	public void removeDataContainer(int dataContainerID);
}
