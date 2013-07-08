/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.listener;

import org.caleydo.core.event.AEvent;

public class HideHeatMapElementsEvent
	extends AEvent {

	private boolean elementsHidden;

	public HideHeatMapElementsEvent(boolean elementsHidden) {
		this.elementsHidden = elementsHidden;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public boolean isElementsHidden() {
		return elementsHidden;
	}

	public void setElementsHidden(boolean elementsHidden) {
		this.elementsHidden = elementsHidden;
	}

}
