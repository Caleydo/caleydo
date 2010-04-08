package org.caleydo.view.compare.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.compare.GLMatchmaker;
import org.caleydo.view.compare.event.UseZoomEvent;

/**
 * @author Alexander Lex
 *
 */
public class UseZoomListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		handler.setUseZoom(((UseZoomEvent) event).isUseZoom());
	}
}
