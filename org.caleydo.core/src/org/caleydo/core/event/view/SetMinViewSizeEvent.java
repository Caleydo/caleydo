/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import org.caleydo.core.view.opengl.canvas.AGLView;

public class SetMinViewSizeEvent extends MinSizeUpdateEvent {

	private AGLView view;

	public SetMinViewSizeEvent(AGLView view) {
		super(view);
		minHeight = -1;
		minWidth = -1;
		this.view = view;
	}

	@Override
	public boolean checkIntegrity() {
		if ((minHeight == -1) || (minWidth == -1) || (view == null))
			throw new IllegalStateException("parameters not set");
		return true;
	}

	public AGLView getView() {
		return view;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

}
