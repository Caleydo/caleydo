package org.caleydo.rcp.views.swt;

import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.tabular.TabularDataViewRep;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TabularDataView
	extends ViewPart
{
	public static final String ID = "org.caleydo.rcp.views.TabularDataView";

	private TabularDataViewRep tabularDataView;

	@Override
	public void createPartControl(Composite parent)
	{
		tabularDataView = (TabularDataViewRep) GeneralManager.get().getViewGLCanvasManager()
				.createView(EManagedObjectType.VIEW_SWT_TABULAR_DATA_VIEWER, -1,
						"Tabular Data Viewer");

		tabularDataView.setInputFile(GeneralManager.get().getGUIBridge()
				.getFileNameCurrentDataSet());
		tabularDataView.initViewRCP(parent);
		tabularDataView.drawView();

		GeneralManager.get().getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR,
				tabularDataView);
		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
				tabularDataView);

		GeneralManager.get().getViewGLCanvasManager().registerItem(tabularDataView);
	}

	@Override
	public void setFocus()
	{

	}

	@Override
	public void dispose()
	{
		super.dispose();

		GeneralManager.get().getEventPublisher().removeSender(
				EMediatorType.SELECTION_MEDIATOR, tabularDataView);
		GeneralManager.get().getEventPublisher().removeReceiver(
				EMediatorType.SELECTION_MEDIATOR, tabularDataView);
	}

	public TabularDataViewRep getTabularDataView()
	{
		return tabularDataView;
	}
}
