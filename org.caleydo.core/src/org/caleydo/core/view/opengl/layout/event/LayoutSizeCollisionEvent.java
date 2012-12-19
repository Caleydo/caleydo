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
package org.caleydo.core.view.opengl.layout.event;

import org.caleydo.core.event.AEvent;

/**
 * Event signalling that a layout can not be rendered within its borders.
 * 
 * @author Alexander Lex
 */
public class LayoutSizeCollisionEvent
	extends AEvent {

	private int managingClassID = -1;

	private int layoutID = -1;

	private float toBigBy = Float.NaN;

	/**
	 * Set the uniqueID of the managing class. Used for notifications on collisions via event and an id to
	 * identify the layout.
	 */
	public void tableIDs(int managingClassID, int layoutID) {
		this.managingClassID = managingClassID;
		this.layoutID = layoutID;
	}

	/**
	 * Set how much the layout is to big by
	 * 
	 * @param toBigBy
	 */
	public void setToBigBy(float toBigBy) {
		this.toBigBy = toBigBy;
	}

	/** Get the id of the class managing the layout */
	public int getManagingClassID() {
		return managingClassID;
	}

	public int getLayoutID() {
		return layoutID;
	}

	/** Get how much this thing is to big by */
	public float getToBigBy() {
		return toBigBy;
	}

	@Override
	public boolean checkIntegrity() {
		if (managingClassID == -1 || layoutID == -1 || Float.isNaN(toBigBy))
			return false;
		return true;
	}

}
