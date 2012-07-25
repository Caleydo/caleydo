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

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.IListenerOwner;

/**
 * Interface for views, mediator or manager classes that needs to get
 * selection-update information. Implementation of this interface are called by
 * {@link ExtendedSelectionUpdateListener}s.
 * 
 * @author Marc Streit
 */
public interface IExtendedSelectionUpdateHandler extends IListenerOwner {

	/**
	 * Handler method to be called when a selection event is caught by a related
	 * {@link ExtendedSelectionUpdateListener}.
	 * 
	 * @param selectionDelta
	 *            difference in the old and new selection
	 * @param scrollToSelection
	 *            tells if the receiver should move/scroll its visible area to
	 *            the new selection
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to
	 *            display in the info-box)
	 */
	public void handleSelectionUpdate(SelectionDelta selectionDelta, String dataDomainID);

}
