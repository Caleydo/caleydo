package org.caleydo.core.manager.datadomain;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceStorageVAInUseCaseEvent;

public class ReplaceStorageVAInUseCaseListener
	extends AEventListener<ATableBasedDataDomain> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceStorageVAInUseCaseEvent) {
			ReplaceStorageVAInUseCaseEvent vaEvent = (ReplaceStorageVAInUseCaseEvent) event;

			handler.replaceStorageVA(vaEvent.getDataDomainID(), vaEvent.getVaType(),
				vaEvent.getVirtualArray());
		}

	}

}
