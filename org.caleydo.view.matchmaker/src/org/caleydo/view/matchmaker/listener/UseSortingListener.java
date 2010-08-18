package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.event.UseSortingEvent;

/**
 * @author Alexander Lex
 * 
 */
public class UseSortingListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		handler.setUseSorting(((UseSortingEvent) event).isUseSorting());
	}
}
