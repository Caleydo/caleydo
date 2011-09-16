package org.caleydo.view.matchmaker.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.event.UseBandBundlingEvent;

/**
 * @author Marc Streit
 * 
 */
public class UseBandBundlingListener extends AEventListener<GLMatchmaker> {

	@Override
	public void handleEvent(AEvent event) {
		handler.setBandBundling(((UseBandBundlingEvent) event).isBandBundlingActive());
	}
}
