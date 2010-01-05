package org.caleydo.view.base.swt;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;
import org.caleydo.core.view.swt.browser.SerializedHTMLBrowserView;
import org.caleydo.view.base.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpHTMLBrowserView
	extends CaleydoRCPViewPart {

	public static final String ID = SerializedHTMLBrowserView.GUI_ID;

	private HTMLBrowserViewRep browserView;

	@Override
	public void createPartControl(Composite parent) {
		browserView =
			(HTMLBrowserViewRep) GeneralManager.get().getViewGLCanvasManager().createView(
				EManagedObjectType.VIEW_SWT_BROWSER_GENOME, -1, "Browser");

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
