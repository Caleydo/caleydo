package org.caleydo.core.view.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.selection.NewConnectionsEvent;
import org.caleydo.core.view.vislink.ISelectionTransformer;

/**
 * Listener for {@link NewConnectionsEvent}s to call the related {@link ISelectionTransformer} for handling.
 * 
 * @author Werner Puff
 */
public class NewConnectionsListener
	extends AEventListener<ISelectionTransformer> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewConnectionsEvent) {
			handler.handleNewConnections();
		}
	}

}
