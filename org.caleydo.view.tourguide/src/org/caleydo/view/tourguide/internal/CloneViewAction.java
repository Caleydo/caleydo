package org.caleydo.view.tourguide.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class CloneViewAction extends Action implements IWorkbenchAction {
	private final RcpGLTourGuideView view;

	public CloneViewAction(RcpGLTourGuideView view) {
		super("Clone Tour Guide View");
		this.view = view;
		setImageDescriptor(Activator.getImageDescriptor("/resources/icons/page_copy.png"));
	}

	@Override
	public void run() {
		view.cloneView(true);
	}

	@Override
	public void dispose() {
	}

}
