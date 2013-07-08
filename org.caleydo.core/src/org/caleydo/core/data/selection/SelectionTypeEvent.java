/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
