package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * @author Alexander Lex
 */
public class DimensionReplaceVAListener
	extends AEventListener<IDimensionVADeltaHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof DimensionReplaceVAEvent) {
			DimensionReplaceVAEvent vaEvent = ((DimensionReplaceVAEvent) event);

			handler.replaceDimensionVA(vaEvent.getDataDomainID(), vaEvent.getPerspectiveID(),
				vaEvent.getVirtualArray());

		}
	}
}
