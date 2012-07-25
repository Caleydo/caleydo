/**
 * 
 */
package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.event.EnRoutePathEvent;

/**
 * Listener for {@link EnRoutePathEvent}.
 * 
 * @author Christian
 *
 */
public class EnRoutePathEventListener extends AEventListener<GLPathway> {


	@Override
	public void handleEvent(AEvent event) {
		if(event.getSender() == handler)
			return;
		if (event instanceof EnRoutePathEvent) {
			EnRoutePathEvent e = (EnRoutePathEvent) event;
			PathwayPath path = e.getPath();
			handler.setSelectedPath(e.getPath());
		}
	}

}
