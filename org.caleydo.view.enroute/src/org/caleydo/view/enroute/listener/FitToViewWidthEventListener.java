/**
 * 
 */
package org.caleydo.view.enroute.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.event.FitToViewWidthEvent;

/**
 * Listener for {@link FitToViewWidthEvent}.
 * 
 * @author Christian Partl
 * 
 */
public class FitToViewWidthEventListener extends AEventListener<GLEnRoutePathway> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof FitToViewWidthEvent) {
			FitToViewWidthEvent fitToViewWidthEvent = (FitToViewWidthEvent) event;
			handler.setFitToViewWidth(fitToViewWidthEvent.isFitToViewWidth());
		}

	}

}
