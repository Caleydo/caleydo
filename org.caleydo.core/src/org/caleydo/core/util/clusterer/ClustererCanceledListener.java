package org.caleydo.core.util.clusterer;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;

/**
 * Listener for IClusterer that checks whether a cancel was triggered
 * 
 * @author Alexander Lex
 */
public class ClustererCanceledListener
	extends AEventListener<IClusterer> {

	@Override
	public void handleEvent(AEvent event) {
		handler.cancel();
	}

}
