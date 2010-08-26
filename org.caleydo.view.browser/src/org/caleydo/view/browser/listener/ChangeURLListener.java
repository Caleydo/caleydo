package org.caleydo.view.browser.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.view.browser.HTMLBrowser;

/**
 * Handles change-url events by setting the related browser-view's URL
 * 
 * @author Marc Streit
 */
public class ChangeURLListener extends AEventListener<HTMLBrowser> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ChangeURLEvent) {
			handler.setUrl(((ChangeURLEvent) event).getUrl());
		}
	}

}
