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
package org.caleydo.core.data.selection.events;

import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.id.IDCategory;

/**
 * Interface for views, mediator or manager classes that needs to get selection-update information.
 * Implementation of this interface are called by {@link SelectionUpdateListener}s.
 *
 * @author Werner Puff
 */
public interface ISelectionHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a selection event is caught by a related
	 * {@link SelectionUpdateListener}.
	 *
	 * @param selectionDelta
	 *            difference in the old and new selection
	 */
	public void handleSelectionUpdate(SelectionDelta selectionDelta);

	/**
	 * Handler method to be called when a TriggerSelectionCommand event is caught that should trigger a
	 * content-selection-command by a related. by a related {@link SelectionCommandListener}.
	 *
	 * @param selectionCommands
	 */
	public void handleSelectionCommand(IDCategory idCategory, SelectionCommand selectionCommand);

}
