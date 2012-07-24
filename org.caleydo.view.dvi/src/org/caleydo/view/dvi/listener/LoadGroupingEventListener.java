/**
 * 
 */
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.LoadGroupingEvent;

/**
 * Listener for {@link LoadGroupingEvent}.
 * 
 * @author Christian Partl
 * 
 */
public class LoadGroupingEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadGroupingEvent) {
			LoadGroupingEvent loadGroupingEvent = (LoadGroupingEvent) event;
			handler.loadGrouping(loadGroupingEvent.getDataDomain(),
					loadGroupingEvent.getIdCategory());
		}

	}

}
