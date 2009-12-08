package org.caleydo.plex;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.selection.AddConnectionLineVerticesEvent;
import org.caleydo.core.manager.event.view.selection.AddSelectionEvent;
import org.caleydo.core.manager.view.SelectionPoint2DList;

/**
 * Listens for {@link AddSelectionEvent} to pass it to the related
 * {@link DeskothequeManager} for handling.
 * 
 * @author Werner Puff
 */
public class AddConnectionLineVerticesListener
		extends
			AEventListener<DeskothequeManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof AddConnectionLineVerticesEvent) {
			AddConnectionLineVerticesEvent add = (AddConnectionLineVerticesEvent) event;
			EIDType idType = add.getIdType();
			int connectionID = add.getConnectionID();
			SelectionPoint2DList points = add.getPoints();
			handler.addConnectionLineVertices(idType, connectionID, points);
		}
	}

}
