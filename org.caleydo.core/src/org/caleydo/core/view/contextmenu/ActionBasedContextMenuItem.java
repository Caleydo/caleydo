/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.util.base.IAction;

/**
 * Generic context menu item that performs an {@link IAction} when clicked.
 *
 * @author Christian Partl
 *
 */
public class ActionBasedContextMenuItem extends AContextMenuItem {

	protected List<IAction> actions = new ArrayList<>();

	public ActionBasedContextMenuItem(String label, IAction action) {
		setLabel(label);
		actions.add(action);
	}

	public ActionBasedContextMenuItem(String label, List<IAction> actions) {
		setLabel(label);
		this.actions.addAll(actions);
	}

	@Override
	public void triggerEvent() {
		for (IAction action : actions) {
			action.perform();
		}
	}

}
