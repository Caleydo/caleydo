package org.caleydo.core.manager.datadomain;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceRecordVAInUseCaseEvent;

public class ReplaceRecordVAInUseCaseListener
	extends AEventListener<ATableBasedDataDomain> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceRecordVAInUseCaseEvent) {
			ReplaceRecordVAInUseCaseEvent vaEvent = (ReplaceRecordVAInUseCaseEvent) event;

			if (vaEvent.getDataTableID() <= 0)
				handler.replaceRecordVA(vaEvent.getDataDomainID(), vaEvent.getVaType(),
					vaEvent.getVirtualArray());
			else
				handler.replaceRecordVA(vaEvent.getDataTableID(), event.getDataDomainID(), vaEvent.getVaType(),
					vaEvent.getVirtualArray());
		}

	}

}
