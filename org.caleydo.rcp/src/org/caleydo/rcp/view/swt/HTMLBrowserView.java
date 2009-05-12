package org.caleydo.rcp.view.swt;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;
import org.caleydo.rcp.view.CaleydoViewPart;
import org.eclipse.swt.widgets.Composite;

public class HTMLBrowserView
	extends CaleydoViewPart {
	public static final String ID = "org.caleydo.rcp.views.swt.HTMLBrowserView";

	private HTMLBrowserViewRep browserView;

	@Override
	public void createPartControl(Composite parent) {
		browserView =
			(HTMLBrowserViewRep) GeneralManager.get().getViewGLCanvasManager().createView(
				EManagedObjectType.VIEW_SWT_BROWSER_GENOME, -1, "Browser");

		browserView.registerEventListeners();
		browserView.initViewRCP(parent);
		browserView.drawView();
		iViewID = browserView.getID();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
		browserView.unregisterEventListeners();
		GeneralManager.get().getViewGLCanvasManager().unregisterItem(browserView.getID());
	}

	public HTMLBrowserViewRep getHTMLBrowserViewRep() {
		return browserView;
	}
}
