/**
 * 
 */
package org.caleydo.view.enroute.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.event.RemoveEnRouteNodeEvent;

/**
 * Listener for {@link RemoveEnRouteNodeEvent}.
 * 
 * @author Christian
 * 
 */
public class RemoveEnRouteNodeEventListener extends
		AEventListener<GLEnRoutePathway> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RemoveEnRouteNodeEvent) {
			handler.removeLinearizedNode(((RemoveEnRouteNodeEvent) event).getNode());
		}

	}

}
