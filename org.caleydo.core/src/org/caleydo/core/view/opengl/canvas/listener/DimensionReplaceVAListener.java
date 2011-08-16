package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.DimensionReplaceVAEvent;

/**
 * @author Alexander Lex
 */
public class DimensionReplaceVAListener
	extends AEventListener<IDimensionVADeltaHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof DimensionReplaceVAEvent) {
			DimensionReplaceVAEvent vaEvent = ((DimensionReplaceVAEvent) event);

			handler.replaceDimensionVA(vaEvent.getDataDomainID(), vaEvent.getVaType());
		}
	}
}
