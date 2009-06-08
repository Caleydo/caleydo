package org.caleydo.core.manager.event.view.radial;

import org.caleydo.core.manager.event.AEvent;

public class UpdateDepthSliderPositionEvent
	extends AEvent {
	
	private int iDepthSliderPosition = -1;
	
	public int getDepthSliderPosition() {
		return iDepthSliderPosition;
	}

	public void setDepthSliderPosition(int iDepthSliderPosition) {
		this.iDepthSliderPosition = iDepthSliderPosition;
	}

	@Override
	public boolean checkIntegrity() {
		if (iDepthSliderPosition == -1)
			throw new IllegalStateException("iDepthSliderPosition was not set");

		return true;
	}

}
