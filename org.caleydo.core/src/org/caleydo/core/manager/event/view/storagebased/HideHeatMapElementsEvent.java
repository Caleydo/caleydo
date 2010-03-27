package org.caleydo.core.manager.event.view.storagebased;

import org.caleydo.core.manager.event.AEvent;

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
