/**
 * 
 */
package org.caleydo.view.dvi.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.RenameDataDomainEvent;

/**
 * Listener for {@link RenameDataDomainEvent}.
 * 
 * @author Christian Partl
 * 
 */
public class RenameDataDomainEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RenameDataDomainEvent) {
			RenameDataDomainEvent renameDataDomainEvent = (RenameDataDomainEvent) event;
			handler.renameDataDomain(renameDataDomainEvent.getDataDomain());
		}
	}

}
