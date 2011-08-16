package org.caleydo.core.data.datadomain;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceDimensionVAInUseCaseEvent;

public class ReplaceDimensionVAInUseCaseListener
	extends AEventListener<ATableBasedDataDomain> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceDimensionVAInUseCaseEvent) {
			ReplaceDimensionVAInUseCaseEvent vaEvent = (ReplaceDimensionVAInUseCaseEvent) event;

			handler.replaceDimensionVA(vaEvent.getDataDomainID(), vaEvent.getVaType(),
				vaEvent.getVirtualArray());
		}
	}
}
