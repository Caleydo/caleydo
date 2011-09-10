package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * @author Alexander Lex
 */
public class ReplaceRecordPerspectiveListener
	extends AEventListener<IRecordVADeltaHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceRecordPerspectiveEvent) {
			ReplaceRecordPerspectiveEvent replaceEvent = ((ReplaceRecordPerspectiveEvent) event);

			handler.replaceRecordPerspective(replaceEvent.getDataDomainID(), replaceEvent.getPerspectiveID(),
				replaceEvent.getPerspectiveInitializationData());
		}
	}
}
