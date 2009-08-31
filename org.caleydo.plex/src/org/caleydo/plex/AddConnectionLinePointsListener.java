package org.caleydo.plex;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.selection.AddConnectionLinePointEvent;
import org.caleydo.core.manager.view.SelectionPoint2DList;

public class AddConnectionLinePointsListener
	extends AEventListener<DeskothequeManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AddConnectionLinePointEvent) {
			AddConnectionLinePointEvent add = (AddConnectionLinePointEvent) event;
			EIDType idType = add.getIdType();
			int connectionID = add.getConnectionID();
			SelectionPoint2DList points = add.getPoints();
			handler.addConnectionLinePoints(idType, connectionID, points);
		}
	}

}
