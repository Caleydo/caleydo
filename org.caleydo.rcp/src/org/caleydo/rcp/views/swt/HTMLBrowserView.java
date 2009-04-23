package org.caleydo.rcp.views.swt;

import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;
import org.caleydo.rcp.views.CaleydoViewPart;
import org.eclipse.swt.widgets.Composite;

public class HTMLBrowserView
	extends CaleydoViewPart {
	public static final String ID = "org.caleydo.rcp.views.swt.HTMLBrowserView";

	private HTMLBrowserViewRep browserView;

	@Override
	public void createPartControl(Composite parent) {
		browserView =
			(HTMLBrowserViewRep) GeneralManager.get().getViewGLCanvasManager().createView(
				EManagedObjectType.VIEW_SWT_BROWSER_GENOME, -1, "Browser");

		browserView.initViewRCP(parent);
		browserView.drawView();
		iViewID = browserView.getID();

		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
			(IMediatorReceiver) browserView);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getEventPublisher().removeReceiver(EMediatorType.SELECTION_MEDIATOR,
			(IMediatorReceiver) browserView);

		GeneralManager.get().getEventPublisher().removeReceiver(EMediatorType.SELECTION_MEDIATOR,
			(IMediatorReceiver) browserView);

		GeneralManager.get().getViewGLCanvasManager().unregisterItem(browserView.getID());
	}

	public HTMLBrowserViewRep getHTMLBrowserViewRep() {
		return browserView;
	}
}
