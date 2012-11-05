/**
 * 
 */
package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.event.SelectPathModeEvent;

/**
 * Listener for {@link SelectPathModeEvent}.
 * 
 * @author Christian Partl
 * 
 */
public class SelectPathModeEventListener extends AEventListener<GLPathway> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SelectPathModeEvent) {
			handler.setPathSelectionMode(((SelectPathModeEvent) event)
					.isPathSelectionMode());
		}
	}

}
