package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.vislink.VisLinkManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * SWT event listener for requesting busy mode
 * 
 * @author Werner Puff
 */
public class StopVisLinksListener
	implements Listener {

	Object requester;

	@Override
	public void handleEvent(Event event) {
		VisLinkManager visLinkManager = VisLinkManager.get();
		visLinkManager.dispose();
	}

	public void setRequester(Object requester) {
		this.requester = requester;
	}

}
