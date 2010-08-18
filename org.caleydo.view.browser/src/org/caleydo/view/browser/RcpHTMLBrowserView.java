package org.caleydo.view.browser;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpHTMLBrowserView extends CaleydoRCPViewPart {

	private HTMLBrowser browserView;

	@Override
	@SuppressWarnings("unchecked")
	public void createPartControl(Composite parent) {
		browserView = (GenomeHTMLBrowser) GeneralManager.get().getViewGLCanvasManager()
				.createView(HTMLBrowser.VIEW_ID, -1, "Browser");

		browserView.initViewRCP(parent);
		browserView.drawView();
		view = browserView;

		if (view instanceof IDataDomainBasedView<?>) {
			String dataDomainType = determineDataDomain(view
					.getSerializableRepresentation());
			((IDataDomainBasedView<IDataDomain>) view).setDataDomain(DataDomainManager
					.getInstance().getDataDomain(dataDomainType));
		}
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

	public HTMLBrowser getHTMLBrowserViewRep() {
		return browserView;
	}
}
