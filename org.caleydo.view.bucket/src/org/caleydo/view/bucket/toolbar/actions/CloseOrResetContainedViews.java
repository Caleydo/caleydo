package org.caleydo.view.bucket.toolbar.actions;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.view.bucket.toolbar.RemoteRenderingToolBarMediator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class CloseOrResetContainedViews extends Action implements IToolBarItem {

	/** mediator to handle actions triggered by instances of this class */
	RemoteRenderingToolBarMediator remoteRenderingToolBarMediator;

	public static final String TEXT = "Remove Pathways, Reset Other views";
	public static final String ICON = "resources/icons/view/remote/close_or_reset_contained_views.png";

	/**
	 * Constructor.
	 */
	public CloseOrResetContainedViews(RemoteRenderingToolBarMediator mediator) {
		remoteRenderingToolBarMediator = mediator;

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();
		remoteRenderingToolBarMediator.closeOrResetViews();
	};
}
