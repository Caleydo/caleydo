package org.caleydo.rcp.action.toolbar.view.remote;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.view.swt.toolbar.content.remote.RemoteRenderingToolBarMediator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class NavigationModeAction
	extends Action
	implements IToolBarItem {

	public static final String TEXT = "Toggle navigation mode";
	public static final String ICON = "resources/icons/view/remote/navigation_mode.png";

	/** mediator to handle actions triggered by instances of this class */
	RemoteRenderingToolBarMediator remoteRenderingToolBarMediator;
	
	/**
	 * Constructor.
	 */
	public NavigationModeAction(RemoteRenderingToolBarMediator mediator) {

		remoteRenderingToolBarMediator = mediator;

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();

		remoteRenderingToolBarMediator.toggleNavigationMode();
	}
}
