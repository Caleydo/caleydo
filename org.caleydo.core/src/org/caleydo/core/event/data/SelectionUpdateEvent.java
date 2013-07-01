/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.AEvent;

/**
 * Event to signal that the user's selection has been updated. Contains both a
 * selection delta and information about the selection in text form. Also
 * contains information whether to scroll to the selection or not.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SelectionUpdateEvent extends AEvent {

	/** delta between old and new selection */
	private SelectionDelta selectionDelta;

	/**
	 * @return the selectionDelta, see {@link #selectionDelta}
	 */
	public SelectionDelta getSelectionDelta() {
		return selectionDelta;
	}

	/**
	 * @param selectionDelta
	 *            setter, see {@link #selectionDelta}
	 */
	public void setSelectionDelta(SelectionDelta selectionDelta) {
		this.selectionDelta = selectionDelta;
	}

	@Override
	public boolean checkIntegrity() {
		if (selectionDelta == null)
			throw new NullPointerException("selectionDelta was null");
		return true;
	}
}
