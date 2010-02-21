package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceStorageVAEvent;

/**
 * @author Alexander Lex
 */
public class ReplaceStorageVAListener
	extends AEventListener<IStorageVAUpdateHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceStorageVAEvent) {
			ReplaceStorageVAEvent vaEvent = ((ReplaceStorageVAEvent) event);

			handler.replaceStorageVA(vaEvent.getIDCategory(), vaEvent.getVaType());

		}

	}

}
