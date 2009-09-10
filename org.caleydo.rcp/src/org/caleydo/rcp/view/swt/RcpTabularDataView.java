package org.caleydo.rcp.view.swt;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.tabular.SerializedTabularDataView;
import org.caleydo.core.view.swt.tabular.TabularDataViewRep;
import org.caleydo.rcp.view.CaleydoRCPViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpTabularDataView
	extends CaleydoRCPViewPart {

	public static final String ID = SerializedTabularDataView.GUI_ID;

	private TabularDataViewRep tabularDataView;

	@Override
	public void createPartControl(Composite parent) {
		tabularDataView =
			(TabularDataViewRep) GeneralManager.get().getViewGLCanvasManager().createView(
				EManagedObjectType.VIEW_SWT_TABULAR_DATA_VIEWER, -1, "Tabular Data Viewer");

		// tabularDataView.setInputFile(GeneralManager.get().getGUIBridge()
		// .getFileNameCurrentDataSet());

		IUseCase useCase = GeneralManager.get().getUseCase();
		tabularDataView.setSet(useCase.getSet());
		tabularDataView.initViewRCP(parent);
		tabularDataView.drawView();
		
		parentComposite = parent;

		GeneralManager.get().getViewGLCanvasManager().registerItem(tabularDataView);
		iViewID = tabularDataView.getID();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public TabularDataViewRep getTabularDataView() {
		return tabularDataView;
	}
}
