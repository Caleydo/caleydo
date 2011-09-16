package org.caleydo.core.view.listener;

import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.selection.AddSelectionEvent;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;

/**
 * Listens for {@link AddSelectionEvent} to pass it to the related
 * {@link ConnectedElementRepresentationManager} for handling.
 * 
 * @author Werner Puff
 */
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
