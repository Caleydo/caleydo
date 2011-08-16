package org.caleydo.view.pathway.listener;

import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.SwitchDataRepresentationEvent;
import org.caleydo.view.pathway.GLPathway;

/**
 * Listener for switches of data representation (see {@link DataRepresentation})
 * 
 * @author Alexander Lex
 */
public class SwitchDataRepresentationListener
	extends AEventListener<GLPathway> {

	/**
	 * Handles {@link SwitchDataRepresentationEvent}s calling the related handler
	 * 
	 */
	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof SwitchDataRepresentationEvent) {
			handler.switchDataRepresentation();
		}
	}
}
