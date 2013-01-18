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
package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.event.IListenerOwner;

/**
 * Interface for ListenerOwners handling dimension VA updates
 *
 * @author Alexander Lex
 */
public interface IDimensionChangeHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a virtual array update event is caught by a related
	 * {@link RecordVADeltaListener}.
	 *
	 * @param delta
	 *            difference in the old and new virtual array
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleDimensionVADelta(DimensionVADelta vaDelta, String info);

	/**
	 * Handler method to be called by the {@link ReplaceRecordPerspectiveListener} when a
	 * {@link ReplacePerspectiveEvent} was received.
	 */
	public void replaceDimensionPerspective(String dataDomainID, String perspectiveID,
		PerspectiveInitializationData data);

}
