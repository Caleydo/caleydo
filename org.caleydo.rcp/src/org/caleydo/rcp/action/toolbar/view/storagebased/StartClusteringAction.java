package org.caleydo.rcp.action.toolbar.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class StartClusteringAction
	extends AToolBarAction
	implements IToolBarItem {
	public static final String TEXT = "Start Clustering";
	// TODO: own icon for "Start Clustering"
	public static final String ICON = "resources/icons/view/storagebased/change_orientation.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public StartClusteringAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(bEnable);
	}

	@Override
	public void run() {
		super.run();
		triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.STORAGEBASED_START_CLUSTERING);
	};
}