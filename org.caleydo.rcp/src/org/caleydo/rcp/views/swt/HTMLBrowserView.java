package org.caleydo.rcp.views.swt;

import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class HTMLBrowserView
	extends ViewPart
{
	public static final String ID = "org.caleydo.rcp.views.HTMLBrowserView";

	private HTMLBrowserViewRep browserView;

	@Override
	public void createPartControl(Composite parent)
	{	
		browserView = (HTMLBrowserViewRep) GeneralManager.get().getViewGLCanvasManager()
				.createView(EManagedObjectType.VIEW_SWT_BROWSER_GENOME, -1, "Browser");

		browserView.initViewRCP(parent);
		browserView.drawView();

		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
				(IMediatorReceiver) browserView);

		GeneralManager.get().getViewGLCanvasManager().registerItem(browserView);
	}

	@Override
	public void setFocus()
	{

	}

	@Override
	public void dispose()
	{
		super.dispose();

		GeneralManager.get().getEventPublisher().removeReceiver(
				EMediatorType.SELECTION_MEDIATOR, (IMediatorReceiver) browserView);

		GeneralManager.get().getEventPublisher().removeReceiver(
				EMediatorType.SELECTION_MEDIATOR, (IMediatorReceiver) browserView);

		GeneralManager.get().getViewGLCanvasManager().unregisterItem(browserView.getID());
	}
}
