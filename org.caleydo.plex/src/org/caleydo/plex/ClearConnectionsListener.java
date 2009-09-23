package org.caleydo.plex;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.selection.ClearConnectionsEvent;

public class ClearConnectionsListener
		extends
			AEventListener<DeskothequeManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ClearConnectionsEvent) {
			ClearConnectionsEvent clearConnectionsEvent = (ClearConnectionsEvent) event;
			EIDType idType = clearConnectionsEvent.getIdType();
			handler.clearConnections(idType);
		}
	}

}
