package org.caleydo.core.manager.datadomain;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;

public class ReplaceContentVAInUseCaseListener
	extends AEventListener<ADataDomain> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceContentVAInUseCaseEvent) {
			ReplaceContentVAInUseCaseEvent vaEvent = (ReplaceContentVAInUseCaseEvent) event;

			if (vaEvent.getSetID() <= 0)
				handler.replaceContentVA(vaEvent.getIDCategory(), vaEvent.getVaType(), vaEvent.getVirtualArray());
			else
				handler.replaceContentVA(vaEvent.getSetID(), vaEvent.getIDCategory(), vaEvent.getVaType(), vaEvent.getVirtualArray());
		}

	}

}
