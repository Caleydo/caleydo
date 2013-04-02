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

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;

/**
 * Event that triggers the removal of a data container with the specified tablePerspectiveID.
 *
 * @author Alexander Lex
 *
 */
public class RemoveTablePerspectiveEvent extends ADirectedEvent {

	/** The ID of the table perspective to be removed */
	private TablePerspective tablePerspective;

	public RemoveTablePerspectiveEvent() {
	}

	public RemoveTablePerspectiveEvent(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	public RemoveTablePerspectiveEvent(TablePerspective tablePerspective, IMultiTablePerspectiveBasedView receiver) {
		this.tablePerspective = tablePerspective;
		to(receiver);
	}

	/**
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	/**
	 * @param tablePerspective
	 *            setter, see {@link tablePerspective}
	 */
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public boolean checkIntegrity() {
		return tablePerspective != null;
	}

	/**
	 * @return the receiver, see {@link #receiver}
	 */
	@Override
	public IMultiTablePerspectiveBasedView getReceiver() {
		return (IMultiTablePerspectiveBasedView) super.getReceiver();
	}

}
