package org.caleydo.view.browser;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.swt.ASWTView;
import org.eclipse.swt.widgets.Composite;

public class RcpHTMLBrowserView extends CaleydoRCPViewPart {

	@Override
	@SuppressWarnings("unchecked")
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		view = new GenomeHTMLBrowser(parentComposite);

		if (view instanceof IDataDomainBasedView<?>) {
				((IDataDomainBasedView<IDataDomain>) view).setDataDomain(DataDomainManager
						.get().getDataDomain(serializedView.getDataDomainType()));
		}

		((ASWTView)view).draw();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
//		browserView.unregisterEventListeners();
//		GeneralManager.get().getViewGLCanvasManager().unregisterItem(browserView.getID());
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedHTMLBrowserView();
		determineDataDomain(serializedView);
	}
}
