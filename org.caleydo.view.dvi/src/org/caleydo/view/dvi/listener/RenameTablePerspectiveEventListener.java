/**
 * 
 */
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.RenameTablePerspectiveEvent;

/**
 * Listener for {@link RenameTablePerspectiveEvent}
 * 
 * @author Christian Partl
 * 
 */
public class RenameTablePerspectiveEventListener extends
		AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {

		if (event instanceof RenameTablePerspectiveEvent) {
			RenameTablePerspectiveEvent renameTablePerspectiveEvent = (RenameTablePerspectiveEvent) event;
			handler.renameTablePerspective(renameTablePerspectiveEvent
					.getTablePerspective());
		}

	}

}
