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
package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.core.view.listener.AddTablePerspectivesListener;

/**
 * Interface for views that manage multiple data containers. </p>
 * <p>
 * Setting data containers can be achieved using the {@link AddTablePerspectivesEvent} and
 * {@link AddTablePerspectivesListener}.
 * </p>
 *
 * @author Alexander Lex
 */
public interface IMultiTablePerspectiveBasedView extends ITablePerspectiveBasedView {

	/** Adds a single data container to the view */
	public void addTablePerspective(TablePerspective newTablePerspective);

	/**
	 * Add a list of tablePerspectives to the view.
	 *
	 * @param newTablePerspectives
	 */
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives);

	/** Returns all {@link TablePerspective}s of this view */
	@Override
	public List<TablePerspective> getTablePerspectives();

	/**
	 * Removes the table perspective from the view
	 * 
	 * @param tablePerspective
	 */
	public void removeTablePerspective(TablePerspective tablePerspective);
}
