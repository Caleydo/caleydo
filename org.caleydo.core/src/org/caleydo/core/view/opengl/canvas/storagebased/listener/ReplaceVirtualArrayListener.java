package org.caleydo.core.view.opengl.canvas.storagebased.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayEvent;
import org.caleydo.core.view.opengl.canvas.listener.IVirtualArrayUpdateHandler;

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
