package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.DataContainersChangedEvent;
import org.caleydo.view.datagraph.GLDataGraph;

/**
 * Listener for {@link DataContainersChangedEvent}
 * 
 * @author Partl
 * @author Alexander Lex
 */
public class DataContainersCangedListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof DataContainersChangedEvent) {
			DataContainersChangedEvent dataContainersChangedEvent = (DataContainersChangedEvent) event;
			handler.updateView(dataContainersChangedEvent.getView());
		}
	}

}
