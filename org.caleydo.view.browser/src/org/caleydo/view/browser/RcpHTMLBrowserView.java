package org.caleydo.view.browser;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpHTMLBrowserView extends CaleydoRCPViewPart {

	private HTMLBrowser browserView;

	@Override
	public void createPartControl(Composite parent) {
		browserView = (GenomeHTMLBrowser) GeneralManager.get()
				.getViewGLCanvasManager().createView(GenomeHTMLBrowser.VIEW_ID, -1,
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
