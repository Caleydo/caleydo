package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * @author Alexander Lex
 */
public class RecordReplaceVAListener
	extends AEventListener<IRecordVADeltaHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RecordReplaceVAEvent) {
			RecordReplaceVAEvent vaEvent = ((RecordReplaceVAEvent) event);

			handler.replaceRecordVA(vaEvent.getDataDomainID(), vaEvent.getPerspectiveID(),
				vaEvent.getVirtualArray());
		}
	}
}
