package org.caleydo.core.manager.event.view.browser;

import org.caleydo.core.manager.event.AEvent;

/**
 * Events that signals browser-views to load a new URL.
 * 
 * @author Marc Streit
 */
public class ChangeURLEvent
	extends AEvent {

	/** the new URL to load by the browser */
	String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean checkIntegrity() {
		if (url == null)
			throw new NullPointerException("url was null");
		return true;
	}

}
