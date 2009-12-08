package org.caleydo.core.manager.usecase;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayInUseCaseEvent;

public class ReplaceVirtualArrayInUseCaseListener
	extends AEventListener<AUseCase> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceVirtualArrayInUseCaseEvent) {
			ReplaceVirtualArrayInUseCaseEvent vaEvent = (ReplaceVirtualArrayInUseCaseEvent) event;

			handler.replaceVirtualArray(vaEvent.getIDCategory(), vaEvent.getVaType(), vaEvent
				.getVirtualArray());
		}

	}

}
