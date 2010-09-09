package org.caleydo.util.r.view;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpStatisticsView extends CaleydoRCPViewPart {

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		view = new StatisticsView(parentComposite);

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
		// view.unregisterEventListeners();
		// GeneralManager.get().getViewGLCanvasManager()
		// .unregisterItem(statisticsView.getID());
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedStatisticsView();
		determineDataDomain(serializedView);
	}
}
