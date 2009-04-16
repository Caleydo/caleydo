package org.caleydo.rcp.action.toolbar.view.remote;

import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ToggleConnectionLinesAction
	extends AToolBarAction
	implements IToolBarItem {
	public static final String TEXT = "Turn on/off connection lines";
	public static final String ICON = "resources/icons/view/remote/connection_lines.png";

	/**
	 * Constructor.
	 */
	public ToggleConnectionLinesAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(true);
	}

	@Override
	public void run() {
		super.run();

		triggerCmdExternalAction(EExternalActionType.REMOTE_RENDERING_TOGGLE_CONNECTION_LINES_MODE);
	};
}
