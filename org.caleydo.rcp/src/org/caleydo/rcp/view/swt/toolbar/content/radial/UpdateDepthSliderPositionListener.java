package org.caleydo.rcp.view.swt.toolbar.content.radial;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.radial.UpdateDepthSliderPositionEvent;

public class UpdateDepthSliderPositionListener
	extends AEventListener<DepthSlider> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof UpdateDepthSliderPositionEvent) {
			handler.setSliderPosition(((UpdateDepthSliderPositionEvent) event)
				.getDepthSliderPosition());
		}
	}

}
