package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceContentVAEvent;

/**
 * @author Alexander Lex
 */
public class ReplaceContentVAListener
	extends AEventListener<IContentVAUpdateHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceContentVAEvent) {
			ReplaceContentVAEvent vaEvent = ((ReplaceContentVAEvent) event);

			handler.replaceVA(vaEvent.getSetID(), vaEvent.getDataDomainType(), vaEvent.getVaType());
		}
	}
}
