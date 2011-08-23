package org.caleydo.view.browser;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
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
						.get().getDataDomainByID(serializedView.getDataDomainID()));
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
		determineDataConfiguration(serializedView);
	}
}
