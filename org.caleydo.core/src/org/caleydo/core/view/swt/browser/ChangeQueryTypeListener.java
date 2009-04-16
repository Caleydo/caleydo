package org.caleydo.core.view.swt.browser;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.IEventListener;
import org.caleydo.core.manager.event.view.browser.ChangeQueryTypeEvent;

/**
 * Handles change-query-type events by setting the related browser-view's query type
 * @author Werner Puff
 */
public class ChangeQueryTypeListener
	extends ABrowserListener
	implements IEventListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ChangeQueryTypeEvent) {
			getBrowserView().changeQueryType(((ChangeQueryTypeEvent) event).getQueryType());
		}
	}

}
