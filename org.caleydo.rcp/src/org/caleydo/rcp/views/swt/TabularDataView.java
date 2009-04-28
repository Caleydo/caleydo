package org.caleydo.rcp.views.swt;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.tabular.TabularDataViewRep;
import org.caleydo.rcp.views.CaleydoViewPart;
import org.eclipse.swt.widgets.Composite;

public class TabularDataView
	extends CaleydoViewPart {
	public static final String ID = "org.caleydo.rcp.views.swt.TabularDataView";

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
		
		useCase.addView(tabularDataView);

		swtComposite = parent;

		GeneralManager.get().getViewGLCanvasManager().registerItem(tabularDataView);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getEventPublisher().removeSender(EMediatorType.SELECTION_MEDIATOR,
			tabularDataView);
		GeneralManager.get().getEventPublisher().removeReceiver(EMediatorType.SELECTION_MEDIATOR,
			tabularDataView);

		GeneralManager.get().getUseCase().removeView(tabularDataView);
	}

	public TabularDataViewRep getTabularDataView() {
		return tabularDataView;
	}
}
