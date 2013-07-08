/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.selection;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.event.EventPublisher;
import org.eclipse.jface.action.Action;

/**
 * @author Samuel Gratzl
 *
 */
public class RemoveSelectionTypeAction extends Action {
	private final SelectionType selectionType;

	public RemoveSelectionTypeAction(SelectionType selectionType) {
		super("Remove");
		this.selectionType = selectionType;
	}

	@Override
	public void run() {
		SelectionTypeEvent event = new SelectionTypeEvent();
		event.addSelectionType(selectionType);
		event.setRemove(true);
		EventPublisher.trigger(event);
	}
}
