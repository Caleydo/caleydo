/**
 * 
 */
package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.event.LinearizedPathwayPathEvent;

/**
 * Listener for {@link LinearizedPathwayPathEvent}.
 * 
 * @author Christian
 *
 */
public class LinearizedPathwayPathEventListener extends AEventListener<GLPathway> {


	@Override
	public void handleEvent(AEvent event) {
		if(event.getSender() == handler)
			return;
		if (event instanceof LinearizedPathwayPathEvent) {
			LinearizedPathwayPathEvent e = (LinearizedPathwayPathEvent) event;
			PathwayPath path = e.getPath();
			handler.setSelectedPath(e.getPath());
		}
	}

}
