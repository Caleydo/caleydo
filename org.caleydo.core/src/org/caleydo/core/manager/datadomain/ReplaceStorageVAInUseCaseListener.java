package org.caleydo.core.manager.datadomain;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.event.data.ReplaceStorageVAInUseCaseEvent;

public class ReplaceStorageVAInUseCaseListener
	extends AEventListener<ASetBasedDataDomain> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceContentVAInUseCaseEvent) {
			ReplaceStorageVAInUseCaseEvent vaEvent = (ReplaceStorageVAInUseCaseEvent) event;

			handler.replaceStorageVA(vaEvent.getIDCategory(), vaEvent.getVaType(), vaEvent.getVirtualArray());
		}

	}

}
