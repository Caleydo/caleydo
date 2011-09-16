package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.event.UseFishEyeEvent;

/**
 * @author Alexander Lex
 * 
 */
public class UseFishEyeListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		handler.setUseFishEye(((UseFishEyeEvent) event).isUseFishEye());
	}

}
