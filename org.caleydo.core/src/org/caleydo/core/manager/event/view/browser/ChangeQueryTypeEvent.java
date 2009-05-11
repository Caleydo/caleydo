package org.caleydo.core.manager.event.view.browser;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.view.swt.browser.EBrowserQueryType;

/**
 * Events that signals browser-views to change the query type
 * 
 * @author Werner Puff
 */
public class ChangeQueryTypeEvent
	extends AEvent {

	/** the query type to set by the browser */
	EBrowserQueryType queryType;

	public EBrowserQueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(EBrowserQueryType queryType) {
		this.queryType = queryType;
	}

	@Override
	public boolean checkIntegrity() {
		if (queryType == null)
			throw new NullPointerException("queryType was null");
		return true;
	}

}
