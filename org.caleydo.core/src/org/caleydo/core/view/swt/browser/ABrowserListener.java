package org.caleydo.core.view.swt.browser;

import org.caleydo.core.manager.event.IEventListener;

public abstract class ABrowserListener 
	implements IEventListener {
	
	/** browser related to this listener */
	GenomeHTMLBrowserViewRep browserView;

	public GenomeHTMLBrowserViewRep getBrowserView() {
		return browserView;
	}

	public void setBrowserView(GenomeHTMLBrowserViewRep browserView) {
		this.browserView = browserView;
	}

}
