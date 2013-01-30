/**
 * 
 */
package org.caleydo.view.dvi.listener;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.dvi.GLDataViewIntegrator;

/**
 * Listener for {@link RecordVAUpdateEvent} specific for
 * {@link GLDataViewIntegrator}.
 * 
 * @author Christian
 * 
 */
public class RecordVAUpdateEventListener extends AEventListener<GLDataViewIntegrator> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RecordVAUpdateEvent) {
			RecordVAUpdateEvent virtualArrayUpdateEvent = (RecordVAUpdateEvent) event;
			IDataDomain datadomain = DataDomainManager.get().getDataDomainByID(
					virtualArrayUpdateEvent.getEventSpace());
			handler.updateDataDomain(datadomain);
		}

	}

}
