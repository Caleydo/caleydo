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
package org.caleydo.core.data.selection;

import org.caleydo.core.event.AEvent;

/**
 * Create or remove a selection type
 * 
 * @author Alexander Lex
 */
public class SelectionTypeEvent
	extends AEvent {

	private SelectionType selectionType;
	private boolean isRemove = false;
	private boolean isCurrent = false;

	public SelectionTypeEvent() {
	}

	public SelectionTypeEvent(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public void addSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	public SelectionType getSelectionType() {
		return selectionType;
	}

	public boolean isRemove() {
		return isRemove;
	}

	public void setRemove(boolean isRemove) {
		this.isRemove = isRemove;
	}

	public boolean isCurrent() {
		return isCurrent;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	@Override
	public boolean checkIntegrity() {
		if (selectionType == null)
			return false;
		if (SelectionType.isDefaultType(selectionType)) {
			throw new IllegalArgumentException("Can not add or remove default types");
		}
		return true;
	}

}
