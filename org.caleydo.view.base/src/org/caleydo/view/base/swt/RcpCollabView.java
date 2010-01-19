package org.caleydo.view.base.swt;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.swt.collab.CollabViewRep;
import org.caleydo.view.base.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpCollabView extends CaleydoRCPViewPart {
	public static final String ID = "org.caleydo.rcp.views.swt.CollabView";

	private CollabViewRep testingView;

	@Override
	public void createPartControl(Composite parent) {
		testingView = (CollabViewRep) GeneralManager.get()
				.getViewGLCanvasManager().createView("org.caleydo.view.collab",
						-1, "Collaboration");

		testingView.initViewRCP(parent);
		testingView.drawView();

		parentComposite = parent;

		GeneralManager.get().getViewGLCanvasManager().registerItem(testingView);
		iViewID = testingView.getID();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
		testingView.dispose();
	}

	public CollabViewRep getTestingView() {
		return testingView;
	}
}
