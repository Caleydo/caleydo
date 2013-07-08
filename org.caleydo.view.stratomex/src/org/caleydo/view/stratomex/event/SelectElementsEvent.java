/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.event;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.id.IDType;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectElementsEvent extends ADirectedEvent {
	private Iterable<Integer> ids;
	private IDType idType;
	private SelectionType selectionType;


	public SelectElementsEvent(Iterable<Integer> ids, IDType idType, SelectionType selectionType) {
		this.ids = ids;
		this.selectionType = selectionType;
		this.idType = idType;
	}

	@Override
	public boolean checkIntegrity() {
		return ids != null && idType != null && selectionType != null;
	}

	/**
	 * @return the ids, see {@link #ids}
	 */
	public Iterable<Integer> getIds() {
		return ids;
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}

	/**
	 * @return
	 */
	public SelectionType getSelectionType() {
		return selectionType;
	}

}
