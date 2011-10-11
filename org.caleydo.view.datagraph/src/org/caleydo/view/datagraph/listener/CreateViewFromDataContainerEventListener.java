package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.event.CreateViewFromDataContainerEvent;

public class CreateViewFromDataContainerEventListener extends
		AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof CreateViewFromDataContainerEvent) {

			CreateViewFromDataContainerEvent e = (CreateViewFromDataContainerEvent) event;
			handler.createView(e.getViewType(), e.getDataDomain(),
					e.getDataContainer());

		}

	}

}
