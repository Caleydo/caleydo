package org.caleydo.core.manager.usecase;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;

public class ReplaceContentVAInUseCaseListener
	extends AEventListener<AUseCase> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceContentVAInUseCaseEvent) {
			ReplaceContentVAInUseCaseEvent vaEvent = (ReplaceContentVAInUseCaseEvent) event;

			handler.replaceContentVA(vaEvent.getIDCategory(), vaEvent.getVaType(), vaEvent.getVirtualArray());
		}

	}

}
