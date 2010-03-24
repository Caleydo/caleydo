package org.caleydo.view.compare.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.event.UseBandBundlingEvent;
import org.caleydo.view.compare.event.UseSortingEvent;

/**
 * @author Marc Streit
 *
 */
public class UseBandBundlingListener extends AEventListener<GLCompare> {

	@Override
	public void handleEvent(AEvent event) {
		handler.setBandBundling(((UseBandBundlingEvent) event).isBandBundlingActive());
	}
}
