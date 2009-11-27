package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayEvent;

/**
 * @author Alexander Lex
 */
public class ReplaceVirtualArrayListener
	extends AEventListener<IVirtualArrayUpdateHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ReplaceVirtualArrayEvent) {
			handler.replaceVirtualArray(((ReplaceVirtualArrayEvent) event).getVaType());

		}

	}

}
