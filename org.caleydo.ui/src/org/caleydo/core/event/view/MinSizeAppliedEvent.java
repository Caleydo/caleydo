/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.view.MinimumSizeComposite;
import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Event that is triggered when a minimum size was successfully applied to a {@link MinimumSizeComposite}.
 * 
 * @author Christian
 */
public class MinSizeAppliedEvent
	extends AEvent {

	private AGLView view;

	@Override
	public boolean checkIntegrity() {
		return view != null;
	}

	public AGLView getView() {
		return view;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

}
