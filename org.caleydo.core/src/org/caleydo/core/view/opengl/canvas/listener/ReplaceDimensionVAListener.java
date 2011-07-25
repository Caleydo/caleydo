package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceDimensionVAEvent;

/**
 * @author Alexander Lex
 */
public class ReplaceDimensionVAListener
	extends AEventListener<IDimensionVAUpdateHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceDimensionVAEvent) {
			ReplaceDimensionVAEvent vaEvent = ((ReplaceDimensionVAEvent) event);

			handler.replaceDimensionVA(vaEvent.getDataDomainID(), vaEvent.getVaType());

		}

	}

}
