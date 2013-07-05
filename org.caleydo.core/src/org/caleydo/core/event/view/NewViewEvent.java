/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewManager;

/**
 * This Event is triggered when a new view is registered at the {@link ViewManager}.
 *
 * @author Partl
 */
public class NewViewEvent
	extends AEvent {

	private IView view;

	public NewViewEvent(IView view) {
		this.setView(view);
	}

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setView(IView view) {
		this.view = view;
	}

	public IView getView() {
		return view;
	}

}
