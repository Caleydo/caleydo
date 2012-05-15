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
package org.caleydo.view.stratomex.event;

import java.util.List;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.view.listener.AddDataContainersEvent;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.configurer.NumericalDataConfigurer;

/**
 * <p>
 * The {@link AddGroupsToVisBricksEvent} is an event that signals to add one or
 * several {@link DataContainer}s as DimensionGroups to {@link GLStratomex}.
 * </p>
 * <p>
 * There are two ways to specify a group to be added:
 * <ol>
 * <li>by adding a list of pre-existing {@link DataContainer}s</li>
 * <li>by specifying exactly one {@link DataContainer}</li>
 * </ol>
 * </p>
 * 
 * @author Alexander Lex
 * 
 */
public class AddGroupsToVisBricksEvent extends AddDataContainersEvent {

	/**
	 * Optional member for determining a specialized data configurer that will
	 * be used in visbricks. If not specified, visbricks will use the
	 * {@link NumericalDataConfigurer}.
	 */
	private IBrickConfigurer dataConfigurer;

	public AddGroupsToVisBricksEvent() {
	}

	/**
	 * Initialize event with a single data container
	 */
	public AddGroupsToVisBricksEvent(DataContainer dataContainer) {
		super(dataContainer);
	}

	/**
	 * Add a list of data containers, creating multiple dimension groups at the
	 * same time
	 * 
	 * @param dataContainers
	 */
	public AddGroupsToVisBricksEvent(List<DataContainer> dataContainers) {
		super(dataContainers);
	}

	/**
	 * @param dataConfigurer
	 *            setter, see {@link #dataConfigurer}
	 */
	public void setDataConfigurer(IBrickConfigurer dataConfigurer) {
		this.dataConfigurer = dataConfigurer;
	}

	/**
	 * @return the dataConfigurer, see {@link #dataConfigurer}
	 */
	public IBrickConfigurer getDataConfigurer() {
		return dataConfigurer;
	}
}
