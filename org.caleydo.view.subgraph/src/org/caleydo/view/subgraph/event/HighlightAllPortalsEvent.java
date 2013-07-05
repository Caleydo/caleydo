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
package org.caleydo.view.subgraph.event;

import org.caleydo.core.event.AEvent;

/**
 * Event to trigger highlighting all portal nodes within the current workspace.
 *
 * @author Christian Partl
 *
 */
public class HighlightAllPortalsEvent extends AEvent {

	private boolean isHighlight;

	/**
	 *
	 */
	public HighlightAllPortalsEvent(boolean isHighlight) {
		this.isHighlight = isHighlight;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param isHighlight
	 *            setter, see {@link isHighlight}
	 */
	public void setHighlight(boolean isHighlight) {
		this.isHighlight = isHighlight;
	}

	/**
	 * @return the isHighlight, see {@link #isHighlight}
	 */
	public boolean isHighlight() {
		return isHighlight;
	}

}
