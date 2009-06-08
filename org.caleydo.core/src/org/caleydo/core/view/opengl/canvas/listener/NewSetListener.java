package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.NewSetEvent;

/**
 * Listener for the new set event. Calls setSet on {@link #INewSetHandler}s.
 * 
 * @author Alexander Lex
 */
public class NewSetListener
	extends AEventListener<INewSetHandler> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof NewSetEvent) {
			NewSetEvent newSetEvent = (NewSetEvent) event;
			handler.setSet(newSetEvent.getSet());
		}

	}

}
