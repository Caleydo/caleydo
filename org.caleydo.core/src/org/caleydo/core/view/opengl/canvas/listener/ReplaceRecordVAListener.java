package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceRecordVAEvent;

/**
 * @author Alexander Lex
 */
public class ReplaceRecordVAListener
	extends AEventListener<IRecordVAUpdateHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceRecordVAEvent) {
			ReplaceRecordVAEvent vaEvent = ((ReplaceRecordVAEvent) event);

			handler.replaceRecordVA(vaEvent.getDataTableID(), vaEvent.getDataDomainID(), vaEvent.getVaType());
		}
	}
}
