package org.caleydo.core.view.swt.browser;

import org.caleydo.core.manager.event.IEventListener;

public abstract class ABrowserListener 
	implements IEventListener {
	
	/** browser related to this listener */
	HTMLBrowserViewRep browserView;

	public HTMLBrowserViewRep getBrowserView() {
		return browserView;
	}

	public void setBrowserView(HTMLBrowserViewRep browserView) {
		this.browserView = browserView;
	}

}
