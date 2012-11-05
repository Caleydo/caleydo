/**
 * 
 */
package org.caleydo.view.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.pathway.GLPathway;
import org.caleydo.view.pathway.event.ClearMappingEvent;

/**
 * Listener for {@link ClearMappingEvent}.
 * 
 * @author Christian Partl
 * 
 */
public class ClearMappingListener
	extends AEventListener<GLPathway> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ClearMappingEvent) {
			handler.setTablePerspective(null);
		}
	}

}
