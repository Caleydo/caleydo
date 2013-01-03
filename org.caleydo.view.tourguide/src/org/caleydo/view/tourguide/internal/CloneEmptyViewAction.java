package org.caleydo.view.tourguide.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class CloneEmptyViewAction extends Action implements IWorkbenchAction {
	private final RcpGLTourGuideView view;

	public CloneEmptyViewAction(RcpGLTourGuideView view) {
		super("Clone Empty Tour Guide View");
		this.view = view;
		setImageDescriptor(Activator.getImageDescriptor("/resources/icons/page_add.png"));
	}

	@Override
	public void run() {
		view.cloneView(false);
	}

	@Override
	public void dispose() {
	}

}
