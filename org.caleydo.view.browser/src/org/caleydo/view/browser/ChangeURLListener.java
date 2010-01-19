package org.caleydo.view.browser;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;

/**
 * Handles change-url events by setting the related browser-view's URL
 * 
 * @author Marc Streit
 */
public class ChangeURLListener extends ABrowserListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ChangeURLEvent) {
			handler.setUrl(((ChangeURLEvent) event).getUrl());
		}
	}

}
