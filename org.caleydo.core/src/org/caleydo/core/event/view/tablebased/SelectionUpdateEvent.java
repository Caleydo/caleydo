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
package org.caleydo.core.event.view.tablebased;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.AEvent;

/**
 * Event to signal that the user's selection has been updated. Contains both a selection delta and information
 * about the selection in text form. Also contains information whether to scroll to the selection or not.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SelectionUpdateEvent
	extends AEvent {

	/** delta between old and new selection */
	private SelectionDelta selectionDelta;

	/** tells if the selection should be focused (centered, ...) by the receiver */
	private boolean scrollToSelection = true;

	/** additional information about the selection, e.g. to display in the info-box */
	private String info;

	public SelectionDelta getSelectionDelta() {
		return selectionDelta;
	}

	public void setSelectionDelta(SelectionDelta selectionDelta) {
		this.selectionDelta = selectionDelta;
	}

	public boolean isScrollToSelection() {
		return scrollToSelection;
	}

	public void setScrollToSelection(boolean scrollToSelection) {
		this.scrollToSelection = scrollToSelection;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public boolean checkIntegrity() {
		if (selectionDelta == null)
			throw new NullPointerException("selectionDelta was null");
		return true;
	}
}
