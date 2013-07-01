/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.toolbar;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.radial.event.UpdateDepthSliderPositionEvent;

/**
 * Listener that reacts on update depth slider position events for DepthSlider.
 * 
 * @author Christian Partl
 */
public class UpdateDepthSliderPositionListener extends AEventListener<DepthSlider> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof UpdateDepthSliderPositionEvent) {
			handler.setSliderPosition(((UpdateDepthSliderPositionEvent) event)
					.getDepthSliderPosition());
		}
	}

}
