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
package org.caleydo.core.view.listener;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Event that triggers adding a list of {@link DataContainer}s to a specific
 * view.
 * 
 * @author Alexander Lex
 * 
 */
public class AddDataContainersEvent extends AEvent {

	/** The data containers that are to be added to the view */
	private List<DataContainer> dataContainers;

	/** The view which is the receiver of the data containers */
	private AGLView receiver;

	/**
	 * Default constructor.
	 */
	public AddDataContainersEvent() {
	}

	/**
	 * Constructor initializing the event with a single data container.
	 * 
	 * @param dataContainer
	 *            added to a new instance of {@link #dataContainers}
	 */
	public AddDataContainersEvent(DataContainer dataContainer) {
		dataContainers = new ArrayList<DataContainer>();
		this.dataContainers.add(dataContainer);
	}

	/**
	 * Constructor initializing the event with multiple data containers.
	 * 
	 * @param dataContainers
	 *            set to {@link #dataContainers}
	 */
	public AddDataContainersEvent(List<DataContainer> dataContainers) {
		this.dataContainers = dataContainers;
	}

	/**
	 * @param receiver
	 *            setter, see {@link #receiver}
	 */
	public void setReceiver(AGLView receiver) {
		this.receiver = receiver;
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	public AGLView getReceiver() {
		return receiver;
	}

	/**
	 * @param dataContainers
	 *            setter, see {@link #dataContainers}
	 */
	public void setDataContainers(List<DataContainer> dataContainers) {
		this.dataContainers = dataContainers;
	}

	/**
	 * @return the dataContainers, see {@link #dataContainers}
	 */
	public List<DataContainer> getDataContainers() {
		return dataContainers;
	}

	@Override
	public boolean checkIntegrity() {
		if (dataContainers == null)
			return false;

		return true;
	}

}
