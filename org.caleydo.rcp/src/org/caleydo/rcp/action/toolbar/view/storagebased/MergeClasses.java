package org.caleydo.rcp.action.toolbar.view.storagebased;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class MergeClasses
	extends AToolBarAction {
	public static final String TEXT = "Merge classes";
	// TODO: own icon for "Start Clustering"
	public static final String ICON = "resources/icons/view/storagebased/change_orientation.png";

	private boolean bEnable = false;

	/**
	 * Constructor.
	 */
	public MergeClasses(int iViewID) {
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
		triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.STORAGEBASED_MERGE_CLASSES);
	};
}