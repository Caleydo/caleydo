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

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.ITablePerspectiveBasedView;

/**
 * Event that triggers adding a list of {@link TablePerspective}s to a specific
 * view.
 * 
 * @author Alexander Lex
 * 
 */
public class AddTablePerspectivesEvent extends AEvent {

	/** The data containers that are to be added to the view */
	private List<TablePerspective> tablePerspectives;

	/** The view which is the receiver of the data containers */
	private ITablePerspectiveBasedView receiver;

	/**
	 * Default constructor.
	 */
	public AddTablePerspectivesEvent() {
	}

	/**
	 * Constructor initializing the event with a single data container.
	 * 
	 * @param tablePerspective
	 *            added to a new instance of {@link #tablePerspectives}
	 */
	public AddTablePerspectivesEvent(TablePerspective tablePerspective) {
		tablePerspectives = new ArrayList<TablePerspective>();
		this.tablePerspectives.add(tablePerspective);
	}

	/**
	 * Constructor initializing the event with multiple data containers.
	 * 
	 * @param tablePerspectives
	 *            set to {@link #tablePerspectives}
	 */
	public AddTablePerspectivesEvent(List<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	/**
	 * @param receiver
	 *            setter, see {@link #receiver}
	 */
	public void setReceiver(ITablePerspectiveBasedView receiver) {
		this.receiver = receiver;
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	public ITablePerspectiveBasedView getReceiver() {
		return receiver;
	}

	public void addTablePerspecitve(TablePerspective tablePerspective) {
		if (tablePerspectives == null)
			tablePerspectives = new ArrayList<TablePerspective>(1);
		tablePerspectives.add(tablePerspective);

	}

	/**
	 * @param tablePerspectives
	 *            setter, see {@link #tablePerspectives}
	 */
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	/**
	 * @return the tablePerspectives, see {@link #tablePerspectives}
	 */
	public List<TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	@Override
	public boolean checkIntegrity() {
		if (tablePerspectives == null)
			return false;

		return true;
	}

}
