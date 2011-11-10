package org.caleydo.core.util.mapping.color;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;

/**
 * Listener for {@link UpdateColorMappingEvent}
 * 
 * @author Alexander Lex
 */
public class UpdateColorMappingListener
	extends AEventListener<IColorMappingUpdateListener> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof UpdateColorMappingEvent) {
			handler.updateColorMapping();
		}
	}

}
