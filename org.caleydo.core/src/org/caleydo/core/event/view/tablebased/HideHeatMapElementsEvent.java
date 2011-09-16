package org.caleydo.core.event.view.tablebased;

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
