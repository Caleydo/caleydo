package org.caleydo.core.manager.view.listener;

import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.selection.AddSelectionEvent;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;

public class AddSelectionListener
	extends AEventListener<ConnectedElementRepresentationManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AddSelectionEvent) {
			AddSelectionEvent addSelectionEvent = (AddSelectionEvent) event;
			int connectionID = addSelectionEvent.getConnectionID();
			SelectedElementRep ser = addSelectionEvent.getSelectedElementRep();
			handler.handleAddSelectionEvent(connectionID, ser);
		}
	}

}
