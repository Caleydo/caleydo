package org.caleydo.view.browser;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.view.base.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpHTMLBrowserView extends CaleydoRCPViewPart {

	private HTMLBrowser browserView;

	@Override
	public void createPartControl(Composite parent) {
		browserView = (HTMLBrowser) GeneralManager.get()
				.getViewGLCanvasManager().createView(HTMLBrowser.VIEW_ID, -1,
						"Browser");

		browserView.initViewRCP(parent);
		browserView.drawView();
		view = browserView;
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
		browserView.unregisterEventListeners();
		GeneralManager.get().getViewGLCanvasManager().unregisterItem(
				browserView.getID());
	}

	public HTMLBrowser getHTMLBrowserViewRep() {
		return browserView;
	}
}
