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
package org.caleydo.view.radial.event;

import org.caleydo.core.event.AEvent;

/**
 * This event signals that a detail view of the specified element shall be displayed in radial hierarchy.
 * 
 * @author Christian Partl
 */
public class DetailOutsideEvent
	extends AEvent {

	private int elementID;

	public DetailOutsideEvent() {
		elementID = -1;
	}

	@Override
	public boolean checkIntegrity() {
		if (elementID == -1)
			throw new IllegalStateException("iMaxDisplayedHierarchyDepth was not set");
		return true;
	}

	/**
	 * @return Element the detail view shall be displayed for.
	 */
	public int getElementID() {
		return elementID;
	}

	/**
	 * Sets the element the detail view shall be displayed for.
	 * 
	 * @param elementID
	 *            Element the detail view shall be displayed for.
	 */
	public void setElementID(int elementID) {
		this.elementID = elementID;
	}

}
