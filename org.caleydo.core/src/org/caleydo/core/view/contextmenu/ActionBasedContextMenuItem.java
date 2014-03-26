/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;

/**
 * Generic context menu item that performs an {@link IAction} when clicked.
 *
 * @author Christian Partl
 *
 */
public class ActionBasedContextMenuItem extends AContextMenuItem {

	protected List<Runnable> actions = new ArrayList<>();

	public ActionBasedContextMenuItem(String label, Runnable action) {
		setLabel(label);
		actions.add(action);
	}

	public ActionBasedContextMenuItem(String label, List<Runnable> actions) {
		setLabel(label);
		this.actions.addAll(actions);
	}

	@Override
	public void triggerEvent() {
		for (Runnable action : actions) {
			action.run();
		}
	}

}
