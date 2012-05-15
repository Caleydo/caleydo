/**
 * 
 */
package org.caleydo.view.linearizedpathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.event.RemoveLinearizedNodeEvent;

/**
 * Listener for {@link RemoveLinearizedNodeEvent}.
 * 
 * @author Christian
 * 
 */
public class RemoveLinearizedNodeEventListener extends
		AEventListener<GLLinearizedPathway> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RemoveLinearizedNodeEvent) {
			handler.removeLinearizedNode(((RemoveLinearizedNodeEvent) event).getNode());
		}

	}

}
