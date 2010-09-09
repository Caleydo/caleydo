package org.caleydo.view.bucket.toolbar.actions;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ToggleLayoutAction extends AToolBarAction {
	
	public static final String TEXT = "Toggle between Bucket and Jukebox layout";
	public static final String ICON = "resources/icons/view/remote/toggle.png";

	/**
	 * Constructor.
	 */
	public ToggleLayoutAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();

		// not in use ATM
		// triggerCmdExternalAction(EExternalActionType.REMOTE_RENDERING_TOGGLE_LAYOUT_MODE);
	};
}
