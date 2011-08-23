package org.caleydo.view.tabular;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.swt.ASWTView;
import org.eclipse.swt.widgets.Composite;

public class RcpTabularDataView extends CaleydoRCPViewPart {

	private TabularDataView tabularDataView;

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		view = new TabularDataView(parentComposite);

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
		tabularDataView.unregisterEventListeners();
		GeneralManager.get().getViewManager()
				.unregisterItem(tabularDataView.getID());
	}

	public TabularDataView getTabularDataView() {
		return tabularDataView;
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedTabularDataView();
		determineDataConfiguration(serializedView);
	}
}
