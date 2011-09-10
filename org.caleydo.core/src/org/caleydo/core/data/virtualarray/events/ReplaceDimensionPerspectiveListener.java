package org.caleydo.core.data.virtualarray.events;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * @author Alexander Lex
 */
public class ReplaceDimensionPerspectiveListener
	extends AEventListener<IDimensionChangeHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceDimensionPerspectiveEvent) {
			ReplaceDimensionPerspectiveEvent vaEvent = ((ReplaceDimensionPerspectiveEvent) event);

			handler.replaceDimensionPerspective(vaEvent.getDataDomainID(), vaEvent.getPerspectiveID(),
				vaEvent.getPerspectiveInitializationData());

		}
	}
}
