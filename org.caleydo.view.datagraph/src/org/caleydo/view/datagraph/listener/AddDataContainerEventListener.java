package org.caleydo.view.datagraph.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.event.AddDataContainerEvent;

public class AddDataContainerEventListener extends AEventListener<GLDataGraph> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AddDataContainerEvent) {
			AddDataContainerEvent e = (AddDataContainerEvent) event;
			handler.createDataContainer(e.getDataDomain(),
					e.getRecordPerspectiveID(), e.getDimensionPerspectiveID());
		}

	}

}
