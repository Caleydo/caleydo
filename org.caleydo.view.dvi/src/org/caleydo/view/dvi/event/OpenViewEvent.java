/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.IView;

public class OpenViewEvent extends AEvent {

	private IView view;

	public OpenViewEvent(IView view) {
		this.view = view;
	}

	@Override
	public boolean checkIntegrity() {
		return (view != null);
	}

	public void setView(IView view) {
		this.view = view;
	}

	public IView getView() {
		return view;
	}

}
