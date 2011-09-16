package org.caleydo.core.event.view.browser;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;

/**
 * Events that signals browser-views to load a new URL.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
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
