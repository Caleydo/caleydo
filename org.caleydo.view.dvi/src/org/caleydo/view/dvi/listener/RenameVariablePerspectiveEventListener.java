/**
 * 
 */
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.RenameVariablePerspectiveEvent;

/**
 * Listener for {@link RenameVariablePerspectiveEvent}.
 * 
 * @author Christian Partl
 * 
 */
public class RenameVariablePerspectiveEventListener extends
		AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RenameVariablePerspectiveEvent) {
			RenameVariablePerspectiveEvent renameVariablePerspectiveEvent = (RenameVariablePerspectiveEvent) event;
			handler.renameVariablePerspective(
					renameVariablePerspectiveEvent.getPerspectiveID(),
					renameVariablePerspectiveEvent.getDataDomain(),
					renameVariablePerspectiveEvent.isRecordPerspective());
		}

	}
}
