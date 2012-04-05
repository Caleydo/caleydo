/**
 * 
 */
package org.caleydo.view.linearizedpathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.pathway.event.LinearizePathwayPathEvent;

/**
 * Listener for {@link LinearizePathwayPathEvent}.
 * 
 * @author Christian
 *
 */
public class LinearizePathwayPathEventListener extends AEventListener<GLLinearizedPathway> {


	@Override
	public void handleEvent(AEvent event) {
		if(event instanceof LinearizePathwayPathEvent) {
			LinearizePathwayPathEvent e = (LinearizePathwayPathEvent) event;
			handler.setPath(e.getPathway(), e.getPath());
		}

	}

}
