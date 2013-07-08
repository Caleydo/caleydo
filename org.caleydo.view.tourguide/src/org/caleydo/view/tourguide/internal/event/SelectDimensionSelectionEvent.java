/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.event;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.event.ADirectedEvent;

/**
 * helper event for triggering the selection of a stratification dimension perspective
 * 
 * @author Samuel Gratzl
 * 
 */
public class SelectDimensionSelectionEvent extends ADirectedEvent {
	private Perspective dim;

	public SelectDimensionSelectionEvent() {

	}

	public SelectDimensionSelectionEvent(Perspective d) {
		this.dim = d;
	}

	public Perspective getDim() {
		return dim;
	}

	@Override
	public boolean checkIntegrity() {
		return dim != null;
	}
}

