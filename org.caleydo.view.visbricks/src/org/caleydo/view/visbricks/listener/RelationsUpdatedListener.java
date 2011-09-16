package org.caleydo.view.visbricks.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.data.RelationsUpdatedEvent;
import org.caleydo.view.visbricks.brick.GLBrick;

/**
 * Listener for {@link RelationsUpdatedEvent}, calling to {@link GLBrick}.
 * 
 * @author Alexander Lex
 * 
 */
public class RelationsUpdatedListener extends AEventListener<GLBrick> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof RelationsUpdatedEvent) {
			handler.relationsUpdated();
		}
	}

}
