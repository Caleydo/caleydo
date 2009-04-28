package org.caleydo.rcp.views.swt;

import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.collab.CollabViewRep;
import org.caleydo.rcp.views.CaleydoViewPart;
import org.eclipse.swt.widgets.Composite;

public class CollabView
	extends CaleydoViewPart {
	public static final String ID = "org.caleydo.rcp.views.swt.CollabView";

	private CollabViewRep testingView;

	@Override
	public void createPartControl(Composite parent) {
		testingView =
			(CollabViewRep) GeneralManager.get().getViewGLCanvasManager().createView(
				EManagedObjectType.VIEW_SWT_COLLAB, -1, "Collaboration");

		testingView.initViewRCP(parent);
		testingView.drawView();

		swtComposite = parent;

		GeneralManager.get().getViewGLCanvasManager().registerItem(testingView);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getEventPublisher().removeSender(EMediatorType.SELECTION_MEDIATOR, testingView);
		GeneralManager.get().getEventPublisher()
			.removeReceiver(EMediatorType.SELECTION_MEDIATOR, testingView);
	}

	public CollabViewRep getTestingView() {
		return testingView;
	}
}
