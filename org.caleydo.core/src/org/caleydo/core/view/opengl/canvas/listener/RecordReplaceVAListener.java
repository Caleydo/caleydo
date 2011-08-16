package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.RecordReplaceVAEvent;

/**
 * @author Alexander Lex
 */
public class RecordReplaceVAListener
	extends AEventListener<IRecordVADeltaHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RecordReplaceVAEvent) {
			RecordReplaceVAEvent vaEvent = ((RecordReplaceVAEvent) event);

			handler.replaceRecordVA(vaEvent.getTableID(), vaEvent.getDataDomainID(), vaEvent.getVaType());
		}
	}
}
